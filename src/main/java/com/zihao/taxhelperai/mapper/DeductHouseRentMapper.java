package com.zihao.taxhelperai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zihao.taxhelperai.model.entity.DeductHouseRent;
import org.apache.ibatis.annotations.Param;

public interface DeductHouseRentMapper extends BaseMapper<DeductHouseRent> {

    DeductHouseRent selectByDeductId(@Param("deductId") Long deductId);

    int deleteByDeductId(@Param("deductId") Long deductId);
}