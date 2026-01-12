# 传承学院数据同步工作流配置指南

## 工作流文件

1. **academy-news-sync-via-api.json** - 新闻同步（每6小时）
2. **academy-books-sync-via-api.json** - 书籍同步（每天）

## 配置步骤

### 1. 打开 n8n
```
http://localhost:5678
```

### 2. 导入工作流
- 点击左上角 "+" 创建新工作流
- 点击右上角 "..." → "Import from file"
- 选择 `academy-news-sync-via-api.json` 或 `academy-books-sync-via-api.json`

### 3. 激活工作流
- 点击右上角的开关按钮激活工作流
- 工作流会按照设定的时间自动执行

### 4. 手动测试
新闻同步：
```
GET http://localhost:5678/webhook/academy/sync-news
```

书籍同步：
```
GET http://localhost:5678/webhook/academy/sync-books
```

## 工作流说明

### 数据流程
```
定时触发/手动触发 → 生成数据 → 调用后端API → 写入数据库
```

### 后端 API 端点
- 新闻：`POST http://localhost:8088/api/academy/news`
- 书籍：`POST http://localhost:8088/api/academy/books`

### 数据库
- 数据库：`jiyi_academy`
- 新闻表：`red_news`
- 书籍表：`red_book`

## 当前数据状态
- 新闻：8 条
- 书籍：12 本

## 前端访问
```
http://localhost:3001/academy
```

## 注意事项
1. 确保 academy-service 已启动（端口 8088）
2. 确保 n8n 已启动（端口 5678）
3. 工作流通过 HTTP API 写入数据，无需配置数据库连接
