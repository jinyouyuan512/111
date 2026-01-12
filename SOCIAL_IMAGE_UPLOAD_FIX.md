# 社交平台图片上传不显示问题修复

## 问题描述
用户上传图片后发布动态，图片上传成功但发布后的动态中看不到图片。

## 根本原因
`PostService.createPost()`方法在创建动态时，只保存了动态的基本信息到`post`表，但没有将上传的图片URL保存到`media_file`表中。

虽然图片上传到了服务器（`/uploads/images/`目录），但是数据库中没有记录，所以查询动态时无法加载图片。

## 解决方案

### 修改PostService.createPost()方法
**文件**: `backend/social-service/src/main/java/com/jiyi/social/service/PostService.java`

**修改内容**:
```java
@Transactional
public PostVO createPost(Long userId, PostCreateRequest request) {
    Post post = new Post();
    post.setUserId(userId);
    post.setContent(request.getContent());
    post.setLocationName(request.getLocation());
    post.setType("normal");
    post.setVisibility("public");
    post.setStatus("published");
    post.setLikes(0);
    post.setComments(0);
    post.setShares(0);
    post.setViews(0);
    
    postMapper.insert(post);
    
    // 保存图片到media_file表
    if (request.getImages() != null && !request.getImages().isEmpty()) {
        int orderNum = 1;
        for (String imageUrl : request.getImages()) {
            MediaFile mediaFile = new MediaFile();
            mediaFile.setPostId(post.getId());
            mediaFile.setType("image");
            mediaFile.setUrl(imageUrl);
            mediaFile.setThumbnail(imageUrl); // 使用相同URL作为缩略图
            mediaFile.setOrderNum(orderNum++);
            mediaFileMapper.insert(mediaFile);
        }
    }
    
    return convertToVO(post, userId);
}
```

## 工作流程

### 完整的图片上传和发布流程：

1. **用户选择图片** → 前端触发`handleImageUpload`
2. **上传图片** → 调用`POST /api/upload/image`
3. **服务器保存** → 图片保存到`uploads/images/YYYY/MM/DD/`目录
4. **返回URL** → 返回图片访问URL（如`http://localhost:8083/uploads/images/2026/01/03/xxx.png`）
5. **前端保存URL** → 将URL添加到`newPost.images`数组
6. **用户点击发布** → 调用`POST /api/posts`，传递包含图片URL的数据
7. **创建动态记录** → 保存到`post`表
8. **保存图片记录** → 遍历`images`数组，每个URL保存一条记录到`media_file`表
9. **返回动态数据** → `convertToVO`方法从`media_file`表加载图片URL
10. **前端显示** → 动态列表中显示图片

## 数据库表结构

### post表
存储动态的基本信息：
- id, user_id, content, location_name, likes, comments, shares等

### media_file表
存储动态的媒体文件（图片/视频）：
- id, post_id, type, url, thumbnail, order_num等

## 测试步骤

1. **清除旧数据**（可选）：
   ```sql
   DELETE FROM post WHERE user_id = 5;
   DELETE FROM media_file WHERE post_id NOT IN (SELECT id FROM post);
   ```

2. **上传图片并发布**：
   - 登录系统
   - 点击"发布"按钮
   - 输入内容
   - 点击图片图标，选择图片上传
   - 等待上传完成（看到图片预览）
   - 点击"发布"按钮

3. **验证结果**：
   - 刷新页面
   - 应该能看到刚发布的动态
   - 动态中应该显示上传的图片

4. **检查数据库**：
   ```sql
   -- 查看最新的动态
   SELECT * FROM post ORDER BY created_at DESC LIMIT 1;
   
   -- 查看该动态的图片记录
   SELECT * FROM media_file WHERE post_id = (SELECT id FROM post ORDER BY created_at DESC LIMIT 1);
   ```

## 服务状态
✅ social-service: http://localhost:8083 (已重启)
✅ user-service: http://localhost:8081
✅ mall-service: http://localhost:8085
✅ frontend: http://localhost:3001

## 相关文件
- `backend/social-service/src/main/java/com/jiyi/social/service/PostService.java`
- `backend/social-service/src/main/java/com/jiyi/social/dto/PostCreateRequest.java`
- `backend/social-service/src/main/java/com/jiyi/social/entity/MediaFile.java`
- `backend/social-service/src/main/java/com/jiyi/social/mapper/MediaFileMapper.java`
- `frontend/src/views/Social.vue`

## 注意事项
1. 图片必须先上传成功，获得URL后才能发布
2. 前端的`newPost.images`数组存储的是完整的图片URL
3. 后端会将每个URL作为一条记录保存到`media_file`表
4. 查询动态时，会从`media_file`表加载所有关联的图片
5. 图片按`order_num`字段排序显示
