@echo off
chcp 65001 >nul
echo ========================================
echo 🚀 一键启动所有服务
echo ========================================
echo.

echo [1/3] 启动 Social Service (端口 8083)...
start "Social Service" cmd /k "cd backend\social-service && mvn spring-boot:run"
timeout /t 3 >nul

echo [2/3] 启动 Creative Service (端口 8087)...
start "Creative Service" cmd /k "cd backend\creative-service && mvn spring-boot:run"
timeout /t 3 >nul

echo [3/3] 启动前端服务 (端口 3001)...
start "Frontend" cmd /k "cd frontend && npm run dev"

echo.
echo ========================================
echo ✅ 所有服务已启动
echo ========================================
echo.
echo 📌 服务列表：
echo   - Social Service:   http://localhost:8083
echo   - Creative Service: http://localhost:8087
echo   - Frontend:         http://localhost:3001
echo.
echo 📌 等待所有服务完全启动后（约30-60秒），访问：
echo   http://localhost:3001
echo.
echo 📌 测试图片访问（替换为实际文件名）：
echo   http://localhost:8083/uploads/images/2026/01/04/xxx.png
echo.
pause
