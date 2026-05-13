package com.zihao.taxhelperai.service.impl;

import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.exception.ThrowUtils;
import com.zihao.taxhelperai.mapper.BankCardMapper;
import com.zihao.taxhelperai.model.entity.BankCard;
import com.zihao.taxhelperai.service.BankCardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【bank_card(银行卡信息表)】的数据库操作Service实现
*/
@Service
public class BankCardServiceImpl extends ServiceImpl<BankCardMapper, BankCard> implements BankCardService {

    @Override
    public List<BankCard> getByUserId(Long userId) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<BankCard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BankCard::getUserId, userId);
        queryWrapper.orderByDesc(BankCard::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public boolean addBankCard(BankCard bankCard, Long userId) {
        ThrowUtils.throwIf(bankCard == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        
        // 设置用户ID
        bankCard.setUserId(userId);
        
        return this.save(bankCard);
    }

    @Override
    public boolean deleteBankCard(Long id, Long userId) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        
        // 验证是否为当前用户的银行卡
        BankCard bankCard = this.getById(id);
        ThrowUtils.throwIf(bankCard == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!userId.equals(bankCard.getUserId()), ErrorCode.NO_AUTH_ERROR, "只能删除自己的银行卡信息");
        
        return this.removeById(id);
    }
}