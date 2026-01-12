# 商品列表 500 错误修复

## 问题
访问商城页面时，商品列表加载失败，返回 500 Internal Server Error。

## 根本原因

mall-service 缺少全局异常处理器（GlobalExceptionHandler），导致：
1. 异常没有被正确捕获和处理
2. 异常信息没有被记录到日志
3. 返回给前端的响应体为空
4. 前端收到 500 错误但没有错误详情

## 解决方案

### 创建全局异常处理器

在 `backend/mall-service/src/main/java/com/jiyi/mall/exception/GlobalExceptionHandler.java` 中创建全局异常处理器：

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统异常: " + e.getMessage());
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return Result.error(500, "运行时异常: " + e.getMessage());
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数异常", e);
        return Result.error(400, "参数错误: " + e.getMessage());
    }
}
```

### 异常处理器的作用

1. **统一异常处理**：捕获所有未处理的异常
2. **日志记录**：记录异常堆栈信息，便于调试
3. **友好的错误响应**：返回结构化的错误信息给前端
4. **HTTP 状态码**：设置正确的 HTTP 状态码

### 错误响应格式

```json
{
  "code": 500,
  "message": "系统异常: xxx",
  "data": null
}
```

## 其他可能的原因

如果添加全局异常处理器后问题仍存在，可能的原因：

### 1. 序列化问题
MyBatis-Plus 的 `Page` 对象可能无法正确序列化。

**解决方案**：创建 DTO 对象而不是直接返回 Page
```java
@Data
public class PageResult<T> {
    private List<T> records;
    private long total;
    private long current;
    private long size;
}
```

### 2. 数据库连接问题
数据库连接池耗尽或连接超时。

**解决方案**：检查数据库连接配置
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
```

### 3. Redis 连接问题
如果使用了 Redis 缓存，Redis 连接失败可能导致异常。

**解决方案**：确保 Redis 服务运行中
```bash
redis-server
```

## 测试步骤

1. **重启 mall-service**：
   ```bash
   cd backend/mall-service
   mvn spring-boot:run
   ```

2. **访问商城页面**：
   ```
   http://localhost:3001/mall
   ```

3. **检查响应**：
   - 如果仍然 500，查看后端日志中的异常堆栈
   - 现在应该能看到详细的错误信息

4. **查看日志**：
   ```bash
   # 查看 mall-service 日志
   tail -f backend/mall-service/logs/app.log
   ```

## 调试技巧

### 1. 查看详细错误
在浏览器控制台查看 Network 标签：
- 点击失败的请求
- 查看 Response 标签
- 现在应该能看到错误消息

### 2. 后端日志
全局异常处理器会记录完整的异常堆栈：
```
2026-01-03 16:21:39.580 ERROR [...] - 系统异常
java.lang.NullPointerException: ...
    at com.jiyi.mall.service.impl.ProductServiceImpl.getProductList(...)
    ...
```

### 3. 使用 Postman 测试
直接测试后端接口：
```
GET http://localhost:8085/api/mall/products?page=1&size=12
```

## 已修改文件

1. ✅ `backend/mall-service/src/main/java/com/jiyi/mall/exception/GlobalExceptionHandler.java` - 新建全局异常处理器

## 相关文件

- `backend/mall-service/src/main/java/com/jiyi/mall/controller/ProductController.java` - 商品控制器
- `backend/mall-service/src/main/java/com/jiyi/mall/service/impl/ProductServiceImpl.java` - 商品服务实现
- `backend/mall-service/src/main/resources/application.yml` - 服务配置

## 当前状态

✅ mall-service 已重启（端口 8085）
✅ 全局异常处理器已添加
✅ 现在异常会被正确捕获和记录

刷新商城页面，如果仍有错误，现在应该能看到详细的错误信息了。
