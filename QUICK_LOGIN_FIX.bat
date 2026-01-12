@echo off
echo ========================================
echo 登录状态修复工具
echo ========================================
echo.

echo 1. 打开测试页面进行登录...
echo    访问: test_login_persistence.html
echo.
echo 2. 在测试页面中:
echo    - 输入用户名: chovy
echo    - 输入密码: 123456
echo    - 点击"登录"按钮
echo    - 点击"检查存储"确认数据已保存
echo.
echo 3. 然后访问应用:
echo    http://localhost:3001
echo.

start test_login_persistence.html

echo.
echo 测试页面已打开！
echo.
echo 如果登录后状态仍然丢失，请检查:
echo - user-service 是否在运行 (端口 8081)
echo - 浏览器控制台是否有错误
echo - localStorage 是否被浏览器扩展阻止
echo.
pause
