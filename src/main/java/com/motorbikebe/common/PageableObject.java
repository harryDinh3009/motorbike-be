package com.motorbikebe.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
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
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal totalAmount; // Tổng tiền theo filter (optional)

    public PageableObject(Page<T> page) {
        this.data = page.getContent();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber();
        this.totalRecords = page.getTotalElements();
    }
}
