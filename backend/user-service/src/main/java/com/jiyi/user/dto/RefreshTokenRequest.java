package com.jiyi.user.dto;

import lombok.Data;

/**
 * 刷新令牌请求
 */
@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
