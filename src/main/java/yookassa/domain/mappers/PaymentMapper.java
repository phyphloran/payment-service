package yookassa.domain.mappers;

import yookassa.api.dtos.client.PaymentResponseDto;
import yookassa.domain.entities.PaymentEntity;

public interface PaymentMapper {

    PaymentResponseDto toPaymentDto(PaymentEntity paymentEntity);

}
