package yookassa.domain.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yookassa.domain.entities.PaymentEntity;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByIdempotenceKey(UUID idempotenceKey);

    Optional<PaymentEntity> findByYookassaPaymentId(String paymentId);

    @EntityGraph(attributePaths = "paymentDetail")
    Page<PaymentEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

}
