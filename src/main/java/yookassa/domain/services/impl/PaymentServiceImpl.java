package yookassa.domain.services.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import yookassa.api.dtos.client.CreatePaymentResponseDto;
import yookassa.api.dtos.yookassa.notifications.YookassaWebhookEventDto;
import yookassa.api.exceptionHandler.IncorrectIpException;
import yookassa.api.exceptionHandler.PaymentProcessingException;
import yookassa.domain.mappers.RequestMapper;
import yookassa.domain.services.IpValidator;
import yookassa.external.PaymentHttpClient;
import yookassa.api.dtos.yookassa.responses.YooKassaCreatePaymentResponseDto;
import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.yookassa.requests.YooKassaCreatePaymentRequestDto;
import yookassa.domain.services.PaymentService;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final IpValidator ipValidator;

    private final RequestMapper requestMapper;

    private final PaymentHttpClient paymentHttpClient;

    @Override
    public CreatePaymentResponseDto createPayment(CreatePaymentRequestDto createPaymentRequest) {
        YooKassaCreatePaymentRequestDto requestToYooKassa = requestMapper
                .toYooKassaCreatePaymentRequestDto(createPaymentRequest);
        String idempotenceKey = UUID.randomUUID().toString();
        try {
            YooKassaCreatePaymentResponseDto response =
                    paymentHttpClient.createPayment(idempotenceKey, requestToYooKassa);
            return new CreatePaymentResponseDto(response.confirmation().confirmationUrl());
        } catch (HttpClientErrorException e) {
            log.error("YooKassa client error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new PaymentProcessingException("Unexpected error, please try again later");
        } catch (HttpServerErrorException e) {
            log.error("YooKassa server error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new PaymentProcessingException("Unexpected error, please try again later");
        } catch (ResourceAccessException e) {
            log.error("YooKassa connection error", e);
            throw new PaymentProcessingException("Unexpected error, please try again later");
        }
    }

    @Override
    public void processPayment(String ip, YookassaWebhookEventDto yookassaWebhookEventDto) {
        if (!ipValidator.isValid(ip)) {
            log.error("An attempt to send a webhook not from an Yookassa IP address: {}", ip);
            throw new IncorrectIpException("Incorrect ip");
        }
        log.info("recived new yookassaWebhookEvent: {}, from ip: {}", yookassaWebhookEventDto.toString(), ip);
    }

}
