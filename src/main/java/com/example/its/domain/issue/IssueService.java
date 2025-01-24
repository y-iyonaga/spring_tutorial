package com.example.its.domain.issue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private  final IssueRepository issueRepository;

    public List<IssueEntity> findAll() {
        return  issueRepository.findAll();
    }

    @Transactional
    public void create(String summary, String description) {
        issueRepository.insert(summary, description);

        // 後処理が増えたとする (トランザクション確認用)
//        throw new IllegalStateException("NG");
    }

    public IssueEntity findById(long issueId) {
        return issueRepository.findById(issueId);
    }
}
