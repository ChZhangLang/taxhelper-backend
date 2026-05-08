package com.zihao.taxhelperai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zihao.taxhelperai.model.entity.DeductContinueEducation;
import org.apache.ibatis.annotations.Param;

public interface DeductContinueEducationMapper extends BaseMapper<DeductContinueEducation> {

    DeductContinueEducation selectByDeductId(@Param("deductId") Long deductId);

    int deleteByDeductId(@Param("deductId") Long deductId);
}