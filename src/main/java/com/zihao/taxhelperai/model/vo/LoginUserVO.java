package com.zihao.taxhelperai.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 已登录用户视图（脱敏）
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 **/
@Data
public class LoginUserVO implements Serializable {

    /**
     * id
     */
    private Long id;
    /**
     * 账号（手机号）
     */
    private String userAccount;

    /**
     * 用户角色 user-普通用户 admin-管理员
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}