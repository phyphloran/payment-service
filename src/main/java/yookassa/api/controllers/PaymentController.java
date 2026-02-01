package yookassa.api.controllers;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import yookassa.api.dtos.client.PaymentsPageDto;
import yookassa.domain.services.PaymentService;
import org.springframework.web.bind.annotation.*;
import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.client.CreatePaymentResponseDto;
import yookassa.api.dtos.yookassa.notifications.YookassaWebhookEventDto;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<PaymentsPageDto> getPaymentsPage(
            @RequestParam @Min(value = 0, message = "Incorrect value of userId") Long userId,
            @RequestParam @Min(value = 0, message = "Incorrect value of page") int page,
            @RequestParam @Min(value = 1, message = "Incorrect value of size")
            @Max(value = 25, message = "The size cannot exceed 25") int size
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.getPaymentsByUserId(userId, page, size));
    }

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
