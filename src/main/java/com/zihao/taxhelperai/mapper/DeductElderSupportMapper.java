package com.zihao.taxhelperai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zihao.taxhelperai.model.entity.DeductElderSupport;
import org.apache.ibatis.annotations.Param;

public interface DeductElderSupportMapper extends BaseMapper<DeductElderSupport> {

    DeductElderSupport selectByDeductId(@Param("deductId") Long deductId);

    int deleteByDeductId(@Param("deductId") Long deductId);
}