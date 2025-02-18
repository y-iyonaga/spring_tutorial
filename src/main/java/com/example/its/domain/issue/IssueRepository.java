package com.example.its.domain.issue;

import com.example.its.web.issue.IssueForm;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * 課題のデータアクセスを行うリポジトリインターフェース
 * - 課題の登録、取得、検索、更新、削除を管理
 * - MyBatis のマッパーとして機能
 */
@Mapper
public interface IssueRepository {

    /**
     * 課題を概要（summary）で検索する
     *
     * @param summary 課題の概要
     * @return 検索結果（該当する課題があれば Optional に格納）
     */
    @Select("SELECT * FROM issues WHERE summary = #{summary} LIMIT 1")
    Optional<IssueEntity> findBySummary(@Param("summary") String summary);

    /**
     * 新しい課題を登録する
     *
     * @param issue 登録する課題エンティティ
     */
    @Insert("INSERT INTO issues (summary, description, is_deleted) VALUES (#{summary}, #{description}, false)")
    @Options(useGeneratedKeys = true, keyProperty = "id") // 自動生成されたIDを取得
    void insert(IssueEntity issue);

    /**
     * 課題作成者情報を登録する
     *
     * @param issueId 課題ID
     * @param creatorName 作成者名
     */
    @Insert("INSERT INTO issues_creator (issues_ID, creatorName) VALUES (#{issueId}, #{creatorName})")
    void insertCreator(@Param("issueId") long issueId, @Param("creatorName") String creatorName);

    /**
     * 課題を ID で検索する
     *
     * @param issueId 課題ID
     * @return 検索結果（該当する課題があれば Optional に格納）
     */
    @Select("SELECT * FROM issues WHERE id = #{issueId}")
    Optional<IssueEntity> findById(@Param("issueId") long issueId);

    /**
     * 削除されていない課題を全件取得する
     *
     * @return 削除されていない課題のリスト
     */
    @Select("SELECT * FROM issues WHERE is_deleted = false") // is_deleted を boolean 比較に変更
    List<IssueEntity> findAllActiveIssues();

    /**
     * 課題のあいまい検索を実行する（概要・詳細）
     *
     * @param keyword 検索キーワード
     * @return 検索結果のリスト
     */
    @Select("SELECT * FROM issues WHERE is_deleted = false AND (summary LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))")
    List<IssueEntity> searchIssues(@Param("keyword") String keyword);

    /**
     * 課題詳細を取得する（JOIN を使用し作成者情報も取得）
     *
     * @param issueId 課題ID
     * @return 課題の詳細情報（作成者情報を含む）
     */
    @Select("""
        SELECT i.*, ic.creatorName 
        FROM issues i 
        LEFT JOIN issues_creator ic ON i.id = ic.issues_ID 
        WHERE i.id = #{issueId}
    """)
    Optional<IssueForm> findDetailById(@Param("issueId") long issueId);

// ----------------------------------------------------------------------------------------------------
    /**
     * 課題を更新する（同じ概要の課題が存在しないかチェック）
     * - 変更がある場合のみ更新
     *
     * @param id 課題ID
     * @param summary 更新する概要
     * @param description 更新する詳細
     * @return 更新された行数（0なら更新なし）
     */
    @Update("""
        UPDATE issues 
        SET summary = #{summary}, description = #{description}, updated_at = NOW() 
        WHERE id = #{id} 
        AND NOT EXISTS (
            SELECT 1 FROM issues WHERE summary = #{summary} AND id <> #{id} AND is_deleted = false
        )
    """)
    int updateIssue(@Param("id") long id,
                    @Param("summary") String summary,
                    @Param("description") String description);
    /**
     * 課題の登録者（creatorName）を更新する
     *
     * @param id 課題ID
     * @param creatorName 更新する登録者名
     * @return 更新された行数（0なら更新なし）
     */
    @Update("""
        UPDATE issues_creator 
        SET creatorName = #{creatorName} 
        WHERE issues_ID = #{id}
    """)
    int updateCreator(@Param("id") long id, @Param("creatorName") String creatorName);
// ----------------------------------------------------------------------------------------------------

    /**
     * 課題を論理削除する（is_deleted フラグを true に設定）
     *
     * @param issueId 削除対象の課題ID
     * @return 削除された行数（0なら削除なし）
     */
    @Update("UPDATE issues SET is_deleted = true WHERE id = #{issueId}")
    int deleteIssue(@Param("issueId") long issueId);
}
