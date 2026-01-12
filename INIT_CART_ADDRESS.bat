@echo off
echo ========================================
echo 初始化购物车和地址表
echo ========================================

echo.
echo 正在执行数据库迁移...
mysql -u root -p123456 < backend\mall-service\src\main\resources\db\migration\V2__add_cart_address_favorite.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo 购物车和地址表初始化成功！
    echo ========================================
) else (
    echo.
    echo ========================================
    echo 初始化失败，请检查MySQL是否运行
    echo ========================================
)

pause
