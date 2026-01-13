package yookassa.api.dtos.yookassa.requests;


import lombok.Builder;
import yookassa.api.dtos.yookassa.AmountDto;


@Builder
public record YooKassaCreatePaymentRequestDto(

        AmountDto amount,

        String description,

        boolean capture,

        ConfirmationRequestDto confirmation

) {
}
