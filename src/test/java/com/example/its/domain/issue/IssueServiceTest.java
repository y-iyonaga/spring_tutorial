package com.example.its.domain.issue;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    // 課題一覧を取得できる
    void 課題一覧を取得できる() {
        // モックデータ
        List<IssueEntity> mockIssues = List.of(
                new IssueEntity(1L, "バグA", "バグがあります"),
                new IssueEntity(2L, "機能要望B", "Bに追加機能が欲しいです")
        );
        when(issueRepository.findAll()).thenReturn(mockIssues);

        // メソッド実行
        List<IssueEntity> result = issueService.findAll();

        // 検証
        assertThat(result).hasSize(2)
                .extracting(IssueEntity::getSummary)
                .containsExactly("バグA", "機能要望B");

        // モックメソッドの呼び出し回数を確認
        verify(issueRepository, times(1)).findAll();
    }


}
