# 首页轮播图片使用指南

## 当前状态
首页轮播目前使用占位图片服务（picsum.photos），需要替换为真实的河北红色景点图片。

## 如何添加真实图片

### 方案一：使用本地图片（推荐）

1. **准备图片**
   - 西柏坡革命圣地图片（建议尺寸：1920x1080）
   - 狼牙山五壮士纪念地图片（建议尺寸：1920x1080）
   - 冉庄地道战遗址图片（建议尺寸：1920x1080）
   - 李大钊故居图片（建议尺寸：1920x1080）

2. **放置图片**
   将图片放入 `frontend/public/images/hero/` 目录：
   ```
   frontend/public/images/hero/
   ├── xibaipo.jpg          # 西柏坡
   ├── langyashan.jpg       # 狼牙山
   ├── ranzhuang.jpg        # 冉庄
   └── lidazhao.jpg         # 李大钊故居
   ```

3. **更新代码**
   在 `frontend/src/views/Home.vue` 中修改 `heroSlides` 数组：
   ```javascript
   const heroSlides = [
     {
       title: '西柏坡革命圣地',
       subtitle: '新中国从这里走来',
       image: '/images/hero/xibaipo.jpg'
     },
     {
       title: '狼牙山五壮士',
       subtitle: '英雄气概永垂不朽',
       image: '/images/hero/langyashan.jpg'
     },
     {
       title: '冉庄地道战遗址',
       subtitle: '人民战争的伟大创举',
       image: '/images/hero/ranzhuang.jpg'
     },
     {
       title: '李大钊故居',
       subtitle: '中国共产主义运动的先驱',
       image: '/images/hero/lidazhao.jpg'
     }
   ]
   ```

### 方案二：使用在线图片

如果使用在线图片，建议使用稳定的 CDN 服务：

```javascript
const heroSlides = [
  {
    title: '西柏坡革命圣地',
    subtitle: '新中国从这里走来',
    image: 'https://your-cdn.com/xibaipo.jpg'
  },
  // ... 其他图片
]
```

## 图片要求

- **格式**：JPG 或 PNG
- **尺寸**：建议 1920x1080 或更高（保持 16:9 比例）
- **大小**：建议每张图片不超过 500KB（优化后）
- **内容**：清晰展示景点特色，避免过暗或过亮

## 图片优化建议

1. 使用图片压缩工具（如 TinyPNG）压缩图片
2. 使用 WebP 格式可以进一步减小文件大小
3. 考虑为移动端提供较小尺寸的图片

## 获取图片来源

1. **官方网站**：各景点官方网站
2. **版权图库**：
   - 视觉中国（VCG）
   - 全景网
   - 河北省文化和旅游厅官网
3. **免费图库**（需确认版权）：
   - Unsplash
   - Pexels
   - Pixabay

## 注意事项

⚠️ **版权问题**：确保使用的图片有合法授权，避免侵权
⚠️ **图片质量**：选择高质量、有代表性的图片
⚠️ **加载性能**：优化图片大小，提升页面加载速度
