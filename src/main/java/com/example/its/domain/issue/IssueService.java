package com.example.its.domain.issue;

import com.example.its.web.issue.IssueForm;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

/**
 * 課題のビジネスロジックを担当するサービスクラス
 * - 課題の検索、登録、更新、削除を管理
 */
@Service
@RequiredArgsConstructor
public class IssueService {

    private static final Logger logger = LoggerFactory.getLogger(IssueService.class); // ログメッセージを出力できる。logger～のやつ
    private final IssueRepository issueRepository; // 課題データを扱うリポジトリ

    /**
     * 課題の一覧を取得する（検索機能付き）
     *
     * @param keyword 検索キーワード（null または空文字の場合は全件取得）
     * @return 検索結果または全課題のリスト
     */
    public List<IssueEntity> findIssues(String keyword) {
        // バリデーションを追加（controllerの@Validatedと一緒の確認を入れてる）
        if (keyword != null && keyword.length() > 256) {
            throw new IllegalArgumentException("検索キーワードが長すぎます");
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            return issueRepository.findAllActiveIssues(); // 削除されていない全課題を取得
        }
        return issueRepository.searchIssues(keyword);
    }

// ---------------------------------------------------------------
    /**
     * 新しい課題を作成し、作成者情報を登録する
     *
     * @param summary     課題の概要
     * @param description 課題の詳細
     * @param creatorName 作成者名
     * @throws IllegalArgumentException 同じ概要の課題が既に存在する場合
     */
    @Transactional
    public void createIssueWithCreator(String summary, String description, String creatorName) {
        if (summary == null || summary.trim().isEmpty()) {
            throw new IllegalArgumentException("summary は必須です");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("description は必須です");
        }
        if (issueRepository.findBySummary(summary).isPresent()) {
            throw new IllegalArgumentException("同じ概要の課題が既に存在します");
        }
        IssueEntity issue = createIssue(summary, description);
        issueRepository.insertCreator(issue.getId(), creatorName);
    }

    /**
     * 課題を作成し、データベースに保存する
     *
     * @param summary     課題の概要
     * @param description 課題の詳細
     * @return 作成された課題のエンティティ
     */
    private IssueEntity createIssue(String summary, String description) {
        logger.info("★ createIssue 実行: summary={}, description={}", summary, description);
        IssueEntity issue = new IssueEntity(0, summary, description, null, null, false);
        issueRepository.insert(issue); // 課題をデータベースに登録
        logger.info("★ 課題登録完了: id={}", issue.getId());
        return issue;
    }
// ---------------------------------------------------------------

    /**
     * 指定された ID の課題詳細を取得する
     *
     * @param issueId 課題の ID
     * @return 課題の詳細情報（存在しない場合は empty）
     * @throws IllegalArgumentException issueId が null または負の数の場合
     */
    public Optional<IssueForm> findDetailById(Long issueId) {
        if (issueId == null || issueId < 0) {
            throw new IllegalArgumentException("issueId は正の数値である必要があります");
        }
        return issueRepository.findDetailById(issueId); // 課題詳細を取得
    }

    /**
     * 課題を更新する（変更がない場合は更新しない）
     *
     * @param form 更新対象の課題情報
     * @return 更新成功時は true、変更がなかった場合は false
     */
    @Transactional
    public boolean updateIssue(IssueForm form) {
        // issueId のバリデーション
        if (form.getId() == null) {
            throw new IllegalArgumentException("issueId は NULL であってはなりません");
        }
        if (form.getId() < 0) {
            throw new IllegalArgumentException("issueId は正の数値である必要があります");
        }
        if (form.getSummary() == null || form.getSummary().trim().isEmpty()) {
            throw new IllegalArgumentException("summary は必須です");
        }
        if (form.getDescription() == null || form.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("description は必須です");
        }

        // 🔹 同じ `summary` の課題が既に存在するかチェック
        Optional<IssueEntity> duplicateSummary = issueRepository.findBySummary(form.getSummary());
        if (duplicateSummary.isPresent() && duplicateSummary.get().getId() != form.getId()) {
            throw new IllegalArgumentException("同じ概要の課題が既に存在します");
        }

        // 🔹 指定された issueId の課題が存在するかチェック
        Optional<IssueEntity> existingIssue = issueRepository.findById(form.getId());
        if (existingIssue.isEmpty()) {
            throw new RuntimeException("指定された課題が存在しません");
        }
        if (existingIssue.get().is_deleted()) {
            throw new RuntimeException("指定された課題は削除されています");
        }

        // 🔹 課題の更新処理
        int updatedRows = issueRepository.updateIssue(form.getId(), form.getSummary(), form.getDescription());
        int updatedCreatorRows = issueRepository.updateCreator(form.getId(), form.getCreatorName());

        return updatedRows > 0 || updatedCreatorRows > 0;
    }




    /**
     * 課題を論理削除する（is_deleted フラグを true に設定）
     *
     * @param issueId 削除対象の課題 ID
     * @return 削除成功時は true、対象が存在しなかった場合は false
     */
    @Transactional
    public boolean deleteIssue(Long issueId) {
        // issueId が NULL の場合はエラーをスロー
        if (issueId == null) {
            throw new IllegalArgumentException("issueId は NULL であってはなりません");
        }
        if (issueId < 0) {
            throw new IllegalArgumentException("issueId は正の数値である必要があります");
        }

        return issueRepository.deleteIssue(issueId) > 0;
    }


}
