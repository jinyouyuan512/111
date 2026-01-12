# Social Platform Complete Fix Summary

## Overview
Fixed all database-related errors preventing post creation in the social platform. The issues were related to table name mismatches, field name mismatches, and missing auto-fill configuration.

## Issues Fixed

### 1. CORS Configuration (403 Forbidden)
**Error**: `Invalid CORS request`
**Root Cause**: CORS configuration didn't include `http://localhost:3001`
**Solution**: Changed from `setAllowedOrigins()` to `setAllowedOriginPatterns("http://localhost:*")`
**File**: `backend/social-service/src/main/java/com/jiyi/social/config/SecurityConfig.java`

### 2. Database Schema Mismatch (Error 1054)
**Error**: `Unknown column 'likesCount' in 'field list'`
**Root Cause**: Entity field names didn't match database column names
**Solution**: Updated `Post.java` entity to match database:
- `likesCount` → `likes`
- `commentsCount` → `comments`
- `sharesCount` → `shares`
- `location` → `locationName`
- Added missing fields: `locationId`, `latitude`, `longitude`, `type`, `visibility`, `status`, `views`
- Marked non-database fields with `@TableField(exist = false)`: `images`, `category`, `pinned`

**Files**:
- `backend/social-service/src/main/java/com/jiyi/social/entity/Post.java`
- `backend/social-service/src/main/java/com/jiyi/social/service/PostService.java`
- `backend/social-service/src/main/java/com/jiyi/social/service/CommentService.java`

### 3. NULL Constraint Violation (Error 1048)
**Error**: `Column 'deleted' cannot be NULL`
**Root Cause**: MyBatis-Plus auto-fill annotations weren't working because no `MetaObjectHandler` was configured
**Solution**: Created `MyBatisPlusConfig.java` with `MetaObjectHandler` bean to auto-fill:
- `createdAt` (on insert)
- `updatedAt` (on insert and update)
- `deleted` (on insert, default value 0)

**File**: `backend/social-service/src/main/java/com/jiyi/social/config/MyBatisPlusConfig.java`

### 4. Table Not Found (Error 1146)
**Error**: `Table 'post_like' doesn't exist`
**Root Cause**: Database has table named `like_record` but entity was using `@TableName("post_like")`
**Solution**: Updated `PostLike.java` entity:
- Changed table name from `post_like` to `like_record`
- Updated fields to match database: `targetType` and `targetId` instead of `postId`
- Added convenience methods `getPostId()` and `setPostId()` for backward compatibility
- Updated `PostService.java` to use `targetType` and `targetId` in all like operations

**Files**:
- `backend/social-service/src/main/java/com/jiyi/social/entity/PostLike.java`
- `backend/social-service/src/main/java/com/jiyi/social/service/PostService.java`

## Database Schema

### Post Table
```sql
CREATE TABLE post (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  location_name VARCHAR(255),
  location_id BIGINT,
  latitude DOUBLE,
  longitude DOUBLE,
  type VARCHAR(50) DEFAULT 'normal',
  visibility VARCHAR(50) DEFAULT 'public',
  likes INT DEFAULT 0,
  comments INT DEFAULT 0,
  shares INT DEFAULT 0,
  views INT DEFAULT 0,
  status VARCHAR(50) DEFAULT 'published',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted INT DEFAULT 0
);
```

### Like Record Table
```sql
CREATE TABLE like_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  target_type VARCHAR(50) NOT NULL,  -- 'post' or 'comment'
  target_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL
);
```

## API Flow

### Create Post
1. **Frontend** (`Social.vue`):
   - User fills in post form (content, location, category, images, tags)
   - Calls `socialApi.createPost()` with request data

2. **API Request** (`social.ts`):
   ```typescript
   POST /api/posts
   {
     content: string,
     location?: string,
     category: string,
     images?: string[],
     title?: string,
     video?: object,
     tags?: string[]
   }
   ```

3. **Backend** (`PostController.java`):
   - Receives `PostCreateRequest` with: `content`, `location`, `category`, `images`
   - Extracts userId from JWT token
   - Calls `postService.createPost()`

4. **Service** (`PostService.java`):
   - Creates `Post` entity with required fields
   - Sets default values: `type='normal'`, `visibility='public'`, `status='published'`
   - Sets counters to 0: `likes=0`, `comments=0`, `shares=0`, `views=0`
   - Auto-fill handles: `createdAt`, `updatedAt`, `deleted`
   - Inserts into database
   - Returns `PostVO` with user info

### Like Post
1. **Frontend**: Calls `socialApi.likePost(postId)`
2. **Backend**: 
   - Checks if user already liked (query `like_record` with `targetType='post'` and `targetId=postId`)
   - Inserts new record with `targetType='post'` and `targetId=postId`
   - Increments post likes count

## Service Status
- **social-service**: Running on port 8083 ✓
- **mall-service**: Running on port 8085 ✓
- **user-service**: Running on port 8081 ✓
- **frontend**: Running on port 3001 ✓

## Testing

### Test Post Creation
1. Navigate to Social Platform page
2. Click "发布动态" button
3. Fill in content and location
4. Click "发布" button
5. Should see success message and new post appears in feed

### Test Like Functionality
1. Click heart icon on any post
2. Should see like count increment
3. Click again to unlike
4. Should see like count decrement

## Files Modified

### Backend
1. `backend/social-service/src/main/java/com/jiyi/social/config/SecurityConfig.java` - Fixed CORS configuration
2. `backend/social-service/src/main/java/com/jiyi/social/config/MyBatisPlusConfig.java` (NEW) - Added auto-fill handler
3. `backend/social-service/src/main/java/com/jiyi/social/entity/Post.java` - Fixed field names to match database
4. `backend/social-service/src/main/java/com/jiyi/social/entity/PostLike.java` - Fixed table name and fields
5. `backend/social-service/src/main/java/com/jiyi/social/entity/Comment.java` - Fixed field names (likesCount → likes)
6. `backend/social-service/src/main/java/com/jiyi/social/service/PostService.java` - Updated to use correct field names and table structure
7. `backend/social-service/src/main/java/com/jiyi/social/service/CommentService.java` - Fixed field mapping in convertToVO
8. `backend/social-service/src/main/java/com/jiyi/social/exception/GlobalExceptionHandler.java` (NEW) - Added global exception handling

### Frontend
- No changes required (already compatible)

## Field Mapping

### Entity → VO Mapping
The database uses simple field names, but the API returns fields with "Count" suffix:
- Database: `likes`, `comments`, `shares` → API: `likesCount`, `commentsCount`, `sharesCount`
- This mapping is handled in the `convertToVO()` methods in service classes

### Comment Entity
- Database field: `likes` (Integer)
- VO field: `likesCount` (Integer)
- Status field added: `status` (String, default: 'published')

## Next Steps
1. Test post creation with actual user login
2. Verify like/unlike functionality
3. Test comment functionality
4. Add image upload support
5. Integrate with user-service for real user info

## Notes
- The `images` field is not stored in database (marked as `@TableField(exist = false)`)
- The `category` field is not stored in database (marked as `@TableField(exist = false)`)
- User info (nickname, avatar) is currently mocked - needs integration with user-service
- The `like_record` table uses generic `targetType` and `targetId` to support both post and comment likes
