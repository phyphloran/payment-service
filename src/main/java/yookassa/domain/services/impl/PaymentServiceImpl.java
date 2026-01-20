package yookassa.domain.services.impl;


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yookassa.api.dtos.client.CreatePaymentResponseDto;
import yookassa.api.dtos.yookassa.notifications.YookassaWebhookEventDto;
import yookassa.api.exceptionHandler.IncorrectIpException;
import yookassa.domain.mappers.RequestMapper;
import yookassa.domain.services.IpValidator;
import yookassa.external.PaymentHttpClient;
import yookassa.api.dtos.yookassa.responses.YooKassaCreatePaymentResponseDto;
import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.yookassa.requests.YooKassaCreatePaymentRequestDto;
import yookassa.domain.services.PaymentService;


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
        YooKassaCreatePaymentResponseDto response = paymentHttpClient.createPayment(idempotenceKey, requestToYooKassa);
        return new CreatePaymentResponseDto(response.confirmation().confirmationUrl());
    }

    @Override
    public void processPayment(String ip, YookassaWebhookEventDto yookassaWebhookEventDto) {
        if (!ipValidator.isValid(ip)) {
            log.error("An attempt to send a webhook not from an Yookassa IP address: {}", ip);
            throw new IncorrectIpException("Incorrect ip");
        }
        log.info("Recived new yookassaWebhookEvent: {}, from ip: {}", yookassaWebhookEventDto.toString(), ip);
    }

}
