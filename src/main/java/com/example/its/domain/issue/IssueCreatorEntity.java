package com.example.its.domain.issue;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class IssueCreatorEntity {

    private String issues_creator_id;
    private String issue_id;
    private String creator_name;
    private String touroku_date;
}
