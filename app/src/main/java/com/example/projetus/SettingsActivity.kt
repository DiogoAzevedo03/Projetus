package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        userId = intent.getIntExtra("user_id", -1)

        val etSugestao = findViewById<EditText>(R.id.et_sugestao)
        val btnEnviar = findViewById<Button>(R.id.btn_enviar)
        val btnPt = findViewById<Button>(R.id.btn_pt)
        val btnEn = findViewById<Button>(R.id.btn_en)

        // Exemplo: enviar sugestão
        btnEnviar.setOnClickListener {
            val sugestao = etSugestao.text.toString()
            Toast.makeText(this, "Sugestão enviada: $sugestao", Toast.LENGTH_SHORT).show()
            etSugestao.text.clear()
        }

        btnPt.setOnClickListener {
            Toast.makeText(this, "Idioma definido para Português", Toast.LENGTH_SHORT).show()
        }

        btnEn.setOnClickListener {
            Toast.makeText(this, "Idioma definido para Inglês", Toast.LENGTH_SHORT).show()
        }

        // Navegação inferior
        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_perfil).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        val btnAjuda = findViewById<ImageView>(R.id.btn_help_top)

        btnAjuda.setOnClickListener {
            val intent = Intent(this, HelpLoggedActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }


    }
}
