package az.codeworld.springboot.utilities;

import java.time.LocalDate;
import java.time.YearMonth;

public final class DateUtil {
    
    public static LocalDate addOneMonthClamped(LocalDate date) {
        YearMonth next = YearMonth.from(date).plusMonths(1);
        int day = Math.min(date.getDayOfMonth(), next.lengthOfMonth());
        return LocalDate.of(next.getYear(), next.getMonth(), day);
    }
}
