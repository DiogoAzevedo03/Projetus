package com.example.projetus // declaração do pacote

import android.content.Intent // para iniciar outras activities
import android.os.Bundle // aceder ao estado guardado
import android.widget.* // importação de widgets
import androidx.appcompat.app.AppCompatActivity // classe base de activity

class PasswordRecoveryActivity : AppCompatActivity() { // ecrã para recuperar palavra-passe

    override fun onCreate(savedInstanceState: Bundle?) { // ponto de entrada do ciclo de vida
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_recovery) // define o layout

        val etEmail = findViewById<EditText>(R.id.et_recovery_email) // campo de email
        val btnBack = findViewById<Button>(R.id.btn_back) // botão voltar
        val btnNext = findViewById<Button>(R.id.btn_next) // botão continuar
        val btnResend = findViewById<Button>(R.id.btn_resend) // botão reenviar código

        btnBack.setOnClickListener { // voltar ao ecrã de login
            finish()
        }

        btnNext.setOnClickListener { // enviar email de recuperação
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Introduza o email de recuperação", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Aqui podes chamar a tua API PHP para envio de recuperação
                Toast.makeText(this, "Email de recuperação enviado (mock)", Toast.LENGTH_SHORT).show()
            }
        }

        btnResend.setOnClickListener { // reenviar email
            Toast.makeText(this, "Reenviando email (mock)", Toast.LENGTH_SHORT).show()
        }

        val helpIcon = findViewById<ImageView>(R.id.help_icon) // ícone de ajuda

        helpIcon.setOnClickListener { // abrir página de ajuda
            val intent = Intent(this, AjudaActivity::class.java)
            startActivity(intent)
        }
    }
}
