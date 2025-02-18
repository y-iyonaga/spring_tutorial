package com.example.its.web.issue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueForm {

    private Long id; // 課題ID　自動採番

    @NotBlank
    @Size(max = 256)
    private String summary; // 課題概要

    @NotBlank
    @Size(max = 1000)
    private String description; // 課題詳細

    @NotBlank
    @Size(max = 256)
    private String creatorName; // 作成者名

    private String created_at; // 作成日時
    private String updated_at; // 更新日時
}
