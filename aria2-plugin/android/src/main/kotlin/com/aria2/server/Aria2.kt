package com.aria2.server

import android.content.Context
import android.util.Log
import com.aria2.server.setting.Aria2Config
import com.aria2.server.setting.ConfigManager
import com.aria2.server.setting.SingleConfig
import com.getcapacitor.JSObject
import com.getcapacitor.PluginCall
import java.io.File
import java.io.FileOutputStream

class Aria2 {
    private var aria2Process: Process? = null
    private lateinit var aria2Binary: File
    private lateinit var configManager: ConfigManager
    private lateinit var currentConfig: Aria2Config
    private var currentPort: Int = 6800

    companion object {
        private const val TAG = "Aria2"
    }
    /**
     * 将 assets 里的文件复制到应用私有目录（如果不存在的话）
     * @param context 上下文
     * @param fileName 文件名（如 "cacert.pem"）
     * @return 目标文件的绝对路径
     */
    fun prepareAssetFile(context: Context, fileName: String): String? {
        // 获取应用私有文件目录中的目标文件对象
        val destFile = File(context.filesDir, fileName)

        if (destFile.exists() && destFile.length() > 0) {
            return destFile.absolutePath
        }
        Log.d(TAG, "prepareAssetFile: 复制文件 $fileName")
        // 文件不存在或已被损坏，执行复制逻辑
        return try {
            context.assets.open(fileName).use { inputStream ->
                FileOutputStream(destFile).use { outputStream -> inputStream.copyTo(outputStream) }
            }
            Log.d(TAG, "prepareAssetFile: 复制成功${destFile.absolutePath}")
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun load(ctx: Context) {
        try {
            aria2Binary = File(ctx.applicationInfo.nativeLibraryDir, "libaria2c.so")

            if (!aria2Binary.exists()) {
                throw IllegalStateException("未找到 aria2")
            }
            aria2Binary.setExecutable(true)

            configManager = ConfigManager(ctx)
            currentConfig = configManager.loadConfig()

            Log.i(TAG, "Aria2 plugin loaded")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load aria2", e)
        }
    }

    fun configure(call: PluginCall) {
        Log.i(TAG, "configure called")
        try {
            val configJson = call.getObject("config")
            if (configJson == null || configJson.length() == 0) {
                call.reject("配置不能为空")
                return
            }

            val newConfig = Aria2Config().fromJSObject(configJson)

            configManager.saveConfig(newConfig)
            currentConfig = newConfig

            val restart = call.getBoolean("restart") ?: false
            if (restart) {
                if (aria2Process?.isAlive == true) {
                    stopInternal()
                }
                startInternal()
                call.resolve(JSObject().put("success", true).put("message", "配置已保存，服务已重启"))
            } else {
                call.resolve(JSObject().put("success", true).put("message", "配置已保存"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to configure", e)
            call.reject("配置失败: ${e.message}")
        }
    }

    private fun startInternal() {
        val args = currentConfig.toCommandArgs()
        currentPort = currentConfig.get("rpcPort")?.getCommon()?.toIntOrNull() ?: 6800

        Log.i(TAG, "Starting aria2 with args: $args")

        val pb =
                ProcessBuilder(aria2Binary.absolutePath).apply {
                    command().addAll(args)
                    redirectErrorStream(true)
                }

        aria2Process = pb.start()

        Thread {
                    try {
                        aria2Process?.inputStream?.bufferedReader()?.forEachLine { line ->
                            Log.i(TAG, line)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error reading logs", e)
                    }
                }
                .start()
    }

    private fun stopInternal() {
        aria2Process?.destroyForcibly()
        aria2Process = null
    }

    fun start(ctx: Context, call: PluginCall): Boolean {
        val certPath = prepareAssetFile(ctx, "cacert.pem")
        if (certPath == null) {
            currentConfig.set("check-certificate", SingleConfig.common("false"))
        } else {
            currentConfig.set("check-certificate", SingleConfig.common("true"))
            currentConfig.set("ca-certificate", SingleConfig.common(certPath))
        }
        if (aria2Process?.isAlive == true) {
            call.resolve(
                    JSObject().put("success", true).put("message", "已在运行").put("port", currentPort)
            )
        }

        try {
            configManager.saveConfig(currentConfig)
            startInternal()
            call.resolve(JSObject().put("success", true).put("port", currentPort))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start", e)
            call.reject("启动失败: ${e.message}")
        }
        return certPath != null
    }

    fun stop(call: PluginCall) {
        try {
            stopInternal()
            call.resolve(JSObject().put("success", true).put("message", "已停止"))
        } catch (e: Exception) {
            call.reject("停止失败: ${e.message}")
        }
    }

    fun isRunning(call: PluginCall) {
        val running = aria2Process?.isAlive == true
        call.resolve(
                JSObject().put("running", running).put("port", if (running) currentPort else null)
        )
    }

    fun getConfig(call: PluginCall) {
        call.resolve(JSObject().put("config", currentConfig.toJSObject()))
    }
}
