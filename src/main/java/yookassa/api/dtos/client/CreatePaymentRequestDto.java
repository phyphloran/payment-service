package yookassa.api.dtos.client;


import jakarta.validation.constraints.*;
import java.math.BigDecimal;


public record CreatePaymentRequestDto(

        @NotNull(message = "UserId can not be null")
        @Min(value = 0, message = "The user's ID must be positive")
        Long userId,

        @NotNull(message = "Amount can not be null")
        @Digits(integer = 15, fraction = 2, message = "Incorrect format of amount")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be at least 0.01")
        BigDecimal amount,

        @NotBlank(message = "Currency can not be empty")
        @Size(min = 2, max = 10, message = "Incorrect size of currency")
        String currency,

        @NotBlank(message = "Description can not be empty")
        @Size(min = 2, max = 50, message = "Incorrect size of description")
        String description
) {}