@echo off
chcp 65001 >nul
echo ========================================
echo 验证创意上传修复
echo ========================================
echo.

echo [1/3] 检查服务状态...
echo.
netstat -ano | findstr :3001 >nul
if %errorlevel% equ 0 (
    echo ✓ 前端服务运行中 (3001)
) else (
    echo ✗ 前端服务未运行
    echo   需要重启前端服务以应用修复
    echo   cd frontend ^&^& npm run dev
)

netstat -ano | findstr :8087 >nul
if %errorlevel% equ 0 (
    echo ✓ 创意服务运行中 (8087)
) else (
    echo ✗ 创意服务未运行
)
echo.

echo [2/3] 打开测试工具...
echo.
echo 正在打开调试工具...
start debug_creative_request.html
timeout /t 2 >nul
echo.

echo [3/3] 测试步骤...
echo.
echo 请按照以下步骤测试：
echo.
echo 1. 在打开的调试工具中，点击"发送测试请求"
echo    - 如果成功，会显示 HTTP 状态码: 200
echo    - 如果失败，会显示详细错误信息
echo.
echo 2. 或者访问实际页面测试：
echo    http://localhost:3001/creative/upload
echo    - 填写表单
echo    - 上传文件
echo    - 点击提交
echo    - 查看浏览器 Console 的"提交数据"日志
echo.
echo 3. 如果前端服务未重启，请运行：
echo    cd frontend
echo    npm run dev
echo.

echo ========================================
echo 相关文档
echo ========================================
echo.
echo - CREATIVE_400_ERROR_FIX.md       (修复说明)
echo - TEST_CREATIVE_UPLOAD_NOW.md     (诊断指南)
echo - debug_creative_request.html     (调试工具)
echo.
pause
