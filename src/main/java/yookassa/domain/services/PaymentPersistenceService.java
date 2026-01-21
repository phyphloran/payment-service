package yookassa.domain.services;


import yookassa.api.dtos.client.CreatePaymentResponseDto;
import yookassa.domain.entities.PaymentEntity;


public interface PaymentPersistenceService {

    CreatePaymentResponseDto save(PaymentEntity entity);

}
