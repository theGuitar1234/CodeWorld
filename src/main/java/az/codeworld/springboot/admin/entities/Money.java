package az.codeworld.springboot.admin.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.utilities.constants.currency;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public final class Money {
    
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column
    @Enumerated(EnumType.STRING)
    private currency currency;

    public Money() {

    }

    public Money(BigDecimal amount, currency currency) {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);

        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public currency getCurrency() {
        return this.currency;
    }
}
