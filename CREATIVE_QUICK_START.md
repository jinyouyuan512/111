# 众创空间快速启动指南

## 快速启动步骤

### 1. 启动数据库

确保MySQL正在运行，并初始化数据库：

```bash
# 初始化众创空间数据库
mysql -u root -p < backend/creative-service/src/main/resources/db/schema.sql
mysql -u root -p < backend/creative-service/src/main/resources/db/data.sql
```

### 2. 启动后端服务

```bash
cd backend/creative-service
mvn spring-boot:run
```

服务将在 http://localhost:8087 启动

### 3. 启动前端服务

```bash
cd frontend
npm run dev
```

前端将在 http://localhost:5173 启动

### 4. 访问众创空间

浏览器访问: http://localhost:5173/creative

## 功能测试清单

### 基础功能测试

#### 1. 页面加载
- [ ] 访问众创空间页面
- [ ] 查看作品列表是否正常显示
- [ ] 检查分类标签是否显示正确的数量

#### 2. 分类筛选
- [ ] 点击"全部作品"查看所有作品
- [ ] 点击"海报设计"筛选海报类作品
- [ ] 点击"Logo设计"筛选Logo类作品
- [ ] 点击"文创产品"筛选产品类作品
- [ ] 点击"视频动画"筛选视频类作品

#### 3. 作品详情
- [ ] 点击任意作品卡片
- [ ] 查看作品详情对话框
- [ ] 检查作品信息是否完整
- [ ] 查看浏览量、票数、评论数

#### 4. 投票功能
- [ ] 在作品详情中点击"点赞作品"按钮
- [ ] 检查票数是否增加
- [ ] 按钮文字是否变为"已点赞"
- [ ] 再次点击取消点赞
- [ ] 检查票数是否减少
- [ ] 按钮文字是否恢复为"点赞作品"

#### 5. 作品上传
- [ ] 点击"上传作品"按钮
- [ ] 填写作品标题
- [ ] 填写作品描述
- [ ] 填写设计理念（可选）
- [ ] 输入封面图片URL
- [ ] 输入作品文件URL
- [ ] 点击"提交作品"
- [ ] 检查是否显示成功提示

#### 6. 我的作品
- [ ] 点击"我的作品"按钮
- [ ] 查看作品数量提示
- [ ] 如果没有作品，应显示相应提示

### API测试

#### 测试众创空间首页API

```bash
curl http://localhost:8087/api/creative/space
```

预期返回：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "contests": [...],
    "creativeCalls": [...],
    "featuredDesigns": [...]
  }
}
```

#### 测试获取作品详情

```bash
curl http://localhost:8087/api/creative/designs/1
```

#### 测试投票功能

```bash
# 投票
curl -X POST http://localhost:8087/api/creative/designs/1/vote \
  -H "X-User-Id: 1"

# 取消投票
curl -X DELETE http://localhost:8087/api/creative/designs/1/vote \
  -H "X-User-Id: 1"
```

#### 测试提交作品

```bash
curl -X POST http://localhost:8087/api/creative/designs \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "title": "测试作品",
    "description": "这是一个测试作品",
    "designConcept": "测试设计理念",
    "coverImage": "https://example.com/cover.jpg",
    "files": ["https://example.com/file1.jpg"],
    "copyrightStatement": "版权归设计师所有"
  }'
```

#### 测试获取我的作品

```bash
curl http://localhost:8087/api/creative/designs/my \
  -H "X-User-Id: 1"
```

## 常见问题

### 1. 页面显示示例数据而非真实数据

**原因**: 后端服务未启动或API调用失败

**解决方案**:
1. 检查后端服务是否正常运行
2. 检查浏览器控制台是否有错误信息
3. 确认API地址配置正确

### 2. 投票功能不工作

**原因**: 用户未登录或用户ID未传递

**解决方案**:
1. 确保用户已登录
2. 检查localStorage中是否有用户信息
3. 查看网络请求是否包含X-User-Id头

### 3. 作品上传失败

**原因**: 表单验证失败或API调用失败

**解决方案**:
1. 确保必填项已填写
2. 检查URL格式是否正确
3. 查看后端日志确认错误原因

### 4. 数据库连接失败

**原因**: MySQL未启动或配置错误

**解决方案**:
1. 启动MySQL服务
2. 检查application.yml中的数据库配置
3. 确认数据库用户名密码正确

## 开发调试

### 前端调试

1. 打开浏览器开发者工具（F12）
2. 查看Console标签页的日志输出
3. 查看Network标签页的API请求
4. 使用Vue DevTools查看组件状态

### 后端调试

1. 查看控制台日志输出
2. 使用IDE断点调试
3. 检查数据库数据是否正确
4. 使用Postman测试API接口

## 性能优化建议

### 前端优化
1. 使用虚拟滚动处理大量作品
2. 图片懒加载
3. 缓存API响应数据
4. 使用CDN加速静态资源

### 后端优化
1. 添加Redis缓存
2. 优化数据库查询
3. 使用分页加载
4. 添加索引提升查询速度

## 下一步

完成基础测试后，可以：

1. 查看 [CREATIVE_ENHANCEMENT_SUMMARY.md](CREATIVE_ENHANCEMENT_SUMMARY.md) 了解完善内容
2. 查看 [CREATIVE_SERVICE_IMPLEMENTATION.md](CREATIVE_SERVICE_IMPLEMENTATION.md) 了解实施细节
3. 查看 [backend/creative-service/README.md](backend/creative-service/README.md) 了解API文档
4. 根据实际需求进行功能扩展

## 联系支持

如遇到问题，请：
1. 查看相关文档
2. 检查日志输出
3. 联系开发团队

---

祝您使用愉快！🎨
