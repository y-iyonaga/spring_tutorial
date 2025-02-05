package com.example.its.domain.issue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssueService {

    private  final IssueRepository issueRepository;

    public List<IssueEntity> findAll() {
        return  issueRepository.findAll();
    }

    @Transactional //トランザクションのアノテーション
    public void create(String summary, String description) {
        if (summary == null || summary.trim().isEmpty()) {
            throw new IllegalArgumentException("Summary を空にすることはできません");
        }
        if (summary.length() > 255) {
            throw new IllegalArgumentException("Summary が長すぎます");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description を空にすることはできません");
        }
        if (description.length() > 1000) {
            throw new IllegalArgumentException("Description が長すぎます");
        }

        issueRepository.insert(summary, description);
    }

    public Optional<IssueEntity> findById(Long issueId) {
        if (issueId < 0) {
            throw new IllegalArgumentException("issueId は正の数値である必要があります");
        }
        if (issueId == null) {
            throw new IllegalArgumentException("issueId を空にすることはできません");
        }
        return issueRepository.findById(issueId);
    }

}
