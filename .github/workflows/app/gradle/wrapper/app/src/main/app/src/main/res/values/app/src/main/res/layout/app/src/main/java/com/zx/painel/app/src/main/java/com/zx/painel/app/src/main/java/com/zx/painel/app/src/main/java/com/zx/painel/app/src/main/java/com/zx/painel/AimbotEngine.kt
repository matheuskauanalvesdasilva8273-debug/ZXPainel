package com.zx.painel

import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.*

class AimbotEngine(
    private val context: Context,
    private val injector: InputInjector
) {
    var enabled = false
    var fov = 180f
    var bone = "Cabeça"
    var triggerMode = "Ao Atirar"
    var noRecoil = false
    var smoothing = 0.3f

    private val metrics: DisplayMetrics by lazy {
        DisplayMetrics().also {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.getMetrics(it)
        }
    }

    private val screenCenter: PointF get() =
        PointF(metrics.widthPixels / 2f, metrics.heightPixels / 2f)

    private var running = false
    private var loopThread: Thread? = null

    fun start() {
        if (running) return
        running = true
        loopThread = Thread {
            while (running) {
                if (enabled) tick()
                Thread.sleep(16)
            }
        }.also { it.isDaemon = true; it.start() }
    }

    fun shutdown() {
        running = false
        loopThread?.interrupt()
    }

    private fun tick() {
        val target = detectTarget() ?: return
        val center = screenCenter
        val dx = target.x - center.x
        val dy = target.y - center.y
        val dist = sqrt(dx * dx + dy * dy)

        if (dist > fov) return

        val moveX = dx * smoothing
        val moveY = dy * smoothing

        when (triggerMode) {
            "Ao Atirar" -> injector.injectSwipe(
                center.x, center.y,
                center.x + moveX, center.y + moveY,
                durationMs = 30
            )
            "Ao Olhar" -> injector.injectSwipe(
                center.x, center.y,
                center.x + moveX, center.y + moveY,
                durationMs = 50
            )
        }

        if (noRecoil) {
            injector.injectSwipe(
                center.x, center.y + moveY,
                center.x, center.y,
                durationMs = 20
            )
        }
    }

    private fun detectTarget(): PointF? = null
}
