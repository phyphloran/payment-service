package yookassa.unit.data;


import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.yookassa.AmountDto;
import yookassa.api.dtos.yookassa.notifications.ObjectDto;
import yookassa.api.dtos.yookassa.notifications.PaymentMethodDto;
import yookassa.api.dtos.yookassa.notifications.YookassaWebhookEventDto;
import yookassa.domain.entities.PaymentDetailEntity;
import yookassa.domain.entities.PaymentEntity;
import yookassa.domain.entities.PaymentStatus;
import java.math.BigDecimal;
import java.util.UUID;


public class PaymentServiceImplTestData {

    public static final UUID DEFAULT_IDEMPOTENCE_KEY = UUID.randomUUID();
    public static final Long DEFAULT_USER_ID = 245L;

    public static PaymentEntity existingSucceededPayment() {
        return PaymentEntity.builder()
                .id(1L)
                .userId(DEFAULT_USER_ID)
                .paymentStatus(PaymentStatus.PAYMENT_SUCCEEDED)
                .idempotenceKey(DEFAULT_IDEMPOTENCE_KEY)
                .build();
    }

    public static PaymentEntity existingCancelledPayment() {
        return PaymentEntity.builder()
                .id(1L)
                .userId(DEFAULT_USER_ID)
                .paymentStatus(PaymentStatus.PAYMENT_CANCELLED)
                .idempotenceKey(DEFAULT_IDEMPOTENCE_KEY)
                .build();
    }

    public static CreatePaymentRequestDto createPaymentRequestDto(UUID idempotenceKey) {
        return CreatePaymentRequestDto.builder()
                .idempotenceKey(idempotenceKey)
                .userId(DEFAULT_USER_ID)
                .build();
    }

    public static PaymentEntity existingPaymentWithUserId(Long userId) {
        return PaymentEntity.builder()
                .id(3L)
                .userId(userId)
                .paymentStatus(PaymentStatus.PAYMENT_SUCCEEDED)
                .idempotenceKey(DEFAULT_IDEMPOTENCE_KEY)
                .build();
    }

    public static CreatePaymentRequestDto createPaymentRequestDtoWithUserId(UUID idempotenceKey, Long userId) {
        return CreatePaymentRequestDto.builder()
                .idempotenceKey(idempotenceKey)
                .userId(userId)
                .build();
    }

    public static AmountDto webhookAmount(String value) {
        return AmountDto.builder()
                .value(value)
                .build();
    }

    public static ObjectDto webhookObject(String id, String status, String amount) {
        return ObjectDto.builder()
                .id(id)
                .status(status)
                .amount(webhookAmount(amount))
                .build();
    }

    public static YookassaWebhookEventDto webhookEvent(String type, String event, ObjectDto objectDto) {
        return YookassaWebhookEventDto.builder()
                .type(type)
                .event(event)
                .object(objectDto)
                .build();
    }

    public static PaymentEntity webhookSucceededPayment(Long id, String amount) {
        return PaymentEntity.builder()
                .id(id)
                .paymentStatus(PaymentStatus.PAYMENT_SUCCEEDED)
                .amount(new BigDecimal(amount))
                .build();
    }

    public static PaymentEntity webhookPendingPayment(Long id, String amount) {
        return PaymentEntity.builder()
                .id(id)
                .paymentStatus(PaymentStatus.PAYMENT_PENDING)
                .amount(new BigDecimal(amount))
                .build();
    }


    public static YookassaWebhookEventDto webhookEvent(String type, String event, String paymentId, String status, String amount) {
        return YookassaWebhookEventDto.builder()
                .type(type)
                .event(event)
                .object(webhookObject(paymentId, status, amount))
                .build();
    }

    public static YookassaWebhookEventDto succeededWebhook(String paymentId, String amount) {
        ObjectDto objectDto = webhookObject(paymentId, "succeeded", amount);
        return webhookEvent("notification", "payment.succeeded", objectDto);
    }

    public static YookassaWebhookEventDto buildWebhookEvent(String amount) {
        return YookassaWebhookEventDto.builder()
                .type("notification")
                .event("payment.succeeded")
                .object(webhookObjectBuild(
                        "woiefjhUHUI-8789GHI-kU",
                        "succeeded",
                        amount
                ))
                .build();
    }

    public static ObjectDto webhookObjectBuild(String id, String status, String amount) {
        return ObjectDto.builder()
                .id(id)
                .status(status)
                .amount(AmountDto.builder()
                        .value(amount)
                        .currency("RUB")
                        .build())
                .paymentMethod(PaymentMethodDto.builder()
                        .type("yoo_money")
                        .title("YooMoney wallet 410011758831136")
                        .build())
                .build();
    }

    public static PaymentEntity webhookPendingPaymentWithLink(Long id, String amount) {
        return PaymentEntity.builder()
                .id(id)
                .paymentStatus(PaymentStatus.PAYMENT_PENDING)
                .amount(new BigDecimal(amount))
                .paymentDetail(PaymentDetailEntity.builder()
                        .paymentUrl("link")
                        .build())
                .build();
    }

}