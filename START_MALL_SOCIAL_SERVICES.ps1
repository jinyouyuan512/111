# 商城和社区服务启动脚本
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "商城和社区服务启动脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查 MySQL
Write-Host "[1/8] 检查 MySQL..." -ForegroundColor Yellow
$mysqlRunning = netstat -an | Select-String "3306"
if ($mysqlRunning) {
    Write-Host "[成功] MySQL 正在运行" -ForegroundColor Green
} else {
    Write-Host "[错误] MySQL 未运行！" -ForegroundColor Red
    exit 1
}

# 检查 Redis
Write-Host "[2/8] 检查 Redis..." -ForegroundColor Yellow
$redisRunning = netstat -an | Select-String ":6379"
if ($redisRunning) {
    Write-Host "[成功] Redis 正在运行" -ForegroundColor Green
} else {
    Write-Host "[警告] Redis 未运行，正在启动..." -ForegroundColor Yellow
    docker start jiyi-redis
    Start-Sleep -Seconds 3
}

# 检查商城数据库
Write-Host "[3/8] 检查商城数据库..." -ForegroundColor Yellow
$mallDb = mysql -u root -proot -e "SELECT COUNT(*) FROM jiyi_mall.product WHERE deleted = 0;" 2>&1
if ($mallDb -match "20") {
    Write-Host "[成功] 商城数据库已就绪 (20个商品)" -ForegroundColor Green
} else {
    Write-Host "[警告] 商城数据库可能未初始化" -ForegroundColor Yellow
}

# 初始化社区数据库
Write-Host "[4/8] 初始化社区数据库..." -ForegroundColor Yellow
try {
    # 读取并执行 schema.sql
    $schemaContent = Get-Content "backend/social-service/src/main/resources/db/schema.sql" -Raw -Encoding UTF8
    $schemaContent | mysql -u root -proot jiyi_social 2>&1 | Out-Null
    
    # 读取并执行 data.sql
    $dataContent = Get-Content "backend/social-service/src/main/resources/db/data.sql" -Raw -Encoding UTF8
    $dataContent | mysql -u root -proot jiyi_social 2>&1 | Out-Null
    
    Write-Host "[成功] 社区数据库已初始化" -ForegroundColor Green
} catch {
    Write-Host "[警告] 社区数据库初始化可能失败: $_" -ForegroundColor Yellow
}

# 编译 Mall Service
Write-Host "[5/8] 编译 Mall Service..." -ForegroundColor Yellow
Push-Location backend/mall-service
mvn clean install -DskipTests -q
if ($LASTEXITCODE -eq 0) {
    Write-Host "[成功] Mall Service 编译完成" -ForegroundColor Green
} else {
    Write-Host "[错误] Mall Service 编译失败" -ForegroundColor Red
}
Pop-Location

# 编译 Social Service
Write-Host "[6/8] 编译 Social Service..." -ForegroundColor Yellow
Push-Location backend/social-service
mvn clean install -DskipTests -q
if ($LASTEXITCODE -eq 0) {
    Write-Host "[成功] Social Service 编译完成" -ForegroundColor Green
} else {
    Write-Host "[错误] Social Service 编译失败" -ForegroundColor Red
}
Pop-Location

# 启动 Mall Service
Write-Host "[7/8] 启动 Mall Service (端口 8085)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/mall-service; mvn spring-boot:run"
Write-Host "[信息] Mall Service 正在后台启动..." -ForegroundColor Cyan

# 启动 Social Service
Write-Host "[8/8] 启动 Social Service (端口 8086)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend/social-service; mvn spring-boot:run"
Write-Host "[信息] Social Service 正在后台启动..." -ForegroundColor Cyan

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "启动完成！" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "等待服务启动 (约30秒)..." -ForegroundColor Yellow
Write-Host ""
Write-Host "服务地址:" -ForegroundColor Cyan
Write-Host "  - User Service:   http://localhost:8081" -ForegroundColor White
Write-Host "  - Mall Service:   http://localhost:8085" -ForegroundColor White
Write-Host "  - Social Service: http://localhost:8086" -ForegroundColor White
Write-Host "  - Frontend:       http://localhost:3000" -ForegroundColor White
Write-Host ""
Write-Host "API 文档:" -ForegroundColor Cyan
Write-Host "  - User Service:   http://localhost:8081/doc.html" -ForegroundColor White
Write-Host "  - Mall Service:   http://localhost:8085/doc.html" -ForegroundColor White
Write-Host "  - Social Service: http://localhost:8086/doc.html" -ForegroundColor White
Write-Host ""
Write-Host "前端页面:" -ForegroundColor Cyan
Write-Host "  - 商城: http://localhost:3000/mall" -ForegroundColor White
Write-Host "  - 社区: http://localhost:3000/social" -ForegroundColor White
Write-Host ""
