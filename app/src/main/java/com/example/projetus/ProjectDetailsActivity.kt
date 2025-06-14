package com.example.projetus

import android.content.Intent // Navegação entre ecrãs
import android.os.Bundle // Para o ciclo de vida da Activity
import android.widget.* // Widgets usados no layout
import androidx.appcompat.app.AppCompatActivity // Activity base
import com.example.projetus.network.Project // Modelo de projeto
import com.example.projetus.network.UtilizadoresResponse // Resposta com utilizadores
import retrofit2.Call // Chamada Retrofit
import retrofit2.Callback // Callback Retrofit
import retrofit2.Response // Resposta Retrofit

class ProjectDetailsActivity : AppCompatActivity() { // Activity com detalhes do projeto

    override fun onCreate(savedInstanceState: Bundle?) { // Método principal da Activity
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_details) // Define layout

        val nome = findViewById<TextView>(R.id.tv_nome) // Texto do nome
        val descricao = findViewById<TextView>(R.id.tv_descricao) // Texto descrição
        val inicio = findViewById<TextView>(R.id.tv_inicio) // Data de início
        val fim = findViewById<TextView>(R.id.tv_fim) // Data de fim
        val gestor = findViewById<EditText>(R.id.et_gestor) // Campo gestor
        val tempo = findViewById<EditText>(R.id.et_tempo) // Campo tempo total
        val progresso = findViewById<ProgressBar>(R.id.progressBar) // Barra de progresso
        val percentagem = findViewById<TextView>(R.id.tv_percent) // Texto percentagem
        val btnConcluir = findViewById<Button>(R.id.btn_concluir) // Botão concluir
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador" // Tipo de perfil



        val projeto = intent.getSerializableExtra("projeto") as? Project // Projeto recebido na intent

        if (projeto != null) { // Se foi passado um projeto
            nome.text = "Nome: ${projeto.nome}" // Mostra nome
            descricao.text = "Descrição: ${projeto.descricao}" // Mostra descrição
            inicio.text = "Início: ${projeto.data_inicio}" // Mostra data início
            fim.text = "Fim: ${projeto.data_fim}" // Mostra data fim
            gestor.setText(projeto.gestor_nome) // Mostra gestor
            val totalSegundos = projeto.tempo_total_segundos // Converte tempo total
            val horas = totalSegundos / 3600 // Horas totais
            val minutos = (totalSegundos % 3600) / 60 // Minutos restantes
            val tempoFormatado = "${horas}h ${minutos}min" // Formato texto
            tempo.setText(tempoFormatado) // Define no campo
            progresso.progress = projeto.progresso.toInt() // Preenche barra
            percentagem.text = "${projeto.progresso}%" // Mostra percentagem
        }

        val userId = intent.getIntExtra("user_id", -1) // ID do utilizador

        val btnAssociarColaboradores = findViewById<Button>(R.id.btn_associar_colaboradores) // Botão para associar colaboradores

        btnAssociarColaboradores.setOnClickListener { // Ao clicar associar
            val intent = Intent(this, AssociarColaboradoresActivity::class.java)
            intent.putExtra("projeto_id", projeto?.id) // Passa id do projeto
            intent.putExtra("user_id", userId) // Passa id do utilizador
            intent.putExtra("projeto", projeto) // Passa projeto completo
            intent.putExtra("tipo_perfil", tipoPerfil) // Passa tipo de perfil

            startActivity(intent) // Abre atividade
        }


        btnConcluir.setOnClickListener { // Ao clicar em concluir
            val intent = Intent(this, ProjectEvaluationActivity::class.java)
            intent.putExtra("projeto_id", projeto?.id)
            intent.putExtra("projeto_nome", projeto?.nome)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent) // Inicia avaliação
        }
        val colaboradoresTv = findViewById<TextView>(R.id.tv_colaboradores) // Texto que lista colaboradores

        val data = mapOf("projeto_id" to (projeto?.id ?: -1)) // Dados para a API
        RetrofitClient.instance.getColaboradoresDoProjeto(data).enqueue(object : Callback<UtilizadoresResponse> { // Chamada para obter colaboradores
            override fun onResponse(call: Call<UtilizadoresResponse>, response: Response<UtilizadoresResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val nomes = response.body()?.utilizadores?.joinToString(", ") { it.nome } ?: "Nenhum" // Junta nomes
                    colaboradoresTv.text = nomes
                } else {
                    colaboradoresTv.text = "Erro ao carregar" // Caso de erro
                }
            }

            override fun onFailure(call: Call<UtilizadoresResponse>, t: Throwable) { // Falha de rede
                colaboradoresTv.text = "Falha na rede"
            }
        })

        val btnHome = findViewById<ImageView>(R.id.btn_home) // Botão home

        findViewById<ImageView>(R.id.btn_home).setOnClickListener { // Navega para dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }
        val btnProfile = findViewById<ImageView>(R.id.btn_profile) // Botão perfil
        val btnSettings = findViewById<ImageView>(R.id.btn_settings) // Botão definições

        btnProfile.setOnClickListener { // Vai para perfil
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        btnSettings.setOnClickListener { // Vai para definições
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

    }
}
