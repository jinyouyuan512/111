@echo off
chcp 65001 >nul
echo ========================================
echo 🌐 打开前端页面
echo ========================================
echo.

echo 正在打开浏览器...
echo 地址: http://localhost:3002
echo.

start http://localhost:3002

echo.
echo ✅ 浏览器已打开
echo.
echo 📌 测试步骤：
echo   1. 等待页面加载完成
echo   2. 点击导航栏的"众创空间"
echo   3. 点击任意作品卡片
echo   4. 查看作品详情弹窗
echo   5. 确认图片正常显示
echo.
echo ⚠️  如果图片仍然不显示：
echo   - 按 F12 打开开发者工具
echo   - 查看 Console 标签的错误信息
echo   - 查看 Network 标签的请求详情
echo.
pause
