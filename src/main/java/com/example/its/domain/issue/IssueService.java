package com.example.its.domain.issue;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssueService {

    private static final Logger logger = LoggerFactory.getLogger(IssueService.class);
    private final IssueRepository issueRepository;


    // 課題の一覧・検索
    public List<IssueEntity> findIssues(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return issueRepository.findAllActiveIssues();
        }
        return issueRepository.searchIssues(keyword);
    }

    // 課題登録
    @Transactional
    public void createIssueWithCreator(String summary, String description, String creatorName) {
        if (issueRepository.findBySummary(summary).isPresent()) {
            throw new IllegalArgumentException("同じ概要の課題が既に存在します");
        }

        long issueId = createIssue(summary, description);
        issueRepository.insertCreator(issueId, creatorName);
    }


    // ここで既存の課題をチェック（トランザクション管理は createIssueWithCreator 側で行う）
    @Transactional
    private synchronized boolean issueExists(String summary) {
        String normalizedSummary = Normalizer.normalize(summary, Normalizer.Form.NFKC);
        Optional<IssueEntity> existingIssue = issueRepository.findBySummary(normalizedSummary);

        logger.info("★ issueExists 実行: summary={}, 存在する?={}", normalizedSummary, existingIssue.isPresent());

        return existingIssue.isPresent();
    }


    // 課題登録（トランザクション管理は createIssueWithCreator 側で行う）
    private long createIssue(String summary, String description) {
        logger.info("★ createIssue 実行: summary={}, description={}", summary, description);
        issueRepository.insert(summary, description);
        long lastInsertId = issueRepository.getLastInsertId();
        logger.info("★ getLastInsertId() の結果: {}", lastInsertId);
        return lastInsertId;
    }

    // 課題詳細取得
    public Optional<IssueEntity> findById(Long issueId) {
        if (issueId == null || issueId < 0) {
            throw new IllegalArgumentException("issueId は正の数値である必要があります");
        }
        return issueRepository.findById(issueId);
    }
}
