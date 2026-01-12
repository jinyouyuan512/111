# User API and Social Post 403/500 Error Fix

## Problems
1. `/api/users/me` returning 500 Internal Server Error when no token present
2. `/api/posts` returning 403 Forbidden (权限不足，请先登录)
3. Placeholder images (via.placeholder.com) failing to load

## Root Causes

### 1. User API 500 Error
UserController's `getCurrentUser()` method was trying to extract userId from Authorization header without checking if it exists, causing NullPointerException when user is not logged in.

### 2. Social Post 403 Error
User's token is likely expired or invalid. The `@PreAuthorize("isAuthenticated()")` annotation on PostController's `createPost()` method requires valid authentication.

### 3. Placeholder Images
The via.placeholder.com service is experiencing connection issues (ERR_CONNECTION_CLOSED).

## Solutions

### 1. Fixed User API 500 Error
**File**: `backend/user-service/src/main/java/com/jiyi/user/controller/UserController.java`

Made Authorization header optional and added proper error handling:
```java
@GetMapping("/me")
public Result<UserInfo> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
    if (token == null || token.isEmpty()) {
        return Result.error(401, "未登录");
    }
    try {
        String accessToken = token.replace("Bearer ", "");
        String userId = JwtUtil.getUserIdFromToken(accessToken);
        UserInfo userInfo = userService.getUserById(Long.parseLong(userId));
        return Result.success(userInfo);
    } catch (Exception e) {
        return Result.error(401, "Token无效或已过期");
    }
}
```

### 2. Social Post 403 - User Needs to Login
The 403 error is correct behavior - users must be logged in to create posts. The issue is that:
- User's token may have expired
- User needs to login again to get a fresh token

**Solution**: User should:
1. Click "登录" button in the navigation bar
2. Login with credentials (e.g., username: chovy, password: 123456)
3. After successful login, try posting again

### 3. Placeholder Images - Already Fixed
Previous fixes already replaced via.placeholder.com with Element Plus CDN:
- Default avatar: `https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png`
- These are used in PostService and CommentService

## Testing Steps

1. **Test without login**:
   - Visit http://localhost:3001
   - Should see posts and user info without errors
   - Clicking "发布" should show login prompt

2. **Test with login**:
   - Click "登录" button
   - Login with: username=chovy, password=123456
   - After login, user avatar should appear in nav bar
   - Try creating a post - should work

3. **Test user info loading**:
   - `/api/users/me` should return 401 when not logged in (not 500)
   - `/api/users/me` should return user info when logged in

## Services Status
- user-service: http://localhost:8081 ✓ (restarted)
- social-service: http://localhost:8083 ✓
- mall-service: http://localhost:8085 ✓
- frontend: http://localhost:3001 ✓

## Next Steps
1. User should login to get a valid token
2. Test post creation after login
3. Verify user avatars display correctly
4. Check that hot topics and active users load properly
