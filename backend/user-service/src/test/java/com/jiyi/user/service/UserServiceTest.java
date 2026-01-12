package com.jiyi.user.service;

import com.jiyi.common.exception.BusinessException;
import com.jiyi.user.dto.PasswordChangeRequest;
import com.jiyi.user.dto.UserInfo;
import com.jiyi.user.dto.UserStatistics;
import com.jiyi.user.entity.User;
import com.jiyi.user.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 * 测试用户信息管理、密码修改等功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // ==================== 获取用户信息测试 ====================

    @Test
    @DisplayName("获取用户信息成功 - 用户存在")
    void testGetUserById_Success() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setNickname("Test User");
        mockUser.setPhone("13800138000");
        mockUser.setRole("user");

        // Mock行为
        when(userMapper.selectById(1L)).thenReturn(mockUser);

        // 执行测试
        UserInfo result = userService.getUserById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getNickname());
        assertEquals("13800138000", result.getPhone());
        verify(userMapper).selectById(1L);
    }

    @Test
    @DisplayName("获取用户信息失败 - 用户不存在")
    void testGetUserById_UserNotFound() {
        // Mock行为 - 用户不存在
        when(userMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserById(999L);
        });

        assertEquals(404, exception.getCode());
        assertEquals("用户不存在", exception.getMessage());
    }

    // ==================== 更新用户信息测试 ====================

    @Test
    @DisplayName("更新用户信息成功 - 更新昵称")
    void testUpdateProfile_UpdateNickname() {
        // 准备测试数据
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testuser");
        existingUser.setNickname("Old Nickname");

        UserInfo updateInfo = new UserInfo();
        updateInfo.setNickname("New Nickname");

        // Mock行为
        when(userMapper.selectById(1L)).thenReturn(existingUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        UserInfo result = userService.updateProfile(1L, updateInfo);

        // 验证结果
        assertNotNull(result);
        assertEquals("New Nickname", result.getNickname());

        // 验证更新操作
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(userCaptor.capture());
        assertEquals("New Nickname", userCaptor.getValue().getNickname());
    }

    @Test
    @DisplayName("更新用户信息成功 - 更新手机号")
    void testUpdateProfile_UpdatePhone() {
        // 准备测试数据
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testuser");
        existingUser.setPhone("13800138000");

        UserInfo updateInfo = new UserInfo();
        updateInfo.setPhone("13900139000");

        // Mock行为
        when(userMapper.selectById(1L)).thenReturn(existingUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        UserInfo result = userService.updateProfile(1L, updateInfo);

        // 验证结果
        assertEquals("13900139000", result.getPhone());

        // 验证更新操作
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(userCaptor.capture());
        assertEquals("13900139000", userCaptor.getValue().getPhone());
    }

    @Test
    @DisplayName("更新用户信息成功 - 更新头像")
    void testUpdateProfile_UpdateAvatar() {
        // 准备测试数据
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testuser");
        existingUser.setAvatar("old-avatar.jpg");

        UserInfo updateInfo = new UserInfo();
        updateInfo.setAvatar("new-avatar.jpg");

        // Mock行为
        when(userMapper.selectById(1L)).thenReturn(existingUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        UserInfo result = userService.updateProfile(1L, updateInfo);

        // 验证结果
        assertEquals("new-avatar.jpg", result.getAvatar());

        // 验证更新操作
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(userCaptor.capture());
        assertEquals("new-avatar.jpg", userCaptor.getValue().getAvatar());
    }

    @Test
    @DisplayName("更新用户信息失败 - 用户不存在")
    void testUpdateProfile_UserNotFound() {
        // 准备测试数据
        UserInfo updateInfo = new UserInfo();
        updateInfo.setNickname("New Nickname");

        // Mock行为 - 用户不存在
        when(userMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.updateProfile(999L, updateInfo);
        });

        assertEquals(404, exception.getCode());
        assertEquals("用户不存在", exception.getMessage());
        verify(userMapper, never()).updateById(any(User.class));
    }

    // ==================== 获取用户统计信息测试 ====================

    @Test
    @DisplayName("获取用户统计信息成功")
    void testGetUserStatistics_Success() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPoints(500);
        mockUser.setLevel(5);

        // Mock行为
        when(userMapper.selectById(1L)).thenReturn(mockUser);

        // 执行测试
        UserStatistics result = userService.getUserStatistics(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(500, result.getPoints());
        assertEquals(5, result.getLevel());
    }

    @Test
    @DisplayName("获取用户统计信息 - 默认值处理")
    void testGetUserStatistics_DefaultValues() {
        // 准备测试数据 - 积分和等级为null
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPoints(null);
        mockUser.setLevel(null);

        // Mock行为
        when(userMapper.selectById(1L)).thenReturn(mockUser);

        // 执行测试
        UserStatistics result = userService.getUserStatistics(1L);

        // 验证默认值
        assertEquals(0, result.getPoints());
        assertEquals(1, result.getLevel());
    }

    @Test
    @DisplayName("获取用户统计信息失败 - 用户不存在")
    void testGetUserStatistics_UserNotFound() {
        // Mock行为 - 用户不存在
        when(userMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserStatistics(999L);
        });

        assertEquals(404, exception.getCode());
        assertEquals("用户不存在", exception.getMessage());
    }

    // ==================== 修改密码测试 ====================

    @Test
    @DisplayName("修改密码成功 - 旧密码正确")
    void testChangePassword_Success() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPasswordHash("$2a$10$oldPasswordHash");

        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword123");

        // Mock行为
        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(passwordEncoder.matches("oldPassword", "$2a$10$oldPasswordHash")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$newPasswordHash");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        userService.changePassword(1L, request);

        // 验证密码已更新
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(userCaptor.capture());
        assertEquals("$2a$10$newPasswordHash", userCaptor.getValue().getPasswordHash());
        verify(passwordEncoder).encode("newPassword123");
    }

    @Test
    @DisplayName("修改密码失败 - 用户不存在")
    void testChangePassword_UserNotFound() {
        // 准备测试数据
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword123");

        // Mock行为 - 用户不存在
        when(userMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.changePassword(999L, request);
        });

        assertEquals(404, exception.getCode());
        assertEquals("用户不存在", exception.getMessage());
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    @DisplayName("修改密码失败 - 旧密码错误")
    void testChangePassword_WrongOldPassword() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPasswordHash("$2a$10$correctHash");

        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("wrongOldPassword");
        request.setNewPassword("newPassword123");

        // Mock行为
        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(passwordEncoder.matches("wrongOldPassword", "$2a$10$correctHash")).thenReturn(false);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.changePassword(1L, request);
        });

        assertEquals(400, exception.getCode());
        assertEquals("旧密码错误", exception.getMessage());
        verify(userMapper, never()).updateById(any(User.class));
    }

    // ==================== 上传头像测试 ====================

    @Test
    @DisplayName("上传头像成功")
    void testUploadAvatar_Success() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setAvatar("old-avatar.jpg");

        MultipartFile mockFile = mock(MultipartFile.class);

        // Mock行为
        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        String avatarUrl = userService.uploadAvatar(1L, mockFile);

        // 验证结果
        assertNotNull(avatarUrl);
        assertTrue(avatarUrl.startsWith("https://"));

        // 验证头像已更新
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(userCaptor.capture());
        assertNotNull(userCaptor.getValue().getAvatar());
    }

    @Test
    @DisplayName("上传头像失败 - 用户不存在")
    void testUploadAvatar_UserNotFound() {
        // 准备测试数据
        MultipartFile mockFile = mock(MultipartFile.class);

        // Mock行为 - 用户不存在
        when(userMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.uploadAvatar(999L, mockFile);
        });

        assertEquals(404, exception.getCode());
        assertEquals("用户不存在", exception.getMessage());
        verify(userMapper, never()).updateById(any(User.class));
    }
}
