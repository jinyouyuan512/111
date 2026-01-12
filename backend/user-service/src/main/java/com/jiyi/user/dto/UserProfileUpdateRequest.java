package com.jiyi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户资料更新请求
 */
@Data
@Schema(description = "用户资料更新请求")
public class UserProfileUpdateRequest {
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "性别")
    private String gender;
    
    @Schema(description = "生日")
    private LocalDateTime birthdate;
    
    @Schema(description = "兴趣爱好")
    private String interests;
}
