package yookassa.api.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.client.CreatePaymentResponseDto;
import yookassa.api.dtos.yookassa.notifications.YookassaWebhookEventDto;
import yookassa.domain.services.PaymentService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<CreatePaymentResponseDto> getPaymentLink(
            @Valid @RequestBody CreatePaymentRequestDto createPaymentRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPayment(createPaymentRequest));
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            @RequestHeader(name = "x-real-ip") String ip,
            @RequestBody YookassaWebhookEventDto yookassaWebhookEventDto
    ) {
        paymentService.processPayment(ip, yookassaWebhookEventDto);
        return ResponseEntity.ok().build();
    }

}
