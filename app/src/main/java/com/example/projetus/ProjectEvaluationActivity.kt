package com.example.projetus

import AvaliacoesResponse // Modelo de resposta com avaliações
import Evaluation // Modelo local de avaliação
import android.content.Intent // Para navegação
import android.os.Bundle // Ciclo de vida da Activity
import android.util.Log // Registos de debug
import android.view.LayoutInflater // Para inflar layouts
import android.widget.* // Vários widgets
import androidx.appcompat.app.AppCompatActivity // Activity base
import com.example.projetus.RetrofitClient // Cliente Retrofit
import com.example.projetus.network.AvaliacaoRequest // Pedido de avaliação
import com.example.projetus.network.SimpleResponse // Resposta simples
import retrofit2.Call // Chamada Retrofit
import retrofit2.Callback // Callback Retrofit
import retrofit2.Response // Resposta Retrofit

class ProjectEvaluationActivity : AppCompatActivity() { // Activity para avaliar projeto

    private lateinit var avaliacoesContainer: LinearLayout // Layout onde serão adicionadas avaliações
    private var userId = -1 // ID do utilizador
    private var projectId = -1 // ID do projeto

    private val avaliacoes = mutableListOf<Evaluation>() // Lista de avaliações locais

    override fun onCreate(savedInstanceState: Bundle?) { // Início da Activity
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_evaluation) // Layout da Activity
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador" // Tipo de perfil

        userId = intent.getIntExtra("user_id", -1) // Lê ID do utilizador
        projectId = intent.getIntExtra("projeto_id", -1) // Lê ID do projeto

        if (userId == -1 || projectId == -1) { // Verifica se IDs são válidos
            Toast.makeText(this, "Dados do projeto inválidos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        avaliacoesContainer = findViewById(R.id.avaliacoes_container) // Container de avaliações

        carregarColaboradoresComTarefas() // Carrega colaboradores e tarefas

        findViewById<Button>(R.id.btn_guardar_avaliacao).setOnClickListener { // Botão guardar
            guardarAvaliacoes() // Guarda avaliações
        }

        findViewById<Button>(R.id.btn_concluir_projeto).setOnClickListener { // Botão concluir
            concluirProjeto()
        }

        // Navegação
        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java) // Ir para dashboard
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java) // Ir para perfil
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_settings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java) // Ir para definições
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)
            startActivity(intent)
        }
    }

    private fun carregarColaboradoresComTarefas() { // Pede à API as tarefas e colaboradores
        val requestData = mapOf("projeto_id" to projectId) // Dados para a chamada

        RetrofitClient.instance.getTarefasComColaboradores(requestData)
            .enqueue(object : Callback<AvaliacoesResponse> { // Chamada assíncrona
                override fun onResponse(call: Call<AvaliacoesResponse>, response: Response<AvaliacoesResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val lista = response.body()?.avaliacoes ?: emptyList() // Lista de itens
                        val inflater = LayoutInflater.from(this@ProjectEvaluationActivity)
                        avaliacoesContainer.removeAllViews() // Limpa views
                        avaliacoes.clear() // Limpa lista local

                        for (item in lista) { // Para cada item
                            val view = inflater.inflate(R.layout.item_avaliacao, avaliacoesContainer, false) // Infla layout

                            view.findViewById<TextView>(R.id.tv_nome_colaborador).text = "${item.utilizador_nome}  ${item.tarefa_nome}" // Nome e tarefa

                            val ratingBar = view.findViewById<RatingBar>(R.id.rating_bar) // Barra de nota
                            val comentarioEt = view.findViewById<EditText>(R.id.et_comentario) // Campo comentário

                            // Preencher os campos com valores existentes (ou 0/"")
                            ratingBar.rating = (item.avaliacao_nota ?: 0).toFloat() // Nota existente
                            comentarioEt.setText(item.avaliacao_comentario ?: "") // Comentário existente

                            // Guardar na lista local para envio posterior
                            avaliacoes.add(
                                Evaluation(
                                    colaborador = item.utilizador_nome,
                                    tarefa = item.tarefa_nome,
                                    nota = item.avaliacao_nota ?: 0,
                                    comentario = item.avaliacao_comentario ?: "",
                                    tarefa_id = item.tarefa_id,
                                    utilizador_id = item.utilizador_id
                                )
                            )

                            Log.d("Avaliacao", "Adicionado: ${item.utilizador_nome} - Tarefa ${item.tarefa_id}, ID ${item.utilizador_id}, Nota=${item.avaliacao_nota}, Comentário=${item.avaliacao_comentario}") // Log para depuração

                            avaliacoesContainer.addView(view) // Adiciona view à lista
                        }

                    } else {
                        Toast.makeText(this@ProjectEvaluationActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AvaliacoesResponse>, t: Throwable) { // Erro de rede
                    Toast.makeText(this@ProjectEvaluationActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                }
            })
    }



    private fun guardarAvaliacoes() { // Envia avaliações para a API
        Log.d("Avaliacao", "Iniciando envio de avaliações...")

        for (i in 0 until avaliacoesContainer.childCount) { // Percorre views
            val view = avaliacoesContainer.getChildAt(i)
            val rating = view.findViewById<RatingBar>(R.id.rating_bar).rating.toInt() // Lê nota
            val comentario = view.findViewById<EditText>(R.id.et_comentario).text.toString() // Lê comentário

            avaliacoes[i].nota = rating // Atualiza valor local
            avaliacoes[i].comentario = comentario
        }

        for (avaliacao in avaliacoes) { // Envia cada avaliação
            val request = AvaliacaoRequest(
                tarefa_id = avaliacao.tarefa_id,
                utilizador_id = avaliacao.utilizador_id,
                nota = avaliacao.nota,
                comentario = avaliacao.comentario
            )

            Log.d("Avaliacao", "Enviando: $request") // Log do envio

            RetrofitClient.instance.avaliarTarefa(request).enqueue(object : Callback<SimpleResponse> {
                override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Log.d("Avaliacao", "Resposta OK: ${response.body()}")
                    } else {
                        Log.e("Avaliacao", "Erro na resposta: ${response.errorBody()?.string()}")
                        Toast.makeText(this@ProjectEvaluationActivity, "Erro ao guardar avaliação", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                    Log.e("Avaliacao", "Erro de rede", t)
                    Toast.makeText(this@ProjectEvaluationActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                }
            })
        }

        Toast.makeText(this, "Avaliações guardadas!", Toast.LENGTH_SHORT).show() // Confirmação
    }


    private fun concluirProjeto() { // Envia pedido para concluir projeto
        val data = mapOf("projeto_id" to projectId) // Dados do projeto

        RetrofitClient.instance.concluirProjeto(data).enqueue(object : Callback<SimpleResponse> {
            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@ProjectEvaluationActivity, "Projeto concluído!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@ProjectEvaluationActivity, "Erro ao concluir projeto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Toast.makeText(this@ProjectEvaluationActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
