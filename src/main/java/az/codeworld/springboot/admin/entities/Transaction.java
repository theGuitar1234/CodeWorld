package az.codeworld.springboot.admin.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import az.codeworld.springboot.utilities.constants.currency;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.utilities.constants.transactionstatus;
import az.codeworld.springboot.utilities.converters.StatusConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name="TRANSACTIONS")
public class Transaction {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_generator")
    @SequenceGenerator(name = "transaction_id_generator", sequenceName = "transaction_id_sequence", allocationSize = 50, initialValue = 787648363)
    // @TableGenerator(
    private Long transactionId;

    @Column
    private LocalDate transactionDate;

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

    @Column(nullable = false)
    @Convert(converter = StatusConverter.class)
    private transactionstatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private currency currency;

    @Column
    @Enumerated(EnumType.STRING)
    private roles role;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "Transaction [transactionId=" + transactionId + ", transactionDate=" + transactionDate
                + ", transactionDescription=" + transactionDescription + ", transactionAmount=" + transactionAmount 
                + ", status=" + status + ", currency=" + currency + "]";
    }
}
