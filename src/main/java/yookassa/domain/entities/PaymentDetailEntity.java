package yookassa.domain.entities;


import lombok.*;
import jakarta.persistence.*;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_details")
public class PaymentDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_url", length = 1000, nullable = true)
    private String paymentUrl;

    @Column(name = "payment_method", length = 50, nullable = true)
    private String paymentMethod;

    @Column(name = "payment_method_detail", length = 100, nullable = true)
    private String paymentMethodDetail;

    @JoinColumn(name = "payment_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private PaymentEntity payment;

}
