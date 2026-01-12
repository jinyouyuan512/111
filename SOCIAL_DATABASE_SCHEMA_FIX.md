# 社区平台数据库架构修复

## 问题描述
发布社区动态时返回 400 错误，错误信息显示数据库错误 1054（Unknown column）。

## 错误信息
```
POST http://localhost:3001/api/posts 400 (Bad Request)
错误数据: {
  code: 400, 
  message: 'Error updating database. Cause: java.sql.SQLException: Unknown error 1054'
}
```

## 根本原因

### 实体与数据库表结构不匹配

**Post 实体原有字段**:
- `likesCount` (Integer)
- `commentsCount` (Integer)
- `sharesCount` (Integer)
- `location` (String)
- `category` (String)
- `images` (String)
- `pinned` (Boolean)

**数据库实际字段**:
- `likes` (int)
- `comments` (int)
- `shares` (int)
- `location_name` (varchar)
- `location_id` (bigint)
- `latitude` (decimal)
- `longitude` (decimal)
- `type` (varchar)
- `visibility` (varchar)
- `status` (varchar)
- `views` (int)

**不匹配的字段**:
1. 计数字段命名不一致（`likesCount` vs `likes`）
2. 位置字段不同（`location` vs `location_name`）
3. `category` 字段不存在于数据库
4. `images` 字段不存在（应该使用 `media_file` 表）
5. `pinned` 字段不存在

## 解决方案

### 1. 更新 Post 实体以匹配数据库

**文件**: `backend/social-service/src/main/java/com/jiyi/social/entity/Post.java`

```java
@Data
@TableName("post")
public class Post {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String content;
    
    // 图片列表（非数据库字段）
    @TableField(exist = false)
    private String images;
    
    // 位置相关字段（匹配数据库）
    private String locationName;
    private Long locationId;
    private Double latitude;
    private Double longitude;
    
    // 类型和可见性
    private String type;
    private String visibility;
    
    // 分类（非数据库字段）
    @TableField(exist = false)
    private String category;
    
    // 计数字段（匹配数据库列名）
    private Integer likes;
    private Integer comments;
    private Integer shares;
    private Integer views;
    
    // 状态
    private String status;
    
    // 置顶（非数据库字段）
    @TableField(exist = false)
    private Boolean pinned;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
```

**关键改进**:
- ✅ 使用 `likes`, `comments`, `shares` 而不是 `likesCount`, `commentsCount`, `sharesCount`
- ✅ 使用 `locationName` 而不是 `location`
- ✅ 添加 `locationId`, `latitude`, `longitude` 字段
- ✅ 添加 `type`, `visibility`, `status`, `views` 字段
- ✅ 使用 `@TableField(exist = false)` 标记非数据库字段（`images`, `category`, `pinned`）

### 2. 更新 PostService 以使用新字段

**文件**: `backend/social-service/src/main/java/com/jiyi/social/service/PostService.java`

**创建动态**:
```java
@Transactional
public PostVO createPost(Long userId, PostCreateRequest request) {
    Post post = new Post();
    post.setUserId(userId);
    post.setContent(request.getContent());
    post.setLocationName(request.getLocation());  // 使用 locationName
    post.setType("normal");
    post.setVisibility("public");
    post.setStatus("published");
    post.setLikes(0);      // 使用 likes
    post.setComments(0);   // 使用 comments
    post.setShares(0);     // 使用 shares
    post.setViews(0);
    
    postMapper.insert(post);
    return convertToVO(post, userId);
}
```

**点赞操作**:
```java
@Transactional
public void likePost(Long postId, Long userId) {
    // ...
    post.setLikes(post.getLikes() + 1);  // 使用 likes
    postMapper.updateById(post);
}
```

**转换为 VO**:
```java
private PostVO convertToVO(Post post, Long currentUserId) {
    PostVO vo = new PostVO();
    BeanUtils.copyProperties(post, vo);
    
    // 设置位置
    vo.setLocation(post.getLocationName());
    
    // images 字段不在数据库中，返回空列表
    vo.setImages(new ArrayList<>());
    
    // ...
}
```

## 数据库表结构

### post 表完整结构

```sql
CREATE TABLE `post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '动态ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content` TEXT NOT NULL COMMENT '内容',
    `location_id` BIGINT COMMENT '位置ID',
    `location_name` VARCHAR(100) COMMENT '位置名称',
    `latitude` DECIMAL(10,7) COMMENT '纬度',
    `longitude` DECIMAL(10,7) COMMENT '经度',
    `type` VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '类型',
    `visibility` VARCHAR(20) NOT NULL DEFAULT 'public' COMMENT '可见性',
    `likes` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comments` INT NOT NULL DEFAULT 0 COMMENT '评论数',
    `shares` INT NOT NULL DEFAULT 0 COMMENT '转发数',
    `views` INT NOT NULL DEFAULT 0 COMMENT '浏览数',
    `status` VARCHAR(20) NOT NULL DEFAULT 'published' COMMENT '状态',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 发布用户ID |
| content | TEXT | 动态内容 |
| location_id | BIGINT | 位置ID（关联位置表） |
| location_name | VARCHAR(100) | 位置名称 |
| latitude | DECIMAL(10,7) | 纬度 |
| longitude | DECIMAL(10,7) | 经度 |
| type | VARCHAR(20) | 类型：normal, checkin, share |
| visibility | VARCHAR(20) | 可见性：public, friends, private |
| likes | INT | 点赞数 |
| comments | INT | 评论数 |
| shares | INT | 转发数 |
| views | INT | 浏览数 |
| status | VARCHAR(20) | 状态：published, deleted, hidden |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除标记 |

## MyBatis-Plus 字段映射

### 自动映射规则

MyBatis-Plus 默认使用驼峰命名转下划线：
- Java: `userId` → SQL: `user_id`
- Java: `locationName` → SQL: `location_name`
- Java: `createdAt` → SQL: `created_at`

### 非数据库字段标记

使用 `@TableField(exist = false)` 标记非数据库字段：

```java
// 这些字段不会被插入或查询
@TableField(exist = false)
private String images;

@TableField(exist = false)
private String category;

@TableField(exist = false)
private Boolean pinned;
```

### 自动填充字段

使用 `@TableField(fill = FieldFill.INSERT)` 标记自动填充：

```java
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createdAt;

@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updatedAt;
```

## 测试验证

### 1. 创建动态测试

```bash
curl -X POST http://localhost:8083/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "content": "测试动态内容",
    "location": "西柏坡纪念馆",
    "category": "travel"
  }'
```

**期望响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "content": "测试动态内容",
    "location": "西柏坡纪念馆",
    "likes": 0,
    "comments": 0,
    "shares": 0,
    "views": 0,
    "status": "published",
    "createdAt": "2026-01-03T17:00:00"
  }
}
```

### 2. 查询动态列表

```bash
curl -X GET http://localhost:8083/api/posts?page=1&size=10
```

### 3. 点赞动态

```bash
curl -X POST http://localhost:8083/api/posts/1/like \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 常见问题

### Q1: 为什么不修改数据库而是修改实体？
**A**: 
- 数据库表结构已经有数据，修改风险大
- 实体修改更灵活，不影响现有数据
- 保持与现有数据库设计一致

### Q2: images 字段怎么处理？
**A**: 
- 当前标记为非数据库字段
- 未来可以使用 `media_file` 表存储图片
- 或者添加 `images` TEXT 列存储 JSON 数组

### Q3: category 字段怎么处理？
**A**: 
- 当前标记为非数据库字段
- 可以使用 `type` 字段代替
- 或者添加 `category` 列到数据库

### Q4: 如何添加图片支持？
**A**: 有两种方案：

**方案1：添加 images 列**
```sql
ALTER TABLE `post` ADD COLUMN `images` TEXT COMMENT '图片列表（JSON数组）';
```

**方案2：使用 media_file 表**
```sql
CREATE TABLE `media_file` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `post_id` BIGINT NOT NULL,
    `type` VARCHAR(20) NOT NULL,
    `url` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`)
);
```

## 修改文件清单

1. ✅ `backend/social-service/src/main/java/com/jiyi/social/entity/Post.java`
   - 更新字段名以匹配数据库
   - 添加缺失字段
   - 标记非数据库字段

2. ✅ `backend/social-service/src/main/java/com/jiyi/social/service/PostService.java`
   - 更新 createPost 方法
   - 更新 likePost/unlikePost/sharePost 方法
   - 更新 convertToVO 方法

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
- ✅ 实体字段与数据库列匹配
- ✅ 创建动态不再返回 400 错误
- ✅ 动态可以成功保存到数据库
- ✅ 点赞、评论、分享功能正常

## 总结

数据库架构问题已修复，主要改进：

1. **实体字段对齐**:
   - 使用数据库实际列名
   - 标记非数据库字段
   - 添加缺失字段

2. **服务层更新**:
   - 使用正确的字段名
   - 设置默认值
   - 正确的数据转换

3. **数据完整性**:
   - 所有必填字段都有默认值
   - 状态字段正确设置
   - 计数字段初始化为0

现在用户可以成功发布社区动态了！
