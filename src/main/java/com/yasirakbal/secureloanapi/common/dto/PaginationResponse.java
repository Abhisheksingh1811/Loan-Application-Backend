package com.yasirakbal.secureloanapi.common.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public abstract class PaginationResponse<T> {
    private final List<T> content;
    private final Integer totalElements;
    private final Integer totalPages;
    private final Integer pageSize;
    private final Integer pageNumber;
    private final Boolean isFirst;
    private final Boolean isLast;

    public PaginationResponse(Page page, List<T> content) {
        this.content = content;
        this.totalElements = (int)page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.pageSize = page.getSize();
        this.pageNumber = page.getNumber();
        this.isFirst = page.isFirst();
        this.isLast = page.isLast();
    }
}
