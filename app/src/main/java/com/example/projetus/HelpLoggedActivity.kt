package com.example.projetus // Pacote principal

import android.content.Intent // Para navegação
import android.os.Bundle // Estado da Activity
import android.widget.* // Widgets
import androidx.appcompat.app.AppCompatActivity // Activity base

class HelpLoggedActivity : AppCompatActivity() { // Ecrã de ajuda para utilizadores autenticados
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_logged) // Define o layout

        val userId = intent.getIntExtra("user_id", -1) // ID do utilizador
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador" // Perfil

        val etMessage = findViewById<EditText>(R.id.et_message) // Campo de mensagem
        val btnSend = findViewById<Button>(R.id.btn_send) // Botão enviar

        btnSend.setOnClickListener { // Envio da mensagem
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
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_settings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }
    }
}
