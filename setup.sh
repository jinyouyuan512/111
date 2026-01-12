#!/bin/bash

echo "冀忆红途项目初始化脚本"
echo "========================"

# 检查环境
echo "检查开发环境..."

# 检查 Java
if command -v java &> /dev/null; then
    echo "✓ Java 已安装: $(java -version 2>&1 | head -n 1)"
else
    echo "✗ Java 未安装，请安装 JDK 17+"
    exit 1
fi

# 检查 Maven
if command -v mvn &> /dev/null; then
    echo "✓ Maven 已安装: $(mvn -version | head -n 1)"
else
    echo "✗ Maven 未安装，请安装 Maven 3.8+"
    exit 1
fi

# 检查 Node.js
if command -v node &> /dev/null; then
    echo "✓ Node.js 已安装: $(node -v)"
else
    echo "✗ Node.js 未安装，请安装 Node.js 18+"
    exit 1
fi

# 检查 npm
if command -v npm &> /dev/null; then
    echo "✓ npm 已安装: $(npm -v)"
else
    echo "✗ npm 未安装"
    exit 1
fi

echo ""
echo "环境检查完成！"
echo ""

# 启动基础设施
echo "启动基础设施服务 (MySQL, Redis, MongoDB, Nacos)..."
docker-compose up -d

echo ""
echo "等待服务启动..."
sleep 10

echo ""
echo "初始化完成！"
echo ""
echo "后端启动命令:"
echo "  cd backend && mvn clean install"
echo "  cd backend/gateway && mvn spring-boot:run"
echo ""
echo "前端启动命令:"
echo "  cd frontend && npm install && npm run dev"
echo ""
echo "访问地址:"
echo "  前端: http://localhost:3000"
echo "  网关: http://localhost:8080"
echo "  Nacos: http://localhost:8848/nacos (用户名/密码: nacos/nacos)"
