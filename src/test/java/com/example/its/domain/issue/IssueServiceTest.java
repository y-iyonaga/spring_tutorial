package com.example.its.domain.issue;

import static org.mockito.Mockito.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @InjectMocks
    private IssueService issueService;

    @Test
    @DisplayName("課題一覧が取得できることを確認")
    void testFindAllIssues() {
        // モックデータ
        List<IssueEntity> mockIssues = List.of(
                new IssueEntity(1L, "バグA", "バグがあります"),
                new IssueEntity(2L, "機能要望B", "Bに追加機能が欲しいです")
        );
        when(issueRepository.findAll()).thenReturn(mockIssues);
        // ↑ when(モックオブジェクト.メソッド()).thenReturn(戻り値);
        // メソッド実行
        List<IssueEntity> result = issueService.findAll();

        SoftAssertions softly = new SoftAssertions(); // SoftAssertions を使うとすべてを評価してから結果をまとめて報告できる
        softly.assertThat(result).hasSize(2); // リストのサイズ
        softly.assertThat(result).extracting(IssueEntity::getId) // 1Lと2Lが順番通りか（リスト内の順番）
                .containsExactly(1L, 2L);
        softly.assertAll(); // すべてのアサーションを評価した後、一度にエラーメッセージを表示する

        // モックメソッドの呼び出し回数を確認
        verify(issueRepository, times(1)).findAll();
    }


}
