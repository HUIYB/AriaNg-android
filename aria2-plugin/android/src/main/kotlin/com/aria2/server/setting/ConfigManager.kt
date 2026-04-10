package com.aria2.server.setting

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/** 配置管理器 - 支持持久化和内存缓存 */
class ConfigManager(private val context: Context) {
    private val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private var cachedConfig: Aria2Config? = null

    companion object {
        private const val PREFS_NAME = "aria2_config"
        private const val KEY_CONFIG_JSON = "config_json"
    }

    /** 保存配置到持久化存储 */
    fun saveConfig(config: Aria2Config) {
        val jsonString = config.toJSObject().toString()
        prefs.edit().putString(KEY_CONFIG_JSON, jsonString).apply()
        cachedConfig = config.clone()
    }

    /** 从持久化存储加载配置 */
    fun loadConfig(): Aria2Config {
        // 返回缓存的配置
        cachedConfig?.let {
            return it.clone()
        }

        val jsonString = prefs.getString(KEY_CONFIG_JSON, null)
        val config =
                if (jsonString != null) {
                    try {
                        val jsObject = com.getcapacitor.JSObject(jsonString)
                        Aria2Config().fromJSObject(jsObject)
                    } catch (e: Exception) {
                        Log.e("ConfigManager", "Failed to load config from JSON", e)
                        Aria2Config() // 返回默认配置
                    }
                } else {
                    Log.e("ConfigManager", "No config found in preferences")
                    Aria2Config() // 返回默认配置
                }

        cachedConfig = config.clone()
        return config
    }

    /** 清除持久化配置 */
    fun clearConfig() {
        prefs.edit().remove(KEY_CONFIG_JSON).apply()
        cachedConfig = null
    }

    /** 检查是否有持久化配置 */
    fun hasPersistedConfig(): Boolean {
        return prefs.contains(KEY_CONFIG_JSON)
    }

    /** 导出配置为JSON字符串 */
    fun exportConfig(): String {
        return loadConfig().toJSObject().toString()
    }

    /** 从JSON字符串导入配置 */
    fun importConfig(jsonString: String): Boolean {
        return try {
            val jsObject = com.getcapacitor.JSObject(jsonString)
            val config = Aria2Config().fromJSObject(jsObject)
            saveConfig(config)
            true
        } catch (e: Exception) {
            Log.e("ConfigManager", "importConfig failed: ${e.message}", e)
            false
        }
    }
}
