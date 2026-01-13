# 冀忆红途 - 数据库SQL文件

## 文件说明

| 文件 | 说明 | 大小 |
|------|------|------|
| `all_databases.sql` | 所有数据库合并文件 | 246KB |
| `jiyi_user.sql` | 用户服务数据库 | 11KB |
| `jiyi_social.sql` | 社交服务数据库 | 62KB |
| `jiyi_mall.sql` | 商城服务数据库 | 37KB |
| `jiyi_creative.sql` | 众创空间数据库 | 46KB |
| `jiyi_tourism.sql` | 旅游服务数据库 | 43KB |
| `jiyi_academy.sql` | 学院服务数据库 | 61KB |

## 导入方式

### 方式一：导入所有数据库（推荐）
```bash
mysql -u root -p < sql/all_databases.sql
```

### 方式二：分别导入各数据库
```bash
# 先创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS jiyi_user;"
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS jiyi_social;"
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS jiyi_mall;"
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS jiyi_creative;"
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS jiyi_tourism;"
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS jiyi_academy;"

# 导入数据
mysql -u root -p jiyi_user < sql/jiyi_user.sql
mysql -u root -p jiyi_social < sql/jiyi_social.sql
mysql -u root -p jiyi_mall < sql/jiyi_mall.sql
mysql -u root -p jiyi_creative < sql/jiyi_creative.sql
mysql -u root -p jiyi_tourism < sql/jiyi_tourism.sql
mysql -u root -p jiyi_academy < sql/jiyi_academy.sql
```

## 数据库配置

- MySQL 版本: 8.0+
- 字符集: utf8mb4
- 排序规则: utf8mb4_unicode_ci
- 默认连接: localhost:3306, root/root
