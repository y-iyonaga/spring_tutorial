package com.example.its.domain.issue;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class) // MockitoをJUnitに統合
class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository; // IssueRepository のモックを作成

    @InjectMocks
    private IssueService issueService; // IssueService にモックを注入

    @BeforeEach
    void setUp() {
        // 毎回モックの状態をリセット
        reset(issueRepository);
    }

    // ------------------------
    // 課題削除
    // ------------------------

    @Test
    @DisplayName("✅ 存在する課題を正常に削除できる")
    void testDeleteExistingIssue() {
        // 事前にモックの振る舞いを定義（1件削除されることを想定）
        when(issueRepository.deleteIssue(9223372036854770000L)).thenReturn(1);

        // サービスメソッドを実行
        boolean result = issueService.deleteIssue(9223372036854770000L);

        // 結果を検証
        assertThat(result).isTrue();
        verify(issueRepository, times(1)).deleteIssue(9223372036854770000L);
    }

    @Test
    @DisplayName("❌ 存在しない issueId の削除（削除対象なし）")
    void testDeleteNonExistingIssue() {
        // 事前にモックの振る舞いを定義（削除されるレコードがない）
        when(issueRepository.deleteIssue(9223372036854770001L)).thenReturn(0);

        // サービスメソッドを実行
        boolean result = issueService.deleteIssue(9223372036854770001L);

        // 結果を検証
        assertThat(result).isFalse();
        verify(issueRepository, times(1)).deleteIssue(9223372036854770001L);
    }

    @Test
    @DisplayName("❌ issueId が NULL の場合")
    void testDeleteIssueWithNullId() {
        // issueId が NULL の場合は IllegalArgumentException がスローされることを検証
        assertThatThrownBy(() -> issueService.deleteIssue(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("issueId は NULL であってはなりません");

        // リポジトリの deleteIssue が一切呼ばれていないことを確認
        verify(issueRepository, never()).deleteIssue(anyLong());
    }

    @Test
    @DisplayName("❌ issueId が負の値の場合")
    void testDeleteIssueWithNegativeId() {
        // 負の数の issueId で IllegalArgumentException を期待
        assertThatThrownBy(() -> issueService.deleteIssue(-9223372036854770000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("issueId は正の数値である必要があります");

        verify(issueRepository, never()).deleteIssue(anyLong());
    }

    @Test
    @DisplayName("❌ issueId が文字列の場合")
    void testDeleteIssueWithStringId() {
        // issueId は long 型のため、そもそもコンパイルエラーになる（手動テスト向けの確認）
        // 実装側で適切なバリデーションをかける必要がある
    }

    @Test
    @DisplayName("❌ issueId が特殊文字の場合")
    void testDeleteIssueWithSpecialCharacters() {
        // issueId は long 型のため、そもそもコンパイルエラーになる（手動テスト向けの確認）
        // 実装側で適切なバリデーションをかける必要がある
    }

    @Test
    @DisplayName("❌ 既に論理削除済みの issueId を削除")
    void testDeleteAlreadyDeletedIssue() {
        // 事前にモックの振る舞いを定義（既に削除済みのため0件更新）
        when(issueRepository.deleteIssue(9223372036854770000L)).thenReturn(0);

        // サービスメソッドを実行
        boolean result = issueService.deleteIssue(9223372036854770000L);

        // 結果を検証
        assertThat(result).isFalse();
        verify(issueRepository, times(1)).deleteIssue(9223372036854770000L);
    }

    @Test
    @DisplayName("❌ 削除直前に別ユーザーが削除した場合")
    void testDeleteIssueDeletedByAnotherUser() {
        // 事前にモックの振る舞いを定義（削除直前に別ユーザーが削除済み）
        when(issueRepository.deleteIssue(9223372036854770000L)).thenReturn(0);

        // サービスメソッドを実行
        boolean result = issueService.deleteIssue(9223372036854770000L);

        // 結果を検証
        assertThat(result).isFalse();
        verify(issueRepository, times(1)).deleteIssue(9223372036854770000L);
    }

    @Test
    @DisplayName("❌ DB接続エラーが発生した場合")
    void testDeleteIssueWithDatabaseError() {
        // DB 接続エラーをシミュレート
        when(issueRepository.deleteIssue(9223372036854770000L)).thenThrow(new RuntimeException("DB接続エラー"));

        // 例外がスローされることを検証
        assertThatThrownBy(() -> issueService.deleteIssue(9223372036854770000L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB接続エラー");

        // deleteIssue が 1 回呼ばれたことを確認
        verify(issueRepository, times(1)).deleteIssue(9223372036854770000L);
    }


}
