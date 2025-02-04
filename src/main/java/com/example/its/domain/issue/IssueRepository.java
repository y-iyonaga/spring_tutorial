package com.example.its.domain.issue;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper // MyBatisはこのアノテーションだけでBeanを含んでる
public interface IssueRepository {

    @Select("select * from issues") // issues全件取得
    List<IssueEntity> findAll();

    @Insert("insert into issues (summary, description) values (#{summary}, #{description})")
    void insert(String summary, String description);

    @Select("select * from issues where id = #{issueId}")
    Optional<IssueEntity> findById(long issueId);
}
