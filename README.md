# Campus Share

Campus Share 是一个面向校园用户的图文笔记分享社区，支持用户注册登录、发布笔记、图片上传、评论、点赞、收藏和个人资料管理，并提供基础的管理员页面。

## 技术栈

- 前端：Vue 3、Vite、Vue Router、Axios、Element Plus
- 后端：Java 21、Spring Boot 3.5、MyBatis、Maven、JWT、BCrypt
- 数据库：MySQL 8.x
- 文件存储：阿里云 OSS
- 部署：Nginx 静态文件托管和反向代理

## 项目结构

```text
CampusProject/
├── frontend/   # Vue 3 前端
├── backend/    # Spring Boot 后端
├── sql/        # 数据库初始化脚本
├── docs/       # 项目文档
├── .env.example
├── .gitignore
└── README.md
```

## 环境要求

- JDK 21
- Node.js 18 或更高版本
- npm
- MySQL 8.x

## 本地运行

### 1. 初始化数据库

创建 MySQL 数据库并执行：

```text
sql/schema.sql
```

脚本会创建名为 `campus` 的数据库及项目所需的数据表。

### 2. 配置后端环境变量

复制 `.env.example` 中的变量到当前系统环境，至少配置：

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
OSS_ACCESS_KEY_ID
OSS_ACCESS_KEY_SECRET
```


### 3. 启动后端

在 `backend/` 目录执行：

```powershell
.\mvnw.cmd spring-boot:run
```

后端默认地址：

```text
http://localhost:8080/campus-api
```

### 4. 启动前端

在 `frontend/` 目录执行：

```powershell
npm install
npm run dev:real
```

前端默认地址：

```text
http://localhost:5173
```

前端开发服务器会把 `/campus-api` 请求代理到 `http://localhost:8080`。


## Nginx 部署

1. 在 `frontend/` 目录执行 `npm run build:real`。
2. 将生成的 `frontend/dist/` 内容复制到 Nginx 网站目录，例如 `campus-dist/`。
3. 将 `/campus-api` 配置为反向代理到后端 `http://localhost:8080`。
4. 启动后端服务和 Nginx。

## 接口前缀

后端统一使用 `/campus-api` 作为上下文路径。前端真实模式默认请求 `/campus-api`，适合通过 Nginx 配置同源访问。

## 公开仓库安全说明

本仓库只提交源代码、测试代码、SQL 结构和配置模板，不提交以下内容：

- 数据库密码
- JWT 密钥
- 阿里云 AccessKey
- `node_modules/`
- `backend/target/`
- `frontend/dist/`
- 真实用户数据和生产环境文件
