# 手动删除商城服务目录说明

## 问题说明

由于 `backend/mall-service` 目录中的文件正在被其他进程使用(可能是IDE、编辑器或构建工具),无法通过脚本自动删除。

## 手动删除步骤

### 1. 关闭所有相关程序

请关闭以下可能占用文件的程序:

- ✅ 所有IDE (IntelliJ IDEA, Eclipse, VS Code等)
- ✅ 所有文本编辑器
- ✅ 所有终端/命令行窗口
- ✅ Maven进程 (如果有正在运行的构建)
- ✅ Java进程 (如果有正在运行的服务)

### 2. 删除目录

#### 方法一: 使用文件资源管理器

1. 打开文件资源管理器
2. 导航到项目根目录: `D:\ruler\backend\`
3. 找到 `mall-service` 文件夹
4. 右键点击 -> 删除
5. 如果提示文件被占用,点击"重试"或"跳过"

#### 方法二: 使用命令行 (推荐)

打开新的PowerShell窗口,执行:

```powershell
# 进入项目目录
cd D:\ruler

# 强制删除(需要管理员权限)
Remove-Item -Path "backend\mall-service" -Recurse -Force
```

如果仍然失败,尝试:

```powershell
# 使用 robocopy 清空目录后删除
mkdir empty_temp
robocopy empty_temp backend\mall-service /MIR
rmdir empty_temp
rmdir backend\mall-service
```

#### 方法三: 重启后删除

1. 重启计算机
2. 重启后立即删除 `backend\mall-service` 目录
3. 这样可以确保没有程序占用文件

### 3. 验证删除

删除后,执行以下命令验证:

```powershell
Test-Path backend\mall-service
```

如果返回 `False`,说明删除成功。

### 4. 清理数据库 (可选)

如果需要完全清理商城相关数据:

```sql
-- 连接到MySQL
mysql -u root -p

-- 删除商城数据库
DROP DATABASE IF EXISTS jiyi_mall;

-- 退出
exit;
```

### 5. 清理Redis缓存 (可选)

```bash
# 连接到Redis
redis-cli

# 清空Database 5 (商城缓存)
SELECT 5
FLUSHDB

# 退出
exit
```

## 已完成的清理工作

✅ 前端商城页面已删除:
- `frontend/src/views/Mall.vue`
- `frontend/src/views/Cart.vue`
- `frontend/src/views/Orders.vue`

✅ 前端路由配置已更新:
- 删除了 `/mall`、`/cart`、`/orders` 路由

✅ 前端Home页面已更新:
- 删除了商城导航链接
- 删除了购物车图标
- 删除了订单按钮

✅ 后端配置已更新:
- `backend/pom.xml` 中删除了 mall-service 模块
- `docker-compose.yml` 中删除了商城数据库挂载

✅ 文档已更新:
- `README.md` 更新了项目结构
- 创建了 `DELETION_SUMMARY_MALL.md` 删除总结

## 待完成的工作

⚠️ 需要手动删除:
- `backend/mall-service/` 目录及其所有内容

## 删除后的后续步骤

1. **重新构建项目**
   ```bash
   cd backend
   mvn clean install
   ```

2. **启动服务验证**
   ```bash
   # 启动其他服务,确保没有错误
   cd backend/gateway
   mvn spring-boot:run
   ```

3. **测试前端**
   ```bash
   cd frontend
   npm run dev
   ```
   访问 http://localhost:3001 确保页面正常显示

## 常见问题

### Q: 为什么无法删除?
A: 文件正在被其他程序使用。请确保关闭所有IDE、编辑器和相关进程。

### Q: 删除后Maven构建失败?
A: 确保 `backend/pom.xml` 中已经删除了 `<module>mall-service</module>`。

### Q: 前端页面出现404错误?
A: 检查路由配置,确保没有指向 `/mall`、`/cart`、`/orders` 的链接。

### Q: 需要恢复商城功能怎么办?
A: 参考 `DELETION_SUMMARY_MALL.md` 中的"恢复方法"部分。

## 技术支持

如果遇到问题,请检查:
1. 任务管理器中是否有Java或Maven进程
2. IDE是否完全关闭
3. 是否有其他程序打开了该目录下的文件

---

**注意**: 删除 `backend/mall-service` 目录后,此文件也可以删除。
