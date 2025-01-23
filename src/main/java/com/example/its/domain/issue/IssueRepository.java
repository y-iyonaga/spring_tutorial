package com.example.its.domain.issue;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper // MyBatisはこのアノテーションだけでBeanを含んでる
public interface IssueRepository {

    @Select("select * from issues") // issues全件取得
    List<IssueEntity> findAll();
}
