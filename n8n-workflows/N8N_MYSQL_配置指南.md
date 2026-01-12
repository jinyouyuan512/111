# n8n MySQL 数据库配置指南

## 第一步：在 n8n 中创建 MySQL 凭据

1. 打开 n8n: http://localhost:5678

2. 点击左侧菜单 **Credentials** (凭据)

3. 点击 **Add Credential** (添加凭据)

4. 搜索并选择 **MySQL**

5. 填写以下信息：
   ```
   Credential Name: MySQL jiyi_academy
   Host: host.docker.internal    ← 重要！不是 localhost
   Port: 3306
   Database: jiyi_academy
   User: root
   Password: root (或你的MySQL密码)
   ```
   
   **⚠️ 重要**：因为 n8n 运行在 Docker 容器中，必须使用 `host.docker.internal` 来访问主机上的 MySQL，而不是 `localhost`。

6. 点击 **Save** 保存

## 第二步：导入工作流

1. 点击左侧菜单 **Workflows**

2. 点击 **Import from File**

3. 选择以下文件导入：
   - `n8n-workflows/academy-news-to-db.json` (新闻同步)
   - `n8n-workflows/academy-books-to-db.json` (书籍同步)

## 第三步：配置工作流中的 MySQL 节点

导入后，需要为 MySQL 节点选择刚才创建的凭据：

1. 打开导入的工作流

2. 点击 **写入MySQL数据库** 节点

3. 在右侧面板中，找到 **Credential to connect with**

4. 选择 **MySQL jiyi_academy**

5. 点击 **Save** 保存工作流

## 第四步：测试工作流

### 方法1：手动执行
1. 点击工作流右上角的 **Execute Workflow** 按钮
2. 查看执行结果

### 方法2：通过 Webhook 触发
- 新闻同步: `GET http://localhost:5678/webhook/academy/trigger-news-sync`
- 书籍同步: `GET http://localhost:5678/webhook/academy/trigger-books-sync`

## 数据库表结构

### red_news 表
```sql
CREATE TABLE IF NOT EXISTS red_news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    summary TEXT,
    content TEXT,
    category VARCHAR(50),
    source VARCHAR(100),
    author VARCHAR(50),
    external_url VARCHAR(500),
    is_top TINYINT DEFAULT 0,
    is_hot TINYINT DEFAULT 0,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### red_book 表
```sql
CREATE TABLE IF NOT EXISTS red_book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100),
    publisher VARCHAR(100),
    isbn VARCHAR(20),
    description TEXT,
    category VARCHAR(50),
    page_count INT,
    publish_date DATE,
    rating DECIMAL(2,1) DEFAULT 0,
    is_recommended TINYINT DEFAULT 0,
    has_ebook TINYINT DEFAULT 0,
    tags VARCHAR(255),
    external_url VARCHAR(500),
    read_count INT DEFAULT 0,
    favorite_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## 常见问题

### Q: 连接失败怎么办？
A: 检查以下几点：
1. MySQL 服务是否运行 (端口 3306)
2. 用户名密码是否正确
3. 数据库 `jiyi_academy` 是否存在

### Q: 写入失败怎么办？
A: 检查：
1. 表是否存在
2. 字段名是否匹配
3. 查看 n8n 执行日志中的错误信息

## 定时执行

工作流配置了每天自动执行一次。如需修改频率：
1. 点击 **每天定时触发** 节点
2. 修改 **Interval** 设置
