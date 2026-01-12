package com.jiyi.user.util;

import com.jiyi.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类单元测试
 * 测试JWT令牌生成、验证和解析功能
 */
@DisplayName("JWT工具类测试")
class JwtUtilTest {

    // ==================== 访问令牌生成测试 ====================

    @Test
    @DisplayName("生成访问令牌 - 包含正确的用户信息")
    void testGenerateAccessToken_ContainsCorrectInfo() {
        // 执行测试
        String token = JwtUtil.generateAccessToken("123", "testuser", "admin");

        // 验证令牌不为空
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // 验证令牌格式（JWT格式为三部分，用.分隔）
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT令牌应该包含3个部分");

        // 验证令牌有效
        assertTrue(JwtUtil.validateToken(token));

        // 验证令牌内容
        String userId = JwtUtil.getUserIdFromToken(token);
        String username = JwtUtil.getUsernameFromToken(token);
        String role = JwtUtil.getRoleFromToken(token);

        assertEquals("123", userId);
        assertEquals("testuser", username);
        assertEquals("admin", role);
    }

    @Test
    @DisplayName("生成访问令牌 - 不同用户生成不同令牌")
    void testGenerateAccessToken_DifferentUsersGenerateDifferentTokens() {
        // 生成两个不同用户的令牌
        String token1 = JwtUtil.generateAccessToken("1", "user1", "user");
        String token2 = JwtUtil.generateAccessToken("2", "user2", "user");

        // 验证令牌不同
        assertNotEquals(token1, token2);

        // 验证各自包含正确的用户信息
        assertEquals("1", JwtUtil.getUserIdFromToken(token1));
        assertEquals("user1", JwtUtil.getUsernameFromToken(token1));
        
        assertEquals("2", JwtUtil.getUserIdFromToken(token2));
        assertEquals("user2", JwtUtil.getUsernameFromToken(token2));
    }

    // ==================== 刷新令牌生成测试 ====================

    @Test
    @DisplayName("生成刷新令牌 - 包含用户ID")
    void testGenerateRefreshToken_ContainsUserId() {
        // 执行测试
        String token = JwtUtil.generateRefreshToken("456");

        // 验证令牌不为空
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // 验证令牌有效
        assertTrue(JwtUtil.validateToken(token));

        // 验证令牌包含用户ID
        String userId = JwtUtil.getUserIdFromToken(token);
        assertEquals("456", userId);
    }

    @Test
    @DisplayName("生成刷新令牌 - 不同用户生成不同令牌")
    void testGenerateRefreshToken_DifferentUsersGenerateDifferentTokens() {
        // 生成两个不同用户的刷新令牌
        String token1 = JwtUtil.generateRefreshToken("1");
        String token2 = JwtUtil.generateRefreshToken("2");

        // 验证令牌不同
        assertNotEquals(token1, token2);

        // 验证各自包含正确的用户ID
        assertEquals("1", JwtUtil.getUserIdFromToken(token1));
        assertEquals("2", JwtUtil.getUserIdFromToken(token2));
    }

    // ==================== 令牌验证测试 ====================

    @Test
    @DisplayName("验证令牌 - 有效的访问令牌返回true")
    void testValidateToken_ValidAccessToken() {
        // 生成有效令牌
        String token = JwtUtil.generateAccessToken("1", "testuser", "user");

        // 验证令牌有效
        assertTrue(JwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("验证令牌 - 有效的刷新令牌返回true")
    void testValidateToken_ValidRefreshToken() {
        // 生成有效刷新令牌
        String token = JwtUtil.generateRefreshToken("1");

        // 验证令牌有效
        assertTrue(JwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("验证令牌 - 无效令牌返回false")
    void testValidateToken_InvalidToken() {
        // 使用无效令牌
        String invalidToken = "invalid.jwt.token";

        // 验证令牌无效
        assertFalse(JwtUtil.validateToken(invalidToken));
    }

    @Test
    @DisplayName("验证令牌 - 空令牌返回false")
    void testValidateToken_EmptyToken() {
        // 验证空令牌无效
        assertFalse(JwtUtil.validateToken(""));
    }

    @Test
    @DisplayName("验证令牌 - null令牌返回false")
    void testValidateToken_NullToken() {
        // 验证null令牌无效
        assertFalse(JwtUtil.validateToken(null));
    }

    @Test
    @DisplayName("验证令牌 - 格式错误的令牌返回false")
    void testValidateToken_MalformedToken() {
        // 使用格式错误的令牌
        String malformedToken = "not.a.valid.jwt.token.format";

        // 验证令牌无效
        assertFalse(JwtUtil.validateToken(malformedToken));
    }

    // ==================== 令牌解析测试 ====================

    @Test
    @DisplayName("解析令牌 - 正确提取用户ID")
    void testParseToken_ExtractUserId() {
        // 生成令牌
        String token = JwtUtil.generateAccessToken("789", "john", "user");

        // 解析用户ID
        String userId = JwtUtil.getUserIdFromToken(token);

        // 验证结果
        assertEquals("789", userId);
    }

    @Test
    @DisplayName("解析令牌 - 正确提取用户名")
    void testParseToken_ExtractUsername() {
        // 生成令牌
        String token = JwtUtil.generateAccessToken("1", "alice", "user");

        // 解析用户名
        String username = JwtUtil.getUsernameFromToken(token);

        // 验证结果
        assertEquals("alice", username);
    }

    @Test
    @DisplayName("解析令牌 - 正确提取角色")
    void testParseToken_ExtractRole() {
        // 生成令牌
        String token = JwtUtil.generateAccessToken("1", "admin_user", "admin");

        // 解析角色
        String role = JwtUtil.getRoleFromToken(token);

        // 验证结果
        assertEquals("admin", role);
    }

    @Test
    @DisplayName("解析令牌 - 提取所有Claims")
    void testParseToken_ExtractAllClaims() {
        // 生成令牌
        String token = JwtUtil.generateAccessToken("100", "bob", "moderator");

        // 解析Claims
        Claims claims = JwtUtil.parseToken(token);

        // 验证所有字段
        assertNotNull(claims);
        assertEquals("100", claims.getSubject());
        assertEquals("bob", claims.get("username", String.class));
        assertEquals("moderator", claims.get("role", String.class));
        assertEquals("access", claims.get("type", String.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    @DisplayName("解析令牌 - 刷新令牌包含正确的type")
    void testParseToken_RefreshTokenType() {
        // 生成刷新令牌
        String token = JwtUtil.generateRefreshToken("1");

        // 解析Claims
        Claims claims = JwtUtil.parseToken(token);

        // 验证type字段
        assertEquals("refresh", claims.get("type", String.class));
    }

    // ==================== 令牌过期测试 ====================

    @Test
    @DisplayName("令牌过期 - 新生成的令牌未过期")
    void testTokenExpiration_NewTokenNotExpired() {
        // 生成新令牌
        String token = JwtUtil.generateAccessToken("1", "testuser", "user");

        // 验证令牌未过期
        assertTrue(JwtUtil.validateToken(token));

        // 验证过期时间在未来
        Claims claims = JwtUtil.parseToken(token);
        assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis());
    }

    // ==================== 边界情况测试 ====================

    @Test
    @DisplayName("边界情况 - 用户ID为空字符串")
    void testEdgeCase_EmptyUserId() {
        // 生成令牌（用户ID为空字符串）
        String token = JwtUtil.generateAccessToken("", "testuser", "user");

        // 验证令牌有效
        assertTrue(JwtUtil.validateToken(token));

        // 验证可以解析（空字符串会被JWT解析为null或空字符串）
        String userId = JwtUtil.getUserIdFromToken(token);
        // JWT可能将空字符串解析为null或空字符串
        assertTrue(userId == null || userId.isEmpty());
    }

    @Test
    @DisplayName("边界情况 - 用户名包含特殊字符")
    void testEdgeCase_UsernameWithSpecialCharacters() {
        // 生成令牌（用户名包含特殊字符）
        String token = JwtUtil.generateAccessToken("1", "user@example.com", "user");

        // 验证令牌有效
        assertTrue(JwtUtil.validateToken(token));

        // 验证可以正确解析
        String username = JwtUtil.getUsernameFromToken(token);
        assertEquals("user@example.com", username);
    }

    @Test
    @DisplayName("边界情况 - 角色为空字符串")
    void testEdgeCase_EmptyRole() {
        // 生成令牌（角色为空字符串）
        String token = JwtUtil.generateAccessToken("1", "testuser", "");

        // 验证令牌有效
        assertTrue(JwtUtil.validateToken(token));

        // 验证可以解析
        String role = JwtUtil.getRoleFromToken(token);
        assertEquals("", role);
    }

    @Test
    @DisplayName("边界情况 - 长用户ID")
    void testEdgeCase_LongUserId() {
        // 生成令牌（很长的用户ID）
        String longUserId = "1234567890".repeat(10); // 100位数字
        String token = JwtUtil.generateAccessToken(longUserId, "testuser", "user");

        // 验证令牌有效
        assertTrue(JwtUtil.validateToken(token));

        // 验证可以正确解析
        String userId = JwtUtil.getUserIdFromToken(token);
        assertEquals(longUserId, userId);
    }

    // ==================== 令牌一致性测试 ====================

    @Test
    @DisplayName("令牌一致性 - 相同参数生成的令牌可以互相验证")
    void testTokenConsistency_SameParametersValidate() {
        // 生成两个相同参数的令牌
        String token1 = JwtUtil.generateAccessToken("1", "testuser", "user");
        String token2 = JwtUtil.generateAccessToken("1", "testuser", "user");

        // 两个令牌都应该有效
        assertTrue(JwtUtil.validateToken(token1));
        assertTrue(JwtUtil.validateToken(token2));

        // 两个令牌应该包含相同的用户信息
        assertEquals(JwtUtil.getUserIdFromToken(token1), JwtUtil.getUserIdFromToken(token2));
        assertEquals(JwtUtil.getUsernameFromToken(token1), JwtUtil.getUsernameFromToken(token2));
        assertEquals(JwtUtil.getRoleFromToken(token1), JwtUtil.getRoleFromToken(token2));
    }
}
