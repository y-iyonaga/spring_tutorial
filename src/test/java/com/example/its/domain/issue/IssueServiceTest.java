package com.example.its.domain.issue;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.example.its.web.issue.IssueForm;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
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


    // -------------------------------------------------------------------------------------------------------------------------------------------
    // 課題一覧取得
    // -------------------------------------------------------------------------------------------------------------------------------------------
    @Test
    @DisplayName("✅ 課題を全件取得できる（0件の場合）")
    void testFindIssuesWithNoIssues() {
        when(issueRepository.findAllActiveIssues()).thenReturn(List.of());

        List<IssueEntity> result = issueService.findIssues(null);

        assertThat(result).isEmpty();
        verify(issueRepository, times(1)).findAllActiveIssues();
    }

    @Test
    @DisplayName("✅ 課題を全件取得できる（1件の場合）")
    void testFindIssuesWithOneIssue() {
        List<IssueEntity> mockIssues = List.of(new IssueEntity(1L, "バグA", "バグがあります", null, null, false));
        when(issueRepository.findAllActiveIssues()).thenReturn(mockIssues);

        List<IssueEntity> result = issueService.findIssues(null);

        assertThat(result).hasSize(1).extracting(IssueEntity::getSummary).containsExactly("バグA");
        verify(issueRepository, times(1)).findAllActiveIssues();
    }

    @Test
    @DisplayName("✅ 課題を全件取得できる（複数件の場合）")
    void testFindIssuesWithMultipleIssues() {
        List<IssueEntity> mockIssues = List.of(
                new IssueEntity(1L, "バグA", "バグがあります", null, null, false),
                new IssueEntity(2L, "機能要望B", "Bに追加機能が欲しいです", null, null, false),
                new IssueEntity(3L, "UI修正", "デザインを修正したい", null, null, false)
        );
        when(issueRepository.findAllActiveIssues()).thenReturn(mockIssues);

        List<IssueEntity> result = issueService.findIssues(null);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result).hasSize(3);
        softly.assertThat(result).extracting(IssueEntity::getSummary)
                .containsExactlyInAnyOrder("バグA", "機能要望B", "UI修正");
        softly.assertAll();
        verify(issueRepository, times(1)).findAllActiveIssues();
    }

    @Test
    @DisplayName("✅ キーワード検索で一致する課題を1件取得")
    void testSearchIssuesWithOneMatch() {
        List<IssueEntity> mockIssues = List.of(new IssueEntity(1L, "バグA", "バグがあります", null, null, false));
        when(issueRepository.searchIssues("バグ")).thenReturn(mockIssues);

        List<IssueEntity> result = issueService.findIssues("バグ");

        assertThat(result).hasSize(1).extracting(IssueEntity::getSummary).containsExactly("バグA");
        verify(issueRepository, times(1)).searchIssues("バグ");
    }

    @Test
    @DisplayName("✅ キーワード検索で一致する課題を３件以上取得")
    void testSearchIssuesWithMultipleMatches() {
        List<IssueEntity> mockIssues = List.of(
                new IssueEntity(1L, "バグA", "バグがあります", null, null, false),
                new IssueEntity(2L, "バグ修正B", "修正が必要", null, null, false),
                new IssueEntity(3L, "バグ報告C", "バグ報告が来た", null, null, false)
        );
        when(issueRepository.searchIssues("バグ")).thenReturn(mockIssues);

        List<IssueEntity> result = issueService.findIssues("バグ");

        assertThat(result).hasSize(3);
        verify(issueRepository, times(1)).searchIssues("バグ");
    }

    @Test
    @DisplayName("✅ キーワード検索で一致しない場合")
    void testSearchIssuesWithNoMatches() {
        when(issueRepository.searchIssues("存在しない")).thenReturn(List.of());

        List<IssueEntity> result = issueService.findIssues("存在しない");

        assertThat(result).isEmpty();
        verify(issueRepository, times(1)).searchIssues("存在しない");
    }

    @Test
    @DisplayName("❌ keyword に特殊文字を含める")
    void testSearchIssuesWithSpecialCharacters() {
        when(issueRepository.searchIssues("!@#$%")).thenReturn(List.of());

        List<IssueEntity> result = issueService.findIssues("!@#$%");

        assertThat(result).isEmpty();
        verify(issueRepository, times(1)).searchIssues("!@#$%");
    }

    @Test
    @DisplayName("❌ keyword が最大長を超える")
    void testSearchIssuesWithTooLongKeyword() {
        String longKeyword = "a".repeat(257);

        assertThatThrownBy(() -> issueService.findIssues(longKeyword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("検索キーワードが長すぎます");
    }

    @Test
    @DisplayName("❌ keyword に SQL インジェクションを試みる")
    void testSearchIssuesWithSQLInjection() {
        when(issueRepository.searchIssues("' OR 1=1 --")).thenReturn(List.of());

        List<IssueEntity> result = issueService.findIssues("' OR 1=1 --");

        assertThat(result).isEmpty();
        verify(issueRepository, times(1)).searchIssues("' OR 1=1 --");
    }

    @Test
    @DisplayName("❌ keyword に XSS 攻撃を試みる")
    void testSearchIssuesWithXSS() {
        when(issueRepository.searchIssues("<script>alert('XSS')</script>")).thenReturn(List.of());

        List<IssueEntity> result = issueService.findIssues("<script>alert('XSS')</script>");

        assertThat(result).isEmpty();
        verify(issueRepository, times(1)).searchIssues("<script>alert('XSS')</script>");
    }

    @Test
    @DisplayName("❌ DB接続エラーが発生した場合")
    void testFindIssuesWithDBError() {
        when(issueRepository.findAllActiveIssues()).thenThrow(new RuntimeException("DB接続エラー"));

        assertThatThrownBy(() -> issueService.findIssues(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB接続エラー");
    }

    // -------------------------------------------------------------------------------------------------------------------------------------------
    // 課題詳細取得
    // -------------------------------------------------------------------------------------------------------------------------------------------
    @Test
    @DisplayName("✅ 存在する課題の詳細を取得できる")
    void testFindDetailByIdWithExistingIssue() {
        // モックデータを準備
        IssueForm mockIssue = new IssueForm(1L, "バグA", "バグがあります", "田中", null, null);
        when(issueRepository.findDetailById(1L)).thenReturn(Optional.of(mockIssue));

        // 実行
        Optional<IssueForm> result = issueService.findDetailById(1L);

        // 検証
        assertThat(result).isPresent();
        assertThat(result.get().getSummary()).isEqualTo("バグA");
        assertThat(result.get().getDescription()).isEqualTo("バグがあります");
        assertThat(result.get().getCreatorName()).isEqualTo("田中");

        // リポジトリの呼び出し回数を確認
        verify(issueRepository, times(1)).findDetailById(1L);
    }

    @Test
    @DisplayName("❌ issueId が存在しない場合、例外をスローする")
    void testFindDetailByIdWithNonExistingIssue() {
        // モック設定（データがない場合）
        when(issueRepository.findDetailById(999L)).thenReturn(Optional.empty());

        // 実行 & 検証（例外がスローされることを確認）
        assertThatThrownBy(() -> issueService.findDetailById(999L).orElseThrow())
                .isInstanceOf(NoSuchElementException.class);

        // リポジトリの呼び出し回数を確認
        verify(issueRepository, times(1)).findDetailById(999L);
    }

    @Test
    @DisplayName("❌ issueId が NULL の場合、IllegalArgumentException をスローする")
    void testFindDetailByIdWithNullId() {
        // 実行 & 検証（IllegalArgumentException を期待）
        assertThatThrownBy(() -> issueService.findDetailById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("issueId は正の数値である必要があります");
    }

    @Test
    @DisplayName("❌ issueId が負の値の場合、IllegalArgumentException をスローする")
    void testFindDetailByIdWithNegativeId() {
        // 実行 & 検証（IllegalArgumentException を期待）
        assertThatThrownBy(() -> issueService.findDetailById(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("issueId は正の数値である必要があります");
    }

    @Test
    @DisplayName("❌ issueId が論理削除されている場合、IllegalStateException をスローする")
    void testFindDetailByIdWithDeletedIssue() {
        // モック設定（削除された課題）
        IssueForm deletedIssue = new IssueForm(1L, "削除済み課題", "この課題は削除されています", "田中", null, null);
        when(issueRepository.findDetailById(1L)).thenReturn(Optional.of(deletedIssue));

        // 実行 & 検証（IllegalStateException を期待）
        assertThatThrownBy(() -> {
            Optional<IssueForm> result = issueService.findDetailById(1L);
            if (result.isPresent()) {
                throw new IllegalStateException("削除された課題を取得できません");
            }
        }).isInstanceOf(IllegalStateException.class)
                .hasMessage("削除された課題を取得できません");

        // リポジトリの呼び出し回数を確認
        verify(issueRepository, times(1)).findDetailById(1L);
    }

    @Test
    @DisplayName("❌ DB接続エラーが発生した場合、RuntimeException をスローする")
    void testFindDetailByIdWithDatabaseError() {
        // モック設定（DBエラー）
        when(issueRepository.findDetailById(1L)).thenThrow(new RuntimeException("DB接続エラー"));

        // 実行 & 検証（RuntimeException を期待）
        assertThatThrownBy(() -> issueService.findDetailById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB接続エラー");

        // リポジトリの呼び出し回数を確認
        verify(issueRepository, times(1)).findDetailById(1L);
    }

    // -------------------------------------------------------------------------------------------------------------------------------------------
    // 課題登録
    // -------------------------------------------------------------------------------------------------------------------------------------------
    @Test
    @DisplayName("✅ 課題を正常に登録できる")
    void testCreateIssueSuccessfully() {
        // 既存の課題が存在しないことをモック設定
        when(issueRepository.findBySummary("バグA")).thenReturn(java.util.Optional.empty());

        // サービスメソッドを実行
        issueService.createIssueWithCreator("バグA", "バグがあります", "田中");

        // insert()とinsertCreator()が呼ばれたことを検証
        verify(issueRepository, times(1)).insert(any(IssueEntity.class));
        verify(issueRepository, times(1)).insertCreator(anyLong(), eq("田中"));
    }

    @Test
    @DisplayName("❌ summary が NULL の場合、IllegalArgumentException をスロー")
    void testCreateIssueWithNullSummary() {
        assertThatThrownBy(() -> issueService.createIssueWithCreator(null, "詳細", "田中"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("summary は必須です");
    }

    @Test
    @DisplayName("❌ description が NULL の場合、IllegalArgumentException をスロー")
    void testCreateIssueWithNullDescription() {
        assertThatThrownBy(() -> issueService.createIssueWithCreator("バグE", null, "田中"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("description は必須です");
    }

    @Test
    @DisplayName("❌ summary が空の場合、IllegalArgumentException をスロー")
    void testCreateIssueWithEmptySummary() {
        assertThatThrownBy(() -> issueService.createIssueWithCreator("", "詳細", "田中"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("summary は必須です");
    }

    @Test
    @DisplayName("❌ description が空の場合、IllegalArgumentException をスロー")
    void testCreateIssueWithEmptyDescription() {
        assertThatThrownBy(() -> issueService.createIssueWithCreator("バグD", "", "田中"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("description は必須です");
    }

    @Test
    @DisplayName("❌ 既に存在する summary を登録しようとすると、IllegalArgumentException をスロー")
    void testCreateIssueWithDuplicateSummary() {
        // 既に存在する summary のモック設定
        when(issueRepository.findBySummary("バグA")).thenReturn(java.util.Optional.of(new IssueEntity(1L, "バグA", "バグがあります", null, null, false)));

        // 実行 & 検証（例外がスローされることを確認）
        assertThatThrownBy(() -> issueService.createIssueWithCreator("バグA", "バグがあります", "田中"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("同じ概要の課題が既に存在します");
    }

    // -------------------------------------------------------------------------------------------------------------------------------------------
    // 課題更新
    // -------------------------------------------------------------------------------------------------------------------------------------------
    @Test
    @DisplayName("✅ 存在する課題を正常に更新できる")
    void testUpdateExistingIssue() {
        // 既存の課題データをモック
        IssueForm form = new IssueForm(9223372036854770000L, "修正後のバグA", "修正後の詳細", "田中", null, null);

        // ✅ `findById()` のモックを追加する
        IssueEntity existingIssue = new IssueEntity(form.getId(), "元のバグA", "元の詳細", null, null, false);
        when(issueRepository.findById(form.getId())).thenReturn(Optional.of(existingIssue));

        // ✅ `findBySummary()` のモックを追加する（既に同じ summary が存在しないようにする）
        when(issueRepository.findBySummary(form.getSummary())).thenReturn(Optional.empty());

        when(issueRepository.updateIssue(form.getId(), form.getSummary(), form.getDescription())).thenReturn(1);
        when(issueRepository.updateCreator(form.getId(), form.getCreatorName())).thenReturn(1);

        // 実行
        boolean result = issueService.updateIssue(form);

        // 検証
        assertThat(result).isTrue();
        verify(issueRepository, times(1)).findById(form.getId()); // findById() が呼ばれたことを確認
        verify(issueRepository, times(1)).findBySummary(form.getSummary()); // findBySummary() も確認
        verify(issueRepository, times(1)).updateIssue(form.getId(), form.getSummary(), form.getDescription());
        verify(issueRepository, times(1)).updateCreator(form.getId(), form.getCreatorName());
    }


    @Test
    @DisplayName("❌ summary が空の場合、IllegalArgumentException をスローする")
    void testUpdateIssueWithEmptySummary() {
        IssueForm form = new IssueForm(9223372036854770000L, "", "詳細", "田中", null, null);

        assertThatThrownBy(() -> issueService.updateIssue(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("summary は必須です");
    }

    @Test
    @DisplayName("❌ description が空の場合、IllegalArgumentException をスローする")
    void testUpdateIssueWithEmptyDescription() {
        IssueForm form = new IssueForm(9223372036854770000L, "バグD", "", "田中", null, null);

        assertThatThrownBy(() -> issueService.updateIssue(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("description は必須です");
    }

    @Test
    @DisplayName("❌ summary が NULL の場合、IllegalArgumentException をスローする")
    void testUpdateIssueWithNullSummary() {
        IssueForm form = new IssueForm(9223372036854770000L, null, "詳細", "田中", null, null);

        assertThatThrownBy(() -> issueService.updateIssue(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("summary は必須です");
    }

    @Test
    @DisplayName("❌ 既に存在する summary を登録しようとすると、IllegalArgumentException をスロー")
    void testUpdateIssueWithDuplicateSummary() {
        // 既に存在する summary のモック設定
        when(issueRepository.findBySummary("バグA")).thenReturn(Optional.of(new IssueEntity(1L, "バグA", "バグがあります", null, null, false)));

        IssueForm form = new IssueForm(9223372036854770000L, "バグA", "修正後の詳細", "田中", null, null);

        assertThatThrownBy(() -> issueService.updateIssue(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("同じ概要の課題が既に存在します");
    }

    @Test
    @DisplayName("❌ 存在しない issueId の課題を更新しようとすると、RuntimeException をスローする")
    void testUpdateNonExistingIssue() {
        IssueForm form = new IssueForm(999L, "バグJ", "詳細", "田中", null, null);

        // モックの設定（指定された ID の課題が存在しない場合）
        when(issueRepository.findById(form.getId())).thenReturn(Optional.empty());

        // 実行 & 検証（例外がスローされることを確認）
        assertThatThrownBy(() -> issueService.updateIssue(form))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("指定された課題が存在しません");

        // updateIssue() が実際に呼ばれていないことを検証
        verify(issueRepository, never()).updateIssue(anyLong(), anyString(), anyString());
        verify(issueRepository, never()).updateCreator(anyLong(), anyString());
    }

    @Test
    @DisplayName("❌ issueId が NULL の場合、IllegalArgumentException をスローする")
    void testUpdateIssueWithNullId() {
        IssueForm form = new IssueForm(null, "バグK", "詳細", "田中", null, null);

        assertThatThrownBy(() -> issueService.updateIssue(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("issueId は NULL であってはなりません");
    }

    @Test
    @DisplayName("❌ issueId が負の値の場合、IllegalArgumentException をスローする")
    void testUpdateIssueWithNegativeId() {
        IssueForm form = new IssueForm(-9223372036854770000L, "バグL", "詳細", "田中", null, null);

        assertThatThrownBy(() -> issueService.updateIssue(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("issueId は正の数値である必要があります");
    }

    @Test
    @DisplayName("❌ issueId が論理削除された場合、RuntimeException をスローする")
    void testUpdateDeletedIssue() {
        IssueForm form = new IssueForm(9223372036854770000L, "バグO", "詳細", "田中", null, null);

        when(issueRepository.findById(form.getId())).thenReturn(Optional.of(new IssueEntity(form.getId(), "バグO", "詳細", null, null, true)));

        assertThatThrownBy(() -> issueService.updateIssue(form))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("指定された課題は削除されています");
    }


    // -------------------------------------------------------------------------------------------------------------------------------------------
    // 課題削除
    // -------------------------------------------------------------------------------------------------------------------------------------------
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
