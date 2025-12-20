package az.codeworld.springboot.admin.records;

import org.springframework.data.domain.Sort.Direction;

import az.codeworld.springboot.utilities.constants.roles;

public record LinkRecord(
    String isActive, 
    int perPage, 
    int pageIndex, 
    Direction direction, 
    roles role
) {}
