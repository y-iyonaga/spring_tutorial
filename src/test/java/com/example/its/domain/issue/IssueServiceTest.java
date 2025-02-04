package com.example.its.domain.issue;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.SoftAssertions;
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
    private IssueRepository issueRepository; // `IssueRepository` のモックを作成

    @InjectMocks
    private IssueService issueService; // `IssueService` にモックを注入

    // ------------------------
    // 課題の全件取得 (findAll)
    // ------------------------

    @Test
    @DisplayName("課題を全件取得できる")
    void testFindAllIssues() {
        // ① モックデータを準備
        List<IssueEntity> mockIssues = List.of(
                new IssueEntity(1L, "バグA", "バグがあります"),
                new IssueEntity(2L, "機能要望B", "Bに追加機能が欲しいです")
        );
        // ② `issueRepository.findAll()` がモックデータを返すよう設定
        when(issueRepository.findAll()).thenReturn(mockIssues);

        // ③ `issueService.findAll()` を実行
        List<IssueEntity> result = issueService.findAll();

        // ④ 結果を検証 (データが正しく取得されているか)
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result).hasSize(2); // 2件のデータが取得されることを確認
        softly.assertThat(result).extracting(IssueEntity::getId)
                .containsExactly(1L, 2L); // IDが期待通りの値になっているか検証
        softly.assertAll();

        // ⑤ `findAll()` が **1回だけ** 呼ばれたことを確認
        verify(issueRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("DBにデータがない場合、空のリストを返す")
    void testFindAll_NoData() {
        // ① `findAll()` が空のリストを返すよう設定
        when(issueRepository.findAll()).thenReturn(List.of());

        // ② 実行
        List<IssueEntity> result = issueService.findAll();

        // ③ 結果を検証 (空リストが返されることを確認)
        assertThat(result).isEmpty();

        // ④ `findAll()` が1回だけ呼ばれたことを確認
        verify(issueRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("DBエラー発生時、例外をスローする")
    void testFindAll_DatabaseError() {
        // ① `findAll()` が `RuntimeException` をスローするよう設定
        when(issueRepository.findAll()).thenThrow(new RuntimeException("DB接続エラー"));

        // ② `findAll()` 実行時に例外が発生することを検証
        assertThatThrownBy(() -> issueService.findAll())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB接続エラー");

        // ③ `findAll()` が1回だけ呼ばれたことを確認
        verify(issueRepository, times(1)).findAll();
    }

    // ------------------------
    // ID指定で課題を取得 (findById)
    // ------------------------

    @Test
    @DisplayName("IDが存在する場合、対応する課題を返す")
    void testFindById_Found() {
        long validId = 1L;
        IssueEntity mockIssue = new IssueEntity(validId, "バグA", "バグがあります");

        // ① `findById()` が `Optional.of(mockIssue)` を返すよう設定
        when(issueRepository.findById(validId)).thenReturn(Optional.of(mockIssue));

        // ② `findById()` を実行
        Optional<IssueEntity> result = issueService.findById(validId);

        // ③ 結果を検証 (`Optional` が正しく返るか)
        assertThat(result).isPresent()
                .get()
                .isEqualTo(mockIssue);

        // ④ `findById()` が1回だけ呼ばれたことを確認
        verify(issueRepository, times(1)).findById(validId);
    }

    @Test
    @DisplayName("IDが存在しない場合、Optional.empty() を返す")
    void testFindById_NotFound() {
        long nonExistentId = 999L;

        // ① `findById()` が `Optional.empty()` を返すよう設定
        when(issueRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // ② 実行
        Optional<IssueEntity> result = issueService.findById(nonExistentId);

        // ③ 結果を検証 (空の `Optional` が返ること)
        assertThat(result).isEmpty();

        // ④ `findById()` が1回だけ呼ばれたことを確認
        verify(issueRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("IDが不正な場合、IllegalArgumentExceptionをスローする")
    void testFindById_InvalidId() {
        long invalidId = -1L;

        // `findById()` を実行し、例外が発生することを検証
        assertThatThrownBy(() -> issueService.findById(invalidId))
                .isInstanceOf(IllegalArgumentException.class);

        // `issueRepository.findById()` が **呼ばれていないこと** を確認
        verify(issueRepository, never()).findById(anyLong());
    }

    // ------------------------
    // 課題の登録 (create)
    // ------------------------

    @Test
    @DisplayName("課題を登録できる")
    void testCreateIssue() {
        // `insert()` が呼ばれた場合、何もしないように設定
        doNothing().when(issueRepository).insert(anyString(), anyString());

        // `create()` を実行
        issueService.create("新しいバグ", "バグの詳細");

        // `insert()` が1回だけ呼ばれたことを確認
        verify(issueRepository, times(1)).insert("新しいバグ", "バグの詳細");
    }

    @Test
    @DisplayName("summary が空の場合、IllegalArgumentException をスローする")
    void testCreateIssue_SummaryEmpty() {
        assertThatThrownBy(() -> issueService.create("", "バグの詳細"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Summary を空にすることはできません");

        verify(issueRepository, never()).insert(anyString(), anyString());
    }

    @Test
    @DisplayName("summary が長すぎる場合、IllegalArgumentException をスローする")
    void testCreateIssue_SummaryTooLong() {
        String longSummary = "a".repeat(256); // 256文字

        assertThatThrownBy(() -> issueService.create(longSummary, "バグの詳細"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Summary が長すぎます");

        verify(issueRepository, never()).insert(anyString(), anyString());
    }

    @Test
    @DisplayName("description が空の場合、IllegalArgumentException をスローする")
    void testCreateIssue_DescriptionEmpty() {
        assertThatThrownBy(() -> issueService.create("新しいバグ", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Description を空にすることはできません");

        verify(issueRepository, never()).insert(anyString(), anyString());
    }

    @Test
    @DisplayName("description が長すぎる場合、IllegalArgumentException をスローする")
    void testCreateIssue_DescriptionTooLong() {
        String longDescription = "a".repeat(1001); // 1001文字

        assertThatThrownBy(() -> issueService.create("新しいバグ", longDescription))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Description が長すぎます");

        verify(issueRepository, never()).insert(anyString(), anyString());
    }
}
