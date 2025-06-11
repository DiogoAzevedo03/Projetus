package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UsernameRecoveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username_recovery)

        val etEmail = findViewById<EditText>(R.id.et_username_email)
        val btnBack = findViewById<Button>(R.id.btn_back_username)
        val btnNext = findViewById<Button>(R.id.btn_next_username)
        val btnResend = findViewById<Button>(R.id.btn_resend_username)

        btnBack.setOnClickListener {
            finish() // volta ao login
        }

        btnNext.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Introduza o email de recuperação", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Chamada à API para enviar código de verificação
                Toast.makeText(this, "Código de verificação enviado (mock)", Toast.LENGTH_SHORT).show()
            }
        }

        btnResend.setOnClickListener {
            Toast.makeText(this, "A reenviar email (mock)", Toast.LENGTH_SHORT).show()
        }

        val helpIcon = findViewById<ImageView>(R.id.help_icon)

        helpIcon.setOnClickListener {
            val intent = Intent(this, AjudaActivity::class.java)
            startActivity(intent)
        }
    }
}
