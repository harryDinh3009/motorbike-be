package com.motorbikebe.common;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageableObject<T> {

    private List<T> data;
    private long totalPages;
    private int currentPage;
    private long totalRecords;

    public PageableObject(Page<T> page) {
        this.data = page.getContent();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber();
        this.totalRecords = page.getTotalElements();
    }
}
