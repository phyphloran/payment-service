package yookassa.domain.services.impl;


import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yookassa.api.dtos.client.CreatePaymentResponseDto;
import yookassa.api.dtos.yookassa.notifications.YookassaWebhookEventDto;
import yookassa.api.exceptionHandler.IdempotenceKeyConflictException;
import yookassa.api.exceptionHandler.IncorrectIpException;
import yookassa.api.exceptionHandler.InvalidWebhookException;
import yookassa.api.exceptionHandler.PaymentAlreadyExists;
import yookassa.domain.entities.PaymentEntity;
import yookassa.domain.entities.PaymentStatus;
import yookassa.domain.factories.PaymentFactory;
import yookassa.domain.mappers.RequestMapper;
import yookassa.domain.repositories.PaymentRepository;
import yookassa.domain.services.IpValidator;
import yookassa.domain.services.PaymentPersistenceService;
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

    private final PaymentPersistenceService paymentPersistenceService;

    @Override
    public CreatePaymentResponseDto createPayment(CreatePaymentRequestDto createPaymentRequest) {
        try {
            PaymentEntity existing = findByIdempotenceKey(createPaymentRequest);
            if (existing != null) {
                return handleExistingPayment(existing);
            }
            YooKassaCreatePaymentRequestDto requestToYooKassa = requestMapper
                    .toYooKassaCreatePaymentRequestDto(createPaymentRequest);
            String idempotenceKey = String.valueOf(createPaymentRequest.idempotenceKey());
            YooKassaCreatePaymentResponseDto response = paymentHttpClient.createPayment(idempotenceKey, requestToYooKassa);
            PaymentEntity paymentEntity = PaymentFactory.buildPaymentEntity(createPaymentRequest, response);
            return paymentPersistenceService.save(paymentEntity);
        } catch (DataIntegrityViolationException exception) {
            throw new PaymentAlreadyExists("The payment already exist");
        }
    }

    @Override
    @Transactional
    public void processPayment(String ip, YookassaWebhookEventDto yookassaWebhookEventDto) {
        if (!ipValidator.isValid(ip)) {
            log.error("An attempt to send a webhook not from an Yookassa IP address: {}", ip);
            throw new IncorrectIpException("Incorrect ip");
        }
        log.info("Recived new yookassaWebhookEvent: {}, from ip: {}", yookassaWebhookEventDto.toString(), ip);
        PaymentEntity paymentEntity = findByPaymentId(yookassaWebhookEventDto.object().id());
        changeStatus(paymentEntity, yookassaWebhookEventDto);
    }

    private PaymentEntity findByIdempotenceKey(CreatePaymentRequestDto createPaymentRequest) {
        UUID idempotenceKey = createPaymentRequest.idempotenceKey();
        Optional<PaymentEntity> existing = paymentRepository.findByIdempotenceKey(idempotenceKey);
        if (existing.isPresent()) {
            PaymentEntity paymentEntity = existing.get();
            if (!paymentEntity.getUserId().equals(createPaymentRequest.userId())) {
                throw new IdempotenceKeyConflictException("Idempotence key already used by another user");
            }
            return paymentEntity;
        }
        return null;
    }

    private CreatePaymentResponseDto handleExistingPayment(PaymentEntity existing) {
        return switch (existing.getPaymentStatus()) {
            case PAYMENT_PENDING -> new CreatePaymentResponseDto(existing.getPaymentUrl());
            case PAYMENT_SUCCEEDED -> throw new PaymentAlreadyExists("The payment already succeeded");
            case PAYMENT_CANCELLED -> throw new PaymentAlreadyExists("The payment already cancelled");
            default -> throw new IllegalStateException(
                    "Unsupported payment status: " + existing.getPaymentStatus()
            );
        };
    }

    private PaymentEntity findByPaymentId(String paymentId) {
        return paymentRepository.findByYookassaPaymentId(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment with paymentId: " + paymentId + " not found"));
    }

    private void changeStatus(PaymentEntity existing, YookassaWebhookEventDto yookassaWebhookEventDto) {
        handlePaymentNotification(yookassaWebhookEventDto);
        String yookassaEvent = yookassaWebhookEventDto.event();
        switch (yookassaEvent) {
            case "payment.succeeded":
                handlePaymentSucceeded(existing, yookassaWebhookEventDto);
                break;
            case "payment.canceled":
                handlePaymentCanceled(existing, yookassaWebhookEventDto);
                break;
            default: throw new InvalidWebhookException();
        }
    }

    private void handlePaymentNotification(YookassaWebhookEventDto yookassaWebhookEventDto) {
        if (!"notification".equals(yookassaWebhookEventDto.type())) {
            log.error("Event type not notification {}", yookassaWebhookEventDto);
            throw new InvalidWebhookException("Unsupported type of notification");
        }
    }

    private void handlePaymentCanceled(PaymentEntity existing, YookassaWebhookEventDto yookassaWebhookEventDto) {
        if (
                PaymentStatus.PAYMENT_PENDING.equals(existing.getPaymentStatus()) &&
                "canceled".equals(yookassaWebhookEventDto.object().status())
        ) {
            existing.setPaymentStatus(PaymentStatus.PAYMENT_CANCELLED);
            paymentRepository.save(existing);
        } else {
            log.error("changeStatus exception. Payment: {}", existing);
            throw new InvalidWebhookException("Incorrect status of payment with id: " + existing.getId());
        }
    }

    private void handlePaymentSucceeded(PaymentEntity existing, YookassaWebhookEventDto yookassaWebhookEventDto) {
        BigDecimal webhookAmount = new BigDecimal(yookassaWebhookEventDto.object().amount().value());
        if (
                webhookAmount.equals(existing.getAmount()) &&
                PaymentStatus.PAYMENT_PENDING.equals(existing.getPaymentStatus()) &&
                "succeeded".equals(yookassaWebhookEventDto.object().status())
        ) {
            existing.setPaymentStatus(PaymentStatus.PAYMENT_SUCCEEDED);
            paymentRepository.save(existing);
        } else {
            log.error("changeStatus exception. Payment: {}", existing);
            throw new InvalidWebhookException("Incorrect status of payment with id: " + existing.getId());
        }
    }

}
