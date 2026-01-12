# 开发指南

## 项目架构

本项目采用 **Java + Vue 3 混合架构**，结合 Spring Boot 微服务和 Vue 3 前端框架。

### 技术选型理由

- **后端**: Spring Boot 3.2 + Spring Cloud Alibaba - 成熟稳定的企业级微服务框架
- **前端**: Vue 3 + TypeScript + Vite - 现代化的前端开发体验
- **数据库**: MySQL (关系数据) + MongoDB (文档数据) + Redis (缓存)
- **服务治理**: Nacos (服务注册与配置) + Sentinel (限流熔断)

## 开发环境配置

### 必需软件

1. **JDK 17+**
   - 下载: https://adoptium.net/
   - 配置 JAVA_HOME 环境变量

2. **Maven 3.8+**
   - 下载: https://maven.apache.org/download.cgi
   - 配置 MAVEN_HOME 环境变量

3. **Node.js 18+**
   - 下载: https://nodejs.org/
   - 自动包含 npm

4. **Docker Desktop**
   - 下载: https://www.docker.com/products/docker-desktop
   - 用于运行基础设施服务

### IDE 推荐

- **后端**: IntelliJ IDEA Ultimate
- **前端**: VS Code + Volar 插件

## 快速启动

### 1. 启动基础设施

```bash
# Windows
setup.bat

# Linux/Mac
chmod +x setup.sh
./setup.sh
```

这将启动:
- MySQL (端口 3306)
- Redis (端口 6379)
- MongoDB (端口 27017)
- Nacos (端口 8848)

### 2. 启动后端服务

```bash
cd backend
mvn clean install

# 启动网关
cd gateway
mvn spring-boot:run

# 启动用户服务 (新终端)
cd user-service
mvn spring-boot:run
```

### 3. 启动前端应用

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:3000

## 代码规范

### 后端规范

1. **命名规范**
   - 类名: PascalCase (UserService)
   - 方法名: camelCase (getUserById)
   - 常量: UPPER_SNAKE_CASE (MAX_RETRY_COUNT)

2. **包结构**
   ```
   com.jiyi.{service}
   ├── controller    # 控制器层
   ├── service       # 业务逻辑层
   ├── mapper        # 数据访问层
   ├── entity        # 实体类
   ├── dto           # 数据传输对象
   └── config        # 配置类
   ```

3. **Checkstyle**
   - 配置文件: backend/checkstyle.xml
   - 最大行长度: 120
   - 最大方法长度: 150

### 前端规范

1. **命名规范**
   - 组件名: PascalCase (UserProfile.vue)
   - 变量名: camelCase (userName)
   - 常量: UPPER_SNAKE_CASE (API_BASE_URL)

2. **目录结构**
   ```
   src/
   ├── views/        # 页面组件
   ├── components/   # 通用组件
   ├── router/       # 路由配置
   ├── stores/       # Pinia 状态管理
   ├── api/          # API 接口
   ├── utils/        # 工具函数
   └── types/        # TypeScript 类型定义
   ```

3. **ESLint + Prettier**
   - 配置文件: .eslintrc.cjs, .prettierrc.json
   - 单引号、无分号、行宽 100

## Git 工作流

### 分支策略

- `main`: 生产环境代码
- `develop`: 开发分支
- `feature/*`: 功能分支
- `hotfix/*`: 紧急修复分支

### 提交规范

使用 Conventional Commits 格式:

```
<type>(<scope>): <subject>

<body>

<footer>
```

类型 (type):
- `feat`: 新功能
- `fix`: 修复 bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具相关

示例:
```
feat(user): 实现用户注册功能

- 添加用户注册 API
- 实现邮箱验证
- 添加密码加密

Closes #123
```

## 测试策略

### 单元测试

**后端**: JUnit 5 + Mockito
```java
@Test
void testUserRegistration() {
    // Given
    UserDTO userDTO = new UserDTO("test@example.com", "password");
    
    // When
    User user = userService.register(userDTO);
    
    // Then
    assertNotNull(user.getId());
    assertEquals("test@example.com", user.getEmail());
}
```

**前端**: Vitest
```typescript
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import UserProfile from '@/components/UserProfile.vue'

describe('UserProfile', () => {
  it('renders user name', () => {
    const wrapper = mount(UserProfile, {
      props: { name: 'Test User' }
    })
    expect(wrapper.text()).toContain('Test User')
  })
})
```

### 集成测试

使用 Spring Boot Test 进行端到端测试:

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testUserRegistrationEndpoint() throws Exception {
        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }
}
```

## 部署

### Docker 部署

```bash
# 构建镜像
docker build -t jiyi-backend ./backend
docker build -t jiyi-frontend ./frontend

# 运行容器
docker-compose up -d
```

### Kubernetes 部署

```bash
# 应用配置
kubectl apply -f k8s/

# 查看状态
kubectl get pods
kubectl get services
```

## 常见问题

### 后端启动失败

1. 检查 MySQL/Redis/MongoDB 是否运行
2. 检查端口是否被占用
3. 查看日志: `tail -f logs/spring.log`

### 前端启动失败

1. 删除 node_modules 重新安装: `rm -rf node_modules && npm install`
2. 清除缓存: `npm cache clean --force`
3. 检查 Node.js 版本: `node -v`

### 数据库连接失败

1. 检查 application.yml 中的数据库配置
2. 确认数据库服务运行: `docker ps`
3. 测试连接: `mysql -h localhost -u root -p`

## 参考资料

- [Spring Boot 文档](https://spring.io/projects/spring-boot)
- [Vue 3 文档](https://vuejs.org/)
- [Element Plus 文档](https://element-plus.org/)
- [MyBatis Plus 文档](https://baomidou.com/)
- [Nacos 文档](https://nacos.io/)
