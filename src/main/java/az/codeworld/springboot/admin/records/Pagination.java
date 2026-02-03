package az.codeworld.springboot.admin.records;

public record Pagination(int pageIndex, int perPage, String sortBy, String payableSortBy) {}