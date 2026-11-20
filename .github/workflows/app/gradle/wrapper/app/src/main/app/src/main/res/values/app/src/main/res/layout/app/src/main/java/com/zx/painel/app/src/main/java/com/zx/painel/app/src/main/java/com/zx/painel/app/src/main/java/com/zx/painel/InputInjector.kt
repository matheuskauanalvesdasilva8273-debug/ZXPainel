package com.zx.painel

import android.content.Context
import rikka.shizuku.Shizuku

class InputInjector(private val context: Context) {

    private fun hasShizuku(): Boolean {
        return try {
            Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) { false }
    }

    fun injectSwipe(x1: Float, y1: Float, x2: Float, y2: Float, durationMs: Long = 30) {
        if (!hasShizuku()) return
        val cmd = "input swipe ${x1.toInt()} ${y1.toInt()} ${x2.toInt()} ${y2.toInt()} $durationMs"
        execShizuku(cmd)
    }

    fun injectTap(x: Float, y: Float) {
        if (!hasShizuku()) return
        execShizuku("input tap ${x.toInt()} ${y.toInt()}")
    }

    private fun execShizuku(cmd: String) {
        try {
            val process = Shizuku.newProcess(
                arrayOf("sh", "-c", cmd),
                null, null
            )
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
