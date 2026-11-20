package com.zx.painel

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_OVERLAY = 1001
        const val REQUEST_SHIZUKU = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnShizuku = findViewById<Button>(R.id.btn_shizuku)
        val btnOverlay = findViewById<Button>(R.id.btn_overlay)
        val btnStart   = findViewById<Button>(R.id.btn_start)
        val tvStatus   = findViewById<TextView>(R.id.tv_status)

        btnShizuku.setOnClickListener {
            try {
                if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    Shizuku.requestPermission(REQUEST_SHIZUKU)
                } else {
                    tvStatus.text = "✓ Shizuku conectado"
                    btnShizuku.setBackgroundColor(0xFF2E7D32.toInt())
                }
            } catch (e: Exception) {
                tvStatus.text = "Instale o Shizuku e ative pelo app"
            }
        }

        btnOverlay.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, REQUEST_OVERLAY)
            } else {
                tvStatus.text = "✓ Overlay já permitido"
                btnOverlay.setBackgroundColor(0xFF2E7D32.toInt())
            }
        }

        btnStart.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                tvStatus.text = "Permita o overlay primeiro"
                return@setOnClickListener
            }
            val svc = Intent(this, OverlayService::class.java)
            startForegroundService(svc)
            tvStatus.text = "Painel ativo — minimize o app"
            moveTaskToBack(true)
        }
    }

    private val shizukuResultListener =
        object : Shizuku.OnRequestPermissionResultListener {
            override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                val tv = findViewById<TextView>(R.id.tv_status)
                if (grantResult == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    tv.text = "✓ Shizuku conectado"
                    findViewById<Button>(R.id.btn_shizuku)
                        .setBackgroundColor(0xFF2E7D32.toInt())
                } else {
                    tv.text = "Shizuku negado — tente novamente"
                }
            }
        }

    override fun onStart() {
        super.onStart()
        Shizuku.addRequestPermissionResultListener(shizukuResultListener)
    }

    override fun onStop() {
        super.onStop()
        Shizuku.removeRequestPermissionResultListener(shizukuResultListener)
    }
}
