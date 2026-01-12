# Social Platform Testing Guide

## Service Status ✓
All services are running and ready for testing:
- **social-service**: Port 8083 ✓
- **mall-service**: Port 8085 ✓
- **user-service**: Port 8081 ✓
- **frontend**: Port 3001 ✓

## All Fixes Applied ✓

### 1. CORS Configuration ✓
- Changed to `setAllowedOriginPatterns("http://localhost:*")`
- Supports all localhost ports

### 2. Database Schema Alignment ✓
- Post entity fields match database columns
- Comment entity fields match database columns
- PostLike entity uses correct table name (`like_record`)

### 3. Auto-Fill Configuration ✓
- MyBatisPlusConfig with MetaObjectHandler
- Auto-fills: `createdAt`, `updatedAt`, `deleted`

### 4. Field Mapping ✓
- Entity fields → VO fields properly mapped
- `likes` → `likesCount`
- `comments` → `commentsCount`
- `shares` → `sharesCount`

## Testing Steps

### Test 1: Create Post
1. Open browser: `http://localhost:3001`
2. Login with test account
3. Navigate to Social Platform (社区)
4. Click "发布动态" button
5. Fill in:
   - Content: "测试动态发布功能"
   - Location: "北京"
   - Category: Select any category
6. Click "发布" button
7. **Expected**: Success message, post appears in feed

### Test 2: View Posts
1. Stay on Social Platform page
2. **Expected**: See list of posts with:
   - User info (nickname, avatar)
   - Content
   - Location
   - Like/Comment/Share counts
   - Timestamp

### Test 3: Like Post
1. Click heart icon on any post
2. **Expected**: 
   - Heart icon turns red
   - Like count increments by 1
3. Click heart icon again
4. **Expected**:
   - Heart icon turns gray
   - Like count decrements by 1

### Test 4: Comment on Post
1. Click comment icon on any post
2. Enter comment text
3. Click submit
4. **Expected**:
   - Comment appears under post
   - Comment count increments by 1

### Test 5: Share Post
1. Click share icon on any post
2. **Expected**:
   - Share count increments by 1
   - Share dialog appears (if implemented)

## API Endpoints

### Create Post
```
POST http://localhost:3001/api/posts
Headers: Authorization: Bearer <token>
Body: {
  "content": "测试内容",
  "location": "北京",
  "category": "travel"
}
```

### Get Posts
```
GET http://localhost:3001/api/posts?page=1&size=10
```

### Like Post
```
POST http://localhost:3001/api/posts/{postId}/like
Headers: Authorization: Bearer <token>
```

### Unlike Post
```
DELETE http://localhost:3001/api/posts/{postId}/like
Headers: Authorization: Bearer <token>
```

### Create Comment
```
POST http://localhost:3001/api/posts/{postId}/comments
Headers: Authorization: Bearer <token>
Body: {
  "content": "评论内容"
}
```

## Database Verification

### Check Post Created
```sql
SELECT * FROM post ORDER BY created_at DESC LIMIT 5;
```

### Check Like Records
```sql
SELECT * FROM like_record WHERE target_type = 'post';
```

### Check Comments
```sql
SELECT * FROM comment ORDER BY created_at DESC LIMIT 5;
```

## Troubleshooting

### Issue: 403 Forbidden
- Check if user is logged in
- Verify Authorization header is present
- Check CORS configuration

### Issue: 400 Bad Request
- Check request body format
- Verify required fields are present
- Check database field names match entity

### Issue: 500 Internal Server Error
- Check service logs: `getProcessOutput(processId: 16)`
- Verify database connection
- Check for SQL errors

### Issue: Post not appearing
- Refresh page
- Check if post was created in database
- Verify status is 'published'

## Success Criteria
✓ Post creation works without errors
✓ Posts appear in feed immediately
✓ Like/unlike functionality works
✓ Comment functionality works
✓ All counts update correctly
✓ No console errors in browser
✓ No errors in service logs

## Next Steps After Testing
1. Add image upload support
2. Integrate with user-service for real user info
3. Add post editing functionality
4. Add post deletion confirmation
5. Implement real-time updates
6. Add pagination for comments
7. Add reply to comment functionality
