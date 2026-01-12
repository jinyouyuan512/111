package com.jiyi.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("user")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String email;
    
    private String passwordHash;
    
    private String avatar;
    
    private String role;
    
    private String nickname;
    
    private String phone;
    
    private String gender;
    
    private LocalDateTime birthdate;
    
    private String interests;
    
    private Integer level;
    
    private Integer points;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    private LocalDateTime lastLoginAt;
    
    @TableLogic
    private Integer deleted;
}
