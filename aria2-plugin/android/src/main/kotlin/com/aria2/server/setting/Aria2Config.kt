package com.aria2.server.setting

import android.util.Log
import com.getcapacitor.JSObject
import org.json.JSONArray

class Aria2Config {
    private val values = mutableMapOf<String, SingleConfig>()

    companion object {
        val DEFAULTS =
                mapOf<String, SingleConfig>(
                        "enable-rpc" to "true".toSingleConfig(),
                        "rpc-listen-port" to "6800".toSingleConfig(),
                        "rpc-secret" to "aria2".toSingleConfig(),
                        "rpc-allow-origin-all" to "true".toSingleConfig(),
                        "continue" to "true".toSingleConfig(),
                        "max-concurrent-downloads" to "15".toSingleConfig(),
                        "max-connection-per-server" to "15".toSingleConfig(),
                        "split" to "5".toSingleConfig(),
                        "min-split-size" to "2M".toSingleConfig(),
                        "timeout" to "60".toSingleConfig(),
                        "log-level" to "notice".toSingleConfig(),
                        "dir" to "/storage/emulated/0/Download/".toSingleConfig()
                )
    }

    init {
        values.putAll(DEFAULTS)
    }
    fun fromJSObject(jsObject: JSObject): Aria2Config {
        val keys = jsObject.keys()
        keys.forEach { key ->
            val value = jsObject.opt(key)
            when (value) {
                is String -> values[key] = value.toSingleConfig()
                is JSONArray -> {
                    val list = mutableListOf<String>()
                    for (i in 0 until value.length()) {
                        list.add(value.optString(i))
                    }
                    values[key] = list.toSingleConfig()
                }
                else -> {
                    Log.w("Aria2Config", "Unexpected value for key $key: $value")
                }
            }
        }
        return this
    }

    fun toJSObject(): JSObject {
        val jsObject = JSObject()
        values.forEach { (key, value) ->
            when (value) {
                is SingleConfig.common -> jsObject.put(key, value.value)
                is SingleConfig.array -> jsObject.put(key, JSONArray(value.value))
            }
        }
        return jsObject
    }

    fun get(key: String): SingleConfig? = values[key]

    fun set(key: String, value: SingleConfig): Aria2Config {
        values[key] = value
        return this
    }

    fun merge(other: Aria2Config): Aria2Config {
        values.putAll(other.values)
        return this
    }

    fun reset(): Aria2Config {
        values.clear()
        values.putAll(DEFAULTS)
        return this
    }

    fun keys(): Set<String> = values.keys

    fun toCommandArgs(): List<String> {
        val argsList = mutableListOf<String>()
        for ((key, value) in values) {
            when (value) {
                is SingleConfig.common -> argsList.add("--$key=${value.value}")
                is SingleConfig.array -> argsList.add("--$key=${value.value.joinToString(" ")}")
            }
        }
        Log.d("Aria2Config", "toCommandArgs: $argsList")
        return argsList
    }

    fun clone(): Aria2Config {
        val newConfig = Aria2Config()
        newConfig.values.clear()
        newConfig.values.putAll(this.values)
        return newConfig
    }
}

public sealed class SingleConfig {
    class common(val value: String) : SingleConfig()
    class array(val value: List<String>) : SingleConfig()
    fun isCommon(): Boolean = this is common
    fun isArray(): Boolean = this is array
    fun getCommon(): String? =
            when (this) {
                is common -> value
                is array -> null
            }
    fun getArray(): List<String>? =
            when (this) {
                is common -> null
                is array -> value
            }
}

fun String.toSingleConfig(): SingleConfig {
    return SingleConfig.common(this)
}

fun List<String>.toSingleConfig(): SingleConfig {
    return SingleConfig.array(this)
}
