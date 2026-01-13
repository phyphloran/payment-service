package yookassa.domain.services;


import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.client.CreatePaymentResponseDto;


public interface PaymentService {

    CreatePaymentResponseDto createPayment(CreatePaymentRequestDto createPaymentRequest);

}
