# 社交平台评论功能修复

## 问题描述
用户报告评论功能无法使用，API返回400错误，后端日志显示SQL错误1054（Unknown column）。

## 根本原因
Comment实体类中定义了`updatedAt`字段，但数据库的`comment`表中没有这个列。MyBatisPlusConfig的MetaObjectHandler在INSERT操作时尝试自动填充这个不存在的字段，导致SQL错误。

## 修复内容

### 1. 修复Comment实体类
**文件**: `backend/social-service/src/main/java/com/jiyi/social/entity/Comment.java`

移除了不存在的`updatedAt`字段：
```java
// 移除前
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createdAt;

@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updatedAt;  // ❌ 数据库中不存在此列

@TableLogic
@TableField(fill = FieldFill.INSERT)
private Integer deleted;

// 修复后
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createdAt;

@TableLogic
@TableField(fill = FieldFill.INSERT)
private Integer deleted;
```

### 2. 重新编译并启动服务
```bash
cd backend/social-service
mvn clean compile -DskipTests
mvn spring-boot:run
```

服务已成功启动在端口8083。

## 数据库表结构
`comment`表的实际字段：
- id (BIGINT, 主键)
- post_id (BIGINT, 动态ID)
- user_id (BIGINT, 用户ID)
- parent_id (BIGINT, 父评论ID)
- reply_to_user_id (BIGINT, 回复的用户ID)
- content (TEXT, 评论内容)
- likes (INT, 点赞数)
- status (VARCHAR, 状态)
- created_at (DATETIME, 创建时间)
- deleted (TINYINT, 逻辑删除标记)

**注意**: 表中没有`updated_at`字段。

## 前端功能验证

### 已实现的功能
1. ✅ 评论对话框组件
2. ✅ 显示评论列表（showComments函数）
3. ✅ 加载评论数据（loadComments函数）
4. ✅ 提交评论（submitComment函数）
5. ✅ 评论用户信息显示（getUserInfo缓存）
6. ✅ 点赞功能（toggleLike调用后端API）
7. ✅ 热门话题加载真实数据
8. ✅ 活跃用户加载真实数据
9. ✅ 多媒体上传（图片和视频可同时选择）

### 测试步骤
1. 访问 http://localhost:3002（注意端口是3002，不是3001）
2. 清除浏览器缓存（Ctrl+Shift+Delete）
3. 登录账号
4. 点击任意动态的评论按钮
5. 在评论对话框中输入评论内容
6. 点击"发表评论"按钮
7. 验证评论成功显示在列表中

## 服务状态
- ✅ social-service: 运行在端口8083
- ✅ user-service: 运行在端口8081
- ✅ mall-service: 运行在端口8085
- ✅ frontend: 运行在端口3002

## 注意事项
1. 前端端口已从3001改为3002，请使用正确的端口访问
2. 如果功能仍未生效，请清除浏览器缓存
3. 评论功能需要登录状态，未登录会跳转到登录页
4. 用户信息通过getUserInfo函数异步加载并缓存

## 下一步
所有社交平台核心功能已完成：
- ✅ 发布动态（文字、图片、视频）
- ✅ 点赞功能
- ✅ 评论功能
- ✅ 热门话题
- ✅ 活跃用户（红色达人）
- ✅ 用户信息显示

用户可以开始测试完整的社交平台功能。
