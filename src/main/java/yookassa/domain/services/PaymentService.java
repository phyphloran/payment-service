package yookassa.domain.services;


import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.client.CreatePaymentResponseDto;
import yookassa.api.dtos.client.PaymentResponseDto;
import yookassa.api.dtos.client.PaymentsPageDto;
import yookassa.api.dtos.yookassa.notifications.YookassaWebhookEventDto;


public interface PaymentService {

    CreatePaymentResponseDto createPayment(CreatePaymentRequestDto createPaymentRequest);

    void processPayment(String ip, YookassaWebhookEventDto yookassaWebhookEventDto);

    PaymentsPageDto getPaymentsByUserId(Long id, int page, int size);

}
