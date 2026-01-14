package yookassa.api.dtos.yookassa.notifications;

public record YookassaWebhookEventDto(

        String type,

        String event,

        ObjectDto object

) {
}
