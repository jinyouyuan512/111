@echo off
echo ========================================
echo 商城和社区服务完整初始化脚本
echo ========================================
echo.

echo [步骤 1/6] 检查 MySQL 是否运行...
netstat -an | findstr "3306" >nul
if errorlevel 1 (
    echo [错误] MySQL 未运行！请先启动 MySQL
    pause
    exit /b 1
)
echo [成功] MySQL 正在运行

echo.
echo [步骤 2/6] 检查 Redis 是否运行...
netstat -an | findstr "6379" >nul
if errorlevel 1 (
    echo [警告] Redis 未运行！正在启动 Docker Redis...
    docker start jiyi-redis
    timeout /t 5 /nobreak >nul
)
echo [成功] Redis 正在运行

echo.
echo [步骤 3/6] 创建商城数据库...
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS jiyi_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
if errorlevel 1 (
    echo [错误] 创建商城数据库失败
    pause
    exit /b 1
)
echo [成功] 商城数据库已创建

echo.
echo [步骤 4/6] 初始化商城数据库表和数据...
mysql -u root -proot jiyi_mall < backend\mall-service\src\main\resources\db\migration\V1__init_mall.sql
if errorlevel 1 (
    echo [错误] 初始化商城数据库失败
    pause
    exit /b 1
)
echo [成功] 商城数据库已初始化

echo.
echo [步骤 5/6] 创建社区数据库...
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS jiyi_social CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
if errorlevel 1 (
    echo [错误] 创建社区数据库失败
    pause
    exit /b 1
)
echo [成功] 社区数据库已创建

echo.
echo [步骤 6/6] 初始化社区数据库表和数据...
type backend\social-service\src\main\resources\db\schema.sql backend\social-service\src\main\resources\db\data.sql | mysql -u root -proot jiyi_social
if errorlevel 1 (
    echo [错误] 初始化社区数据库失败
    pause
    exit /b 1
)
echo [成功] 社区数据库已初始化

echo.
echo ========================================
echo 初始化完成！
echo ========================================
echo.
echo 数据库状态:
mysql -u root -proot -e "SHOW DATABASES LIKE 'jiyi%%';"
echo.
echo 商城商品数量:
mysql -u root -proot -e "USE jiyi_mall; SELECT COUNT(*) as product_count FROM product WHERE deleted = 0;"
echo.
echo 社区话题数量:
mysql -u root -proot -e "USE jiyi_social; SELECT COUNT(*) as topic_count FROM topic;"
echo.
echo 下一步:
echo 1. 启动商城服务: cd backend\mall-service ^&^& mvn spring-boot:run
echo 2. 启动社区服务: cd backend\social-service ^&^& mvn spring-boot:run
echo.
pause
