package yookassa.domain.mappers;


import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.yookassa.requests.YooKassaCreatePaymentRequestDto;


public interface RequestMapper {

    YooKassaCreatePaymentRequestDto toYooKassaCreatePaymentRequestDto(CreatePaymentRequestDto createPaymentRequest);

}
