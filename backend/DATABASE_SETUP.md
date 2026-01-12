# 数据库设置指南

## 概述

冀忆红途平台使用以下数据库：
- **MySQL 8.0**: 主要关系型数据库，存储业务数据
- **Redis 7.0**: 缓存和会话存储
- **MongoDB 6.0**: 文档数据库，存储3D模型元数据

## 数据库架构

### MySQL数据库列表

| 数据库名称 | 服务 | 端口 | 说明 |
|-----------|------|------|------|
| jiyi_user | user-service | 8081 | 用户认证和授权 |
| jiyi_experience | experience-service | 8082 | 沉浸式数字体验馆 |
| jiyi_academy | academy-service | 8083 | 红色精神传承学院 |
| jiyi_tourism | tourism-service | 8084 | 智慧旅游服务引擎 |
| jiyi_guide | guide-service | 8085 | 景区智慧导览与LBS |
| jiyi_mall | mall-service | 8086 | 红色文创精品商城 |
| jiyi_creative | creative-service | 8087 | 创意设计众创空间 |
| jiyi_social | social-service | 8088 | 红色足迹社交平台 |

### Redis数据库分配

| Database | 服务 | 用途 |
|----------|------|------|
| 0 | user-service | 用户会话和令牌 |
| 1 | experience-service | 场景缓存 |
| 2 | academy-service | 课程和进度缓存 |
| 3 | tourism-service | 路线和景点缓存 |
| 4 | guide-service | 位置和导览缓存 |
| 5 | mall-service | 商品和购物车缓存 |
| 6 | creative-service | 作品和投票缓存 |
| 7 | social-service | 动态和社交关系缓存 |

### MongoDB集合

| 集合名称 | 服务 | 说明 |
|---------|------|------|
| scene_model_data | experience-service | 3D场景模型数据 |

## 快速启动

### 使用Docker Compose（推荐）

1. 启动所有数据库服务：
```bash
docker-compose up -d mysql redis mongodb
```

2. 等待MySQL初始化完成（约30秒）

3. 验证服务状态：
```bash
docker-compose ps
```

### 手动初始化数据库

如果需要手动初始化数据库：

1. 创建所有数据库：
```bash
mysql -u root -p < backend/init-all-databases.sql
```

2. 初始化各服务的表结构：
```bash
# 用户服务
mysql -u root -p jiyi_user < backend/user-service/src/main/resources/db/schema.sql

# 体验馆服务
mysql -u root -p jiyi_experience < backend/experience-service/src/main/resources/db/schema.sql

# 学院服务
mysql -u root -p jiyi_academy < backend/academy-service/src/main/resources/db/schema.sql

# 旅游服务
mysql -u root -p jiyi_tourism < backend/tourism-service/src/main/resources/db/schema.sql

# 导览服务
mysql -u root -p jiyi_guide < backend/guide-service/src/main/resources/db/schema.sql

# 商城服务
mysql -u root -p jiyi_mall < backend/mall-service/src/main/resources/db/schema.sql

# 众创服务
mysql -u root -p jiyi_creative < backend/creative-service/src/main/resources/db/schema.sql

# 社交服务
mysql -u root -p jiyi_social < backend/social-service/src/main/resources/db/schema.sql
```

## 数据库连接配置

### MySQL连接信息
- **主机**: localhost
- **端口**: 3306
- **用户名**: root
- **密码**: root

### Redis连接信息
- **主机**: localhost
- **端口**: 6379
- **密码**: 无

### MongoDB连接信息
- **主机**: localhost
- **端口**: 27017
- **认证**: 无

## MyBatis Plus配置

所有服务已配置MyBatis Plus，包含以下特性：

### 全局配置
- **ID类型**: 自动递增（AUTO）
- **逻辑删除**: 启用（deleted字段）
- **驼峰命名**: 启用（下划线转驼峰）
- **分页插件**: 启用（最大1000条/页）

### 自动填充
- `created_at`: 插入时自动填充当前时间
- `updated_at`: 插入和更新时自动填充当前时间

### 基础实体类
所有实体类可继承 `com.jiyi.common.entity.BaseEntity`，包含：
- `id`: 主键ID
- `createdAt`: 创建时间
- `updatedAt`: 更新时间
- `deleted`: 逻辑删除标记

## Redis配置

### 序列化配置
- **Key序列化**: StringRedisSerializer
- **Value序列化**: Jackson2JsonRedisSerializer
- **Hash Key序列化**: StringRedisSerializer
- **Hash Value序列化**: Jackson2JsonRedisSerializer

### 使用示例
```java
@Autowired
private RedisTemplate<String, Object> redisTemplate;

// 存储
redisTemplate.opsForValue().set("key", value, 30, TimeUnit.MINUTES);

// 获取
Object value = redisTemplate.opsForValue().get("key");
```

## MongoDB配置

### 文档模型
3D场景模型数据存储在MongoDB中，包含：
- 模型文件信息
- 纹理和材质数据
- 光照和相机配置
- 交互点详细数据

### 使用示例
```java
@Autowired
private SceneModelDataRepository sceneModelDataRepository;

// 保存
SceneModelData modelData = new SceneModelData();
sceneModelDataRepository.save(modelData);

// 查询
Optional<SceneModelData> data = sceneModelDataRepository.findBySceneId(sceneId);
```

## 数据库维护

### 备份
```bash
# 备份所有数据库
docker exec jiyi-mysql mysqldump -u root -proot --all-databases > backup.sql

# 备份单个数据库
docker exec jiyi-mysql mysqldump -u root -proot jiyi_user > user_backup.sql
```

### 恢复
```bash
# 恢复数据库
docker exec -i jiyi-mysql mysql -u root -proot < backup.sql
```

### 清理
```bash
# 停止并删除所有容器和数据卷
docker-compose down -v
```

## 故障排查

### MySQL连接失败
1. 检查MySQL容器是否运行：`docker ps | grep mysql`
2. 查看MySQL日志：`docker logs jiyi-mysql`
3. 验证端口映射：`netstat -an | grep 3306`

### Redis连接失败
1. 检查Redis容器是否运行：`docker ps | grep redis`
2. 测试连接：`docker exec -it jiyi-redis redis-cli ping`

### MongoDB连接失败
1. 检查MongoDB容器是否运行：`docker ps | grep mongodb`
2. 测试连接：`docker exec -it jiyi-mongodb mongosh --eval "db.version()"`

## 性能优化建议

### MySQL优化
- 为高频查询字段添加索引
- 使用连接池（HikariCP已配置）
- 定期分析慢查询日志

### Redis优化
- 设置合理的过期时间
- 使用Pipeline批量操作
- 监控内存使用情况

### MongoDB优化
- 为查询字段创建索引
- 使用投影减少数据传输
- 定期清理过期数据

## 相关文档
- [MyBatis Plus官方文档](https://baomidou.com/)
- [Spring Data Redis文档](https://spring.io/projects/spring-data-redis)
- [Spring Data MongoDB文档](https://spring.io/projects/spring-data-mongodb)
