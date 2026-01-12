# Redis 自动下载和安装脚本
# 适用于 Windows 系统

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Redis 自动下载和安装" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 配置
$redisVersion = "5.0.14.1"
$redisUrl = "https://github.com/tporadowski/redis/releases/download/v$redisVersion/Redis-x64-$redisVersion.zip"
$downloadPath = "$env:TEMP\Redis-x64-$redisVersion.zip"
$installPath = "C:\Redis"

# 检查是否已经运行
Write-Host "检查 Redis 是否已经运行..." -ForegroundColor Yellow
$redisRunning = Get-NetTCPConnection -LocalPort 6379 -ErrorAction SilentlyContinue
if ($redisRunning) {
    Write-Host "[OK] Redis 已经在运行！" -ForegroundColor Green
    Write-Host ""
    Write-Host "端口 6379 已被占用，Redis 正常运行" -ForegroundColor Green
    Write-Host ""
    pause
    exit 0
}

# 检查是否已经安装
Write-Host "检查 Redis 是否已经安装..." -ForegroundColor Yellow
if (Test-Path "$installPath\redis-server.exe") {
    Write-Host "[FOUND] Redis 已安装在 $installPath" -ForegroundColor Green
    Write-Host ""
    Write-Host "正在启动 Redis..." -ForegroundColor Yellow
    Start-Process -FilePath "$installPath\redis-server.exe" -WindowStyle Normal
    Write-Host "[OK] Redis 已启动！" -ForegroundColor Green
    Write-Host ""
    Write-Host "请保持 Redis 窗口运行" -ForegroundColor Cyan
    Write-Host ""
    pause
    exit 0
}

# 下载 Redis
Write-Host "Redis 未安装，开始下载..." -ForegroundColor Yellow
Write-Host "下载地址: $redisUrl" -ForegroundColor Gray
Write-Host "下载到: $downloadPath" -ForegroundColor Gray
Write-Host ""

try {
    # 使用 .NET WebClient 下载（支持进度显示）
    $webClient = New-Object System.Net.WebClient
    
    # 注册进度事件
    Register-ObjectEvent -InputObject $webClient -EventName DownloadProgressChanged -SourceIdentifier WebClient.DownloadProgressChanged -Action {
        $percent = $EventArgs.ProgressPercentage
        Write-Progress -Activity "下载 Redis" -Status "$percent% 完成" -PercentComplete $percent
    } | Out-Null
    
    # 开始下载
    $webClient.DownloadFile($redisUrl, $downloadPath)
    
    # 取消注册事件
    Unregister-Event -SourceIdentifier WebClient.DownloadProgressChanged -ErrorAction SilentlyContinue
    
    Write-Host "[OK] 下载完成！" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "[ERROR] 下载失败: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "请手动下载：" -ForegroundColor Yellow
    Write-Host $redisUrl -ForegroundColor Cyan
    Write-Host ""
    pause
    exit 1
}

# 解压 Redis
Write-Host "正在解压到 $installPath ..." -ForegroundColor Yellow

try {
    # 创建安装目录
    if (-not (Test-Path $installPath)) {
        New-Item -ItemType Directory -Path $installPath -Force | Out-Null
    }
    
    # 解压文件
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    [System.IO.Compression.ZipFile]::ExtractToDirectory($downloadPath, $installPath)
    
    Write-Host "[OK] 解压完成！" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "[ERROR] 解压失败: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "请手动解压 $downloadPath 到 $installPath" -ForegroundColor Yellow
    Write-Host ""
    pause
    exit 1
}

# 清理下载文件
Write-Host "清理临时文件..." -ForegroundColor Yellow
Remove-Item $downloadPath -Force -ErrorAction SilentlyContinue

# 启动 Redis
Write-Host "正在启动 Redis..." -ForegroundColor Yellow
Write-Host ""

if (Test-Path "$installPath\redis-server.exe") {
    Start-Process -FilePath "$installPath\redis-server.exe" -WindowStyle Normal
    
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "Redis 安装并启动成功！" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "安装位置: $installPath" -ForegroundColor Cyan
    Write-Host "端口: 6379" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "重要提示：" -ForegroundColor Yellow
    Write-Host "1. 请保持 Redis 窗口运行（不要关闭）" -ForegroundColor Yellow
    Write-Host "2. User Service 将在 10-30 秒内自动启动" -ForegroundColor Yellow
    Write-Host "3. 然后就可以正常登录了" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "测试账号：" -ForegroundColor Cyan
    Write-Host "  用户名: testuser" -ForegroundColor White
    Write-Host "  密码: 123456" -ForegroundColor White
    Write-Host ""
    
    # 等待几秒让 Redis 完全启动
    Write-Host "等待 Redis 启动..." -ForegroundColor Yellow
    Start-Sleep -Seconds 3
    
    # 测试 Redis 连接
    Write-Host "测试 Redis 连接..." -ForegroundColor Yellow
    $redisTest = Get-NetTCPConnection -LocalPort 6379 -ErrorAction SilentlyContinue
    if ($redisTest) {
        Write-Host "[OK] Redis 运行正常！" -ForegroundColor Green
    } else {
        Write-Host "[WARNING] 无法确认 Redis 状态，请检查 Redis 窗口" -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "下一步：访问 http://localhost:3001/login 测试登录" -ForegroundColor Cyan
    Write-Host ""
} else {
    Write-Host "[ERROR] 找不到 redis-server.exe" -ForegroundColor Red
    Write-Host ""
}

pause
