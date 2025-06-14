package com.example.projetus // declaração do pacote da aplicação

import android.content.Intent // usado para navegação entre ecrãs
import android.os.Bundle // fornece acesso ao ciclo de vida da activity
import android.util.Log // utilitário de registos
import android.view.LayoutInflater // infla layouts XML
import android.widget.* // disponibiliza widgets de UI
import androidx.appcompat.app.AppCompatActivity // classe base das activities
import com.example.projetus.network.Task // modelo de dados de uma tarefa
import com.example.projetus.network.TasksResponse // invólucro da resposta da API
import retrofit2.Call // objeto de chamada do Retrofit
import retrofit2.Callback // callback do Retrofit para chamadas assíncronas
import retrofit2.Response // invólucro de resposta do Retrofit

class HistoricoTarefasActivity : AppCompatActivity() { // activity que apresenta tarefas concluídas

    private lateinit var container: LinearLayout // layout onde as tarefas são adicionadas dinamicamente
    private var userId: Int = -1 // id do utilizador atual

    override fun onCreate(savedInstanceState: Bundle?) { // ponto de entrada da activity
        super.onCreate(savedInstanceState) // chama a implementação base
        setContentView(R.layout.activity_historico_tarefas) // define o layout
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador" // obtém o tipo de perfil do intent

        container = findViewById(R.id.tasks_container) // container dos itens de tarefa
        userId = intent.getIntExtra("user_id", -1) // obtém o id do utilizador

        if (userId == -1) { // verifica se o id do utilizador é válido
            Toast.makeText(this, "Utilizador inválido", Toast.LENGTH_SHORT).show()
            finish() // fecha a activity se for inválido
            return
        }

        carregarTarefasConcluidas() // carrega tarefas concluídas da API

        // Navegação
        findViewById<ImageView>(R.id.btn_home).setOnClickListener { // ir para o dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId) // passa o id do utilizador
            intent.putExtra("tipo_perfil", tipoPerfil) // passa o tipo de perfil
            startActivity(intent) // inicia a activity
        }

        findViewById<ImageView>(R.id.btn_profile).setOnClickListener { // ir para o perfil
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_settings).setOnClickListener { // ir para as definições
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)
            startActivity(intent)
        }
    }

    private fun carregarTarefasConcluidas() { // obter tarefas concluídas da API
        RetrofitClient.instance.getTasks(mapOf("user_id" to userId)) // chama o endpoint
            .enqueue(object : Callback<TasksResponse> { // enfileira pedido assíncrono
                override fun onResponse(call: Call<TasksResponse>, response: Response<TasksResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) { // verifica sucesso
                        val tarefas = response.body()?.tarefas?.filter { // filtra tarefas
                            it.estado.equals("Concluída", ignoreCase = true)
                        } ?: emptyList()

                        val inflater = LayoutInflater.from(this@HistoricoTarefasActivity) // inflater do layout
                        container.removeAllViews() // limpa as vistas anteriores

                        for (tarefa in tarefas) { // percorre as tarefas
                            val view = inflater.inflate(R.layout.item_task, container, false) // infla a linha
                            view.findViewById<TextView>(R.id.tv_task_name).text = tarefa.nome // define o nome
                            view.findViewById<TextView>(R.id.tv_task_date).text = tarefa.data_entrega // define a data
                            view.findViewById<TextView>(R.id.tv_task_status).text = tarefa.estado // define o estado

                            // adiciona a vista ao container
                            container.addView(view)
                        }
                    } else {
                        Toast.makeText(this@HistoricoTarefasActivity, "Erro ao carregar tarefas", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TasksResponse>, t: Throwable) { // callback de erro
                    Log.e("HistoricoTarefas", "Erro: ${t.message}") // regista o erro
                    Toast.makeText(this@HistoricoTarefasActivity, "Erro de rede", Toast.LENGTH_SHORT).show() // mostra erro
                }
            })
    }
}
