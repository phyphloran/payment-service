package yookassa.api.dtos.client;


import java.math.BigDecimal;

public record CreatePaymentRequestDto(

        BigDecimal amount,

        String currency,

        String description
) {}