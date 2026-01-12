@echo off
chcp 65001 >nul
echo ========================================
echo   重启 Academy Service
echo ========================================
echo.

echo [1/3] 停止现有服务...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8088"') do (
    taskkill /F /PID %%a 2>nul
)
timeout /t 2 /nobreak >nul

echo [2/3] 编译项目...
cd backend\academy-service
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo [3/3] 启动服务...
start "Academy Service" cmd /c "mvn spring-boot:run"

echo.
echo ========================================
echo   Academy Service 正在启动...
echo   端口: 8088
echo   等待 15 秒后测试...
echo ========================================
timeout /t 15 /nobreak

echo.
echo 测试 API...
curl -s http://localhost:8088/api/academy/news | findstr "success"
if %errorlevel% equ 0 (
    echo.
    echo ✓ 服务启动成功！
) else (
    echo.
    echo × 服务可能还在启动中，请稍后再试
)

echo.
pause
