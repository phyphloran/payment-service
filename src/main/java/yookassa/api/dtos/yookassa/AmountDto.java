package yookassa.api.dtos.yookassa;


import lombok.Builder;


@Builder
public record AmountDto(

        String value,

        String currency

) {
}
