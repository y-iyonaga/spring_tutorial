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
    private IssueRepository issueRepository; // IssueRepository のモックを作成

    @InjectMocks
    private IssueService issueService; // IssueService にモックを注入

    // ------------------------
    // 課題の全件取得 (findAll)
    // ------------------------

    @Test
    @DisplayName("課題を全件取得できる")
    void testFindAllIssues() {
        // モックデータを準備
        List<IssueEntity> mockIssues = List.of(
                new IssueEntity(1L, "バグA", "バグがあります"),
                new IssueEntity(2L, "機能要望B", "Bに追加機能が欲しいです")
        );
        // findAll() がモックデータを返すよう設定
        when(issueRepository.findAll()).thenReturn(mockIssues);

        // サービスメソッドを実行
        List<IssueEntity> result = issueService.findAll();

        // 結果を検証
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result).hasSize(2); // 2件のデータが取得されることを確認
        softly.assertThat(result).extracting(IssueEntity::getId)
                .containsExactly(1L, 2L); // IDが期待通りの値になっているか検証
        softly.assertAll();

        // findAll() が1回だけ呼ばれたことを確認
        verify(issueRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("DBにデータがない場合、空のリストを返す")
    void testFindAll_NoData() {
        // findAll() が空のリストを返すよう設定
        when(issueRepository.findAll()).thenReturn(List.of());

        // メソッドを実行
        List<IssueEntity> result = issueService.findAll();

        // 結果を検証（空リストであること）
        assertThat(result).isEmpty();

        // findAll() が1回だけ呼ばれたことを確認
        verify(issueRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("DBエラー発生時、例外をスローする")
    void testFindAll_DatabaseError() {
        // findAll() が例外をスローするよう設定
        when(issueRepository.findAll()).thenThrow(new RuntimeException("DB接続エラー"));

        // メソッド実行時に例外が発生することを検証
        assertThatThrownBy(() -> issueService.findAll())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB接続エラー");

        // findAll() が1回だけ呼ばれたことを確認
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

        // findById() が Optional.of(mockIssue) を返すよう設定
        when(issueRepository.findById(validId)).thenReturn(Optional.of(mockIssue));

        // メソッドを実行
        Optional<IssueEntity> result = issueService.findById(validId);

        // 結果を検証（Optional が正しく返るか）
        assertThat(result).isPresent()
                .get()
                .isEqualTo(mockIssue);

        // findById() が1回だけ呼ばれたことを確認
        verify(issueRepository, times(1)).findById(validId);
    }

    // ------------------------
    // 課題の登録 (create)
    // ------------------------

    @Test
    @DisplayName("課題を登録できる")
    void testCreateIssue() {
        // insert() が呼ばれた場合、何もしないように設定
        doNothing().when(issueRepository).insert(anyString(), anyString());

        // create() を実行
        issueService.create("新しいバグ", "バグの詳細");

        // insert() が1回だけ呼ばれたことを確認
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
    @DisplayName("description が長すぎる場合、IllegalArgumentException をスローする")
    void testCreateIssue_DescriptionTooLong() {
        String longDescription = "a".repeat(1001); // 1001文字

        assertThatThrownBy(() -> issueService.create("新しいバグ", longDescription))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Description が長すぎます");

        verify(issueRepository, never()).insert(anyString(), anyString());
    }
}
