@echo off
chcp 65001 >nul
color 0A
cls
echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║                                                            ║
echo ║          🎨 众创空间 - 作品显示功能测试                    ║
echo ║                                                            ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo.
echo  📋 测试准备:
echo  ────────────────────────────────────────────────────
echo   ✅ 前端服务: http://localhost:3001
echo   ✅ 创意服务: http://localhost:8087
echo   ✅ 数据库: 7 条作品记录
echo   ✅ 代码版本: 2025-01-04 15:30
echo.
echo.
echo  🎯 即将执行:
echo  ────────────────────────────────────────────────────
echo   1. 打开 API 测试页面 (验证后端数据)
echo   2. 打开众创空间页面 (Chrome 无痕模式)
echo.
echo.
echo  ✨ 预期结果:
echo  ────────────────────────────────────────────────────
echo   ✓ 测试页面显示: "成功加载 7 个作品"
echo   ✓ 众创空间右上角: "版本: 2025-01-04 15:30 | 作品数: 7"
echo   ✓ 页面显示: "全部作品 7"
echo   ✓ 7 个作品卡片正常显示
echo.
echo.
pause
echo.
echo  🚀 开始测试...
echo.

echo  [1/2] 打开 API 测试页面...
start "" "test_creative_works_display.html"
timeout /t 3 /nobreak >nul

echo  [2/2] 打开众创空间页面 (无痕模式)...
start chrome --disable-cache --disable-application-cache --incognito "http://localhost:3001/creative" 2>nul

if errorlevel 1 (
    echo  ⚠️  Chrome 未找到，使用默认浏览器...
    start "" "http://localhost:3001/creative"
)

echo.
echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║                                                            ║
echo ║                    ✅ 测试页面已打开                        ║
echo ║                                                            ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo  📝 请检查:
echo  ────────────────────────────────────────────────────
echo   1. 测试页面是否显示 7 个作品
echo   2. 众创空间页面右上角是否有版本号
echo   3. 是否显示 "全部作品 7"
echo.
echo  🔍 调试方法:
echo  ────────────────────────────────────────────────────
echo   • 按 F12 打开开发者工具
echo   • 查看 Console 标签页的日志
echo   • 查找 "=== 众创空间页面已加载" 的消息
echo.
echo  💡 如果看不到作品:
echo  ────────────────────────────────────────────────────
echo   • 按 Ctrl+Shift+Delete 清除浏览器缓存
echo   • 或使用无痕模式: Ctrl+Shift+N
echo   • 或按 Ctrl+F5 强制刷新
echo.
echo.
echo  📖 详细说明请查看: ✅作品显示问题_最终解决方案.md
echo.
echo ════════════════════════════════════════════════════════════
pause
