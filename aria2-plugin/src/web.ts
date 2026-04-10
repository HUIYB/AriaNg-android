import { WebPlugin } from '@capacitor/core';

import type { Aria2Plugin, Aria2Config } from './definitions';

export class Aria2Web extends WebPlugin implements Aria2Plugin {
  private currentConfig: Aria2Config = {};
  private isRunning = false;
  private port = 6800;

  async configure(options: {
    config: Aria2Config;
    restart?: boolean;
  }): Promise<{ success: boolean; message?: string }> {
    const { config, restart } = options;

    this.currentConfig = { ...this.currentConfig, ...config };
    this.port = (config.rpcPort as number) || 6800;

    console.log('Aria2 configured:', this.currentConfig);

    if (restart) {
      this.isRunning = false;
      this.isRunning = true;
      console.log('Aria2 restarted (Web mode)');
    }

    return {
      success: true,
      message: restart ? '配置已保存，服务已重启' : '配置已保存',
    };
  }

  async start(): Promise<{ success: boolean; port?: number; message?: string }> {
    if (this.isRunning) {
      return { success: true, port: this.port, message: '已在运行' };
    }
    this.isRunning = true;
    console.log('Aria2 started (Web mode)');
    return { success: true, port: this.port };
  }

  async stop(): Promise<{ success: boolean; message?: string }> {
    this.isRunning = false;
    console.log('Aria2 stopped (Web mode)');
    return { success: true, message: '已停止' };
  }
}
