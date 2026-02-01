package yookassa.domain.mappers;


import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.client.PaymentResponseDto;
import yookassa.api.dtos.yookassa.requests.YooKassaCreatePaymentRequestDto;
import yookassa.domain.entities.PaymentEntity;


public interface RequestMapper {

    YooKassaCreatePaymentRequestDto toYooKassaCreatePaymentRequestDto(CreatePaymentRequestDto createPaymentRequest);

}
