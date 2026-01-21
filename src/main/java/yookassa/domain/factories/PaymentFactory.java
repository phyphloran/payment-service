package yookassa.domain.factories;


import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.yookassa.responses.YooKassaCreatePaymentResponseDto;
import yookassa.domain.entities.PaymentEntity;
import yookassa.domain.entities.PaymentStatus;


public class PaymentFactory {

    public static PaymentEntity buildPaymentEntity(CreatePaymentRequestDto createPaymentRequest, YooKassaCreatePaymentResponseDto response) {
        return PaymentEntity.builder()
                .userId(createPaymentRequest.userId())
                .yookassaPaymentId(response.id())
                .idempotenceKey(createPaymentRequest.idempotenceKey())
                .amount(createPaymentRequest.amount())
                .currency(createPaymentRequest.currency())
                .description(createPaymentRequest.description())
                .paymentStatus(PaymentStatus.PAYMENT_PENDING)
                .paymentUrl(response.confirmation().confirmationUrl())
                .build();
    }

}
