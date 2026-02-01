package yookassa.api.dtos.client;

import java.util.List;

public record PaymentsPageDto(

        List<PaymentResponseDto> payments,

        PageDto pageDto

) {}
