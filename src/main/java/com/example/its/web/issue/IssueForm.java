package com.example.its.web.issue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IssueForm {


    @NotBlank
    @Size(max = 256)
    private String summary; // 課題概要

    @NotBlank
    @Size(max = 1000)
    private String description; // 課題詳細

    @NotBlank
    @Size(max = 256)
    private String creatorName; // 登録/更新者名
}
