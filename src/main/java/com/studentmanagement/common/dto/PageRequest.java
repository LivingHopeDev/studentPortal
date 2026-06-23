package com.studentmanagement.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageRequest {

    private int page;
    private int size;
    private String sortBy;
    private String sortDir;

}
