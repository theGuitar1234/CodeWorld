package az.codeworld.springboot.admin.projections;

import java.math.BigDecimal;

public interface MonthlyTransactionProjection {
    Integer getYear();
    Integer getMonth();
    Long getTxCount();
    BigDecimal getAmount();
}