@echo off
chcp 65001 >nul
echo ========================================
echo 首页轮播图片下载工具
echo ========================================
echo.
echo 此脚本将下载占位图片到 public/images/hero/ 目录
echo 这些是临时占位图片，建议后续替换为真实的河北红色景点图片
echo.
echo 按任意键开始下载...
pause >nul

echo.
echo 正在创建目录...
if not exist "public\images\hero" mkdir "public\images\hero"

echo.
echo 正在下载图片...
echo.

echo [1/4] 下载西柏坡占位图片...
curl -L "https://images.unsplash.com/photo-1590069261209-f8e9b8642343?w=1920&q=80" -o "public\images\hero\xibaipo.jpg"

echo [2/4] 下载狼牙山占位图片...
curl -L "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&q=80" -o "public\images\hero\langyashan.jpg"

echo [3/4] 下载冉庄占位图片...
curl -L "https://images.unsplash.com/photo-1564760055775-d63b17a55c44?w=1920&q=80" -o "public\images\hero\ranzhuang.jpg"

echo [4/4] 下载李大钊故居占位图片...
curl -L "https://images.unsplash.com/photo-1547448415-e9f5b28e570d?w=1920&q=80" -o "public\images\hero\lidazhao.jpg"

echo.
echo ========================================
echo 下载完成！
echo ========================================
echo.
echo 图片已保存到：public\images\hero\
echo.
echo 下一步操作：
echo 1. 打开 src\views\Home.vue
echo 2. 找到 "const USE_LOCAL_IMAGES = false"
echo 3. 修改为 "const USE_LOCAL_IMAGES = true"
echo 4. 保存文件，浏览器会自动刷新
echo.
echo ⚠️ 注意：这些是占位图片，建议替换为真实的河北红色景点图片
echo.
pause
