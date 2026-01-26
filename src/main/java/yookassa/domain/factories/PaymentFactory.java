package yookassa.domain.factories;


import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.yookassa.responses.YooKassaCreatePaymentResponseDto;
import yookassa.domain.entities.PaymentDetailEntity;
import yookassa.domain.entities.PaymentEntity;
import yookassa.domain.entities.PaymentStatus;


public class PaymentFactory {

    public static PaymentEntity buildPaymentEntity(
            CreatePaymentRequestDto createPaymentRequest,
            YooKassaCreatePaymentResponseDto response
    ) {
        PaymentEntity payment = PaymentEntity.builder()
                .userId(createPaymentRequest.userId())
                .yookassaPaymentId(response.id())
                .idempotenceKey(createPaymentRequest.idempotenceKey())
                .amount(createPaymentRequest.amount())
                .currency(createPaymentRequest.currency())
                .description(createPaymentRequest.description())
                .paymentStatus(PaymentStatus.PAYMENT_PENDING)
                .build();

        PaymentDetailEntity detail = PaymentDetailEntity.builder()
                .paymentUrl(response.confirmation().confirmationUrl())
                .payment(payment)
                .build();

        payment.setPaymentDetail(detail);

        return payment;
    }



}
