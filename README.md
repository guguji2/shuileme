# 睡了么 (Sleep Tracker)

一款睡眠打卡提醒 App，帮助你养成规律作息习惯。

## 功能特色

- **睡眠时间设置**：设置每日睡眠截止时间
- **提前提醒**：支持设置提前提醒分钟数，自定义提醒间隔
- **睡前打卡**：简单易用的打卡功能，每日限制一次
- **智能监控**：定时检测未打卡自动发送邮件通知紧急联系人
- **数据统计**：记录累计睡觉天数和连续睡觉天数
- **多联系人支持**：可添加多个紧急联系人，群发通知邮件
- **通知日志**：记录每次邮件通知的发送状态

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- Spring Mail (SMTP)
- JWT 认证

### 前端
- UniApp (Vue3)
- 跨平台支持（Web、小程序、App）

## 快速开始

### 1. 数据库初始化

```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

### 2. 配置后端

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/slema
    username: root
    password: your_password
  mail:
    host: smtp.qq.com
    username: your_email@qq.com
    password: your_smtp_password
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端服务将运行在 http://localhost:8080

### 4. 配置前端

编辑 `frontend/api/request.js`，修改后端地址：

```javascript
const BASE_URL = 'http://localhost:8080/api'
```

### 5. 运行前端

使用 HBuilderX 打开 `frontend` 目录，运行到浏览器或设备。

## 数据库表结构

### 用户表 (user)
| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR(50) | 用户名 |
| phone | VARCHAR(20) | 手机号（唯一） |
| password | VARCHAR(100) | 密码 |
| status | VARCHAR(20) | 状态（ACTIVE/INACTIVE） |
| last_active_at | DATETIME | 最后活跃时间 |
| created_at | DATETIME | 创建时间 |

### 紧急联系人表 (emergency_contact)
| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID（外键） |
| name | VARCHAR(50) | 联系人姓名 |
| email | VARCHAR(100) | 联系人邮箱 |
| created_at | DATETIME | 创建时间 |

### 睡眠设置表 (sleep_setting)
| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID（外键，唯一） |
| deadline_time | VARCHAR(5) | 睡觉截止时间（HH:mm格式） |
| reminder_minutes | INT | 提前提醒分钟数 |
| created_at | DATETIME | 创建时间 |

### 打卡记录表 (check_in)
| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID（外键） |
| check_in_time | DATETIME | 打卡时间 |
| date | DATE | 日期（YYYY-MM-DD） |
| created_at | DATETIME | 创建时间 |
| unique_user_date | UNIQUE KEY | 用户日期唯一约束 |

### 通知记录表 (notification_log)
| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID（外键） |
| contact_id | BIGINT | 联系人ID（外键） |
| sent_time | DATETIME | 发送时间 |
| status | VARCHAR(20) | 状态（SUCCESS/FAILED） |
| created_at | DATETIME | 创建时间 |

## 项目结构

```
slema/
├── backend/                # Spring Boot 后端
│   ├── src/main/java/com/slema/
│   │   ├── controller/    # REST API
│   │   ├── service/       # 业务逻辑
│   │   ├── mapper/        # MyBatis Mapper
│   │   ├── entity/        # 数据库实体
│   │   ├── dto/           # 数据传输对象
│   │   ├── config/        # 配置类（JWT、跨域等）
│   │   └── scheduled/     # 定时任务
│   └── pom.xml
│
└── frontend/              # UniApp 前端
    ├── pages/            # 页面
    │   ├── index/        # 打卡首页
    │   ├── settings/     # 设置页
    │   └── login/        # 登录注册
    ├── api/              # API 封装
    └── store/            # 状态管理
```

## API 接口

### 认证
- `POST /api/auth/register` - 用户注册
  - 请求参数：`username`, `phone`, `password`
  - 返回：JWT Token + 用户信息（id、username、phone）

- `POST /api/auth/login` - 用户登录
  - 请求参数：`phone`, `password`
  - 返回：JWT Token + 用户信息（id、username、phone）

### 打卡
- `POST /api/checkin` - 睡前打卡
  - 请求参数：`userId`
  - 返回：打卡记录（id、checkInTime、date、message）
  - 约束：同一用户每日只能打卡一次

- `GET /api/checkin/status/{userId}` - 查询今日打卡状态
  - 路径参数：`userId`
  - 返回：`true`（已打卡）/ `false`（未打卡）

### 设置
- `POST /api/settings/sleep` - 保存睡眠设置
  - 请求参数：`userId`, `deadlineTime`, `reminderMinutes`
  - 返回：SleepSetting 对象

- `GET /api/settings/sleep/{userId}` - 获取睡眠设置
  - 路径参数：`userId`
  - 返回：SleepSetting 对象

- `POST /api/settings/contacts` - 添加紧急联系人
  - 请求参数：`userId`, `name`, `email`
  - 返回：EmergencyContact 对象

- `GET /api/settings/contacts/{userId}` - 获取联系人列表
  - 路径参数：`userId`
  - 返回：EmergencyContact 数组

- `DELETE /api/settings/contacts/{contactId}` - 删除联系人
  - 路径参数：`contactId`

### 统计
- `GET /api/stats/{userId}` - 获取用户统计信息
  - 路径参数：`userId`
  - 返回：累计睡觉天数、连续睡觉天数

## 核心功能说明

### 监控服务
后端定时任务每 5 分钟检查一次：
1. 获取所有用户的睡眠设置
2. 检查当前时间是否超过截止时间
3. 检查用户是否已进行今日打卡
4. 检查今日是否已发送过通知（避免重复通知）
5. 如果满足以上条件，则发送邮件给所有紧急联系人
6. 记录邮件发送状态到通知日志表（notification_log）

### JWT 认证
- Token 有效期：7 天
- Token 包含信息：用户 ID、用户名
- 认证方式：请求头 `Authorization: Bearer <token>`

### 邮件发送
- 使用 SMTP 方式发送邮件
- 支持 QQ、163、Gmail 等邮箱服务
- 邮件主题：【睡了么】{用户名} 未按时睡觉提醒
- 群发功能：一封邮件抄送所有紧急联系人
- TLS 加密传输

## 部署说明

### 后端部署
1. 打包：`mvn clean package`
2. 运行：`java -jar target/slema-backend-1.0.0.jar`

### 前端部署
1. 使用 HBuilderX 发行
2. 部署到 Web 服务器或发布应用商店

## 许可证

MIT License
