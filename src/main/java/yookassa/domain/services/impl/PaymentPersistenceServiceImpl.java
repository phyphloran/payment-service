package yookassa.domain.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yookassa.api.dtos.client.CreatePaymentResponseDto;
import yookassa.domain.entities.PaymentEntity;
import yookassa.domain.repositories.PaymentRepository;
import yookassa.domain.services.PaymentPersistenceService;


@Service
@RequiredArgsConstructor
public class PaymentPersistenceServiceImpl implements PaymentPersistenceService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public CreatePaymentResponseDto save(PaymentEntity entity) {
        return new CreatePaymentResponseDto(
                paymentRepository.save(entity).getPaymentDetail().getPaymentUrl()
        );
    }

}
