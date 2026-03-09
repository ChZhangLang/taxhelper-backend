package com.zihao.taxhelperai.controller;

import com.zihao.taxhelperai.common.BaseResponse;
import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.constant.UserConstant;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.model.entity.User;
import com.zihao.taxhelperai.model.vo.LoginUserVO;
import com.zihao.taxhelperai.model.dto.user.UserUpdateRequest;
import com.zihao.taxhelperai.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerUpdateTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private MockHttpServletRequest request;

    // 测试常量
    private static final Long NORMAL_USER_ID = 1L;
    private static final Long ADMIN_USER_ID = 999L;
    private static final Long OTHER_USER_ID = 2L;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
    }

    // 场景1：普通用户修改自己的信息（成功）
    @Test
    void testUpdateUser_NormalUser_UpdateSelf_Success() {
        // 1. 构建请求（含敏感字段，测试过滤）
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setId(NORMAL_USER_ID);
        updateRequest.setRealName("张三");
        updateRequest.setTaxRegion("北京市");
        updateRequest.setUserAccount("13800138000"); // 敏感字段
        updateRequest.setUserRole("admin");          // 敏感字段

        // 2. Mock登录用户（普通用户）
        User mockNormalUser = new User();
        mockNormalUser.setId(NORMAL_USER_ID);
        mockNormalUser.setUserRole(UserConstant.DEFAULT_ROLE);
        LoginUserVO mockNormalUserVO = new LoginUserVO();
        mockNormalUserVO.setId(NORMAL_USER_ID);

        when(userService.getLoginUser(request)).thenReturn(mockNormalUser);
        when(userService.getLoginUserVO(mockNormalUser)).thenReturn(mockNormalUserVO);
        when(userService.updateById(any(User.class))).thenReturn(true);

        // 3. 执行方法
        BaseResponse<Boolean> response = userController.updateUser(updateRequest, request);

        // 4. 断言结果
        assertNotNull(response);
        assertEquals(ErrorCode.SUCCESS.getCode(), response.getCode());
        assertTrue(response.getData());

        // 5. 验证Service调用（3次核心调用）
        verify(userService, times(1)).getLoginUser(request);
        verify(userService, times(1)).getLoginUserVO(eq(mockNormalUser));
        verify(userService, times(1)).updateById(any(User.class));
        verifyNoMoreInteractions(userService);

        // 6. 验证敏感字段被过滤
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).updateById(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertNull(capturedUser.getUserAccount(), "普通用户不能修改手机号");
        assertNull(capturedUser.getUserRole(), "普通用户不能修改角色");
        assertNull(capturedUser.getUserPassword(), "普通用户不能修改密码");
        assertEquals(NORMAL_USER_ID, capturedUser.getId(), "修改的ID应为自己的ID");
        assertEquals("张三", capturedUser.getRealName(), "真实姓名修改成功");
        assertNotNull(capturedUser.getUpdateTime(), "更新时间已设置");
    }

    // 场景2：普通用户修改他人信息（失败，无权限）
    @Test
    void testUpdateUser_NormalUser_UpdateOther_Fail() {
        // 1. 构建请求（修改他人ID）
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setId(OTHER_USER_ID); // 非自己的ID
        updateRequest.setRealName("李四");

        // 2. Mock登录用户（普通用户）
        User mockNormalUser = new User();
        mockNormalUser.setId(NORMAL_USER_ID);
        mockNormalUser.setUserRole(UserConstant.DEFAULT_ROLE);
        LoginUserVO mockNormalUserVO = new LoginUserVO();
        mockNormalUserVO.setId(NORMAL_USER_ID);

        when(userService.getLoginUser(request)).thenReturn(mockNormalUser);
        when(userService.getLoginUserVO(mockNormalUser)).thenReturn(mockNormalUserVO);

        // 3. 执行方法，断言抛出无权限异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userController.updateUser(updateRequest, request);
        });
        assertEquals(ErrorCode.NO_AUTH_ERROR.getCode(), exception.getCode());
        assertEquals("普通用户仅可修改自己的信息", exception.getMessage());

        // 4. 验证无更新操作
        verify(userService, never()).updateById(any(User.class));
    }

    // 场景3：管理员修改任意用户信息（成功，无字段过滤）
    @Test
    void testUpdateUser_Admin_UpdateAny_Success() {
        // 1. 构建请求（含敏感字段）
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setId(OTHER_USER_ID); // 修改他人
        updateRequest.setRealName("管理员修改");
        updateRequest.setUserAccount("13900139000"); // 敏感字段
        updateRequest.setUserRole("admin");          // 敏感字段

        // 2. Mock登录用户（管理员）
        User mockAdminUser = new User();
        mockAdminUser.setId(ADMIN_USER_ID);
        mockAdminUser.setUserRole(UserConstant.ADMIN_ROLE);
        LoginUserVO mockAdminUserVO = new LoginUserVO();
        mockAdminUserVO.setId(ADMIN_USER_ID);

        when(userService.getLoginUser(request)).thenReturn(mockAdminUser);
        when(userService.getLoginUserVO(mockAdminUser)).thenReturn(mockAdminUserVO);
        when(userService.updateById(any(User.class))).thenReturn(true);

        // 3. 执行方法
        BaseResponse<Boolean> response = userController.updateUser(updateRequest, request);

        // 4. 断言结果
        assertNotNull(response);
        assertEquals(ErrorCode.SUCCESS.getCode(), response.getCode());
        assertTrue(response.getData());

        // 5. 验证敏感字段未被过滤（管理员特权）
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).updateById(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertEquals("13900139000", capturedUser.getUserAccount(), "管理员可修改手机号");
        assertEquals("admin", capturedUser.getUserRole(), "管理员可修改角色");
        assertEquals(OTHER_USER_ID, capturedUser.getId(), "管理员可修改他人ID");
    }
}