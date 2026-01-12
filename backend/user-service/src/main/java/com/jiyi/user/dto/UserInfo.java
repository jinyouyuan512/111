package com.jiyi.user.dto;

import lombok.Data;

/**
 * 用户信息
 */
@Data
public class UserInfo {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String role;
    private String nickname;
    private String phone;
    private Integer level;
    private Integer points;
}
