package yookassa.api.dtos.client;


import lombok.Builder;
import yookassa.domain.entities.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PaymentResponseDto(

        BigDecimal amount,

        String currency,

        Instant createdAt,

        String description,

        BigDecimal refundedAmount,

        PaymentStatus paymentStatus,

        String paymentUrl,

        String paymentMethod,

        String paymentMethodDetail

) {
}
