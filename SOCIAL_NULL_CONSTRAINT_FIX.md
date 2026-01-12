# 社区平台 NULL 约束错误修复

## 问题描述
发布社区动态时返回 400 错误，错误信息显示 SQL 约束违反错误 1048（Column cannot be null）。

## 错误信息
```
Error updating database. Cause: java.sql.SQLIntegrityConstraintViolationException: Unknown error 1048

SQL: INSERT INTO post ( user_id, content, location_name, type, visibility, likes, comments, shares, views, status, created_at, updated_at ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )

Cause: java.sql.SQLIntegrityConstraintViolationException: Unknown error 1048
```

## 根本原因

### MySQL Error 1048
错误代码 1048 表示 "Column 'xxx' cannot be null"，即某个 NOT NULL 列没有提供值。

### 缺失的字段
从 SQL 语句可以看出，INSERT 语句中缺少了 `deleted` 字段，但数据库表中 `deleted` 列定义为 `NOT NULL DEFAULT 0`。

### MyBatis-Plus 自动填充未配置
Post 实体使用了 `@TableField(fill = FieldFill.INSERT)` 注解，但项目中没有配置 `MetaObjectHandler`，导致自动填充不生效：

```java
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createdAt;

@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updatedAt;

@TableLogic
private Integer deleted;  // 没有自动填充配置
```

## 解决方案

### 1. 创建 MyBatisPlusConfig 配置类

**文件**: `backend/social-service/src/main/java/com/jiyi/social/config/MyBatisPlusConfig.java`

```java
package com.jiyi.social.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 配置
 */
@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                // 自动填充创建时间
                this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
                // 自动填充更新时间
                this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
                // 自动填充逻辑删除标记
                this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                // 自动填充更新时间
                this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
```

**功能说明**:
- `insertFill`: 在插入记录时自动填充字段
- `updateFill`: 在更新记录时自动填充字段
- `strictInsertFill`: 严格模式，只在字段为 null 时填充
- `strictUpdateFill`: 严格模式，只在字段为 null 时填充

### 2. 更新 Post 实体

**文件**: `backend/social-service/src/main/java/com/jiyi/social/entity/Post.java`

```java
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createdAt;

@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updatedAt;

@TableLogic
@TableField(value = "deleted", fill = FieldFill.INSERT)  // 添加 fill 配置
private Integer deleted;
```

**改进**:
- 为 `deleted` 字段添加 `fill = FieldFill.INSERT` 配置
- 确保在插入时自动填充为 0

## MyBatis-Plus 自动填充机制

### 工作原理

```
┌─────────────────────────────────────────────────────────────┐
│                    插入操作流程                              │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│  1. 创建 Post 对象                                           │
│     Post post = new Post();                                  │
│     post.setUserId(1L);                                      │
│     post.setContent("测试内容");                             │
│     // createdAt, updatedAt, deleted 未设置                 │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│  2. 调用 postMapper.insert(post)                             │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│  3. MyBatis-Plus 拦截器检测到 @TableField(fill = INSERT)    │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│  4. 调用 MetaObjectHandler.insertFill()                      │
│     - 填充 createdAt = LocalDateTime.now()                   │
│     - 填充 updatedAt = LocalDateTime.now()                   │
│     - 填充 deleted = 0                                       │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│  5. 生成 SQL 并执行                                          │
│     INSERT INTO post (user_id, content, created_at,          │
│                       updated_at, deleted, ...)              │
│     VALUES (1, '测试内容', '2026-01-03 17:00:00',           │
│             '2026-01-03 17:00:00', 0, ...)                   │
└─────────────────────────────────────────────────────────────┘
```

### 填充策略

| 策略 | 说明 | 使用场景 |
|------|------|----------|
| FieldFill.DEFAULT | 默认不处理 | 不需要自动填充 |
| FieldFill.INSERT | 插入时填充 | createdAt, deleted |
| FieldFill.UPDATE | 更新时填充 | - |
| FieldFill.INSERT_UPDATE | 插入和更新时都填充 | updatedAt |

### strictInsertFill vs setFieldValByName

```java
// strictInsertFill: 严格模式，只在字段为 null 时填充
this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());

// setFieldValByName: 总是填充，会覆盖已有值
this.setFieldValByName("createdAt", LocalDateTime.now(), metaObject);
```

**推荐使用 strictInsertFill**:
- 不会覆盖用户手动设置的值
- 更安全，避免意外覆盖
- 性能更好

## 数据库约束

### post 表约束

```sql
CREATE TABLE `post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,              -- NOT NULL
    `content` TEXT NOT NULL,                -- NOT NULL
    `location_name` VARCHAR(100),           -- NULL
    `type` VARCHAR(20) NOT NULL DEFAULT 'normal',
    `visibility` VARCHAR(20) NOT NULL DEFAULT 'public',
    `likes` INT NOT NULL DEFAULT 0,
    `comments` INT NOT NULL DEFAULT 0,
    `shares` INT NOT NULL DEFAULT 0,
    `views` INT NOT NULL DEFAULT 0,
    `status` VARCHAR(20) NOT NULL DEFAULT 'published',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,   -- NOT NULL, 必须提供值
    PRIMARY KEY (`id`)
);
```

**NOT NULL 字段**:
- `user_id`: 必须提供
- `content`: 必须提供
- `type`: 有默认值或自动填充
- `visibility`: 有默认值或自动填充
- `likes`, `comments`, `shares`, `views`: 有默认值或自动填充
- `status`: 有默认值或自动填充
- `created_at`: 有默认值或自动填充
- `updated_at`: 有默认值或自动填充
- `deleted`: **需要自动填充**

## 测试验证

### 1. 单元测试

```java
@Test
public void testMetaObjectHandler() {
    Post post = new Post();
    post.setUserId(1L);
    post.setContent("测试内容");
    post.setType("normal");
    post.setVisibility("public");
    post.setStatus("published");
    post.setLikes(0);
    post.setComments(0);
    post.setShares(0);
    post.setViews(0);
    
    // createdAt, updatedAt, deleted 不设置
    
    postMapper.insert(post);
    
    // 验证自动填充
    assertNotNull(post.getCreatedAt());
    assertNotNull(post.getUpdatedAt());
    assertEquals(0, post.getDeleted());
}
```

### 2. 集成测试

```bash
# 发布动态
curl -X POST http://localhost:8083/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "content": "测试动态内容",
    "location": "西柏坡纪念馆",
    "category": "travel"
  }'

# 期望响应：200 OK
```

### 3. 数据库验证

```sql
-- 查询最新插入的记录
SELECT * FROM jiyi_social.post ORDER BY id DESC LIMIT 1;

-- 验证字段值
-- created_at: 应该有值
-- updated_at: 应该有值
-- deleted: 应该为 0
```

## 常见问题

### Q1: 为什么需要 MetaObjectHandler？
**A**: 
- `@TableField(fill = FieldFill.INSERT)` 只是标记，不会自动生效
- 需要 `MetaObjectHandler` 实现具体的填充逻辑
- 类似于 Spring 的 `@Autowired` 需要容器支持

### Q2: 可以不用 MetaObjectHandler 吗？
**A**: 可以，有两种替代方案：

**方案1：手动设置**
```java
Post post = new Post();
post.setCreatedAt(LocalDateTime.now());
post.setUpdatedAt(LocalDateTime.now());
post.setDeleted(0);
```

**方案2：数据库默认值**
```sql
`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
`deleted` TINYINT NOT NULL DEFAULT 0
```

但使用 MetaObjectHandler 更优雅，代码更简洁。

### Q3: strictInsertFill 和 setFieldValByName 的区别？
**A**:
- `strictInsertFill`: 只在字段为 null 时填充，不覆盖已有值
- `setFieldValByName`: 总是填充，会覆盖已有值

推荐使用 `strictInsertFill`，更安全。

### Q4: 为什么 deleted 字段需要自动填充？
**A**:
- `deleted` 是逻辑删除标记，必须有值
- 数据库定义为 NOT NULL
- 如果不填充，INSERT 会失败

## 最佳实践

### 1. 统一的时间戳处理

```java
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
                this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);
                this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
```

### 2. 实体类规范

```java
@Data
@TableName("post")
public class Post {
    // 主键
    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 业务字段
    private Long userId;
    private String content;
    
    // 自动填充字段
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;
}
```

### 3. 日志记录

```java
@Bean
public MetaObjectHandler metaObjectHandler() {
    return new MetaObjectHandler() {
        @Override
        public void insertFill(MetaObject metaObject) {
            log.debug("开始插入填充...");
            this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
            this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
            log.debug("插入填充完成");
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            log.debug("开始更新填充...");
            this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            log.debug("更新填充完成");
        }
    };
}
```

## 修改文件清单

1. ✅ `backend/social-service/src/main/java/com/jiyi/social/config/MyBatisPlusConfig.java`
   - 新建配置类
   - 实现 MetaObjectHandler

2. ✅ `backend/social-service/src/main/java/com/jiyi/social/entity/Post.java`
   - 为 deleted 字段添加 fill 配置

## 重启服务

```bash
# 停止旧进程
taskkill /F /PID <PID>

# 启动新进程
cd backend/social-service
mvn spring-boot:run
```

## 验证清单

- ✅ social-service 成功启动
- ✅ MetaObjectHandler 配置生效
- ✅ 创建动态不再返回 1048 错误
- ✅ created_at 自动填充
- ✅ updated_at 自动填充
- ✅ deleted 自动填充为 0

## 总结

NULL 约束错误已修复，主要改进：

1. **创建 MyBatisPlusConfig**:
   - 实现 MetaObjectHandler
   - 自动填充时间戳和逻辑删除标记

2. **更新 Post 实体**:
   - 为 deleted 字段添加 fill 配置
   - 确保所有 NOT NULL 字段都有值

3. **自动填充机制**:
   - 插入时自动填充 createdAt, updatedAt, deleted
   - 更新时自动填充 updatedAt
   - 使用 strictInsertFill 避免覆盖已有值

现在用户可以成功发布社区动态了！
