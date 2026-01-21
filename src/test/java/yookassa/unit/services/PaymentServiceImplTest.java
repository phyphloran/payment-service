package yookassa.unit.services;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.exceptionHandler.IdempotenceKeyConflictException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void processPayment() {
    }
}