package com.example.projetus

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.network.AssociarRequest
import com.example.projetus.network.Utilizador
import com.example.projetus.network.SimpleResponse
import com.example.projetus.network.UtilizadoresResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssociarColaboradoresActivity : AppCompatActivity() {

    private lateinit var layoutUtilizadores: LinearLayout
    private var projetoId: Int = -1
    private val checkBoxes = mutableListOf<Pair<CheckBox, Int>>() // (CheckBox, utilizador_id)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_associar_colaboradores)

        layoutUtilizadores = findViewById(R.id.layout_utilizadores)
        projetoId = intent.getIntExtra("projeto_id", -1)

        if (projetoId == -1) {
            Toast.makeText(this, "Projeto inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarUtilizadores()

        findViewById<Button>(R.id.btn_associar).setOnClickListener {
            associarSelecionados()
        }
    }

    private fun carregarUtilizadores() {
        // 1. Vai buscar todos os utilizadores
        RetrofitClient.instance.getUtilizadores().enqueue(object : Callback<UtilizadoresResponse> {
            override fun onResponse(call: Call<UtilizadoresResponse>, response: Response<UtilizadoresResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val todosUtilizadores = response.body()?.utilizadores ?: emptyList()

                    // 2. Vai buscar os utilizadores já associados ao projeto
                    val data = mapOf("projeto_id" to projetoId)
                    RetrofitClient.instance.getColaboradoresDoProjeto(data).enqueue(object : Callback<UtilizadoresResponse> {
                        override fun onResponse(
                            call: Call<UtilizadoresResponse>,
                            response: Response<UtilizadoresResponse>
                        ) {
                            if (response.isSuccessful && response.body()?.success == true) {
                                val associados = response.body()?.utilizadores ?: emptyList()
                                val idsAssociados = associados.map { it.id }

                                // 3. Filtrar os que ainda podem ser associados
                                val utilizadoresDisponiveis = todosUtilizadores.filter { it.id !in idsAssociados }

                                // 4. Adicionar à interface apenas os não associados
                                for (utilizador in utilizadoresDisponiveis) {
                                    val checkBox = CheckBox(this@AssociarColaboradoresActivity).apply {
                                        text = utilizador.nome
                                    }
                                    layoutUtilizadores.addView(checkBox)
                                    checkBoxes.add(Pair(checkBox, utilizador.id))
                                }
                            } else {
                                Toast.makeText(this@AssociarColaboradoresActivity, "Erro ao buscar associados", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<UtilizadoresResponse>, t: Throwable) {
                            Toast.makeText(this@AssociarColaboradoresActivity, "Falha na rede (associados)", Toast.LENGTH_SHORT).show()
                        }
                    })

                } else {
                    Toast.makeText(this@AssociarColaboradoresActivity, "Erro ao carregar utilizadores", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UtilizadoresResponse>, t: Throwable) {
                Toast.makeText(this@AssociarColaboradoresActivity, "Falha na rede (todos)", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun associarSelecionados() {
        val selecionados = checkBoxes.filter { it.first.isChecked }.map { it.second }

        if (selecionados.isEmpty()) {
            Toast.makeText(this, "Selecione pelo menos um colaborador", Toast.LENGTH_SHORT).show()
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

        Toast.makeText(this, "Colaboradores associados com sucesso!", Toast.LENGTH_SHORT).show()
        finish()
    }

}
