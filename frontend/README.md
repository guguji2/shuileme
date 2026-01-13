# 睡了么 - 前端

## 技术栈
- UniApp (Vue3)
- Vite

## 开发工具
- HBuilderX
- 或微信开发者工具（小程序端）

## 运行

### 使用 HBuilderX
1. 导入项目到 HBuilderX
2. 运行 -> 运行到浏览器 -> Chrome
3. 或运行到手机模拟器

### 使用命令行
```bash
npm install
npm run dev:h5
```

## 配置后端地址

修改 `api/request.js` 中的 `BASE_URL` 为实际后端地址。

## 目录结构
```
pages/
  index/      # 打卡首页
  settings/   # 设置页
  login/      # 登录注册页
api/         # API 接口封装
store/       # 状态管理
utils/       # 工具函数
```
