package az.codeworld.springboot.admin.records;

import org.springframework.data.domain.Sort.Direction;

public record GenericLinkRecord(
    String isActive, 
    int perPage, 
    int pageIndex, 
    Direction direction
) {}
