package az.codeworld.springboot.utilities.converters;

import az.codeworld.springboot.utilities.constants.transactionstatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TransactionStatusConverter implements AttributeConverter<transactionstatus, String> {

    @Override
    public String convertToDatabaseColumn(transactionstatus attribute) {

        if (attribute == null) 
            return null;

        return switch (attribute) {
            case transactionstatus.CHECKED -> "Checked";
            case transactionstatus.PENDING -> "Pending";
            case transactionstatus.REJECTED -> "Rejected";
            default -> throw new IllegalArgumentException("Unknown value: " + attribute);
        };
    }

    @Override
    public transactionstatus convertToEntityAttribute(String dbData) {
        
        if (dbData == null) 
            return null;

        return switch (dbData) {
            case "Checked" -> transactionstatus.CHECKED;
            case "Pending" -> transactionstatus.PENDING;
            case "Rejected" -> transactionstatus.REJECTED;
            default -> throw new IllegalArgumentException("Unknown value: " + dbData);
        };
    }
    
}
