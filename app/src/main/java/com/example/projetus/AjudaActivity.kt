package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AjudaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajuda)

        val etMensagem = findViewById<EditText>(R.id.et_email_ajuda)
        val btnEnviar = findViewById<Button>(R.id.btn_enviar_ajuda)
        val btnVoltar = findViewById<Button>(R.id.btn_voltar_login)

        btnEnviar.setOnClickListener {
            val mensagem = etMensagem.text.toString().trim()
            if (mensagem.isEmpty()) {
                Toast.makeText(this, "Escreva a sua dúvida", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Enviar mensagem via API (futuramente)
                Toast.makeText(this, "Mensagem enviada", Toast.LENGTH_SHORT).show()
                etMensagem.text.clear()
            }
        }

        btnVoltar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}
