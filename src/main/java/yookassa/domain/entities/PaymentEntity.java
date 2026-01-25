package yookassa.domain.entities;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


@Entity
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_idempotence_key", columnList = "idempotence_key"),
        @Index(name = "idx_payments_yookassa_payment_id", columnList = "yookassa_payment_id"),
        @Index(name = "idx_payments_user_id", columnList = "user_id")
})
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "yookassa_payment_id", unique = true, length = 100, nullable = false)
    private String yookassaPaymentId;

    @Column(name = "idempotence_key", unique = true, length = 36, nullable = false)
    private UUID idempotenceKey;

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 5, nullable = false)
    private String currency;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "description", length = 100, nullable = false)
    private String description;

    @Column(name = "refunded_amount", precision = 19, scale = 2, nullable = true)
    private BigDecimal refundedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 25, nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_url", length = 1000, nullable = false)
    private String paymentUrl;

    @PrePersist
    public void onCreate() {
        setCreatedAt(Instant.now());
    }

}
