package yookassa.api.dtos.yookassa.notifications;


import lombok.Builder;


@Builder
public record YookassaWebhookEventDto(

        String type,

        String event,

        ObjectDto object

) {
}
