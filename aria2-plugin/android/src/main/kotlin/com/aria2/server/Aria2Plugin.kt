package com.aria2.server

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.annotation.PermissionCallback

@CapacitorPlugin(
        name = "Aria2",
)
class Aria2Plugin : Plugin() {
    companion object {
        private const val STORAGE_ALIAS = "storage"
    }
    private val manager = Aria2()
    override fun load() {
        super.load()
        manager.load(context)
        Log.d("Aria2Plugin", "load")
    }
    private fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        val activity = this.activity ?: return
        activity.runOnUiThread { Toast.makeText(activity, message, duration).show() }
    }
    @PluginMethod
    fun checkPermission(call: PluginCall) {
        if (Environment.isExternalStorageManager()) {
            Log.d("Aria2Permission", "GRANTED")
            call.resolve()
        } else {
            try {
                showToast("请授予文件管理权限")
                val intent =
                        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                            addCategory("android.intent.category.DEFAULT")
                            data = Uri.parse("package:${context.packageName}")
                        }

                // 跳转到系统的“所有文件访问权限”页面
                activity.startActivity(intent)
                call.reject("PERMISSION_NOT_GRANTED_REDIRECTED")
            } catch (e: Exception) {
                // 某些定制系统可能无法直接跳转到特定包名页面，跳转到总列表页
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activity.startActivity(intent)
                call.reject("PERMISSION_NOT_GRANTED_REDIRECTED_GENERAL")
            }
        }
    }

    // 权限请求回调（必须实现）
    @PermissionCallback
    private fun storagePermsCallback(call: PluginCall) {
        if (hasPermission("storage")) {
            showToast("存储权限已授予")
        } else {
            showToast("存储权限被拒绝, 将无法下载文件")
        }
    }
    @PluginMethod
    fun configure(call: PluginCall) {
        Log.d("Aria2Plugin", "configure: ${call.data}")
        manager.configure(call)
    }

    @PluginMethod
    fun start(call: PluginCall) {
        Log.d("Aria2Plugin", "start: ${call.data}")
        if (!manager.start(context, call)) {
            showToast("证书获取失败")
        }
    }

    @PluginMethod
    fun stop(call: PluginCall) {
        Log.d("Aria2Plugin", "stop: ${call.data}")
        manager.stop(call)
    }
}
