package com.zihao.taxhelperai.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户视图（脱敏）
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 账号（手机号）
     */
    private String userAccount;

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

    private static final long serialVersionUID = 1L;
}