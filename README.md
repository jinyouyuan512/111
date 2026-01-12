# 冀忆红途 (Jiyi Red Route)

红色文化数字生态平台 - Red Culture Digital Ecosystem Platform

## 项目简介

冀忆红途是一个创新型红色文化数字生态平台，融合沉浸式体验、智慧旅游、文创商业和社交互动等多维度功能。

## 技术栈

### 后端
- Spring Boot 3.2+
- Spring Cloud Alibaba
- MyBatis Plus
- MySQL 8.0+
- Redis 7.0+
- MongoDB 6.0+

### 前端
- Vue 3.4+
- TypeScript 5.0+
- Vite 5.0+
- Element Plus
- Three.js + TresJS
- Pinia

## 项目结构

```
jiyi-red-route/
├── backend/                 # 后端服务
│   ├── gateway/            # API网关
│   ├── user-service/       # 用户服务
│   ├── academy-service/    # 学院服务
│   ├── tourism-service/    # 旅游服务
│   ├── guide-service/      # 导览服务
│   ├── creative-service/   # 众创服务
│   ├── social-service/     # 社交服务
│   └── common/             # 公共模块
├── frontend/               # 前端应用
│   ├── src/
│   │   ├── views/         # 页面组件
│   │   ├── components/    # 通用组件
│   │   ├── router/        # 路由配置
│   │   ├── stores/        # 状态管理
│   │   └── api/           # API接口
│   └── package.json
└── README.md
```

## 开发环境要求

- JDK 17+
- Node.js 18+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.0+
- MongoDB 6.0+

## 快速开始

### 后端启动

```bash
cd backend
mvn clean install
cd gateway
mvn spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

## 开发规范

### 代码规范
- 后端: 遵循 Checkstyle 规范
- 前端: 使用 ESLint + Prettier

### Git 分支策略
- main: 主分支，生产环境代码
- develop: 开发分支
- feature/*: 功能分支
- hotfix/*: 紧急修复分支

### 提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关
- chore: 构建/工具相关

## 许可证

Copyright © 2024 Jiyi Red Route Team
