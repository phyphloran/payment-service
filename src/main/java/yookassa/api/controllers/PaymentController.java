package yookassa.api.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.client.CreatePaymentResponseDto;
import yookassa.domain.services.PaymentService;
import java.util.Map;


@Slf4j
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

    //TODO webhook
    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            @RequestHeader("Authorization") String auth,
            @RequestBody String temp
    ) {
        log.info("recived notification from yookassa: {}", temp);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

}
