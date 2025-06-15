package com.example.projetus // Pacote principal

import android.content.Intent // Para navegação
import android.os.Bundle // Ciclo de vida
import android.util.Log // Logs de depuração
import android.widget.* // Componentes de UI
import androidx.appcompat.app.AppCompatActivity // Activity base
import com.example.projetus.network.AssociarRequest // Modelo de associação
import com.example.projetus.network.Project // Modelo de projeto
import com.example.projetus.network.Utilizador // Modelo de utilizador
import com.example.projetus.network.SimpleResponse // Resposta simples
import com.example.projetus.network.UtilizadoresResponse // Lista de utilizadores
import retrofit2.Call // Chamada HTTP
import retrofit2.Callback // Callback do Retrofit
import retrofit2.Response // Resposta do Retrofit

class AssociarColaboradoresActivity : AppCompatActivity() { // Activity para associar colaboradores a um projeto

    private lateinit var layoutUtilizadores: LinearLayout // Layout que receberá os checkboxes
    private var projetoId: Int = -1 // Id do projeto atual
    private val checkBoxes = mutableListOf<Pair<CheckBox, Int>>() // (CheckBox, utilizador_id)

    override fun onCreate(savedInstanceState: Bundle?) { // Inicialização
        super.onCreate(savedInstanceState) // Super
        setContentView(R.layout.activity_associar_colaboradores) // Layout
        val projeto = intent.getSerializableExtra("projeto") as? Project // Projeto recebido
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador" // Perfil do utilizador

        layoutUtilizadores = findViewById(R.id.layout_utilizadores) // Layout onde serão listados
        projetoId = intent.getIntExtra("projeto_id", -1) // Id do projeto vindo da Intent

        if (projetoId == -1) { // Valida projeto
            Toast.makeText(this, getString(R.string.error_invalid_project), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarUtilizadores() // Carrega utilizadores da API

        findViewById<Button>(R.id.btn_associar).setOnClickListener { // Botão para associar
            associarSelecionados() // Chama função de associação
        }

        findViewById<Button>(R.id.btn_cancelar).setOnClickListener { // Cancelar volta aos detalhes
            val userId = intent.getIntExtra("user_id", -1)
            val projeto = intent.getSerializableExtra("projeto") as? Project

            val intent = Intent(this, ProjectDetailsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("projeto", projeto)
            intent.putExtra("tipo_perfil", tipoPerfil)
            startActivity(intent)
            finish()
        }

    } // Fim do onCreate

    private fun carregarUtilizadores() { // Obtém e lista utilizadores
        // 1. Vai procurar todos os utilizadores
        RetrofitClient.instance.getUtilizadores().enqueue(object : Callback<UtilizadoresResponse> {
            override fun onResponse(call: Call<UtilizadoresResponse>, response: Response<UtilizadoresResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val todosUtilizadores = response.body()?.utilizadores ?: emptyList()

                    // 2. Vai procurar os utilizadores já associados ao projeto
                    val data = mapOf("projeto_id" to projetoId)
                    RetrofitClient.instance.getColaboradoresDoProjeto(data).enqueue(object : Callback<UtilizadoresResponse> {
                        override fun onResponse(
                            call: Call<UtilizadoresResponse>,
                            response: Response<UtilizadoresResponse>
                        ) {
                            if (response.isSuccessful && response.body()?.success == true) {
                                val associados = response.body()?.utilizadores ?: emptyList() // Utilizadores já ligados
                                val idsAssociados = associados.map { it.id } // Ids destes utilizadores

                                // 3. Filtrar os que ainda podem ser associados
                                val utilizadoresDisponiveis = todosUtilizadores.filter { it.id !in idsAssociados }

                                // 4. Adicionar à interface apenas os não associados
                                for (utilizador in utilizadoresDisponiveis) {
                                    val checkBox = CheckBox(this@AssociarColaboradoresActivity).apply {
                                        text = utilizador.nome
                                    }
                                    layoutUtilizadores.addView(checkBox) // Mostra na tela
                                    checkBoxes.add(Pair(checkBox, utilizador.id)) // Guarda para posterior uso
                                }
                            } else {
                                Toast.makeText(this@AssociarColaboradoresActivity, getString(R.string.error_loading_associated), Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<UtilizadoresResponse>, t: Throwable) {
                            Toast.makeText(this@AssociarColaboradoresActivity, getString(R.string.error_network_associated), Toast.LENGTH_SHORT).show()
                        }
                    })

                } else {
                    Toast.makeText(this@AssociarColaboradoresActivity, getString(R.string.error_loading_users), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UtilizadoresResponse>, t: Throwable) {
                Toast.makeText(this@AssociarColaboradoresActivity, getString(R.string.error_network_users), Toast.LENGTH_SHORT).show()
            }
        })
    } // Fim de carregarUtilizadores



    private fun associarSelecionados() { // Associa os utilizadores escolhidos
        val selecionados = checkBoxes.filter { it.first.isChecked }.map { it.second }

        if (selecionados.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_select_at_least_one_collaborator), Toast.LENGTH_SHORT).show()
            return
        }

        for (utilizadorId in selecionados) {
            val request = AssociarRequest(projetoId, utilizadorId)
            RetrofitClient.instance.associarColaborador(request).enqueue(object : Callback<SimpleResponse> {
                override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                    // Apenas loga por agora
                    Log.d("ASSOCIAR", "Utilizador $utilizadorId associado")
                }

                override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                    Log.e("ASSOCIAR", "Erro ao associar $utilizadorId: ${t.message}")
                }
            })
        }

        Toast.makeText(this, getString(R.string.success_collaborators_associated), Toast.LENGTH_SHORT).show()
        finish() // Fecha a Activity após associar
    } // Fim de associarSelecionados
}
