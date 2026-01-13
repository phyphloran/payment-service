package yookassa.api.dtos.yookassa.responses;


import com.fasterxml.jackson.annotation.JsonProperty;


public record ConfirmationResponseDto(

        String type,

        @JsonProperty("confirmation_url")
        String confirmationUrl
) {
}
