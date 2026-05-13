package com.zihao.taxhelperai.service.impl;

import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.exception.ThrowUtils;
import com.zihao.taxhelperai.mapper.FamilyMemberMapper;
import com.zihao.taxhelperai.model.entity.FamilyMember;
import com.zihao.taxhelperai.service.FamilyMemberService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【family_member(家属信息表)】的数据库操作Service实现
*/
@Service
public class FamilyMemberServiceImpl extends ServiceImpl<FamilyMemberMapper, FamilyMember> implements FamilyMemberService {

    @Override
    public List<FamilyMember> getByUserId(Long userId) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<FamilyMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FamilyMember::getUserId, userId);
        queryWrapper.orderByDesc(FamilyMember::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public boolean addFamilyMember(FamilyMember familyMember, Long userId) {
        ThrowUtils.throwIf(familyMember == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        
        // 设置用户ID
        familyMember.setUserId(userId);
        
        // 设置默认值
        if (familyMember.getIdCardType() == null || familyMember.getIdCardType().isEmpty()) {
            familyMember.setIdCardType("居民身份证");
        }
        if (familyMember.getNationality() == null || familyMember.getNationality().isEmpty()) {
            familyMember.setNationality("中华人民共和国");
        }
        
        return this.save(familyMember);
    }

    @Override
    public boolean deleteFamilyMember(Long id, Long userId) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        
        // 验证是否为当前用户的家属
        FamilyMember familyMember = this.getById(id);
        ThrowUtils.throwIf(familyMember == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!userId.equals(familyMember.getUserId()), ErrorCode.NO_AUTH_ERROR, "只能删除自己的家属信息");
        
        return this.removeById(id);
    }
}