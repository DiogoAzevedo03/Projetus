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

class ProjectTasksActivity : AppCompatActivity() {

    private lateinit var tarefasContainer: LinearLayout
    private var userId: Int = -1
    private var projectId: Int = -1
    private var projectName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_tasks)

        tarefasContainer = findViewById(R.id.tarefas_container)

        // Receber dados do intent
        userId = intent.getIntExtra("user_id", -1)
        projectId = intent.getIntExtra("project_id", -1)
        projectName = intent.getStringExtra("project_name") ?: ""

        if (projectId == -1 || userId == -1) {
            Toast.makeText(this, "Erro ao receber dados do projeto", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Definir nome do projeto no topo
        findViewById<TextView>(R.id.tv_nome_projeto).text = projectName

        // Carregar tarefas do projeto
        loadTasks()

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

    private fun loadTasks() {
        val data = mapOf("projeto_id" to projectId)

        RetrofitClient.instance.getTasksByProject(mapOf("projeto_id" to projectId))
            .enqueue(object : Callback<TasksResponse> {
                override fun onResponse(call: Call<TasksResponse>, response: Response<TasksResponse>) {
                    Log.d("DEBUG", "Resposta recebida: ${response.body()}")

                    if (response.isSuccessful && response.body()?.success == true) {
                        val tarefas = response.body()?.tarefas ?: emptyList()
                        renderTasks(tarefas)
                    } else {
                        Log.e("DEBUG", "Falha na resposta: ${response.errorBody()?.string()}")
                        Toast.makeText(this@ProjectTasksActivity, "Erro a carregar tarefas", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TasksResponse>, t: Throwable) {
                    Log.e("DEBUG", "Erro de rede: ${t.message}", t)
                    Toast.makeText(this@ProjectTasksActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun renderTasks(tarefas: List<Task>) {
        val inflater = LayoutInflater.from(this)
        tarefasContainer.removeAllViews()

        for (tarefa in tarefas) {
            val view = inflater.inflate(R.layout.item_task, tarefasContainer, false)

            view.findViewById<TextView>(R.id.tv_task_name).text = tarefa.nome
            view.findViewById<TextView>(R.id.tv_task_date).text = tarefa.data_entrega
            val statusView = view.findViewById<TextView>(R.id.tv_task_status)
            statusView.text = tarefa.estado

            when (tarefa.estado.lowercase()) {
                "pendente" -> statusView.setBackgroundResource(R.drawable.status_bg_yellow)
                "concluída" -> statusView.setBackgroundResource(R.drawable.status_bg_green)
                "em andamento" -> statusView.setBackgroundResource(R.drawable.status_bg_blue)
                else -> statusView.setBackgroundResource(R.drawable.status_bg_yellow)
            }



            tarefasContainer.addView(view)
        }
    }
}
