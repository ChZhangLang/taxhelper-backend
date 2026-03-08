package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表（基于AI的税务小助手）
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账号（手机号）
     */
    private String userAccount;

    /**
     * 加密密码（MD5）
     */
    private String userPassword;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号（脱敏）
     */
    private String idCard;

    /**
     * 税务所属地区（如：北京市海淀区）
     */
    private String taxRegion;

    /**
     * 用户角色 user-普通用户 admin-管理员
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除 0-未删除 1-已删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

///**
// * 用户
// *
// * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
// * @from <a href="https://yupi.icu">编程导航知识星球</a>
// */
//@TableName(value = "user")
//@Data
//public class User implements Serializable {
//
//    /**
//     * id
//     */
//    @TableId(type = IdType.ASSIGN_ID)
//    private Long id;
//
//    /**
//     * 用户账号
//     */
//    private String userAccount;
//
//    /**
//     * 用户密码
//     */
//    private String userPassword;
//
//    /**
//     * 开放平台id
//     */
//    private String unionId;
//
//    /**
//     * 公众号openId
//     */
//    private String mpOpenId;
//
//    /**
//     * 用户昵称
//     */
//    private String userName;
//
//    /**
//     * 用户头像
//     */
//    private String userAvatar;
//
//    /**
//     * 用户简介
//     */
//    private String userProfile;
//
//    /**
//     * 用户角色：user/admin/ban
//     */
//    private String userRole;
//
//    /**
//     * 创建时间
//     */
//    private Date createTime;
//
//    /**
//     * 更新时间
//     */
//    private Date updateTime;
//
//    /**
//     * 是否删除
//     */
//    @TableLogic
//    private Integer isDelete;
//
//    @TableField(exist = false)
//    private static final long serialVersionUID = 1L;
//}