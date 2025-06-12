package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.network.Task
import com.example.projetus.network.TasksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoricoTarefasActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico_tarefas)
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

        container = findViewById(R.id.tasks_container)
        userId = intent.getIntExtra("user_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "Utilizador inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarTarefasConcluidas()

        // Navegação
        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_settings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)
            startActivity(intent)
        }
    }

    private fun carregarTarefasConcluidas() {
        RetrofitClient.instance.getTasks(mapOf("user_id" to userId))
            .enqueue(object : Callback<TasksResponse> {
                override fun onResponse(call: Call<TasksResponse>, response: Response<TasksResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val tarefas = response.body()?.tarefas?.filter {
                            it.estado.equals("Concluída", ignoreCase = true)
                        } ?: emptyList()

                        val inflater = LayoutInflater.from(this@HistoricoTarefasActivity)
                        container.removeAllViews()

                        for (tarefa in tarefas) {
                            val view = inflater.inflate(R.layout.item_task, container, false)
                            view.findViewById<TextView>(R.id.tv_task_name).text = tarefa.nome
                            view.findViewById<TextView>(R.id.tv_task_date).text = tarefa.data_entrega
                            view.findViewById<TextView>(R.id.tv_task_status).text = tarefa.estado



                            container.addView(view)
                        }
                    } else {
                        Toast.makeText(this@HistoricoTarefasActivity, "Erro ao carregar tarefas", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TasksResponse>, t: Throwable) {
                    Log.e("HistoricoTarefas", "Erro: ${t.message}")
                    Toast.makeText(this@HistoricoTarefasActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
