package yookassa.api.controllers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.client.CreatePaymentResponseDto;
import yookassa.domain.services.PaymentService;
import java.util.Map;
import java.util.stream.Collectors;


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

    //test
    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(HttpServletRequest request) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining());

            log.info("ВЕБХУК ПОЛУЧЕН!");
            log.info("Метод: {}", request.getMethod());
            log.info("URL: {}", request.getRequestURL());
            log.info("Query: {}", request.getQueryString());
            log.info("Auth: {}", request.getHeader("Authorization"));
            log.info("Content-Type: {}", request.getHeader("Content-Type"));
            log.info("User-Agent: {}", request.getHeader("User-Agent"));
            log.info("X-Forwarded-For: {}", request.getHeader("X-Forwarded-For"));
            log.info("Тело ({} chars): {}", body.length(), body);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("ОШИБКА В ВЕБХУКЕ: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    //Ips whiteList

    /*185.71.76.0/27
    185.71.77.0/27
    77.75.153.0/25
    77.75.156.11
    77.75.156.35
    77.75.154.128/25
    2a02:5180::/32*/

}
