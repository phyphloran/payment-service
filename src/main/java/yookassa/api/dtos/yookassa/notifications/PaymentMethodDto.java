package yookassa.api.dtos.yookassa.notifications;

import lombok.Builder;

@Builder
public record PaymentMethodDto(

        String type,

        String title

) {
}
