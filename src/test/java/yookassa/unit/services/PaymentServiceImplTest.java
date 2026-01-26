package yookassa.unit.services;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.yookassa.notifications.YookassaWebhookEventDto;
import yookassa.api.exceptionHandler.IdempotenceKeyConflictException;
import yookassa.api.exceptionHandler.InvalidWebhookException;
import yookassa.api.exceptionHandler.PaymentAlreadyExists;
import yookassa.domain.entities.PaymentEntity;
import yookassa.domain.mappers.RequestMapper;
import yookassa.domain.repositories.PaymentRepository;
import yookassa.domain.services.IpValidator;
import yookassa.domain.services.PaymentPersistenceService;
import yookassa.domain.services.impl.PaymentServiceImpl;
import yookassa.external.PaymentHttpClient;
import yookassa.unit.data.PaymentServiceImplTestData;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private IpValidator ipValidator;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private PaymentHttpClient paymentHttpClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentPersistenceService paymentPersistenceService;

    @InjectMocks
    private PaymentServiceImpl paymentService;


    @Test
    void createPayment_shouldThrowException_whenPaymentAlreadySucceeded() {
        PaymentEntity existing = PaymentServiceImplTestData.existingSucceededPayment();
        CreatePaymentRequestDto request =
                PaymentServiceImplTestData.createPaymentRequestDto(existing.getIdempotenceKey());
        when(paymentRepository.findByIdempotenceKey(existing.getIdempotenceKey()))
                .thenReturn(Optional.of(existing));
        PaymentAlreadyExists exception = assertThrows(
                PaymentAlreadyExists.class,
                () -> paymentService.createPayment(request)
        );
        assertEquals("The payment already succeeded", exception.getMessage());
        verifyNoInteractions(paymentHttpClient);
        verifyNoInteractions(paymentPersistenceService);
    }

    @Test
    void createPayment_shouldThrowException_whenPaymentAlreadyCancelled() {
        PaymentEntity existing = PaymentServiceImplTestData.existingCancelledPayment();
        CreatePaymentRequestDto request =
                PaymentServiceImplTestData.createPaymentRequestDto(existing.getIdempotenceKey());
        when(paymentRepository.findByIdempotenceKey(existing.getIdempotenceKey()))
                .thenReturn(Optional.of(existing));
        PaymentAlreadyExists exception = assertThrows(
                PaymentAlreadyExists.class,
                () -> paymentService.createPayment(request)
        );
        assertEquals("The payment already cancelled", exception.getMessage());
        verifyNoInteractions(paymentHttpClient);
        verifyNoInteractions(paymentPersistenceService);
    }

    @Test
    void createPayment_shouldThrowIdempotenceKeyConflictException() {
        PaymentEntity existing = PaymentServiceImplTestData.existingPaymentWithUserId(12L);
        CreatePaymentRequestDto request = PaymentServiceImplTestData.createPaymentRequestDtoWithUserId(
                existing.getIdempotenceKey(), 2L);

        when(paymentRepository.findByIdempotenceKey(existing.getIdempotenceKey()))
                .thenReturn(Optional.of(existing));

        IdempotenceKeyConflictException exception = assertThrows(
                IdempotenceKeyConflictException.class,
                () -> paymentService.createPayment(request)
        );

        assertEquals("Idempotence key already used by another user", exception.getMessage());
        verifyNoInteractions(paymentHttpClient);
        verifyNoInteractions(paymentPersistenceService);
    }

    @Test
    void processPayment_shouldThrowInvalidWebhookTypeException() {
        PaymentEntity existing = PaymentServiceImplTestData.webhookSucceededPayment(23L, "2500.01");
        YookassaWebhookEventDto webhookEvent =
                PaymentServiceImplTestData.webhookEvent(
                        "test",
                        "payment.succeeded",
                        "woiefjhUHUI-8789GHI-kU",
                        "succeeded",
                        "2500.01"
                );
        when(paymentRepository.findByYookassaPaymentId(webhookEvent.object().id()))
                .thenReturn(Optional.of(existing));
        when(ipValidator.isValid(any(String.class)))
                .thenReturn(true);
        InvalidWebhookException exception = assertThrows(
                InvalidWebhookException.class,
                () -> paymentService.processPayment("mock", webhookEvent)
        );
        assertEquals("Unsupported type of notification", exception.getMessage());
        verify(paymentRepository, never()).save(any());
        verify(ipValidator, times(1)).isValid(any());
    }

    @Test
    void processPayment_shouldThrowInvalidWebhookException() {
        PaymentEntity existing = PaymentServiceImplTestData.webhookSucceededPayment(23L, "2500.01");
        YookassaWebhookEventDto webhookEvent =
                PaymentServiceImplTestData.succeededWebhook("woiefjhUHUI-8789GHI-kU", "2500.01");
        when(paymentRepository.findByYookassaPaymentId(webhookEvent.object().id()))
                .thenReturn(Optional.of(existing));
        when(ipValidator.isValid(any(String.class)))
                .thenReturn(true);
        InvalidWebhookException exception = assertThrows(
                InvalidWebhookException.class,
                () -> paymentService.processPayment("mock", webhookEvent)
        );
        assertEquals("Incorrect status of payment with id: " + existing.getId(), exception.getMessage());
        verify(paymentRepository, never()).save(any());
        verify(ipValidator, times(1)).isValid(any());
    }

    // compareTo logic tests
    @Test
    void processPayment_shouldThrowException_whenAmountDoesNotMatch() {
        PaymentEntity existing = PaymentServiceImplTestData.webhookPendingPayment(23L, "2500.55");
        YookassaWebhookEventDto webhookEvent =
                PaymentServiceImplTestData.webhookEvent(
                        "notification",
                        "payment.succeeded",
                        "woiefjhUHUI-8789GHI-kU",
                        "succeeded",
                        "3425345.1"
                );

        when(paymentRepository.findByYookassaPaymentId(webhookEvent.object().id()))
                .thenReturn(Optional.of(existing));
        when(ipValidator.isValid(any(String.class)))
                .thenReturn(true);

        InvalidWebhookException exception = assertThrows(
                InvalidWebhookException.class,
                () -> paymentService.processPayment("mock", webhookEvent)
        );
        assertEquals("Incorrect status of payment with id: " + existing.getId(), exception.getMessage());
        verify(paymentRepository, never()).save(any());
        verify(ipValidator, times(1)).isValid(any());
    }

    @Test
    void processPayment_shouldSucceed_whenPaymentIsPendingAndAmountsMatch() {
        PaymentEntity existing = PaymentServiceImplTestData.webhookPendingPayment(23L, "1212.2000");
        YookassaWebhookEventDto webhookEvent =
                PaymentServiceImplTestData.buildWebhookEvent("1212.2");

        when(paymentRepository.findByYookassaPaymentId(webhookEvent.object().id()))
                .thenReturn(Optional.of(existing));
        when(ipValidator.isValid(any(String.class)))
                .thenReturn(true);

        assertDoesNotThrow(() -> {
            paymentService.processPayment("mock", webhookEvent);
        });

        verify(paymentRepository, times(1)).save(any());
        verify(ipValidator, times(1)).isValid(any());
    }
}