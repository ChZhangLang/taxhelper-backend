package com.zihao.taxhelperai.model.dto.user;

import java.io.Serializable;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户创建请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 账号（手机号）
     */
    @NotBlank(message = "登录账号（手机号）不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String userAccount;

//    /**
//     * 真实姓名
//     */
//    @NotBlank(message = "真实姓名不能为空")
//    private String realName;
//
//    /**
//     * 身份证号（脱敏）
//     */
//    @NotBlank(message = "身份证号不能为空")
//    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$",
//            message = "身份证号格式不正确")
//    private String idCard;

    /**
     * 用户角色 user-普通用户 admin-管理员
     */
    @Pattern(regexp = "^(user|admin)$", message = "用户角色只能是user或admin")
    private String userRole;

    private static final long serialVersionUID = 1L;
}