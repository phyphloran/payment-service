package yookassa.external;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import yookassa.api.dtos.yookassa.responses.YooKassaCreatePaymentResponseDto;
import yookassa.api.dtos.yookassa.requests.YooKassaCreatePaymentRequestDto;


@HttpExchange(
        accept = "application/json",
        contentType = "application/json",
        url = "/v3/payments"
)
public interface PaymentHttpClient {

    @PostExchange
    YooKassaCreatePaymentResponseDto createPayment(
            @RequestHeader("Idempotence-Key") String idempotenceKey,
            @RequestBody YooKassaCreatePaymentRequestDto request
    );

}