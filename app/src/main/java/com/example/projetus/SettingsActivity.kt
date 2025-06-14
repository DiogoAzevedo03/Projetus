package com.example.projetus // Pacote da aplicação

import android.content.Intent // Utilizado para iniciar novas Activities
import android.os.Bundle // Contém dados do ciclo de vida
import android.widget.* // Widgets de interface do usuário
import androidx.appcompat.app.AppCompatActivity // Activity com ActionBar

import com.example.projetus.LocaleManager // Classe responsável pela gestão de idioma

// Activity responsável pelas definições da aplicação
class SettingsActivity : AppCompatActivity() {

    // Identificador do utilizador atual
    private var userId: Int = -1

    // Método executado aquando da criação da Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings) // Define o layout

        // Recupera o ID do utilizador enviado na intent
        userId = intent.getIntExtra("user_id", -1)

        // Elementos de interface
        val etSugestao = findViewById<EditText>(R.id.et_sugestao)
        val btnEnviar = findViewById<Button>(R.id.btn_enviar)
        val btnPt = findViewById<Button>(R.id.btn_pt)
        val btnEn = findViewById<Button>(R.id.btn_en)
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

        // Envia a sugestão escrita pelo utilizador
        btnEnviar.setOnClickListener {
            val sugestao = etSugestao.text.toString()
            Toast.makeText(this, "Sugestão enviada: $sugestao", Toast.LENGTH_SHORT).show()
            etSugestao.text.clear() // Limpa o campo de texto
        }

        // Troca o idioma para Português
        btnPt.setOnClickListener {
            LocaleManager.setLocale(this, "pt")
            Toast.makeText(this, getString(R.string.toast_lang_pt), Toast.LENGTH_SHORT).show()
            recreate() // Recria a activity para aplicar a mudança
        }

        // Troca o idioma para Inglês
        btnEn.setOnClickListener {
            LocaleManager.setLocale(this, "en")
            Toast.makeText(this, getString(R.string.toast_lang_en), Toast.LENGTH_SHORT).show()
            recreate() // Recria a activity
        }

        // Navegação inferior
        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent) // Abre o dashboard
        }

        findViewById<ImageView>(R.id.btn_perfil).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent) // Abre o perfil
        }

        // Botão de ajuda no topo do ecrã
        val btnAjuda = findViewById<ImageView>(R.id.btn_help_top)

        btnAjuda.setOnClickListener {
            val intent = Intent(this, HelpLoggedActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent) // Abre a secção de ajuda
        }


    }
}

