# aria2server

a server for aria2

## Install

To use npm

```bash
npm install aria2server
````

To use yarn

```bash
yarn add aria2server
```

Sync native files

```bash
npx cap sync
```

## API

<docgen-index>

* [`configure(...)`](#configure)
* [`start()`](#start)
* [`stop()`](#stop)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

插件接口定义 - 简化版

### configure(...)

```typescript
configure(options: { config: Aria2Config; restart?: boolean; }) => Promise<{ success: boolean; message?: string; }>
```

| Param         | Type                                                                                |
| ------------- | ----------------------------------------------------------------------------------- |
| **`options`** | <code>{ config: <a href="#aria2config">Aria2Config</a>; restart?: boolean; }</code> |

**Returns:** <code>Promise&lt;{ success: boolean; message?: string; }&gt;</code>

--------------------


### start()

```typescript
start() => Promise<{ success: boolean; port?: number; message?: string; }>
```

**Returns:** <code>Promise&lt;{ success: boolean; port?: number; message?: string; }&gt;</code>

--------------------


### stop()

```typescript
stop() => Promise<{ success: boolean; message?: string; }>
```

**Returns:** <code>Promise&lt;{ success: boolean; message?: string; }&gt;</code>

--------------------


### Interfaces


#### Aria2Config

动态配置对象类型

</docgen-api>
