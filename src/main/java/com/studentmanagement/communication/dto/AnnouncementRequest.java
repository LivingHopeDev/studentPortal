package com.studentmanagement.communication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String audience;

    private String priority;

}
