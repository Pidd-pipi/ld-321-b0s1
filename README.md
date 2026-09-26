# 农机调度管理系统

**项目类型标签：全栈Web应用**

农机调度管理系统 是 农业合作社与种植大户 的农机作业调度平台。

## 快速启动

首次启动前复制环境变量：

```bash
cp .env.example .env
docker compose up -d
```

访问地址：`http://localhost:18621`
后端健康检查：`http://localhost:19621/api/health`

## 主要功能

- 农机档案管理：编号、二维码、照片和状态筛选
- 作业任务调度：推荐空闲农机与驾驶员
- 实时地图轨迹：位置、轨迹回放和地块边界
- 作业统计报表：日报、月报和 Excel 导出
- 维修保养提醒：周期预警与记录追溯
- 驾驶员管理：证照、排班和评价
- 调度看板：待办、空闲、趋势和提醒

## Demo API

- `GET /api/health`：健康检查
- `GET /api/dashboard/overview`：调度看板聚合数据，包含农机档案、任务、轨迹、作业记录、保养提醒、驾驶员和 7 日趋势
- `POST /api/tasks/{taskId}/dispatch`：模拟一键派单
- `GET /api/reports/work/export?mode=daily|monthly&date=YYYY-MM-DD|YYYY-MM`：按所选范围导出作业统计 CSV（含合计行；范围内无记录时返回 204，不生成空文件）

## 本地开发方式

前端：

```bash
cd frontend
npm install
npm run dev
```

后端：

```bash
cd backend
mvn spring-boot:run
```

本地开发后端默认监听 `19621`，前端开发服务器已代理 `/api` 和 `/ws` 到该端口。

## 技术栈

| 分类 | 技术 |
| --- | --- |
| 前端 | Vue 3 + TypeScript + Vite + Element Plus |
| 后端 | Spring Boot 3 + Java 17 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis |
| 认证 | JWT + Spring Security |

## 项目目录结构

```text
.
├── frontend
│   ├── src
│   ├── Dockerfile
│   └── nginx.conf
├── backend
│   ├── src 或 app
│   └── Dockerfile
├── database
│   └── init.sql
├── docker-compose.yml
├── .env.example
└── README.md
```

## 环境变量说明

| 变量 | 说明 |
| --- | --- |
| COMPOSE_PROJECT_NAME | Compose 项目名，默认 cyfarmsched |
| FRONTEND_PORT | 前端映射端口，默认 18621 |
| BACKEND_PORT | 后端映射端口，默认 19621 |
| DB_PORT | MySQL 宿主机映射端口，默认 17621 |
| DB_NAME / DB_USER / DB_PASSWORD | 数据库连接信息 |
| JWT_SECRET | JWT 签名密钥 |

## Docker 部署说明

- Compose 顶层 `name: cyfarmsched`，并在 `.env` 中提供 `COMPOSE_PROJECT_NAME=cyfarmsched`，可在中文目录下启动。
- 前端容器通过 Nginx 托管静态资源，并将 `/api/` 反向代理到后端服务。
- 数据库和 Redis 使用命名卷持久化，避免绑定挂载中文路径。
- 常见问题：端口冲突时修改 `.env` 中的端口值后重新执行 `docker compose up -d`。

## License

MIT
