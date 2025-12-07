package az.codeworld.springboot.utilities.converters;

import az.codeworld.springboot.utilities.constants.status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<status, String> {

    @Override
    public String convertToDatabaseColumn(status attribute) {

        if (attribute == null) 
            return null;

        return switch (attribute) {
            case status.CHECKED -> "Checked";
            case status.PENDING -> "Pending";
            case status.REJECTED -> "Rejected";
            default -> throw new IllegalArgumentException("Unknown value: " + attribute);
        };
    }

    @Override
    public status convertToEntityAttribute(String dbData) {
        
        if (dbData == null) 
            return null;

        return switch (dbData) {
            case "Checked" -> status.CHECKED;
            case "Pending" -> status.PENDING;
            case "Rejected" -> status.REJECTED;
            default -> throw new IllegalArgumentException("Unknown value: " + dbData);
        };
    }
    
}
