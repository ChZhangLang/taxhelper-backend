package com.zihao.taxhelperai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zihao.taxhelperai.model.entity.DeductSeriousIllness;
import org.apache.ibatis.annotations.Param;

public interface DeductSeriousIllnessMapper extends BaseMapper<DeductSeriousIllness> {

    DeductSeriousIllness selectByDeductId(@Param("deductId") Long deductId);

    int deleteByDeductId(@Param("deductId") Long deductId);
}