package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class HelpLoggedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_logged)

        val userId = intent.getIntExtra("user_id", -1)

        val etMessage = findViewById<EditText>(R.id.et_message)
        val btnSend = findViewById<Button>(R.id.btn_send)

        btnSend.setOnClickListener {
            val msg = etMessage.text.toString()
            if (msg.isNotEmpty()) {
                Toast.makeText(this, "Mensagem enviada com sucesso!", Toast.LENGTH_SHORT).show()
                etMessage.text.clear()
            } else {
                Toast.makeText(this, "Escreve uma mensagem primeiro", Toast.LENGTH_SHORT).show()
            }
        }

        // Navegação inferior
        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_settings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }
    }
}
