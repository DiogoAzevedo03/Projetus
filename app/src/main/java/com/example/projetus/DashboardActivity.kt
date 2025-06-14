package com.example.projetus // Pacote principal

import android.content.Intent // Para mudar de Activity
import android.os.Bundle // Dados da Activity
import android.util.Log // Utilizado para logs
import android.widget.Button // Botões da interface
import android.widget.EditText // Campo editável
import android.widget.ImageView // Ícones da barra inferior
import android.widget.TextView // Exibição de texto
import android.widget.Toast // Mensagens rápidas
import androidx.appcompat.app.AppCompatActivity // Activity com ActionBar
import com.example.projetus.network.DashboardResponse // Modelo de resposta da dashboard
import retrofit2.Call // Chamada da API
import retrofit2.Callback // Callback de API
import retrofit2.Response // Resposta da API
import androidx.lifecycle.lifecycleScope // Para coroutines ligadas ao ciclo de vida
import com.example.projetus.data.local.AppDatabase // Base de dados local
import kotlinx.coroutines.Dispatchers // Dispatcher das coroutines
import kotlinx.coroutines.launch // Lançamento de coroutines
import kotlinx.coroutines.withContext // Mudança de contexto

class DashboardActivity : AppCompatActivity() { // Ecrã principal depois do login
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard) // Define o layout

        syncPendingUsers() // Sincroniza utilizadores pendentes com a API


        val tvWelcome = findViewById<TextView>(R.id.tv_welcome) // Texto de boas-vindas
        val tvActiveProjects = findViewById<TextView>(R.id.tv_active_projects) // Número de projetos ativos
        val tvPendingTasks = findViewById<TextView>(R.id.tv_pending_tasks) // Número de tarefas pendentes
        val etNextTask = findViewById<EditText>(R.id.et_next_task) // Próxima tarefa
        val btnHome = findViewById<ImageView>(R.id.btn_home) // Ícone home

        val userId = intent.getIntExtra("user_id", -1) // ID do utilizador
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador" // Perfil recebido

        if (userId == -1) { // Se não veio utilizador
            tvWelcome.text = "Erro: utilizador não autenticado"
            return
        }

        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) { // Busca no BD local
                AppDatabase.getDatabase(this@DashboardActivity).userDao().getById(userId)
            }
            user?.let { tvWelcome.text = "Bem-vindo, ${it.nome}!" }
        }

        // Chamada à API do dashboard
        RetrofitClient.instance.getDashboardData(mapOf("user_id" to userId))
            .enqueue(object : Callback<DashboardResponse> { // Chamada à API
                override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val data = response.body()!!

                        tvActiveProjects.text = data.projetos_ativos.toString() // Mostra projetos ativos
                        tvPendingTasks.text = data.tarefas_pendentes.toString() // Mostra tarefas pendentes
                        etNextTask.setText(
                            data.proxima_tarefa?.let {
                                "${it.data_entrega} - ${it.nome}"
                            } ?: "Nenhuma tarefa agendada"
                        )
                    } else {
                        Toast.makeText(applicationContext, "Erro ao carregar dados do dashboard", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DashboardResponse>, t: Throwable) { // Erro da API
                    Log.e("Dashboard", "Erro na API: ${t.message}")
                    Toast.makeText(applicationContext, "Erro na conexão com o servidor", Toast.LENGTH_SHORT).show()
                }
            })

        val btnStatistics = findViewById<Button>(R.id.btn_statistics) // Botão estatísticas
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

        btnHome.setOnClickListener { // Navegação para o próprio dashboard
            Log.d("TasksActivity", "A abrir DashboardActivity com user_id: $userId")
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        val btnProjectHistory = findViewById<Button>(R.id.btn_project_history) // Histórico de projetos
        btnProjectHistory.setOnClickListener {
            val intent = Intent(this, HistoricoProjetosActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }
        val btnTaskHistory = findViewById<Button>(R.id.btn_task_history) // Histórico de tarefas
        btnTaskHistory.setOnClickListener {
            val intent = Intent(this, HistoricoTarefasActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }


        val btnProfile = findViewById<ImageView>(R.id.btn_profile) // Ícone perfil
        val btnSettings = findViewById<ImageView>(R.id.btn_settings) // Ícone definições

        btnProfile.setOnClickListener { // Vai para o perfil
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        btnSettings.setOnClickListener { // Vai para as definições
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        val btnUserManagement = findViewById<Button>(R.id.btn_user_management) // Gestão de utilizadores

        btnUserManagement.setOnClickListener {
            val intent = Intent(this, UserManagementActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        val btnProjects = findViewById<Button>(R.id.btn_projects) // Botão projetos
        val btnTasks = findViewById<Button>(R.id.btn_tasks) // Botão tarefas

        when (tipoPerfil) {
            "administrador" -> { // Perfil administrador
                btnUserManagement.visibility = Button.VISIBLE
                btnProjects.visibility = Button.VISIBLE
                btnTasks.visibility = Button.VISIBLE
                btnStatistics.visibility = Button.VISIBLE
                btnProjectHistory.visibility = Button.VISIBLE
                btnTaskHistory.visibility = Button.VISIBLE
            }

            "gestor" -> { // Perfil gestor
                btnUserManagement.visibility = Button.GONE  // não pode gerir utilizadores
                btnProjects.visibility = Button.VISIBLE
                btnTasks.visibility = Button.VISIBLE
                btnStatistics.visibility = Button.VISIBLE
                btnProjectHistory.visibility = Button.VISIBLE
                btnTaskHistory.visibility = Button.VISIBLE
            }

            "utilizador" -> { // Perfil comum
                btnUserManagement.visibility = Button.GONE
                btnProjects.visibility = Button.GONE
                btnTasks.visibility = Button.VISIBLE  // só pode ver tarefas
                btnStatistics.visibility = Button.GONE
                btnProjectHistory.visibility = Button.GONE
                btnTaskHistory.visibility = Button.VISIBLE  // ver histórico
            }
        }


    }
    private fun syncPendingUsers() { // Sincroniza utilizadores guardados localmente
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val dao = AppDatabase.getDatabase(this@DashboardActivity).pendingUserDao()
                val pendentes = dao.getAll() // Obtém todos os registos pendentes

                for (user in pendentes) {
                    val call = RetrofitClient.instance.registerUser( // Chamada síncrona para registar
                        user.nome,
                        user.username,
                        user.email,
                        user.password,
                        user.tipo_perfil,
                        user.foto ?: ""
                    )

                    val response = call.execute() // Executa a chamada

                    if (response.isSuccessful && response.body()?.success == true) {
                        dao.delete(user)
                        Log.d("Sync", "Utilizador ${user.username} sincronizado.") // Log de sucesso
                    } else {
                        Log.e("Sync", "Erro ao sincronizar ${user.username}: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("Sync", "Erro geral ao sincronizar: ${e.message}") // Erro inesperado
            }
        }
    }

}

