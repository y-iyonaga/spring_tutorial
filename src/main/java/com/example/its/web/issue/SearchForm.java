package com.example.its.web.issue;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 課題検索用のフォーム
 */
@Getter
@Setter
public class SearchForm {

    @Size(max = 256, message = "検索キーワードは最大256文字までです")
    private String keyword; // 検索キーワード
}
