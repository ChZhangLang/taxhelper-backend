package com.zihao.taxhelperai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zihao.taxhelperai.model.entity.DeductHouseLoan;
import org.apache.ibatis.annotations.Param;

public interface DeductHouseLoanMapper extends BaseMapper<DeductHouseLoan> {

    DeductHouseLoan selectByDeductId(@Param("deductId") Long deductId);

    int deleteByDeductId(@Param("deductId") Long deductId);
}