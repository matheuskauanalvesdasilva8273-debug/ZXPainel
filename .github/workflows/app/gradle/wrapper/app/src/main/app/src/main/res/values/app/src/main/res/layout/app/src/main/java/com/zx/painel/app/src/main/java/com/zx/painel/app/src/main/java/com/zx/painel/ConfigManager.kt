package com.zx.painel

import android.content.Context
import org.json.JSONObject
import java.io.File

class ConfigManager(private val context: Context) {

    private val file = File(context.filesDir, "zx_config.json")

    fun save(data: Map<String, Any>) {
        val json = JSONObject()
        data.forEach { (k, v) ->
            when (v) {
                is Boolean -> json.put(k, v)
                is Float   -> json.put(k, v.toDouble())
                is String  -> json.put(k, v)
                else       -> json.put(k, v.toString())
            }
        }
        file.writeText(json.toString())
    }

    fun load(): Map<String, Any> {
        if (!file.exists()) return emptyMap()
        return try {
            val json = JSONObject(file.readText())
            val result = mutableMapOf<String, Any>()
            json.keys().forEach { k ->
                result[k] = json.get(k)
            }
            result
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
