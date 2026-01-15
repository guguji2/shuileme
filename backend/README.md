# 睡了么 - 后端服务

## 技术栈
- Java 17
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- Spring Mail
- JWT

## 初始化数据库

```bash
mysql -u root -p < src/main/resources/schema.sql
```

## 配置文件

修改 `src/main/resources/application.yml`:
- 数据库连接信息
- SMTP邮件服务配置

## 运行

```bash
mvn spring-boot:run
```

## API接口

### 认证相关
- POST `/api/auth/register` - 用户注册
- POST `/api/auth/login` - 用户登录

### 打卡相关
- POST `/api/checkin` - 睡前打卡
- GET `/api/checkin/status/{userId}` - 查询今日打卡状态

### 设置相关
- POST `/api/settings/sleep` - 保存睡眠设置
- GET `/api/settings/sleep/{userId}` - 获取睡眠设置
- POST `/api/settings/contacts` - 添加紧急联系人
- GET `/api/settings/contacts/{userId}` - 获取紧急联系人列表
- DELETE `/api/settings/contacts/{contactId}` - 删除紧急联系人



###
# 2. 停止并删除旧容器
- docker stop slema-backend
- docker rm slema-backend

# 3. 重新创建容器（会读取新的 .env 文件）
docker run -d \
--name slema-backend \
--restart unless-stopped \
--env-file docker/.env \
--network 1panel-network \
-p 8080:8080 \
slema-backend:latest

# 4. 查看日志
- docker logs -f slema-backend