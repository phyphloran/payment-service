package yookassa.domain.services.impl;


import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yookassa.api.dtos.client.CreatePaymentResponseDto;
import yookassa.api.dtos.yookassa.notifications.YookassaWebhookEventDto;
import yookassa.api.exceptionHandler.IncorrectIpException;
import yookassa.domain.entities.PaymentEntity;
import yookassa.domain.factories.PaymentFactory;
import yookassa.domain.mappers.RequestMapper;
import yookassa.domain.repositories.PaymentRepository;
import yookassa.domain.services.IpValidator;
import yookassa.external.PaymentHttpClient;
import yookassa.api.dtos.yookassa.responses.YooKassaCreatePaymentResponseDto;
import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.yookassa.requests.YooKassaCreatePaymentRequestDto;
import yookassa.domain.services.PaymentService;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final IpValidator ipValidator;

    private final RequestMapper requestMapper;

    private final PaymentHttpClient paymentHttpClient;

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public CreatePaymentResponseDto createPayment(CreatePaymentRequestDto createPaymentRequest) {
        PaymentEntity existing = findByIdempotenceKey(createPaymentRequest);
        if (existing != null) {
            return new CreatePaymentResponseDto(existing.getPaymentUrl());
        }
        YooKassaCreatePaymentRequestDto requestToYooKassa = requestMapper
                .toYooKassaCreatePaymentRequestDto(createPaymentRequest);
        String idempotenceKey = String.valueOf(createPaymentRequest.idempotenceKey());
        YooKassaCreatePaymentResponseDto response = paymentHttpClient.createPayment(idempotenceKey, requestToYooKassa);
        PaymentEntity paymentEntity = PaymentFactory.buildPaymentEntity(createPaymentRequest, response);
        return new CreatePaymentResponseDto(paymentRepository.save(paymentEntity).getPaymentUrl());
    }

    @Override
    @Transactional
    public void processPayment(String ip, YookassaWebhookEventDto yookassaWebhookEventDto) {
        if (!ipValidator.isValid(ip)) {
            log.error("An attempt to send a webhook not from an Yookassa IP address: {}", ip);
            throw new IncorrectIpException("Incorrect ip");
        }
        log.info("Recived new yookassaWebhookEvent: {}, from ip: {}", yookassaWebhookEventDto.toString(), ip);
    }

    private PaymentEntity findByIdempotenceKey(CreatePaymentRequestDto createPaymentRequest) {
        UUID idempotenceKey = createPaymentRequest.idempotenceKey();
        Optional<PaymentEntity> existing = paymentRepository.findByIdempotenceKey(idempotenceKey);
        if (existing.isPresent()) {
            PaymentEntity paymentEntity = existing.get();
            if (!paymentEntity.getUserId().equals(createPaymentRequest.userId())) {
                throw new RuntimeException("Idempotence key already used by another user");
            }
            return paymentEntity;
        }
        return null;
    }

}
