package com.example.its.domain.issue;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class IssueCreatorEntity {

    private String ID;
    private String issue_ID;
    private String creatorName;
    private String tourokuDate;
}
