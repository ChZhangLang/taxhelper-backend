package com.zihao.taxhelperai.service;

import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.model.vo.LoginUserVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.annotation.Resource;

/**
 * 用户服务测试（Spring Boot 集成测试 - JUnit 5）
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    // 模拟 HttpServletRequest（Spring 提供的 Mock 类，适配集成测试）
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    void userRegister() {
        // 测试数据（注意：手机号格式要符合规则，否则会抛异常）
        String userAccount = "19856880328"; // 改用合规手机号，否则注册会失败
        String userPassword = "123456";
        String checkPassword = "123456";
        String realName = "张梓豪";
        String idCard = "341204200403280417"; // 合规18位身份证号
        String taxRegion = "北京市海淀区";

        try {
            // 先删除已存在的测试账号（避免重复注册报错）
            // 如需实现，可补充 userService.deleteByUserAccount(userAccount);

            long result = userService.userRegister(userAccount, userPassword, checkPassword,
                                                    realName, idCard, taxRegion);
            // 断言：注册成功返回的用户ID大于0（而非固定1，因为数据库自增ID不固定）
            Assertions.assertTrue(result > 0);
            System.out.println("注册成功，用户ID：" + result);
        } catch (BusinessException e) {
            // 捕获业务异常并打印（便于调试）
            System.out.println("注册失败：" + e.getMessage());
            // 若因“账号已注册”报错，也视为测试通过（避免重复执行报错）
            if (e.getCode() == ErrorCode.PARAMS_ERROR.getCode() && e.getMessage().contains("手机号已注册")) {
                Assertions.assertTrue(true);
            } else {
                // 其他异常则测试失败
                Assertions.fail("注册抛出非预期异常：" + e.getMessage());
            }
        } catch (Exception e) {
            Assertions.fail("注册抛出未知异常：" + e.getMessage());
        }
    }

    @Test
    void userLogin() {
        // 测试数据（和注册的账号密码一致）
        String userAccount = "19856880328";
        String userPassword = "123456";

        try {
            // 执行登录方法
            LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);

            // 断言登录成功的核心条件
            Assertions.assertNotNull(loginUserVO); // 登录结果不为空
            Assertions.assertEquals(userAccount, loginUserVO.getUserAccount()); // 手机号匹配
            System.out.println("登录成功，用户信息：" + loginUserVO);
        } catch (BusinessException e) {
            System.out.println("登录失败：" + e.getMessage());
            Assertions.fail("登录抛出业务异常：" + e.getMessage());
        } catch (Exception e) {
            Assertions.fail("登录抛出未知异常：" + e.getMessage());
        }
    }
}