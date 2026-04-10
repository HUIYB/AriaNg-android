/**
 * Aria2配置值类型
 */
export type Aria2ConfigValue = string | number | boolean | string[];

/**
 * 动态配置对象类型
 */
export interface Aria2Config {
  [key: string]: Aria2ConfigValue | undefined;
}

/**
 * 插件接口定义 - 简化版
 */
export interface Aria2Plugin {
  configure(options: { config: Aria2Config; restart?: boolean }): Promise<{
    success: boolean;
    message?: string;
  }>;

  start(): Promise<{ success: boolean; port?: number; message?: string }>;

  stop(): Promise<{ success: boolean; message?: string }>;
}
