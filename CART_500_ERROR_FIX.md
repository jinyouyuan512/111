# 购物车500错误修复指南

## 问题描述
加入购物车时出现500 Internal Server Error

## 根本原因
1. 购物车表(cart)可能未创建
2. MyBatis Plus自动填充处理器缺失

## 修复步骤

### 1. 初始化购物车表 ✅ 已完成
运行以下命令创建购物车、地址、收藏等表：
```bash
INIT_CART_ADDRESS.bat
```

或手动执行SQL：
```bash
mysql -u root -p123456 < backend\mall-service\src\main\resources\db\migration\V2__add_cart_address_favorite.sql
```

### 2. 重启mall-service ⚠️ 必须执行
创建了MyMetaObjectHandler后**必须重启**服务才能生效：

**方法1：使用重启脚本（推荐）**
```bash
RESTART_MALL_SERVICE.bat
```

**方法2：手动重启**
1. 找到运行mall-service的命令行窗口
2. 按 Ctrl+C 停止服务
3. 重新运行：
```bash
cd backend/mall-service
mvn spring-boot:run
```

**重要：** 等待服务完全启动（约30秒）后再测试购物车功能

## 已修复的问题
1. ✅ 创建了MyMetaObjectHandler处理createdAt和updatedAt自动填充
2. ✅ 修复了Mall.vue中qty变量作用域问题
3. ✅ 添加了X-User-Id请求头
4. ✅ 修复了userStore使用方式

## 验证步骤
1. 确保mall-service正常运行
2. 登录系统
3. 在商城页面点击"加入购物车"
4. 应该看到成功提示消息

## 相关文件
- `backend/mall-service/src/main/java/com/jiyi/mall/config/MyMetaObjectHandler.java` - 新建
- `backend/mall-service/src/main/resources/db/migration/V2__add_cart_address_favorite.sql` - 数据库迁移
- `INIT_CART_ADDRESS.bat` - 快速初始化脚本
- `frontend/src/views/Mall.vue` - 前端修复
- `frontend/src/api/request.ts` - 请求拦截器修复
