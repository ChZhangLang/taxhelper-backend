package com.zihao.taxhelperai.model.dto.user;

import java.io.Serializable;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 用户更新请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * 用户主键ID（必传，唯一标识要修改的用户）
     */
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /**
     * 登录账号（手机号）- 仅管理员可修改
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String userAccount;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号（18位）
     */
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$",
            message = "身份证号格式不正确")
    private String idCard;

    /**
     * 税务所属地区
     */
    private String taxRegion;

    /**
     * 用户角色（user/admin）- 仅管理员可修改
     */
    @Pattern(regexp = "^(user|admin)$", message = "用户角色只能是user或admin")
    private String userRole;

    private static final long serialVersionUID = 1L;
}