package com.example.its.domain.issue;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class IssueDetailDto {
    private long id;
    private String summary;
    private String description;
    private String creatorName;
    private String created_at;
    private String updated_at;
}
