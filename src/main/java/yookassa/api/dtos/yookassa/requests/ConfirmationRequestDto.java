package yookassa.api.dtos.yookassa.requests;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;


@Builder
public record ConfirmationRequestDto(

        String type,

        @JsonProperty("return_url")
        String return_url

) {
}
