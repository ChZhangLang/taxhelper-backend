package com.zihao.taxhelperai.service;

import com.zihao.taxhelperai.model.entity.FamilyMember;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【family_member(家属信息表)】的数据库操作Service
*/
public interface FamilyMemberService extends IService<FamilyMember> {

    /**
     * 根据用户ID获取家属列表
     * @param userId 用户ID
     * @return 家属列表
     */
    List<FamilyMember> getByUserId(Long userId);

    /**
     * 添加家属信息
     * @param familyMember 家属信息
     * @param userId 当前登录用户ID
     * @return 是否成功
     */
    boolean addFamilyMember(FamilyMember familyMember, Long userId);

    /**
     * 删除家属信息
     * @param id 家属ID
     * @param userId 当前登录用户ID
     * @return 是否成功
     */
    boolean deleteFamilyMember(Long id, Long userId);
}