# 🖼️ 首页轮播图片快速设置指南

## 📋 当前状态

✅ 图片目录已创建：`frontend/public/images/hero/`  
✅ 代码已配置：支持本地图片和在线图片切换  
⚠️ 需要操作：添加真实的河北红色景点图片

---

## 🚀 快速设置步骤

### 步骤 1：准备图片

下载或拍摄以下 4 张河北红色景点图片：

| 景点 | 文件名 | 推荐内容 |
|------|--------|----------|
| 西柏坡革命圣地 | `xibaipo.jpg` | 西柏坡纪念馆、七届二中全会会址 |
| 狼牙山五壮士 | `langyashan.jpg` | 狼牙山主峰、五壮士纪念塔 |
| 冉庄地道战遗址 | `ranzhuang.jpg` | 地道入口、地道战纪念馆 |
| 李大钊故居 | `lidazhao.jpg` | 李大钊纪念馆、故居建筑 |

**图片要求：**
- 尺寸：1920x1080 或更高（16:9 比例）
- 格式：JPG 或 PNG
- 大小：每张不超过 500KB（建议压缩）

### 步骤 2：放置图片

将 4 张图片复制到以下目录：
```
frontend/public/images/hero/
```

完成后目录结构应该是：
```
frontend/public/images/hero/
├── xibaipo.jpg
├── langyashan.jpg
├── ranzhuang.jpg
└── lidazhao.jpg
```

### 步骤 3：启用本地图片

打开 `frontend/src/views/Home.vue` 文件，找到第 638 行左右：

```javascript
const USE_LOCAL_IMAGES = false // 当前使用占位图片
```

修改为：

```javascript
const USE_LOCAL_IMAGES = true // 使用本地真实图片
```

### 步骤 4：查看效果

保存文件后，浏览器会自动刷新，您将看到真实的河北红色景点图片！

---

## 🎨 图片获取建议

### 官方来源（推荐）
1. **河北省文化和旅游厅官网**
   - 网址：http://wlt.hebei.gov.cn/
   - 可能有官方授权图片

2. **各景点官方网站**
   - 西柏坡纪念馆官网
   - 狼牙山景区官网
   - 冉庄地道战遗址官网
   - 李大钊纪念馆官网

### 版权图库（需购买）
- 视觉中国（VCG）
- 全景网
- 站酷海洛

### 免费图库（需确认授权）
- Unsplash（当前占位图片来源）
- Pexels
- Pixabay

---

## 🛠️ 图片优化工具

### 在线压缩工具
- **TinyPNG**：https://tinypng.com/
- **Squoosh**：https://squoosh.app/
- **Compressor.io**：https://compressor.io/

### 批量处理工具
- **XnConvert**（免费）
- **Adobe Photoshop**（批处理）
- **ImageMagick**（命令行）

---

## ⚠️ 重要提醒

### 版权问题
- ✅ 确保图片有合法使用权
- ✅ 优先使用官方授权图片
- ✅ 购买商业图库授权
- ❌ 避免直接使用网络搜索的图片

### 性能优化
- 压缩图片到 500KB 以内
- 考虑使用 WebP 格式
- 为移动端提供小尺寸版本

---

## 🔧 故障排除

### 图片不显示？

1. **检查文件名**：确保文件名完全匹配（区分大小写）
2. **检查路径**：图片必须在 `frontend/public/images/hero/` 目录
3. **检查开关**：确认 `USE_LOCAL_IMAGES = true`
4. **清除缓存**：按 Ctrl+Shift+R 强制刷新浏览器
5. **检查控制台**：打开浏览器开发者工具查看错误信息

### 图片加载慢？

1. 压缩图片大小
2. 使用 WebP 格式
3. 考虑使用 CDN

---

## 📞 需要帮助？

如果遇到问题，请检查：
1. `frontend/IMAGE_GUIDE.md` - 详细使用指南
2. `frontend/public/images/hero/README.md` - 图片目录说明
3. 浏览器开发者工具控制台 - 查看错误信息

---

**最后更新：** 2024年12月16日  
**版本：** 1.0
