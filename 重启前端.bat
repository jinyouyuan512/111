@echo off
echo 重启前端服务...
cd frontend
taskkill /F /IM node.exe /T 2>nul
timeout /t 2 /nobreak >nul
start "Frontend" cmd /k "npm run dev"
echo 前端服务正在重启...
echo 请等待约 10 秒后访问 http://localhost:3002
pause
