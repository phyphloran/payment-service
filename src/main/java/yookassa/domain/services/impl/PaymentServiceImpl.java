package yookassa.domain.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yookassa.api.dtos.client.CreatePaymentResponseDto;
import yookassa.api.dtos.yookassa.AmountDto;
import yookassa.api.dtos.yookassa.requests.ConfirmationRequestDto;
import yookassa.external.PaymentHttpClient;
import yookassa.api.dtos.yookassa.responses.YooKassaCreatePaymentResponseDto;
import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.yookassa.requests.YooKassaCreatePaymentRequestDto;
import yookassa.domain.services.PaymentService;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentHttpClient paymentHttpClient;

    @Value("${yookassa.type}")
    private String type;

    @Value("${yookassa.return-url}")
    private String returnUrl;

    @Override
    public CreatePaymentResponseDto createPayment(CreatePaymentRequestDto createPaymentRequest) {
        YooKassaCreatePaymentRequestDto requestToYooKassa = createRequestToYooKassa(createPaymentRequest);
        String idempotenceKey = UUID.randomUUID().toString();
        YooKassaCreatePaymentResponseDto response = paymentHttpClient.createPayment(idempotenceKey, requestToYooKassa);
        return new CreatePaymentResponseDto(response.confirmation().confirmationUrl());
    }

    private YooKassaCreatePaymentRequestDto createRequestToYooKassa(CreatePaymentRequestDto createPaymentRequest) {
        return YooKassaCreatePaymentRequestDto.builder()
                .amount(createAmountDto(createPaymentRequest))
                .description(createPaymentRequest.description())
                .capture(true)
                .confirmation(createConfirmationDto(createPaymentRequest))
                .build();
    }

    private AmountDto createAmountDto(CreatePaymentRequestDto createPaymentRequest) {
        return AmountDto.builder()
                .value(String.valueOf(createPaymentRequest.amount()))
                .currency(createPaymentRequest.currency())
                .build();
    }

    private ConfirmationRequestDto createConfirmationDto(CreatePaymentRequestDto createPaymentRequest) {
        return ConfirmationRequestDto.builder()
                .type(type)
                .return_url(returnUrl)
                .build();
    }

}
