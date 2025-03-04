package com.example.its.web.issue;

import com.example.its.domain.issue.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 課題管理のコントローラークラス
 * - 課題の一覧表示、詳細表示
 * - 課題の作成、更新、削除
 */
@Controller
@RequestMapping("/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService; // 課題のビジネスロジックを処理するサービス

    /**
     * 課題一覧の表示（検索機能付き）
     * @param model ビューにデータを渡す
     * @return 課題一覧画面
     */
    @GetMapping
    public String showList(@Validated @ModelAttribute SearchForm form, BindingResult bindingResult, Model model) {
        // バリデーションエラーがある場合
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "検索キーワードが長すぎます");
            model.addAttribute("issueList", List.of()); // エラー時は空リストを設定
            return "issues/list";
        }

        // 検索処理を実行
        model.addAttribute("issueList", issueService.findIssues(form.getKeyword()));
        model.addAttribute("keyword", form.getKeyword());
        return "issues/list";
    }


    /**
     * 課題作成フォームの表示
     * @param form 課題フォーム（バインド用）
     * @return 課題作成画面
     */
    @GetMapping("/creationForm")
    public String showCreationForm(@ModelAttribute IssueForm form) {
        return "issues/creationForm";
    }

    /**
     * 課題の新規作成
     * @param form ユーザーが入力した課題情報
     * @param bindingResult バリデーション結果
     * @param model ビューにエラーメッセージを渡す
     * @return 作成成功時: 課題一覧 / 失敗時: 作成画面
     */
    @PostMapping
    public String createIssueWithCreator(@Validated IssueForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return showCreationForm(form); // バリデーションエラー時は作成画面に戻る
        }
        try {
            issueService.createIssueWithCreator(form.getSummary(), form.getDescription(), form.getCreatorName());
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage()); // エラーメッセージを表示
            return showCreationForm(form);
        }
        return "redirect:/issues"; // 作成後は一覧画面へリダイレクト
    }

    /**
     * 課題詳細の表示
     * @param issueId 課題ID
     * @param model ビューに課題情報を渡す
     * @return 課題詳細画面
     */
    @GetMapping("/{issueId}")
    public String showDetail(@PathVariable("issueId") long issueId, Model model) {
        IssueForm issueForm = issueService.findDetailById(issueId)
                .orElseThrow(); // 課題が存在しない場合はエラーをスロー
        model.addAttribute("issueForm", issueForm); // 課題情報をビューに渡す
        return "issues/detail";
    }

    /**
     * 課題の更新処理
     * @param issueId 更新対象の課題ID
     * @param form ユーザーが入力した課題情報
     * @param model エラーメッセージを渡す
     * @return 更新成功時: 課題詳細 / 失敗時: 再度詳細画面を表示
     */
    @PostMapping("/{issueId}/update")
    public String updateIssue(@PathVariable("issueId") long issueId, @Validated @ModelAttribute IssueForm form,
                              BindingResult bindingResult, Model model) {
        form.setId(issueId); // IDをセット（更新対象の課題を指定）

        // バリデーションエラーがある場合は詳細画面に戻る
        if (bindingResult.hasErrors()) {
            return showDetail(issueId, model);
        }

        if (!issueService.updateIssue(form)) {
            model.addAttribute("errorMessage", "同じ概要の課題が既に存在するか、内容が変わっていません");
            return showDetail(issueId, model); // 更新失敗時は詳細画面を再表示
        }
        return "redirect:/issues/" + issueId; // 更新成功時は詳細ページへリダイレクト
    }

    /**
     * 課題の削除処理（論理削除）
     * @param issueId 削除対象の課題ID
     * @return 削除後は一覧画面へリダイレクト
     */
    @PostMapping("/{issueId}/delete")
    public String deleteIssue(@PathVariable("issueId") long issueId, RedirectAttributes redirectAttributes) {
        if (!issueService.deleteIssue(issueId)) { // 削除に失敗した場合
            redirectAttributes.addFlashAttribute("errorMessage", "削除対象の課題が見つかりません");
        }
        return "redirect:/issues"; // 削除成功時でもエラー時でも一覧画面へリダイレクト
    }



}
