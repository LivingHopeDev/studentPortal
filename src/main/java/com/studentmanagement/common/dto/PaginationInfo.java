package com.studentmanagement.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationInfo {

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

}
