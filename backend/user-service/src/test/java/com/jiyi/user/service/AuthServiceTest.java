package com.jiyi.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiyi.common.exception.BusinessException;
import com.jiyi.common.util.JwtUtil;
import com.jiyi.user.dto.LoginRequest;
import com.jiyi.user.dto.LoginResponse;
import com.jiyi.user.dto.RefreshTokenRequest;
import com.jiyi.user.dto.RegisterRequest;
import com.jiyi.user.entity.User;
import com.jiyi.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 认证服务单元测试
 * 测试用户注册验证逻辑、登录认证流程和JWT令牌生成验证
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("认证服务测试")
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // Setup is done by Mockito annotations
    }

    // ==================== 用户注册测试 ====================

    @Test
    @DisplayName("注册成功 - 使用有效的用户数据")
    void testRegister_Success() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("SecurePass123");
        request.setNickname("Test User");
        request.setPhone("13800138000");

        // Mock行为
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // 执行测试
        User result = authService.register(request);

        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getNickname());
        assertEquals("13800138000", result.getPhone());
        assertEquals("user", result.getRole());
        assertEquals(1, result.getLevel());
        assertEquals(0, result.getPoints());
        assertNotNull(result.getAvatar());

        // 验证密码已加密
        verify(passwordEncoder).encode("SecurePass123");
        verify(userMapper).insert(any(User.class));
    }

    @Test
    @DisplayName("注册失败 - 用户名已存在")
    void testRegister_UsernameExists() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setEmail("new@example.com");
        request.setPassword("SecurePass123");

        // Mock行为 - 用户名查询返回1条记录
        when(userMapper.selectCount(argThat(wrapper -> {
            // 验证是按用户名查询
            return wrapper != null;
        }))).thenReturn(1L).thenReturn(0L);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register(request);
        });

        assertEquals(409, exception.getCode());
        assertEquals("用户名已存在", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("注册失败 - 邮箱已被注册")
    void testRegister_EmailExists() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");
        request.setPassword("SecurePass123");

        // Mock行为 - 用户名不存在，但邮箱存在
        when(userMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(0L)  // 用户名查询返回0
            .thenReturn(1L); // 邮箱查询返回1

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register(request);
        });

        assertEquals(409, exception.getCode());
        assertEquals("邮箱已被注册", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("注册验证 - 密码正确加密")
    void testRegister_PasswordEncryption() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("MySecretPassword123");

        // Mock行为
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(passwordEncoder.encode("MySecretPassword123")).thenReturn("$2a$10$encodedHash");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // 执行测试
        User result = authService.register(request);

        // 验证密码已加密
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        
        assertEquals("$2a$10$encodedHash", capturedUser.getPasswordHash());
        verify(passwordEncoder).encode("MySecretPassword123");
    }

    // ==================== 用户登录测试 ====================

    @Test
    @DisplayName("登录成功 - 使用正确的用户名和密码")
    void testLogin_Success() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("correctPassword");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setPasswordHash("$2a$10$encodedPassword");
        mockUser.setRole("user");
        mockUser.setNickname("Test User");

        // Mock行为
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockUser);
        when(passwordEncoder.matches("correctPassword", "$2a$10$encodedPassword")).thenReturn(true);
        when(userMapper.updateById(any(User.class))).thenReturn(1);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 执行测试
        LoginResponse response = authService.login(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertNotNull(response.getUserInfo());
        assertEquals("testuser", response.getUserInfo().getUsername());
        assertEquals("test@example.com", response.getUserInfo().getEmail());

        // 验证最后登录时间已更新
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(userCaptor.capture());
        assertNotNull(userCaptor.getValue().getLastLoginAt());

        // 验证刷新令牌已存储到Redis
        verify(valueOperations).set(
            eq("refresh_token:1"),
            anyString(),
            eq(7L),
            eq(TimeUnit.DAYS)
        );
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void testLogin_UserNotFound() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password");

        // Mock行为 - 用户不存在
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(request);
        });

        assertEquals(401, exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void testLogin_WrongPassword() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongPassword");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPasswordHash("$2a$10$encodedPassword");

        // Mock行为
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockUser);
        when(passwordEncoder.matches("wrongPassword", "$2a$10$encodedPassword")).thenReturn(false);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(request);
        });

        assertEquals(401, exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    @DisplayName("登录验证 - 密码匹配逻辑")
    void testLogin_PasswordMatching() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("myPassword123");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPasswordHash("$2a$10$hashedPassword");
        mockUser.setRole("user");

        // Mock行为
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockUser);
        when(passwordEncoder.matches("myPassword123", "$2a$10$hashedPassword")).thenReturn(true);
        when(userMapper.updateById(any(User.class))).thenReturn(1);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 执行测试
        authService.login(request);

        // 验证密码匹配方法被正确调用
        verify(passwordEncoder).matches("myPassword123", "$2a$10$hashedPassword");
    }

    // ==================== JWT令牌测试 ====================

    @Test
    @DisplayName("JWT令牌生成 - 访问令牌包含正确信息")
    void testLogin_AccessTokenGeneration() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        User mockUser = new User();
        mockUser.setId(123L);
        mockUser.setUsername("testuser");
        mockUser.setPasswordHash("$2a$10$hash");
        mockUser.setRole("admin");

        // Mock行为
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userMapper.updateById(any(User.class))).thenReturn(1);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 执行测试
        LoginResponse response = authService.login(request);

        // 验证访问令牌
        assertNotNull(response.getAccessToken());
        assertTrue(JwtUtil.validateToken(response.getAccessToken()));
        
        // 验证令牌中的用户信息
        String userId = JwtUtil.getUserIdFromToken(response.getAccessToken());
        String username = JwtUtil.getUsernameFromToken(response.getAccessToken());
        String role = JwtUtil.getRoleFromToken(response.getAccessToken());
        
        assertEquals("123", userId);
        assertEquals("testuser", username);
        assertEquals("admin", role);
    }

    @Test
    @DisplayName("JWT令牌生成 - 刷新令牌包含正确信息")
    void testLogin_RefreshTokenGeneration() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        User mockUser = new User();
        mockUser.setId(456L);
        mockUser.setUsername("testuser");
        mockUser.setPasswordHash("$2a$10$hash");
        mockUser.setRole("user");

        // Mock行为
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userMapper.updateById(any(User.class))).thenReturn(1);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 执行测试
        LoginResponse response = authService.login(request);

        // 验证刷新令牌
        assertNotNull(response.getRefreshToken());
        assertTrue(JwtUtil.validateToken(response.getRefreshToken()));
        
        // 验证令牌中的用户ID
        String userId = JwtUtil.getUserIdFromToken(response.getRefreshToken());
        assertEquals("456", userId);
    }

    @Test
    @DisplayName("JWT令牌验证 - 有效令牌返回true")
    void testJwtValidation_ValidToken() {
        // 生成有效令牌
        String token = JwtUtil.generateAccessToken("1", "testuser", "user");
        
        // 验证令牌有效
        assertTrue(JwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("JWT令牌验证 - 无效令牌返回false")
    void testJwtValidation_InvalidToken() {
        // 使用无效令牌
        String invalidToken = "invalid.jwt.token";
        
        // 验证令牌无效
        assertFalse(JwtUtil.validateToken(invalidToken));
    }

    @Test
    @DisplayName("JWT令牌解析 - 正确提取用户信息")
    void testJwtParsing_ExtractUserInfo() {
        // 生成令牌
        String token = JwtUtil.generateAccessToken("789", "john_doe", "admin");
        
        // 解析令牌
        String userId = JwtUtil.getUserIdFromToken(token);
        String username = JwtUtil.getUsernameFromToken(token);
        String role = JwtUtil.getRoleFromToken(token);
        
        // 验证解析结果
        assertEquals("789", userId);
        assertEquals("john_doe", username);
        assertEquals("admin", role);
    }

    // ==================== 刷新令牌测试 ====================

    @Test
    @DisplayName("刷新令牌成功 - 使用有效的刷新令牌")
    void testRefreshToken_Success() {
        // 准备测试数据
        String refreshToken = JwtUtil.generateRefreshToken("1");
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setRole("user");

        // Mock行为
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:1")).thenReturn(refreshToken);
        when(userMapper.selectById(1L)).thenReturn(mockUser);

        // 执行测试
        LoginResponse response = authService.refreshToken(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertNotNull(response.getUserInfo());
    }

    @Test
    @DisplayName("刷新令牌失败 - 令牌不存在于Redis")
    void testRefreshToken_TokenNotInRedis() {
        // 准备测试数据
        String refreshToken = JwtUtil.generateRefreshToken("1");
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);

        // Mock行为 - Redis中没有该令牌
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:1")).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.refreshToken(request);
        });

        assertEquals(401, exception.getCode());
        assertTrue(exception.getMessage().contains("刷新令牌"));
    }

    @Test
    @DisplayName("刷新令牌失败 - 令牌不匹配")
    void testRefreshToken_TokenMismatch() {
        // 准备测试数据
        String refreshToken = JwtUtil.generateRefreshToken("1");
        String differentToken = JwtUtil.generateRefreshToken("2"); // 不同用户ID生成不同令牌
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);

        // Mock行为 - Redis中的令牌不匹配
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:1")).thenReturn(differentToken);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.refreshToken(request);
        });

        assertEquals(401, exception.getCode());
        assertTrue(exception.getMessage().contains("刷新令牌"));
    }

    // ==================== 用户登出测试 ====================

    @Test
    @DisplayName("登出成功 - 删除Redis中的刷新令牌")
    void testLogout_Success() {
        // 执行测试
        authService.logout(1L);

        // 验证Redis删除操作
        verify(redisTemplate).delete("refresh_token:1");
    }
}
