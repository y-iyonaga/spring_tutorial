package com.example.its.web.issue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueForm {

    private Long id; // 課題ID（自動採番）

    @NotBlank(message = "概要は必須です")
    @Size(max = 256, message = "概要は最大256文字までです")
//    @Pattern(regexp = "^[a-zA-Z0-9ぁ-んァ-ヶ一-龥々ー\\s]+$", message = "概要には英数字または日本語のみ使用できます")
//    @Pattern(regexp = "^[^;'\\-]*$", message = "概要に不正な文字が含まれています")
    private String summary; // 課題概要

    @NotBlank(message = "詳細は必須です")
    @Size(max = 1000, message = "詳細は最大1000文字までです")
//    @Pattern(regexp = "^[^;'\\-]*$", message = "詳細に不正な文字が含まれています")
    private String description; // 課題詳細

    @NotBlank(message = "作成者名は必須です")
    @Size(max = 256, message = "作成者名は最大256文字までです")
//    @Pattern(regexp = "^[a-zA-Zぁ-んァ-ヶ一-龥々ー]+$", message = "名前には英字または日本語のみ使用できます")
//    @Pattern(regexp = "^[^;'\\-]*$", message = "名前に不正な文字が含まれています")
    private String creatorName; // 作成者名

    private String created_at; // 作成日時
    private String updated_at; // 更新日時

// MEMO 以下バリデーションはserviceに直書きした。 @Validatedを使用しcontrollerにだけ記載してしまうと別ルートからの処理があった場合すり抜けてしまう
//    @Size(max = 256, message = "検索キーワードは最大256文字までです")
//    private String keyword; // 検索キーワード

}
