package com.example.its.domain.issue;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface IssueRepository {

    @Select("SELECT * FROM issues")
    List<IssueEntity> findAll();

    @Select("SELECT * FROM issues WHERE LOWER(TRIM(summary)) = LOWER(TRIM(#{summary})) LIMIT 1 FOR UPDATE")
    Optional<IssueEntity> findBySummary(@Param("summary") String summary);

    @Insert("INSERT INTO issues (summary, description) VALUES (#{summary}, #{description})")
    void insert(@Param("summary") String summary, @Param("description") String description);

    @Insert("INSERT INTO issues_creator (issues_ID, creatorName) VALUES (#{issueId}, #{creatorName})")
    void insertCreator(@Param("issueId") long issueId, @Param("creatorName") String creatorName);

    @Select("SELECT * FROM issues WHERE id = #{issueId}")
    Optional<IssueEntity> findById(@Param("issueId") long issueId);

    @Select("SELECT LAST_INSERT_ID()")
    long getLastInsertId();
}
