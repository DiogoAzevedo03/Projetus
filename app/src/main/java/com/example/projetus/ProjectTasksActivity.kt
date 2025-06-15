package com.example.projetus

import android.content.Intent // Navegação entre ecrãs
import android.os.Bundle // Ciclo de vida
import android.util.Log // Registos de debug
import android.view.LayoutInflater // Inflar layouts
import android.widget.* // Widgets Android
import androidx.appcompat.app.AppCompatActivity // Activity base
import com.example.projetus.network.Task // Modelo de tarefa
import com.example.projetus.network.TasksResponse // Resposta com tarefas
import retrofit2.Call // Chamada Retrofit
import retrofit2.Callback // Callback Retrofit
import retrofit2.Response // Resposta Retrofit

class ProjectTasksActivity : AppCompatActivity() { // Lista de tarefas do projeto

    private lateinit var tarefasContainer: LinearLayout // Container das tarefas
    private var userId: Int = -1 // ID do utilizador
    private var projectId: Int = -1 // ID do projeto
    private var projectName: String = "" // Nome do projeto

    override fun onCreate(savedInstanceState: Bundle?) { // onCreate
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_tasks)
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador" // Tipo de perfil

        tarefasContainer = findViewById(R.id.tarefas_container) // Container de tarefas

        // Receber dados do intent
        userId = intent.getIntExtra("user_id", -1)
        projectId = intent.getIntExtra("project_id", -1)
        projectName = intent.getStringExtra("project_name") ?: ""

        if (projectId == -1 || userId == -1) { // Validação dos dados
            Toast.makeText(this, getString(R.string.error_receiving_project_data), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Definir nome do projeto no topo
        findViewById<TextView>(R.id.tv_nome_projeto).text = projectName

        // Carregar tarefas do projeto
        loadTasks()

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

    private fun loadTasks() { // Pede tarefas do projeto
        val data = mapOf("projeto_id" to projectId)

        RetrofitClient.instance.getTasksByProject(mapOf("projeto_id" to projectId))
            .enqueue(object : Callback<TasksResponse> { // Chamada assíncrona
                override fun onResponse(call: Call<TasksResponse>, response: Response<TasksResponse>) {
                    Log.d("DEBUG", "Resposta recebida: ${response.body()}")

                    if (response.isSuccessful && response.body()?.success == true) {
                        val tarefas = response.body()?.tarefas ?: emptyList()
                        renderTasks(tarefas)
                    } else {
                        Log.e("DEBUG", "Falha na resposta: ${response.errorBody()?.string()}")
                        Toast.makeText(this@ProjectTasksActivity, getString(R.string.error_loading_tasks), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TasksResponse>, t: Throwable) {
                    Log.e("DEBUG", "Erro de rede: ${t.message}", t)
                    Toast.makeText(this@ProjectTasksActivity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun renderTasks(tarefas: List<Task>) { // Mostra tarefas no ecrã
        val inflater = LayoutInflater.from(this)
        tarefasContainer.removeAllViews()

        for (tarefa in tarefas) { // Percorre lista de tarefas
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



            tarefasContainer.addView(view) // Adiciona card ao container
        }
    }
}
