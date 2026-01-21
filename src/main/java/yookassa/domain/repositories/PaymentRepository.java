package yookassa.domain.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yookassa.domain.entities.PaymentEntity;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByIdempotenceKey(UUID idempotenceKey);

    Optional<PaymentEntity> findByYookassaPaymentId(String paymentId);

}
