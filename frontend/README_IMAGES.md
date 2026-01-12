# 🖼️ 首页轮播图片说明

## 当前状态
✅ 代码已配置  
⚠️ 等待添加真实图片

## 快速开始

### 方式 1：下载占位图片（测试用）
```bash
# Windows
download-placeholder-images.bat

# Mac/Linux
chmod +x download-placeholder-images.sh
./download-placeholder-images.sh
```

### 方式 2：使用真实图片（推荐）
1. 将 4 张图片放入 `public/images/hero/` 目录：
   - `xibaipo.jpg` - 西柏坡
   - `langyashan.jpg` - 狼牙山
   - `ranzhuang.jpg` - 冉庄
   - `lidazhao.jpg` - 李大钊故居

2. 打开 `src/views/Home.vue`，修改：
   ```javascript
   const USE_LOCAL_IMAGES = true
   ```

## 图片要求
- 尺寸：1920x1080（16:9）
- 格式：JPG 或 PNG
- 大小：每张 < 500KB

## 详细文档
- 📖 [快速设置指南](QUICK_IMAGE_SETUP.md)
- 📖 [完整使用指南](IMAGE_GUIDE.md)
- 📖 [工作总结](IMAGE_UPDATE_SUMMARY.md)
- 📋 [操作清单](图片更新操作清单.md)

## 前端地址
开发：`http://localhost:5173/`  
预览：`http://localhost:4173/`
