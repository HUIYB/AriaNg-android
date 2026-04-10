# Aria2 配置接口使用示例

## 基本使用

### 1. 使用默认配置启动

```typescript
import { Aria2 } from 'aria2server';

await Aria2.start();
```

### 2. 使用自定义配置启动

```typescript
await Aria2.start({
  config: {
    rpcPort: 6801,
    rpcSecret: 'my-secret-key',
    downloadDir: '/sdcard/Download',
    maxConcurrentDownloads: 5,
    split: 3,
  },
});
```

### 3. 使用额外命令行参数启动

```typescript
await Aria2.start({
  config: { rpcPort: 6802 },
  args: ['--seed-time=0', '--bt-tracker=tracker1:80'],
});
```

### 4. 持久化配置

```typescript
// 启动时持久化配置
await Aria2.start({
  config: { rpcPort: 6803 },
  persistConfig: true,
});

// 单独设置配置
await Aria2.setConfig({
  config: { maxConcurrentDownloads: 8 },
  persist: true,
});
```

## 配置管理

### 获取当前配置

```typescript
const { config } = await Aria2.getConfig();
console.log('RPC Port:', config.rpcPort);
```

### 重置配置

```typescript
await Aria2.resetConfig();
```

### 验证配置

```typescript
const { valid, errors } = await Aria2.validateConfig({
  config: { rpcPort: 100 }, // 无效端口
});
```

### 获取所有可用选项

```typescript
const { options } = await Aria2.getAvailableOptions();
options.forEach((opt) => {
  console.log(`${opt.name} (${opt.type}): ${opt.description}`);
});
```

## 配置选项参考

| 选项名                 | 类型    | 默认值   | 说明                 |
| ---------------------- | ------- | -------- | -------------------- |
| rpcEnabled             | boolean | true     | 启用RPC服务器        |
| rpcPort                | number  | 6800     | RPC端口 (1024-65535) |
| rpcSecret              | string  | "aria2"  | RPC密钥              |
| downloadDir            | string  | -        | 下载目录             |
| maxConcurrentDownloads | number  | 10       | 最大并发下载数       |
| split                  | number  | 5        | 单文件连接数 (1-16)  |
| timeout                | number  | 60       | 超时时间（秒）       |
| logLevel               | string  | "notice" | 日志级别             |

## 注意事项

- rpcPort 必须在 1024-65535 之间
- split 必须在 1-16 之间
- logLevel 必须是: debug, info, notice, warn, error
