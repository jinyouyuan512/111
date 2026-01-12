# 创意作品提交 500 错误修复

## 🐛 问题描述

提交创意作品时出现 500 Internal Server Error：
```
POST http://localhost:3001/api/creative/designs 500 (Internal Server Error)
```

## 🔍 问题分析

1. **前端数据格式问题**
   - 前端将 `files` 数组转换为逗号分隔的字符串
   - 后端期望接收 `List<String>` 类型

2. **缺少异常处理**
   - 创意服务没有全局异常处理器
   - 错误信息不够详细，难以定位问题

3. **日志不足**
   - Service 层缺少详细日志
   - 无法追踪数据处理过程

## ✅ 修复方案

### 1. 修复前端数据格式

**修改前：**
```typescript
const designData = {
  // ...
  files: uploadForm.files.join(','), // 转换为字符串
  // ...
}
```

**修改后：**
```typescript
const designData = {
  // ...
  files: uploadForm.files, // 直接发送数组
  // ...
}
```

### 2. 添加全局异常处理器

创建 `GlobalExceptionHandler.java`：
```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统内部错误: " + e.getMessage());
    }
    
    // ... 其他异常处理
}
```

### 3. 增强 Service 层日志

在 `CreativeServiceImpl` 中添加详细日志：
```java
@Slf4j
@Service
@RequiredArgsConstructor
public class CreativeServiceImpl implements CreativeService {
    
    @Override
    @Transactional
    public DesignVO submitDesign(Long userId, DesignSubmitRequest request) {
        log.info("提交设计作品 - 用户ID: {}, 请求数据: {}", userId, request);
        
        // ... 业务逻辑
        
        try {
            String filesJson = objectMapper.writeValueAsString(request.getFiles());
            log.info("文件列表JSON: {}", filesJson);
            design.setFiles(filesJson);
        } catch (Exception e) {
            log.error("文件列表序列化失败", e);
            throw new BusinessException("文件列表格式错误: " + e.getMessage());
        }
        
        try {
            designMapper.insert(design);
            log.info("作品插入成功 - ID: {}", design.getId());
        } catch (Exception e) {
            log.error("作品插入失败", e);
            throw new BusinessException("作品保存失败: " + e.getMessage());
        }
        
        return convertToDesignVO(design, userId);
    }
}
```

## 📝 数据流程

### 前端 → 后端

1. **前端发送数据**
```json
{
  "title": "作品标题",
  "categoryType": 1,
  "description": "作品描述",
  "designConcept": "设计理念",
  "coverImage": "http://localhost:8083/uploads/images/2026/01/04/xxx.jpg",
  "files": [
    "http://localhost:8083/uploads/images/2026/01/04/file1.jpg",
    "http://localhost:8083/uploads/images/2026/01/04/file2.jpg"
  ],
  "copyrightStatement": "版权声明",
  "tags": "标签1,标签2"
}
```

2. **后端接收 DTO**
```java
public class DesignSubmitRequest {
    private String title;
    private Integer categoryType;
    private String description;
    private String designConcept;
    private List<String> files;  // 接收数组
    private String coverImage;
    private String copyrightStatement;
    private String tags;
}
```

3. **转换为实体**
```java
Design design = new Design();
// 将 List<String> 转换为 JSON 字符串存储
design.setFiles(objectMapper.writeValueAsString(request.getFiles()));
```

4. **存储到数据库**
```sql
INSERT INTO design (files, ...) VALUES 
('["http://...", "http://..."]', ...);
```

## 🧪 测试步骤

### 1. 重启创意服务
```bash
cd backend/creative-service
mvn clean install
mvn spring-boot:run
```

### 2. 测试文件上传
1. 访问 `http://localhost:5173/creative/upload`
2. 填写作品信息
3. 上传封面图片
4. 上传作品文件
5. 点击提交

### 3. 检查日志
查看后端控制台日志：
```
提交设计作品 - 用户ID: 1, 请求数据: DesignSubmitRequest(...)
文件列表JSON: ["http://...", "http://..."]
作品插入成功 - ID: 123
```

### 4. 验证数据库
```sql
SELECT * FROM jiyi_creative.design ORDER BY id DESC LIMIT 1;
```

检查 `files` 字段是否为 JSON 数组格式。

## 🔧 可能的其他问题

### 1. 数据库连接问题
确保创意服务的数据库配置正确：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jiyi_creative
    username: root
    password: your_password
```

### 2. 端口冲突
确认创意服务运行在正确的端口：
```yaml
server:
  port: 3001
```

### 3. 跨域配置
确保 CORS 配置允许前端访问：
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        // 配置允许的源
    }
}
```

### 4. 用户认证
确保请求头包含用户ID：
```typescript
const uploadHeaders = {
  'X-User-Id': userStore.userId?.toString() || ''
}
```

## 📊 错误排查清单

- [ ] 前端是否正确发送数组格式的 files
- [ ] 后端是否成功接收到请求
- [ ] 数据库连接是否正常
- [ ] 表结构是否正确
- [ ] 用户ID是否正确传递
- [ ] 文件URL是否有效
- [ ] 日志中是否有详细错误信息

## 🚀 后续优化

1. **添加请求参数验证**
```java
public class DesignSubmitRequest {
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotNull(message = "分类不能为空")
    private Integer categoryType;
    
    @NotBlank(message = "描述不能为空")
    private String description;
    
    @NotEmpty(message = "至少上传一个文件")
    private List<String> files;
    
    @NotBlank(message = "封面图片不能为空")
    private String coverImage;
}
```

2. **添加文件URL验证**
```java
private void validateFileUrls(List<String> files) {
    for (String file : files) {
        if (!file.startsWith("http://") && !file.startsWith("https://")) {
            throw new BusinessException("无效的文件URL: " + file);
        }
    }
}
```

3. **添加事务回滚测试**
```java
@Test
public void testSubmitDesignRollback() {
    // 测试异常情况下的事务回滚
}
```

## 📚 相关文档

- [创意文件上传更新](CREATIVE_FILE_UPLOAD_UPDATE.md)
- [创意上传错误修复](CREATIVE_UPLOAD_ERROR_FIX.md)
- [创意服务实现](CREATIVE_SERVICE_IMPLEMENTATION.md)

## ✨ 总结

本次修复主要解决了前后端数据格式不匹配的问题，并增强了错误处理和日志记录能力。修复后，用户可以正常提交创意作品，系统会提供清晰的错误提示，便于问题排查和调试。
