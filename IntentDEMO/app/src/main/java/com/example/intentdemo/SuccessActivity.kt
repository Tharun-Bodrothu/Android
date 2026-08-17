package com.example.intentdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_success)

        val username = intent.getStringExtra("USERNAME") ?: "User"
        val successMessage = findViewById<android.widget.TextView>(R.id.successMessage)
        successMessage.text = getString(R.string.login_successful, username)

        val successRoot = findViewById<android.view.View>(R.id.success_root)
        ViewCompat.setOnApplyWindowInsetsListener(successRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnOk = findViewById<Button>(R.id.btnOk)
        btnOk.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
