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

class TasksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        val userId = intent.getIntExtra("user_id", -1)
        val container = findViewById<LinearLayout>(R.id.tasks_container)
        val btnAddTask = findViewById<Button>(R.id.btn_add_task)
        val btnHome = findViewById<ImageView>(R.id.btn_home)
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

        if (userId == -1) {
            Toast.makeText(this, "Utilizador inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        RetrofitClient.instance.getTasks(mapOf("user_id" to userId))
            .enqueue(object : Callback<TasksResponse> {
                override fun onResponse(call: Call<TasksResponse>, response: Response<TasksResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val tasks = response.body()?.tarefas?.filter {
                            it.estado.equals("Pendente", ignoreCase = true)
                        } ?: emptyList()

                        for (task in tasks) {
                            val view = LayoutInflater.from(this@TasksActivity)
                                .inflate(R.layout.item_task, container, false)

                            view.findViewById<TextView>(R.id.tv_task_name).text = task.nome
                            view.findViewById<TextView>(R.id.tv_task_date).text = task.data_entrega
                            view.findViewById<TextView>(R.id.tv_task_status).text = task.estado

                            // ABRIR DETALHES AO CLICAR
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
                        Toast.makeText(this@TasksActivity, "Erro ao carregar tarefas", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TasksResponse>, t: Throwable) {
                    Log.e("TasksActivity", "Erro: ${t.message}")
                    Toast.makeText(this@TasksActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                }
            })

        btnAddTask.setOnClickListener {
            Log.d("TasksActivity", "A abrir CreateTaskActivity com user_id: $userId")
            val intent = Intent(this, CreateTaskActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }


        btnHome.setOnClickListener {
            Log.d("TasksActivity", "A abrir DashboardActivity com user_id: $userId")
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        val btnSettings = findViewById<ImageView>(R.id.btn_settings)


        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        val btnProfile = findViewById<ImageView>(R.id.btn_profile)

        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

    }
}
