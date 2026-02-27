package com.cx.consultant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import entity.CasesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CasesMapper extends BaseMapper<CasesEntity> {

    @Select("SELECT * FROM cases ORDER BY RAND() LIMIT 1")
    CasesEntity randomSelectOne();

    @Select("SELECT * FROM cases WHERE CASE_ID = #{id}")
    CasesEntity testChosenOne(int id);

}
