package yookassa.api.dtos.client;


import jakarta.validation.constraints.*;
import java.math.BigDecimal;


public record CreatePaymentRequestDto(

        @Min(value = 0, message = "The user's ID must be positive")
        @NotNull(message = "UserId can not be null")
        Long userId,

        @NotNull(message = "Amount can not be null")
        @Digits(integer = 10, fraction = 2, message = "Incorrect format of amount")
        BigDecimal amount,

        @NotBlank(message = "Currency can not be empty")
        @Size(min = 2, max = 10, message = "Incorrect size of currency")
        String currency,

        @NotBlank(message = "Description can not be empty")
        @Size(min = 2, max = 50, message = "Incorrect size of description")
        String description
) {}