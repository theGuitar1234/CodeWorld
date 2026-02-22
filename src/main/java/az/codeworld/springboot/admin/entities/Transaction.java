package az.codeworld.springboot.admin.entities;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import az.codeworld.springboot.utilities.constants.currency;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.utilities.constants.transactionstatus;
import az.codeworld.springboot.utilities.converters.TransactionStatusConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name="TRANSACTIONS",
    indexes = {
        @Index(name = "idx_belongs_to", columnList = "belongs_to"),
        @Index(name = "idx_transaction_time", columnList = "transaction_time")
    }
)
public class Transaction {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_generator")
    @SequenceGenerator(name = "transaction_id_generator", sequenceName = "transaction_id_sequence", allocationSize = 50, initialValue = 787648363)
    // @TableGenerator(
    private Long transactionId;

    @Column
    private Instant transactionTime;

    @Column
    private String transactionPaidBy;

    // @Lob
    @Column(columnDefinition = "TEXT")
    private String transactionDescription;

    // @Lob
    @Column(columnDefinition = "TEXT")
    private String transactionDetails;

    @Column
    private BigDecimal transactionAmount;

    @Column
    private BigDecimal transactionFee;

    @Column
    private BigDecimal transactionTotal;

    @Column
    @Enumerated(EnumType.STRING)
    private currency currency;

    @Column(nullable = false)
    @Convert(converter = TransactionStatusConverter.class)
    private transactionstatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private roles belongsTo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "Transaction [transactionId=" + transactionId + ", transactionTime=" + transactionTime
                + ", transactionDescription=" + transactionDescription + ", transactionAmount=" + transactionAmount 
                + ", status=" + status + ", currency=" + currency + "]";
    }
}
