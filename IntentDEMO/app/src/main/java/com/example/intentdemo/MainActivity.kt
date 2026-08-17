package com.example.intentdemo

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.intentdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: MyReceiver
    private lateinit var i_f: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        i_f = IntentFilter().apply{
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        receiver = MyReceiver()
        registerReceiver(receiver, i_f)
        Log.d("Receiver", "Receiver Registered")

        binding.btnLogin.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            
            if (username == "Tharun" && password == "Tharun@123") {
                val intent = Intent(this, SuccessActivity::class.java)
                intent.putExtra("USERNAME", username)
                startActivity(intent)
            } else if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.enter_both, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnClear.setOnClickListener {
            binding.username.text.clear()
            binding.password.text.clear()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        Log.d("Receiver", "Receiver Unregistered")
    }
}
