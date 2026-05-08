package com.zihao.taxhelperai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zihao.taxhelperai.model.entity.TaxSpecialDeduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaxSpecialDeductMapper extends BaseMapper<TaxSpecialDeduct> {

    List<TaxSpecialDeduct> selectByUserIdAndType(@Param("userId") Long userId, @Param("deductType") Integer deductType);

    List<TaxSpecialDeduct> selectActiveByUserId(@Param("userId") Long userId);

    List<TaxSpecialDeduct> selectActiveByTypes(@Param("userId") Long userId, @Param("types") List<Integer> types);
}