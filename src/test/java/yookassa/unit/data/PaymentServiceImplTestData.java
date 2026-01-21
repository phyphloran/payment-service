package yookassa.unit.data;


import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.domain.entities.PaymentEntity;
import yookassa.domain.entities.PaymentStatus;
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

}