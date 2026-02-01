package yookassa.domain.mappers.Impl;


import org.springframework.stereotype.Service;
import yookassa.api.dtos.client.PaymentResponseDto;
import yookassa.domain.entities.PaymentDetailEntity;
import yookassa.domain.entities.PaymentEntity;
import yookassa.domain.mappers.PaymentMapper;


@Service
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public PaymentResponseDto toPaymentDto(PaymentEntity paymentEntity) {
        PaymentDetailEntity detail = paymentEntity.getPaymentDetail();
        return PaymentResponseDto.builder()
                .amount(paymentEntity.getAmount())
                .currency(paymentEntity.getCurrency())
                .createdAt(paymentEntity.getCreatedAt())
                .description(paymentEntity.getDescription())
                .refundedAmount(detail != null ? detail.getRefundedAmount() : null)
                .paymentStatus(paymentEntity.getPaymentStatus())
                .paymentUrl(detail != null ? detail.getPaymentUrl() : null)
                .paymentMethod(detail != null ? detail.getPaymentMethod() : null)
                .paymentMethodDetail(detail != null ? detail.getPaymentMethodDetail() : null)
                .build();
    }

}
