# 前端项目修复说明

## 已修复的问题

### 1. 缺少 index.html
**问题**: HBuilderX 报错 "根目录缺少 index.html"
**修复**: 创建了 `index.html` 入口文件

### 2. 缺少 uni.scss
**问题**: UniApp 项目需要全局样式变量文件
**修复**: 创建了 `uni.scss` 文件

### 3. 完善项目配置
- 更新了 `main.js` 使用 Vue 3 的 createSSRApp
- 更新了 `App.vue` 使用 setup 语法
- 更新了 `vite.config.js` 配置代理
- 更新了 `package.json` 依赖版本

## 项目结构

```
frontend/
├── index.html          # 入口 HTML（新增）
├── main.js            # 主入口文件（已更新）
├── App.vue            # 应用根组件（已更新）
├── pages.json         # 页面配置
├── manifest.json      # 应用配置
├── uni.scss           # 全局样式变量（新增）
├── vite.config.js     # Vite 配置（新增）
├── package.json       # 依赖配置（已更新）
├── pages/             # 页面目录
│   ├── index/         # 打卡首页
│   ├── settings/      # 设置页
│   └── login/         # 登录页
├── api/               # API 封装
├── store/             # 状态管理
├── static/            # 静态资源
│   ├── css/           # 样式文件
│   └── index.css      # 全局样式（新增）
└── utils/             # 工具函数
```

## 现在可以启动

在 HBuilderX 中：
1. 打开 frontend 目录
2. 运行 -> 运行到浏览器 -> Chrome
3. 或运行到内置浏览器

前端将运行在 http://localhost:3000

## API 代理配置

已配置 Vite 代理：
- 前端请求 `/api/*` 会自动转发到 `http://localhost:8080/api/*`

无需修改前端代码中的 API 地址。
