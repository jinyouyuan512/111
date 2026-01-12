# 社交平台发布403错误修复

## 问题
用户已登录，但发布动态时仍然显示403 Forbidden错误，提示"权限不足，请先登录"。

## 根本原因
PostController使用了`@PreAuthorize("isAuthenticated()")`注解来检查认证状态。这个注解依赖于Spring Security的SecurityContext中的Authentication对象。

问题在于：
1. JWT验证在SecurityConfig的filter中进行
2. 如果JWT验证失败（token过期、格式错误等），filter会静默失败，不设置Authentication
3. `@PreAuthorize("isAuthenticated()")`检查时发现没有Authentication，返回403

## 解决方案
移除`@PreAuthorize("isAuthenticated()")`注解，改为在Controller方法内部手动验证token并返回更友好的错误信息。

### 修改的文件
**backend/social-service/src/main/java/com/jiyi/social/controller/PostController.java**

### 修改内容

#### 1. 创建动态 (createPost)
```java
@Operation(summary = "创建动态")
@PostMapping
public Result<PostVO> createPost(@RequestBody PostCreateRequest request,
                                 @RequestHeader(value = "Authorization", required = false) String token) {
    if (token == null || token.isEmpty()) {
        return Result.error(401, "请先登录");
    }
    try {
        String accessToken = token.replace("Bearer ", "");
        if (!JwtUtil.validateToken(accessToken)) {
            return Result.error(401, "Token已过期，请重新登录");
        }
        String userId = JwtUtil.getUserIdFromToken(accessToken);
        PostVO post = postService.createPost(Long.parseLong(userId), request);
        return Result.success(post);
    } catch (Exception e) {
        return Result.error(401, "Token无效，请重新登录");
    }
}
```

#### 2. 删除动态 (deletePost)
```java
@Operation(summary = "删除动态")
@DeleteMapping("/{postId}")
public Result<Void> deletePost(@PathVariable Long postId,
                               @RequestHeader(value = "Authorization", required = false) String token) {
    if (token == null || token.isEmpty()) {
        return Result.error(401, "请先登录");
    }
    try {
        String accessToken = token.replace("Bearer ", "");
        String userId = JwtUtil.getUserIdFromToken(accessToken);
        postService.deletePost(postId, Long.parseLong(userId));
        return Result.success();
    } catch (Exception e) {
        return Result.error(401, "Token无效，请重新登录");
    }
}
```

#### 3. 点赞/取消点赞 (likePost/unlikePost)
同样的模式应用到点赞和取消点赞方法。

## 改进点
1. **更友好的错误信息**：
   - "请先登录" - 当没有token时
   - "Token已过期，请重新登录" - 当token过期时
   - "Token无效，请重新登录" - 当token格式错误时

2. **更好的错误处理**：
   - 使用try-catch捕获token解析异常
   - 返回401状态码而不是403
   - 前端可以根据401自动跳转到登录页

3. **可选的Authorization header**：
   - `required = false`允许请求不带token
   - 在方法内部检查并返回适当的错误

## 服务状态
✅ social-service: http://localhost:8083 (已重启)
✅ user-service: http://localhost:8081
✅ mall-service: http://localhost:8085
✅ frontend: http://localhost:3001

## 测试步骤
1. 确保已登录（localStorage中有token）
2. 访问社交平台页面
3. 点击"发布"按钮
4. 输入内容并发布
5. 应该成功创建动态

## 如果仍然失败
1. **检查token是否有效**：
   - 打开浏览器开发者工具 -> Application -> Local Storage
   - 查看token字段
   - 如果token过期，请重新登录

2. **清除缓存重新登录**：
   ```javascript
   localStorage.clear()
   ```
   然后重新登录

3. **检查网络请求**：
   - 打开开发者工具 -> Network
   - 查看POST /api/posts请求
   - 检查Request Headers中是否有Authorization字段
   - 检查Response中的错误信息

## 相关文件
- `backend/social-service/src/main/java/com/jiyi/social/controller/PostController.java`
- `backend/social-service/src/main/java/com/jiyi/social/config/SecurityConfig.java`
- `backend/common/src/main/java/com/jiyi/common/util/JwtUtil.java`
