package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.network.Project
import com.example.projetus.network.UtilizadoresResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_details)

        val nome = findViewById<TextView>(R.id.tv_nome)
        val descricao = findViewById<TextView>(R.id.tv_descricao)
        val inicio = findViewById<TextView>(R.id.tv_inicio)
        val fim = findViewById<TextView>(R.id.tv_fim)
        val gestor = findViewById<EditText>(R.id.et_gestor)
        val tempo = findViewById<EditText>(R.id.et_tempo)
        val progresso = findViewById<ProgressBar>(R.id.progressBar)
        val percentagem = findViewById<TextView>(R.id.tv_percent)
        val btnConcluir = findViewById<Button>(R.id.btn_concluir)
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"



        val projeto = intent.getSerializableExtra("projeto") as? Project

        if (projeto != null) {
            nome.text = "Nome: ${projeto.nome}"
            descricao.text = "Descrição: ${projeto.descricao}"
            inicio.text = "Início: ${projeto.data_inicio}"
            fim.text = "Fim: ${projeto.data_fim}"
            gestor.setText(projeto.gestor_nome)
            val totalSegundos = projeto.tempo_total_segundos
            val horas = totalSegundos / 3600
            val minutos = (totalSegundos % 3600) / 60
            val tempoFormatado = "${horas}h ${minutos}min"
            tempo.setText(tempoFormatado)
            progresso.progress = projeto.progresso.toInt()
            percentagem.text = "${projeto.progresso}%"
        }

        val userId = intent.getIntExtra("user_id", -1)

        val btnAssociarColaboradores = findViewById<Button>(R.id.btn_associar_colaboradores)

        btnAssociarColaboradores.setOnClickListener {
            val intent = Intent(this, AssociarColaboradoresActivity::class.java)
            intent.putExtra("projeto_id", projeto?.id)
            intent.putExtra("user_id", userId)
            intent.putExtra("projeto", projeto) // <-- Adiciona isto
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }


        btnConcluir.setOnClickListener {
            val intent = Intent(this, ProjectEvaluationActivity::class.java)
            intent.putExtra("projeto_id", projeto?.id)
            intent.putExtra("projeto_nome", projeto?.nome)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }
        val colaboradoresTv = findViewById<TextView>(R.id.tv_colaboradores)

        val data = mapOf("projeto_id" to (projeto?.id ?: -1))
        RetrofitClient.instance.getColaboradoresDoProjeto(data).enqueue(object : Callback<UtilizadoresResponse> {
            override fun onResponse(call: Call<UtilizadoresResponse>, response: Response<UtilizadoresResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val nomes = response.body()?.utilizadores?.joinToString(", ") { it.nome } ?: "Nenhum"
                    colaboradoresTv.text = nomes
                } else {
                    colaboradoresTv.text = "Erro ao carregar"
                }
            }

            override fun onFailure(call: Call<UtilizadoresResponse>, t: Throwable) {
                colaboradoresTv.text = "Falha na rede"
            }
        })

        val btnHome = findViewById<ImageView>(R.id.btn_home)

        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }
        val btnProfile = findViewById<ImageView>(R.id.btn_profile)
        val btnSettings = findViewById<ImageView>(R.id.btn_settings)

        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

    }
}
