package yookassa.api.dtos.yookassa.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import yookassa.api.dtos.yookassa.AmountDto;

public record ObjectDto(

        String id,

        String status,

        AmountDto amount,

        @JsonProperty("income_amount")
        AmountDto incomeAmount,

        String description,

        boolean paid,

        boolean refundable,

        @JsonProperty("refunded_amount")
        AmountDto refundedAmount,

        boolean test,

        @JsonProperty("payment_method")
        PaymentMethodDto paymentMethod

) {
}
