# 创意设计众创空间服务 (Creative Service)

## 概述

创意设计众创空间服务是冀忆红途平台的核心模块之一，提供设计大赛、创意征集、作品管理、投票系统、奖励发放和设计师匹配等功能。

## 功能特性

### 1. 设计大赛管理
- 大赛列表展示
- 大赛详情查看
- 参赛作品提交
- 大赛状态管理（即将开始、进行中、投票中、已结束）

### 2. 创意征集
- 征集项目发布
- 征集详情展示
- 作品投稿管理
- 征集状态跟踪

### 3. 设计作品管理
- 作品上传提交
- 作品审核流程
- 作品展示发布
- 作品详情查看
- 我的作品管理

### 4. 投票系统
- 作品投票功能
- 取消投票功能
- 投票记录管理
- 排行榜展示

### 5. 奖励发放
- 奖金发放管理
- 版权收益管理
- 奖励记录跟踪

### 6. 设计师匹配
- 设计师资料管理
- 智能匹配算法
- 需求对接功能

## 技术栈

- **框架**: Spring Boot 3.2+
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis Plus 3.5+
- **缓存**: Redis 7.0+
- **服务注册**: Nacos 2.3+

## 数据库设计

### 核心表结构

1. **contest** - 设计大赛表
2. **creative_call** - 创意征集表
3. **design** - 设计作品表
4. **vote_record** - 投票记录表
5. **reward_record** - 奖励记录表
6. **designer_profile** - 设计师资料表
7. **design_requirement** - 设计需求表
8. **designer_match** - 设计师匹配记录表

## API 接口

### 众创空间首页
```
GET /api/creative/space
```
返回首页数据，包含进行中的大赛、开放的征集和精选作品。

### 设计大赛

#### 获取所有大赛
```
GET /api/creative/contests
```

#### 获取大赛详情
```
GET /api/creative/contests/{contestId}
```

#### 获取大赛作品列表
```
GET /api/creative/contests/{contestId}/designs
```

### 创意征集

#### 获取所有征集
```
GET /api/creative/calls
```

#### 获取征集详情
```
GET /api/creative/calls/{callId}
```

#### 获取征集作品列表
```
GET /api/creative/calls/{callId}/designs
```

### 设计作品

#### 提交作品
```
POST /api/creative/designs
Headers: X-User-Id: {userId}
Body: {
  "contestId": 1,
  "callId": null,
  "title": "作品标题",
  "description": "作品描述",
  "designConcept": "设计理念",
  "files": ["file1.jpg", "file2.pdf"],
  "coverImage": "cover.jpg",
  "copyrightStatement": "版权声明"
}
```

#### 获取作品详情
```
GET /api/creative/designs/{designId}
Headers: X-User-Id: {userId} (可选)
```

#### 获取我的作品
```
GET /api/creative/designs/my
Headers: X-User-Id: {userId}
```

#### 审核作品
```
POST /api/creative/designs/{designId}/review
Body: {
  "status": "approved",  // approved 或 rejected
  "rejectReason": "拒绝原因"
}
```

### 投票系统

#### 投票
```
POST /api/creative/designs/{designId}/vote
Headers: X-User-Id: {userId}
```

#### 取消投票
```
DELETE /api/creative/designs/{designId}/vote
Headers: X-User-Id: {userId}
```

#### 获取排行榜
```
GET /api/creative/designs/top?limit=10
```

### 奖励发放

#### 发放奖励
```
POST /api/creative/designs/{designId}/reward
Params: 
  - type: prize/royalty/bonus
  - amount: 金额
```

### 设计师匹配

#### 匹配设计师
```
GET /api/creative/requirements/{requirementId}/match
```

## 配置说明

### application.yml

```yaml
server:
  port: 8087

spring:
  application:
    name: creative-service
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/jiyi_creative?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379
      database: 6

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
```

## 部署说明

### 1. 数据库初始化

```bash
# 执行数据库脚本
mysql -u root -p < src/main/resources/db/schema.sql
mysql -u root -p < src/main/resources/db/data.sql
```

### 2. 构建项目

```bash
mvn clean package -DskipTests
```

### 3. 运行服务

```bash
java -jar target/creative-service-1.0.0.jar
```

或使用 Maven:

```bash
mvn spring-boot:run
```

### 4. 验证服务

访问: http://localhost:8087/api/creative/space

## 开发指南

### 添加新功能

1. 在 `entity` 包中创建实体类
2. 在 `mapper` 包中创建 Mapper 接口
3. 在 `dto` 包中创建 VO 和 Request 类
4. 在 `service` 包中定义服务接口
5. 在 `service.impl` 包中实现服务逻辑
6. 在 `controller` 包中创建 REST 接口

### 代码规范

- 使用 Lombok 简化代码
- 遵循 RESTful API 设计规范
- 使用统一的返回结果格式 `Result<T>`
- 异常使用 `BusinessException` 处理
- 数据库字段使用下划线命名，Java 字段使用驼峰命名

## 测试

### 单元测试

```bash
mvn test
```

### 集成测试

使用 Postman 或其他 API 测试工具测试接口。

## 常见问题

### 1. 数据库连接失败

检查 MySQL 是否启动，用户名密码是否正确。

### 2. Redis 连接失败

检查 Redis 是否启动，端口是否正确。

### 3. 文件上传失败

确保配置了正确的文件存储路径或云存储服务。

## 后续优化

1. **文件存储**: 集成阿里云 OSS 或其他云存储服务
2. **消息队列**: 使用 RabbitMQ 处理异步任务
3. **搜索功能**: 集成 Elasticsearch 实现全文搜索
4. **推荐算法**: 优化设计师匹配算法
5. **性能优化**: 添加缓存策略，优化数据库查询

## 联系方式

如有问题，请联系开发团队。
