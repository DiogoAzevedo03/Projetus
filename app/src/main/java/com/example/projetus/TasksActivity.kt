package com.example.projetus

import android.content.Intent // Navegação entre Activities
import android.os.Bundle // Dados de ciclo de vida
import android.util.Log // Logs de depuração
import android.view.LayoutInflater // Para inflar layouts dinamicamente
import android.widget.* // Componentes de UI
import androidx.appcompat.app.AppCompatActivity // Activity base
import com.example.projetus.network.Task // Modelo de tarefa
import com.example.projetus.network.TasksResponse // Modelo da resposta com tarefas
import retrofit2.Call // Chamada Retrofit
import retrofit2.Callback // Callback do Retrofit
import retrofit2.Response // Resposta Retrofit

// Activity que lista as tarefas pendentes do utilizador
class TasksActivity : AppCompatActivity() {

    // Metodo executado quando a activity é criada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        // Obtém referências aos elementos de interface
        val userId = intent.getIntExtra("user_id", -1)
        val container = findViewById<LinearLayout>(R.id.tasks_container)
        val btnAddTask = findViewById<Button>(R.id.btn_add_task)
        val btnHome = findViewById<ImageView>(R.id.btn_home)
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

        // Caso o ID não seja válido, encerra a Activity
        if (userId == -1) {
            Toast.makeText(this, getString(R.string.invalid_user), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Solicita ao servidor a lista de tarefas do utilizador
        RetrofitClient.instance.getTasks(mapOf("user_id" to userId))
            .enqueue(object : Callback<TasksResponse> {
                override fun onResponse(call: Call<TasksResponse>, response: Response<TasksResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        // Filtra apenas as tarefas pendentes
                        val tasks = response.body()?.tarefas?.filter {
                            it.estado.equals("Pendente", ignoreCase = true)
                        } ?: emptyList()

                        // Adiciona cada tarefa ao layout
                        for (task in tasks) {
                            val view = LayoutInflater.from(this@TasksActivity)
                                .inflate(R.layout.item_task, container, false)

                            view.findViewById<TextView>(R.id.tv_task_name).text = task.nome
                            view.findViewById<TextView>(R.id.tv_task_date).text = task.data_entrega
                            view.findViewById<TextView>(R.id.tv_task_status).text = task.estado

                            // Abre os detalhes da tarefa ao clicar
                            view.setOnClickListener {
                                val intent = Intent(this@TasksActivity, TaskDetailsActivity::class.java)
                                intent.putExtra("task_id", task.id)
                                intent.putExtra("user_id", userId)
                                intent.putExtra("tipo_perfil", tipoPerfil)

                                startActivity(intent)
                            }

                            container.addView(view)
                        }


                    } else {
                        Toast.makeText(this@TasksActivity, getString(R.string.error_loading_tasks), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TasksResponse>, t: Throwable) {
                    Log.e("TasksActivity", "Erro: ${t.message}")
                    Toast.makeText(this@TasksActivity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                }
            })

        // Botão para criar nova tarefa
        btnAddTask.setOnClickListener {
            Log.d("TasksActivity", "A abrir CreateTaskActivity com user_id: $userId")
            val intent = Intent(this, CreateTaskActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }


        // Botão para regressar ao dashboard
        btnHome.setOnClickListener {
            Log.d("TasksActivity", "A abrir DashboardActivity com user_id: $userId")
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        val btnSettings = findViewById<ImageView>(R.id.btn_settings)


        // Abre o ecrã de definições
        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        val btnProfile = findViewById<ImageView>(R.id.btn_profile)

        // Abre o perfil do utilizador
        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

    }
}


