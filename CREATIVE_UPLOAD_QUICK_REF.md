# 🎨 创意作品上传 - 快速参考

## 🚀 一键启动

```bash
# 检查服务状态
DIAGNOSE_CREATIVE_UPLOAD.bat

# 访问上传页面
http://localhost:3001/creative/upload
```

## 📋 必填项检查清单

- [ ] 作品标题 (2-100字符)
- [ ] 作品分类 (海报/Logo/文创/视频)
- [ ] 作品描述 (10-1000字符)
- [ ] 封面图片 (<10MB, JPG/PNG/GIF/WEBP)
- [ ] 作品文件 (<100MB, 最多5个)

## 🔧 服务端口

| 服务 | 端口 | 用途 |
|------|------|------|
| 前端 | 3001 | 用户界面 |
| 创意 | 8087 | 作品管理 |
| 社交 | 8083 | 文件上传 |
| 用户 | 8081 | 身份认证 |

## 🐛 快速排错

### 400 错误
```
1. 检查必填字段
2. 确认文件已上传
3. 验证用户已登录
```

### 文件上传失败
```
1. 检查文件大小
2. 确认文件格式
3. 验证服务运行
```

### 无响应
```
1. 运行诊断脚本
2. 检查服务状态
3. 查看浏览器控制台
```

## 📝 测试账号

```
用户名: test1
密码: 123456
```

## 🧪 测试文件

```
test_creative_submit.html          # API测试
test_creative_file_upload.html     # 文件上传测试
DIAGNOSE_CREATIVE_UPLOAD.bat       # 服务诊断
```

## 📚 详细文档

```
CREATIVE_UPLOAD_STATUS.md          # 当前状态
CREATIVE_UPLOAD_COMPLETE_GUIDE.md  # 完整指南
CREATIVE_UPLOAD_FINAL_FIX.md       # 修复历程
```

## ✅ 状态：完全可用

所有功能已实现并测试通过，可以正常使用！

---
**更新时间**: 2026-01-04
