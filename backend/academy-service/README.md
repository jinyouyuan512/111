# Academy Service - 红色精神传承学院服务

## 概述

红色精神传承学院服务提供完整的在线学习功能，包括课程管理、学习进度跟踪、章节测验和结业证书颁发。

## 功能特性

### 1. 课程管理
- 课程列表查询（支持分类筛选）
- 课程详情查看
- 课程报名
- 课程分类：党史教育、革命精神、英雄人物、时代楷模

### 2. 学习进度跟踪
- 章节完成状态记录
- 学习进度百分比计算
- 自动更新课程完成状态

### 3. 章节测验系统
- 支持单选题、多选题、判断题
- 自动评分
- 记录最佳成绩
- 及格分数线设置

### 4. 结业证书
- 完成所有章节后自动颁发
- 唯一证书编号
- 积分奖励系统

## API 接口

### 课程相关

#### 获取课程列表
```
GET /api/academy/courses?category={category}
```

#### 获取课程详情
```
GET /api/academy/courses/{courseId}
```

#### 报名课程
```
POST /api/academy/courses/{courseId}/enroll
Header: X-User-Id: {userId}
```

### 学习进度

#### 更新学习进度
```
POST /api/academy/courses/{courseId}/chapters/{chapterId}/complete
Header: X-User-Id: {userId}
```

#### 获取课程进度
```
GET /api/academy/courses/{courseId}/progress
Header: X-User-Id: {userId}
```

### 测验相关

#### 提交测验
```
POST /api/academy/courses/quizzes/submit
Header: X-User-Id: {userId}
Body: {
  "quizId": 1,
  "answers": {
    "1": "石家庄市",
    "2": "七届二中全会,土地会议,九月会议",
    "3": "正确"
  }
}
```

### 证书相关

#### 颁发证书
```
POST /api/academy/courses/{courseId}/certificate
Header: X-User-Id: {userId}
```

#### 获取用户证书列表
```
GET /api/academy/courses/certificates
Header: X-User-Id: {userId}
```

## 数据模型

### Course（课程）
- id: 课程ID
- title: 课程标题
- category: 分类
- description: 课程描述
- instructor: 讲师
- coverImage: 封面图片
- totalHours: 总课时
- level: 难度等级
- enrollmentCount: 报名人数

### Chapter（章节）
- id: 章节ID
- courseId: 课程ID
- title: 章节标题
- videoUrl: 视频URL
- duration: 时长（秒）
- orderNum: 排序号

### Quiz（测验）
- id: 测验ID
- chapterId: 章节ID
- title: 测验标题
- passScore: 及格分数
- timeLimit: 时间限制（分钟）

### QuizQuestion（测验题目）
- id: 题目ID
- quizId: 测验ID
- question: 题目内容
- type: 题型（single_choice, multiple_choice, true_false）
- options: 选项（JSON数组）
- correctAnswer: 正确答案
- score: 分值

## 业务逻辑

### 学习进度计算
```
进度百分比 = (已完成章节数 / 总章节数) × 100
```

### 证书颁发条件
1. 完成所有章节
2. 通过所有章节测验
3. 每门课程只能颁发一次证书

### 积分奖励
- 完成课程并获得证书：100积分

## 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jiyi_academy
    username: root
    password: root
```

### Redis配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 2
```

## 启动说明

### 1. 创建数据库
```sql
CREATE DATABASE jiyi_academy DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 执行数据库脚本
```bash
mysql -u root -p jiyi_academy < src/main/resources/db/schema.sql
mysql -u root -p jiyi_academy < src/main/resources/db/data.sql
```

### 3. 启动服务
```bash
mvn spring-boot:run
```

服务将在 http://localhost:8083 启动

## 测试数据

系统已预置5门示例课程：
1. 河北党史：新中国从这里走来
2. 西柏坡精神深度解读
3. 李大钊与马克思主义在河北
4. 狼牙山五壮士英雄事迹
5. 塞罕坝精神：荒原变林海

## 技术栈

- Spring Boot 3.2+
- MyBatis Plus 3.5+
- MySQL 8.0+
- Redis 7.0+
- Jackson (JSON处理)

## 注意事项

1. 所有需要用户身份的接口都需要在Header中传递 `X-User-Id`
2. 测验答案格式：
   - 单选题：直接传答案字符串
   - 多选题：用逗号分隔多个答案
   - 判断题：传"正确"或"错误"
3. 证书编号格式：`CERT-{日期}-{课程ID}-{用户ID}`
4. 学习进度是单调递增的，不会因为重复完成章节而减少
