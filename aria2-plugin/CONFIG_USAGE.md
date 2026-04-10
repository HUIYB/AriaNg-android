# Aria2配置接口使用指南

## 快速开始

### 基本使用

```typescript
import { Aria2 } from 'aria2server';

// 1. 使用默认配置启动
await Aria2.start();

// 2. 使用自定义配置启动
await Aria2.start({
  config: {
    rpcPort: 6801,
    rpcSecret: 'my-secret-key',
    downloadDir: '/sdcard/Download',
    maxConcurrentDownloads: 5,
    split: 3,
  },
});

// 3. 使用额外命令行参数启动
await Aria2.start({
  config: { rpcPort: 6802 },
  args: ['--seed-time=0', '--bt-tracker=tracker1:80,tracker2:80'],
});
```

### 配置持久化

```typescript
// 启动时持久化配置
await Aria2.start({
  config: { rpcPort: 6803 },
  persistConfig: true, // 配置将保存到本地
});

// 单独设置配置（可选持久化）
await Aria2.setConfig({
  config: {
    rpcPort: 6804,
    maxConcurrentDownloads: 8,
  },
  persist: true, // 保存到本地存储
});

// 重置为默认配置
await Aria2.resetConfig();
```

### 获取和验证配置

```typescript
// 获取当前配置
const { config } = await Aria2.getConfig();
console.log('Current RPC port:', config.rpcPort);

// 验证配置
const { valid, errors } = await Aria2.validateConfig({
  config: { rpcPort: 100 }, // 无效端口
});

if (!valid) {
  console.error('Validation errors:', errors);
}

// 获取所有可用选项
const { options } = await Aria2.getAvailableOptions();
options.forEach((opt) => {
  console.log(`${opt.name}: ${opt.description}`);
});
```

## 配置选项参考

### RPC配置

| 选项名              | 类型    | 默认值  | 说明                           |
| ------------------- | ------- | ------- | ------------------------------ |
| `rpcEnabled`        | boolean | true    | 启用JSON-RPC/XML-RPC服务器     |
| `rpcPort`           | number  | 6800    | RPC服务器监听端口 (1024-65535) |
| `rpcSecret`         | string  | "aria2" | RPC授权密钥                    |
| `rpcListenAll`      | boolean | false   | 在所有网络接口上监听RPC请求    |
| `rpcAllowOriginAll` | boolean | false   | 允许所有来源的CORS请求         |

### 下载配置

| 选项名                   | 类型    | 默认值 | 说明                          |
| ------------------------ | ------- | ------ | ----------------------------- |
| `downloadDir`            | string  | -      | 下载文件存储目录              |
| `continueDownload`       | boolean | true   | 继续未完成的下载              |
| `maxConcurrentDownloads` | number  | 10     | 最大并发下载数                |
| `maxConnectionPerServer` | number  | 1      | 每个服务器的最大连接数 (1-16) |
| `split`                  | number  | 5      | 单个文件下载连接数 (1-16)     |
| `minSplitSize`           | string  | "20M"  | 最小分片大小                  |

### 连接配置

| 选项名           | 类型   | 默认值 | 说明               |
| ---------------- | ------ | ------ | ------------------ |
| `timeout`        | number | 60     | 超时时间（秒）     |
| `connectTimeout` | number | 60     | 连接超时时间（秒） |
| `maxTries`       | number | 5      | 最大重试次数       |
| `retryWait`      | number | 0      | 重试等待时间（秒） |

### BitTorrent配置

| 选项名               | 类型    | 默认值      | 说明                    |
| -------------------- | ------- | ----------- | ----------------------- |
| `enableDht`          | boolean | true        | 启用DHT功能             |
| `enablePeerExchange` | boolean | true        | 启用Peer Exchange扩展   |
| `listenPort`         | string  | "6881-6999" | BitTorrent监听端口范围  |
| `btMaxPeers`         | number  | 55          | 每个torrent的最大peer数 |

### 其他配置

| 选项名      | 类型   | 默认值   | 说明                                       |
| ----------- | ------ | -------- | ------------------------------------------ |
| `logLevel`  | string | "notice" | 日志级别: debug, info, notice, warn, error |
| `logFile`   | string | -        | 日志文件路径                               |
| `userAgent` | string | -        | HTTP用户代理                               |

## 高级用法

### 动态注册自定义选项

```typescript
// 注册新的配置选项（扩展点）
await Aria2.registerOption({
  name: 'customTimeout',
  type: 'number',
  description: '自定义超时时间',
  category: 'custom',
  aria2Option: '--custom-timeout',
  defaultValue: 120,
});
```

### 导入/导出配置

```typescript
// 导出当前配置
const { config } = await Aria2.exportConfig();
console.log('Exported config:', config);

// 导入配置
await Aria2.importConfig({
  config: '{"rpcPort": 6805, "maxConcurrentDownloads": 3}',
});
```

### 批量更新配置

```typescript
// 合并多个配置项
await Aria2.setConfig({
  config: {
    rpcPort: 6806,
    maxConcurrentDownloads: 6,
    split: 4,
    timeout: 90,
    enableDht: false,
  },
  persist: true,
});
```

## 扩展性设计

本插件采用易扩展的设计：

1. **动态配置系统** - 配置选项通过 `OptionMapper` 注册，支持动态添加新选项
2. **Map-based存储** - `Aria2Config` 使用Map存储，不固定字段
3. **元数据注册** - 选项元数据可动态注册和查询
4. **验证器支持** - 每个选项可自定义验证逻辑

### 添加新的aria2c参数支持

在Android端，可以通过以下方式添加新的配置选项：

```kotlin
// 在代码中注册新选项
OptionMapper.registerOption(
    OptionMeta(
        name = "newOption",
        type = "string",
        description = "新选项说明",
        category = "other",
        aria2Option = "--new-option",
        defaultValue = "default"
    )
)
```

## 错误处理

```typescript
try {
  await Aria2.start({
    config: { rpcPort: 80 }, // 端口无效
  });
} catch (error) {
  console.error('Start failed:', error.message);
  // 输出: 配置验证失败: rpcPort must be between 1024 and 65535
}
```

## 注意事项

1. **端口范围** - RPC端口必须在 1024-65535 之间
2. **并发数限制** - `maxConcurrentDownloads` 至少为 1
3. **分片数限制** - `split` 必须在 1-16 之间
4. **日志级别** - 必须是 debug/info/notice/warn/error 之一
5. **持久化** - 只有在 `persistConfig: true` 或 `persist: true` 时配置才会保存到本地
