package com.example.projetus // Pacote principal

import android.content.Intent // Para navegação
import android.os.Bundle // Estado da Activity
import android.view.LayoutInflater // Para inflar layouts
import android.widget.Button // Botões
import android.widget.ImageView // Ícones de navegação
import android.widget.LinearLayout // Layout de lista
import android.widget.TextView // Texto
import android.widget.Toast // Mensagens rápidas
import androidx.appcompat.app.AppCompatActivity // Activity base
import com.example.projetus.network.Project // Modelo de projeto
import com.example.projetus.network.ProjectResponse // Resposta da API
import retrofit2.Call // Chamada
import retrofit2.Callback // Callback
import retrofit2.Response // Resposta

class HistoricoProjetosActivity : AppCompatActivity() { // Lista projetos concluídos

    private lateinit var projectsContainer: LinearLayout // Contém os projetos
    private var userId: Int = -1 // ID do utilizador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico_projetos) // Define o layout

        projectsContainer = findViewById(R.id.projects_container) // Container dos projetos
        userId = intent.getIntExtra("user_id", -1) // ID vindo da intent
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

        if (userId == -1) { // Se não existir utilizador
            Toast.makeText(this, "Utilizador não autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadProjects() // Carrega projetos concluídos

        /// Navegação
        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_profile).setOnClickListener { // Vai para o perfil
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_settings).setOnClickListener { // Vai para definições
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)
            startActivity(intent)
        }
    }

    private fun loadProjects() { // Obter projetos do utilizador
        val data = mapOf("utilizador_id" to userId)
        RetrofitClient.instance.getProjects(data).enqueue(object : Callback<ProjectResponse> {
            override fun onResponse(call: Call<ProjectResponse>, response: Response<ProjectResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val projetos = response.body()?.projetos?.filter {
                        it.estado.equals("Concluído", ignoreCase = true)
                    } ?: emptyList()
                    renderProjects(projetos)
                } else {
                    Toast.makeText(this@HistoricoProjetosActivity, "Erro ao carregar projetos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProjectResponse>, t: Throwable) {
                Toast.makeText(this@HistoricoProjetosActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun renderProjects(projetos: List<Project>) { // Mostra projetos na tela
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

        val inflater = LayoutInflater.from(this) // Para criar views
        projectsContainer.removeAllViews()

        for (projeto in projetos) {
            val view = inflater.inflate(R.layout.item_project, projectsContainer, false)

            view.findViewById<TextView>(R.id.tv_project_name).text = projeto.nome
            view.findViewById<TextView>(R.id.tv_project_manager).text = "Gestor: ${projeto.gestor_nome}"
            view.findViewById<TextView>(R.id.tv_project_status).text = "Estado: ${projeto.estado}"
            view.findViewById<TextView>(R.id.tv_task_count).text = "${projeto.total_tarefas} tarefas"

            //  Esconde o botão "Ver"
            val btnVer = view.findViewById<Button>(R.id.btn_project_action)
            btnVer.visibility = Button.GONE

            //  Mantém o botão "Ver Tarefas"
            view.findViewById<Button>(R.id.btn_ver_tarefas).setOnClickListener {
                val intent = Intent(this, ProjectTasksActivity::class.java)
                intent.putExtra("project_id", projeto.id)
                intent.putExtra("project_name", projeto.nome)
                intent.putExtra("user_id", userId)
                intent.putExtra("tipo_perfil", tipoPerfil)
                startActivity(intent)
            }

            projectsContainer.addView(view)
        }
    }

}
