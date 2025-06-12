package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

import com.example.projetus.LocaleManager

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
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

        // Exemplo: enviar sugestão
        btnEnviar.setOnClickListener {
            val sugestao = etSugestao.text.toString()
            Toast.makeText(this, "Sugestão enviada: $sugestao", Toast.LENGTH_SHORT).show()
            etSugestao.text.clear()
        }

        btnPt.setOnClickListener {
            LocaleManager.setLocale(this, "pt")
            Toast.makeText(this, getString(R.string.toast_lang_pt), Toast.LENGTH_SHORT).show()
            recreate()
        }

        btnEn.setOnClickListener {
            LocaleManager.setLocale(this, "en")
            Toast.makeText(this, getString(R.string.toast_lang_en), Toast.LENGTH_SHORT).show()
            recreate()
        }

        // Navegação inferior
        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_perfil).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        val btnAjuda = findViewById<ImageView>(R.id.btn_help_top)

        btnAjuda.setOnClickListener {
            val intent = Intent(this, HelpLoggedActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }


    }
}
