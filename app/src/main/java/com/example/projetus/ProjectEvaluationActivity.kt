package com.example.projetus

import AvaliacoesResponse
import Evaluation
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.RetrofitClient
import com.example.projetus.network.AvaliacaoRequest
import com.example.projetus.network.SimpleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectEvaluationActivity : AppCompatActivity() {

    private lateinit var avaliacoesContainer: LinearLayout
    private var userId = -1
    private var projectId = -1

    private val avaliacoes = mutableListOf<Evaluation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_evaluation)

        userId = intent.getIntExtra("user_id", -1)
        projectId = intent.getIntExtra("projeto_id", -1)

        if (userId == -1 || projectId == -1) {
            Toast.makeText(this, "Dados do projeto inválidos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        avaliacoesContainer = findViewById(R.id.avaliacoes_container)

        carregarColaboradoresComTarefas() // este é o método a substituir com API real

        findViewById<Button>(R.id.btn_guardar_avaliacao).setOnClickListener {
            guardarAvaliacoes()
        }

        findViewById<Button>(R.id.btn_concluir_projeto).setOnClickListener {
            concluirProjeto()
        }

        // Navegação inferior
        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java).putExtra("user_id", userId))
        }
        findViewById<ImageView>(R.id.btn_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).putExtra("user_id", userId))
        }
        findViewById<ImageView>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java).putExtra("user_id", userId))
        }
    }

    private fun carregarColaboradoresComTarefas() {
        val requestData = mapOf("projeto_id" to projectId)

        RetrofitClient.instance.getTarefasComColaboradores(requestData)
            .enqueue(object : Callback<AvaliacoesResponse> {
                override fun onResponse(call: Call<AvaliacoesResponse>, response: Response<AvaliacoesResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val lista = response.body()?.avaliacoes ?: emptyList()
                        val inflater = LayoutInflater.from(this@ProjectEvaluationActivity)
                        avaliacoesContainer.removeAllViews()
                        avaliacoes.clear()

                        for (item in lista) {
                            val view = inflater.inflate(R.layout.item_avaliacao, avaliacoesContainer, false)

                            view.findViewById<TextView>(R.id.tv_nome_colaborador).text = "${item.utilizador_nome}  ${item.tarefa_nome}"

                            val ratingBar = view.findViewById<RatingBar>(R.id.rating_bar)
                            val comentarioEt = view.findViewById<EditText>(R.id.et_comentario)

                            // Preencher os campos com valores existentes (ou 0/"")
                            ratingBar.rating = (item.avaliacao_nota ?: 0).toFloat()
                            comentarioEt.setText(item.avaliacao_comentario ?: "")

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

                            Log.d("Avaliacao", "Adicionado: ${item.utilizador_nome} - Tarefa ${item.tarefa_id}, ID ${item.utilizador_id}, Nota=${item.avaliacao_nota}, Comentário=${item.avaliacao_comentario}")

                            avaliacoesContainer.addView(view)
                        }

                    } else {
                        Toast.makeText(this@ProjectEvaluationActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AvaliacoesResponse>, t: Throwable) {
                    Toast.makeText(this@ProjectEvaluationActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                }
            })
    }



    private fun guardarAvaliacoes() {
        Log.d("Avaliacao", "Iniciando envio de avaliações...")

        for (i in 0 until avaliacoesContainer.childCount) {
            val view = avaliacoesContainer.getChildAt(i)
            val rating = view.findViewById<RatingBar>(R.id.rating_bar).rating.toInt()
            val comentario = view.findViewById<EditText>(R.id.et_comentario).text.toString()

            avaliacoes[i].nota = rating
            avaliacoes[i].comentario = comentario
        }

        for (avaliacao in avaliacoes) {
            val request = AvaliacaoRequest(
                tarefa_id = avaliacao.tarefa_id,
                utilizador_id = avaliacao.utilizador_id,
                nota = avaliacao.nota,
                comentario = avaliacao.comentario
            )

            Log.d("Avaliacao", "Enviando: $request")

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

        Toast.makeText(this, "Avaliações guardadas!", Toast.LENGTH_SHORT).show()
    }


    private fun concluirProjeto() {
        val data = mapOf("projeto_id" to projectId)

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
