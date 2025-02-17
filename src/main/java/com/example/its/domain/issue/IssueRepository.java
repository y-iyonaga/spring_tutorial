package com.example.its.domain.issue;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface IssueRepository {

    @Select("SELECT * FROM issues")
    List<IssueEntity> findAll();

    @Select("SELECT * FROM issues WHERE summary = #{summary} LIMIT 1")
    Optional<IssueEntity> findBySummary(@Param("summary") String summary);


    @Insert("INSERT INTO issues (summary, description) VALUES (#{summary}, #{description})")
    void insert(@Param("summary") String summary, @Param("description") String description);

    @Insert("INSERT INTO issues_creator (issues_ID, creatorName) VALUES (#{issueId}, #{creatorName})")
    void insertCreator(@Param("issueId") long issueId, @Param("creatorName") String creatorName);



    @Select("SELECT LAST_INSERT_ID()")
    long getLastInsertId();

    @Select("""
        SELECT i.id, i.summary, i.description, ic.creatorName, i.created_at, i.updated_at 
        FROM issues i
        LEFT JOIN issues_creator ic ON i.id = ic.issues_ID
        WHERE i.id = #{issueId} AND i.is_deleted = FALSE
    """)
    Optional<IssueDetailDto> findById(@Param("issueId") long issueId);

//    @Select("SELECT * FROM issues WHERE id = #{issueId}")
//    Optional<IssueEntity> findById(@Param("issueId") long issueId);
}
