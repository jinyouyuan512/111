@echo off
chcp 65001 >nul
echo ========================================
echo    n8n 工作流引擎启动脚本
echo ========================================
echo.

:: 检查 Docker 是否安装
docker --version >nul 2>&1
if %errorlevel% equ 0 (
    echo [√] Docker 已安装
    goto :docker_start
) else (
    echo [!] Docker 未安装，尝试使用 npm 方式启动
    goto :npm_start
)

:docker_start
echo.
echo 正在检查 n8n 容器状态...

:: 检查容器是否存在
docker ps -a --filter "name=n8n" --format "{{.Names}}" | findstr /i "n8n" >nul 2>&1
if %errorlevel% equ 0 (
    echo [i] 发现已有 n8n 容器
    
    :: 检查是否运行中
    docker ps --filter "name=n8n" --format "{{.Names}}" | findstr /i "n8n" >nul 2>&1
    if %errorlevel% equ 0 (
        echo [√] n8n 容器正在运行
    ) else (
        echo [i] 正在启动 n8n 容器...
        docker start n8n
    )
) else (
    echo [i] 首次运行，正在创建 n8n 容器...
    docker run -d ^
        --name n8n ^
        -p 5678:5678 ^
        -v %USERPROFILE%\.n8n:/home/node/.n8n ^
        -e N8N_BASIC_AUTH_ACTIVE=true ^
        -e N8N_BASIC_AUTH_USER=admin ^
        -e N8N_BASIC_AUTH_PASSWORD=jiyi123456 ^
        -e GENERIC_TIMEZONE=Asia/Shanghai ^
        n8nio/n8n
)

goto :success

:npm_start
echo.
echo 检查 n8n 是否已安装...
where n8n >nul 2>&1
if %errorlevel% equ 0 (
    echo [√] n8n 已安装
    echo [i] 正在启动 n8n...
    start cmd /k "n8n start"
    goto :success
) else (
    echo [!] n8n 未安装
    echo.
    echo 请选择安装方式：
    echo 1. 使用 npm 安装 n8n
    echo 2. 退出
    echo.
    set /p choice=请输入选项 (1/2): 
    
    if "%choice%"=="1" (
        echo.
        echo 正在安装 n8n...
        npm install n8n -g
        if %errorlevel% equ 0 (
            echo [√] n8n 安装成功
            echo [i] 正在启动 n8n...
            start cmd /k "n8n start"
            goto :success
        ) else (
            echo [×] n8n 安装失败
            goto :end
        )
    ) else (
        goto :end
    )
)

:success
echo.
echo ========================================
echo    n8n 启动成功！
echo ========================================
echo.
echo 访问地址: http://localhost:5678
echo 用户名: admin
echo 密码: jiyi123456
echo.
echo 下一步：
echo 1. 打开浏览器访问 http://localhost:5678
echo 2. 导入 n8n-workflows 目录下的工作流文件
echo 3. 激活工作流（点击右上角开关）
echo.

:: 等待几秒后打开浏览器
timeout /t 5 /nobreak >nul
start http://localhost:5678

:end
echo.
pause
