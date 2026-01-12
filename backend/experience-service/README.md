# Experience Service - 沉浸式数字体验馆服务

## 概述

体验馆服务负责管理3D虚拟场景、交互点和用户体验进度。通过数字孪生技术还原河北红色历史场景，为用户提供沉浸式的文化体验。

## 功能特性

- 场景列表管理
- 3D场景数据加载
- 交互点触发和内容展示
- 用户体验进度跟踪
- 体验证书生成

## 技术栈

- Spring Boot 3.2.0
- MyBatis Plus 3.5.7
- MySQL 8.0+
- jqwik 1.8.2 (Property-Based Testing)

## 项目结构

```
experience-service/
├── src/
│   ├── main/
│   │   ├── java/com/jiyi/experience/
│   │   │   ├── entity/          # 实体类
│   │   │   │   └── Scene.java   # 场景实体
│   │   │   ├── dto/             # 数据传输对象
│   │   │   │   └── SceneVO.java # 场景视图对象
│   │   │   ├── mapper/          # MyBatis Mapper
│   │   │   │   └── SceneMapper.java
│   │   │   ├── service/         # 服务接口
│   │   │   │   ├── ExperienceService.java
│   │   │   │   └── impl/
│   │   │   │       └── ExperienceServiceImpl.java
│   │   │   └── ExperienceServiceApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/jiyi/experience/
│           └── service/
│               └── ExperienceServicePropertyTest.java  # 属性测试
└── pom.xml
```

## 属性测试

本服务使用基于属性的测试（Property-Based Testing）来验证系统的正确性属性。

### 已实现的属性测试

#### 属性 1: 场景列表完整性
- **描述**: 对于任意场景列表请求，返回的每个场景对象都应该包含名称、预览图、时代背景和体验时长字段
- **验证需求**: 1.1
- **测试类**: `ExperienceServicePropertyTest.sceneListCompleteness()`
- **测试次数**: 100次迭代
- **状态**: ✅ 通过

## 运行测试

```bash
# 运行所有测试
mvn test -pl experience-service

# 运行属性测试
mvn test -Dtest=ExperienceServicePropertyTest -pl experience-service
```

## API端点

（待实现）

- `GET /api/experience/scenes` - 获取场景列表
- `GET /api/experience/scenes/{id}` - 获取场景详情
- `POST /api/experience/progress` - 记录体验进度
- `GET /api/experience/certificate/{sceneId}` - 生成体验证书

## 数据库表

### scene 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(100) | 场景名称 |
| description | TEXT | 场景描述 |
| era | VARCHAR(50) | 时代背景 |
| duration | INT | 体验时长（分钟） |
| thumbnail | VARCHAR(255) | 预览图URL |
| model_url | VARCHAR(255) | 3D模型URL |
| interaction_count | INT | 交互点数量 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

## 开发状态

- [x] 基础项目结构
- [x] Scene实体和DTO
- [x] ExperienceService接口和实现
- [x] 属性测试 1: 场景列表完整性
- [ ] 属性测试 2: 场景数据加载一致性
- [ ] 属性测试 3: 交互点触发响应性
- [ ] 属性测试 4: 体验进度持久化
- [ ] Controller层实现
- [ ] 数据库表创建
- [ ] 前端API集成

## 注意事项

1. 本服务使用jqwik进行基于属性的测试，确保系统在各种输入下的正确性
2. 每个属性测试运行100次迭代，覆盖大量随机生成的测试用例
3. 所有场景对象必须包含完整的必需字段（name, thumbnail, era, duration）
