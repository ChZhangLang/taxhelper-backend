package com.zihao.taxhelperai.service;

import com.zihao.taxhelperai.model.entity.BankCard;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【bank_card(银行卡信息表)】的数据库操作Service
*/
public interface BankCardService extends IService<BankCard> {

    /**
     * 根据用户ID获取银行卡列表
     * @param userId 用户ID
     * @return 银行卡列表
     */
    List<BankCard> getByUserId(Long userId);

    /**
     * 添加银行卡信息
     * @param bankCard 银行卡信息
     * @param userId 当前登录用户ID
     * @return 是否成功
     */
    boolean addBankCard(BankCard bankCard, Long userId);

    /**
     * 删除银行卡信息
     * @param id 银行卡ID
     * @param userId 当前登录用户ID
     * @return 是否成功
     */
    boolean deleteBankCard(Long id, Long userId);
}