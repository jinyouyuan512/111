@echo off
chcp 65001 >nul
echo ========================================
echo 🚀 一键打开测试页面
echo ========================================
echo.

echo 前端地址: http://localhost:3001
echo.

echo [1/2] 打开众创空间...
start http://localhost:3001/creative
timeout /t 2 >nul

echo [2/2] 打开诊断工具...
start http://localhost:3001/test_creative_detail_media.html

echo.
echo ========================================
echo ✅ 页面已打开
echo ========================================
echo.
echo 📋 测试步骤：
echo 1. 在众创空间页面按 F12 打开控制台
echo 2. 点击任意作品查看详情
echo 3. 查看控制台是否有 "=== 获取媒体URL ===" 日志
echo 4. 查看详情弹窗是否显示图片/视频
echo.
echo 💡 如果没有变化：
echo 1. 按 Ctrl+Shift+Delete 清除缓存
echo 2. 或按 Ctrl+F5 强制刷新
echo 3. 关闭所有标签重新打开
echo.
pause
