package az.codeworld.springboot.admin.dtos.transactions;

import java.util.List;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.web.entities.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class UserTransactionDTO {
    private List<Transaction> transactions;
}
