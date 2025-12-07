package az.codeworld.springboot.web.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.utilities.constants.currency;
import az.codeworld.springboot.utilities.constants.status;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private status status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private currency currency;

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
