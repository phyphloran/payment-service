package yookassa.api.dtos.yookassa.responses;


import com.fasterxml.jackson.annotation.JsonProperty;
import yookassa.api.dtos.yookassa.AmountDto;

import java.time.Instant;


public record YooKassaCreatePaymentResponseDto(

        String id,

        String status,

        AmountDto amount,

        String description,

        @JsonProperty("created_at")
        Instant createdAt,

        ConfirmationResponseDto confirmation,

        boolean test,

        boolean paid,

        boolean refundable
) {
}
