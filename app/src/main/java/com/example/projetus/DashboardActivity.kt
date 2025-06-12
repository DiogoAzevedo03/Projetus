package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.network.DashboardResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvWelcome = findViewById<TextView>(R.id.tv_welcome)
        val tvActiveProjects = findViewById<TextView>(R.id.tv_active_projects)
        val tvPendingTasks = findViewById<TextView>(R.id.tv_pending_tasks)
        val etNextTask = findViewById<EditText>(R.id.et_next_task)
        val btnHome = findViewById<ImageView>(R.id.btn_home)

        val userId = intent.getIntExtra("user_id", -1)
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

        if (userId == -1) {
            tvWelcome.text = "Erro: utilizador não autenticado"
            return
        }

        // Chamada à API do dashboard
        RetrofitClient.instance.getDashboardData(mapOf("user_id" to userId))
            .enqueue(object : Callback<DashboardResponse> {
                override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val data = response.body()!!

                        tvWelcome.text = "Bem-vindo!"
                        tvActiveProjects.text = data.projetos_ativos.toString()
                        tvPendingTasks.text = data.tarefas_pendentes.toString()
                        etNextTask.setText(
                            data.proxima_tarefa?.let {
                                "${it.data_entrega} - ${it.nome}"
                            } ?: "Nenhuma tarefa agendada"
                        )
                    } else {
                        Toast.makeText(this@DashboardActivity, "Erro ao carregar dados do dashboard", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                    Log.e("Dashboard", "Erro na API: ${t.message}")
                    Toast.makeText(this@DashboardActivity, "Erro na conexão com o servidor", Toast.LENGTH_SHORT).show()
                }
            })

        val btnStatistics = findViewById<Button>(R.id.btn_statistics)
        btnStatistics.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)
            startActivity(intent)
        }

        // Botão para ver projetos
        findViewById<Button>(R.id.btn_projects).setOnClickListener {
            val intent = Intent(this, ProjectsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }
        findViewById<Button>(R.id.btn_tasks).setOnClickListener {
            val intent = Intent(this, TasksActivity::class.java)
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

        val btnProjectHistory = findViewById<Button>(R.id.btn_project_history)
        btnProjectHistory.setOnClickListener {
            val intent = Intent(this, HistoricoProjetosActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }
        val btnTaskHistory = findViewById<Button>(R.id.btn_task_history)
        btnTaskHistory.setOnClickListener {
            val intent = Intent(this, HistoricoTarefasActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }


        val btnProfile = findViewById<ImageView>(R.id.btn_profile)
        val btnSettings = findViewById<ImageView>(R.id.btn_settings)

        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        val btnUserManagement = findViewById<Button>(R.id.btn_user_management)

        btnUserManagement.setOnClickListener {
            val intent = Intent(this, UserManagementActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        val btnProjects = findViewById<Button>(R.id.btn_projects)
        val btnTasks = findViewById<Button>(R.id.btn_tasks)

        when (tipoPerfil) {
            "administrador" -> {
                // Pode tudo
                btnUserManagement.visibility = Button.VISIBLE
                btnProjects.visibility = Button.VISIBLE
                btnTasks.visibility = Button.VISIBLE
                btnStatistics.visibility = Button.VISIBLE
                btnProjectHistory.visibility = Button.VISIBLE
                btnTaskHistory.visibility = Button.VISIBLE
            }

            "gestor" -> {
                btnUserManagement.visibility = Button.GONE  // não pode gerir utilizadores
                btnProjects.visibility = Button.VISIBLE
                btnTasks.visibility = Button.VISIBLE
                btnStatistics.visibility = Button.VISIBLE
                btnProjectHistory.visibility = Button.VISIBLE
                btnTaskHistory.visibility = Button.VISIBLE
            }

            "utilizador" -> {
                btnUserManagement.visibility = Button.GONE
                btnProjects.visibility = Button.GONE
                btnTasks.visibility = Button.VISIBLE  // só pode ver tarefas
                btnStatistics.visibility = Button.GONE
                btnProjectHistory.visibility = Button.GONE
                btnTaskHistory.visibility = Button.VISIBLE  // ver histórico
            }
        }


    }
}

