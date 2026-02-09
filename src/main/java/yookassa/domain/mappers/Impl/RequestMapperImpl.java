package yookassa.domain.mappers.Impl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yookassa.api.dtos.client.CreatePaymentRequestDto;
import yookassa.api.dtos.yookassa.AmountDto;
import yookassa.api.dtos.yookassa.requests.ConfirmationRequestDto;
import yookassa.api.dtos.yookassa.requests.YooKassaCreatePaymentRequestDto;
import yookassa.domain.mappers.RequestMapper;


@Service
public class RequestMapperImpl implements RequestMapper {

    @Value("${yookassa.type}")
    private String type;

    @Value("${yookassa.return-url}")
    private String returnUrl;

    @Override
    public YooKassaCreatePaymentRequestDto toYooKassaCreatePaymentRequestDto(CreatePaymentRequestDto createPaymentRequest) {
        return YooKassaCreatePaymentRequestDto.builder()
                .amount(createAmountDto(createPaymentRequest))
                .description(createPaymentRequest.description())
                .capture(true)
                .confirmation(createConfirmationDto())
                .build();
    }

    private AmountDto createAmountDto(CreatePaymentRequestDto createPaymentRequest) {
        return AmountDto.builder()
                .value(String.valueOf(createPaymentRequest.amount()))
                .currency(createPaymentRequest.currency())
                .build();
    }

    private ConfirmationRequestDto createConfirmationDto() {
        return ConfirmationRequestDto.builder()
                .type(type)
                .return_url(returnUrl)
                .build();
    }

}
