package com.jiyi.social.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${user-service.url:http://localhost:8081}")
    private String userServiceUrl;
    
    // 简单的内存缓存，避免频繁调用
    private final Map<Long, UserInfo> userCache = new ConcurrentHashMap<>();
    
    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
        private String email;
        private String phone;
    }
    
    @Data
    public static class ApiResponse {
        private Integer code;
        private String message;
        private UserInfo data;
    }
    
    public UserInfo getUserById(Long userId) {
        // 先检查缓存
        if (userCache.containsKey(userId)) {
            return userCache.get(userId);
        }
        
        try {
            String url = userServiceUrl + "/api/users/" + userId;
            ApiResponse response = restTemplate.getForObject(url, ApiResponse.class);
            
            if (response != null && response.getCode() == 200 && response.getData() != null) {
                UserInfo userInfo = response.getData();
                // 缓存用户信息（简单实现，生产环境应使用 Redis）
                userCache.put(userId, userInfo);
                return userInfo;
            }
        } catch (Exception e) {
            log.warn("获取用户信息失败, userId: {}, error: {}", userId, e.getMessage());
        }
        
        // 返回默认用户信息
        UserInfo defaultUser = new UserInfo();
        defaultUser.setId(userId);
        defaultUser.setNickname("用户" + userId);
        defaultUser.setAvatar("");
        return defaultUser;
    }
    
    // 清除缓存（可选）
    public void clearCache() {
        userCache.clear();
    }
    
    public void clearCache(Long userId) {
        userCache.remove(userId);
    }
}
