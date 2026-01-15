package yookassa.domain.services;


import jakarta.servlet.http.HttpServletRequest;
import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.client.CreatePaymentResponseDto;
import yookassa.api.dtos.yookassa.notifications.YookassaWebhookEventDto;


public interface PaymentService {

    CreatePaymentResponseDto createPayment(CreatePaymentRequestDto createPaymentRequest);

    void processPayment(YookassaWebhookEventDto yookassaWebhookEventDto, HttpServletRequest httpServletRequest);

}
