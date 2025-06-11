package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PasswordRecoveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_recovery)

        val etEmail = findViewById<EditText>(R.id.et_recovery_email)
        val btnBack = findViewById<Button>(R.id.btn_back)
        val btnNext = findViewById<Button>(R.id.btn_next)
        val btnResend = findViewById<Button>(R.id.btn_resend)

        btnBack.setOnClickListener {
            finish() // volta ao login
        }

        btnNext.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Introduza o email de recuperação", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Aqui podes chamar a tua API PHP para envio de recuperação
                Toast.makeText(this, "Email de recuperação enviado (mock)", Toast.LENGTH_SHORT).show()
            }
        }

        btnResend.setOnClickListener {
            Toast.makeText(this, "Reenviando email (mock)", Toast.LENGTH_SHORT).show()
        }

        val helpIcon = findViewById<ImageView>(R.id.help_icon)

        helpIcon.setOnClickListener {
            val intent = Intent(this, AjudaActivity::class.java)
            startActivity(intent)
        }
    }
}
