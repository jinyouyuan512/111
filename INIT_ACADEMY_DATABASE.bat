@echo off
chcp 65001 >nul
echo ========================================
echo 初始化传承学院数据库
echo ========================================
echo.

echo 请确保 MySQL 正在运行...
echo.

echo 执行数据库初始化脚本...
echo.

mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS jiyi_academy CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo 创建表结构...
mysql -u root -proot jiyi_academy < backend\academy-service\src\main\resources\db\schema_news_book.sql

echo 导入初始数据...
mysql -u root -proot jiyi_academy < backend\academy-service\src\main\resources\db\data_news_book.sql

echo.
echo ========================================
echo 数据库初始化完成！
echo ========================================
echo.
echo 现在可以运行 START_ACADEMY_SERVICE.bat 启动服务
echo.
pause
