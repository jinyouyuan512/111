@echo off
echo ========================================
echo 初始化社区服务数据库
echo ========================================

echo.
echo 正在执行数据库初始化...
mysql -u root -p123456 < backend\social-service\src\main\resources\db\schema.sql

if %errorlevel% equ 0 (
    echo.
    echo 正在插入测试数据...
    mysql -u root -p123456 < backend\social-service\src\main\resources\db\data.sql
    
    if %errorlevel% equ 0 (
        echo.
        echo ========================================
        echo 社区服务数据库初始化成功！
        echo ========================================
    ) else (
        echo.
        echo ========================================
        echo 测试数据插入失败
        echo ========================================
    )
) else (
    echo.
    echo ========================================
    echo 数据库初始化失败，请检查MySQL是否运行
    echo ========================================
)

pause
