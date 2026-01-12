# 启动所有服务的 PowerShell 脚本

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "冀忆红途 - 启动所有服务" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查 MySQL
Write-Host "检查 MySQL..." -ForegroundColor Yellow
$mysqlRunning = netstat -ano | Select-String "3306"
if ($mysqlRunning) {
    Write-Host "✓ MySQL 正在运行" -ForegroundColor Green
} else {
    Write-Host "✗ MySQL 未运行，请先启动 MySQL" -ForegroundColor Red
    exit 1
}

# 检查 Redis
Write-Host "检查 Redis..." -ForegroundColor Yellow
$redisRunning = docker ps | Select-String "jiyi-redis"
if ($redisRunning) {
    Write-Host "✓ Redis 正在运行" -ForegroundColor Green
} else {
    Write-Host "启动 Redis..." -ForegroundColor Yellow
    docker start jiyi-redis
    if ($?) {
        Write-Host "✓ Redis 启动成功" -ForegroundColor Green
    } else {
        Write-Host "✗ Redis 启动失败" -ForegroundColor Red
        Write-Host "请运行: docker run -d --name jiyi-redis -p 6379:6379 redis" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "启动后端服务..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 启动 User Service
Write-Host "启动 User Service (端口 8081)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/user-service; mvn spring-boot:run"
Start-Sleep -Seconds 2

# 启动 Mall Service
Write-Host "启动 Mall Service (端口 8085)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/mall-service; mvn spring-boot:run"
Start-Sleep -Seconds 2

# 启动 Social Service
Write-Host "启动 Social Service (端口 8086)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/social-service; mvn spring-boot:run"
Start-Sleep -Seconds 2

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "等待服务启动..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "User Service 启动中... (约 30 秒)" -ForegroundColor Yellow
Write-Host "Mall Service 启动中... (约 30 秒)" -ForegroundColor Yellow
Write-Host "Social Service 启动中... (约 30 秒)" -ForegroundColor Yellow
Write-Host ""
Write-Host "请等待所有服务启动完成后再启动前端..." -ForegroundColor Yellow
Write-Host ""

# 等待用户确认
Read-Host "按 Enter 键启动前端服务"

# 启动前端
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "启动前端服务..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "启动 Frontend (端口 3000)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd frontend; npm run dev"

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "所有服务启动完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "访问地址:" -ForegroundColor Cyan
Write-Host "  前端首页: http://localhost:3000" -ForegroundColor White
Write-Host "  管理后台: http://localhost:3000/admin" -ForegroundColor White
Write-Host ""
Write-Host "管理员账号:" -ForegroundColor Cyan
Write-Host "  用户名: admin" -ForegroundColor White
Write-Host "  密码: admin123" -ForegroundColor White
Write-Host ""
Write-Host "服务端口:" -ForegroundColor Cyan
Write-Host "  User Service: 8081" -ForegroundColor White
Write-Host "  Mall Service: 8085" -ForegroundColor White
Write-Host "  Social Service: 8086" -ForegroundColor White
Write-Host "  Frontend: 3000" -ForegroundColor White
Write-Host ""
Write-Host "按 Ctrl+C 退出此脚本（不会停止服务）" -ForegroundColor Yellow
Write-Host ""

# 保持脚本运行
Read-Host "按 Enter 键退出"
