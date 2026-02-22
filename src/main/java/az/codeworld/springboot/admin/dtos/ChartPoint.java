package az.codeworld.springboot.admin.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChartPoint {
    private int year;
    private int month;
    private long transactionCount;
    private BigDecimal transactionAmount;
}
