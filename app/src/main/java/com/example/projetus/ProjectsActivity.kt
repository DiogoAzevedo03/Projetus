package com.example.projetus

import android.content.Intent // Para iniciar novas Activities
import android.os.Bundle // Ciclo de vida da Activity
import android.view.LayoutInflater // Para inflar layouts
import android.widget.* // Widgets Android
import androidx.appcompat.app.AppCompatActivity // Activity base
import com.example.projetus.network.ProjectResponse // Resposta da API com projetos
import com.example.projetus.network.Project // Modelo de projeto
import retrofit2.Call // Chamada Retrofit
import retrofit2.Callback // Callback Retrofit
import retrofit2.Response // Resposta Retrofit

class ProjectsActivity : AppCompatActivity() { // Activity que lista projetos

    private lateinit var projectsContainer: LinearLayout // Container dos projetos
    private var userId: Int = -1 // ID do utilizador

    override fun onCreate(savedInstanceState: Bundle?) { // Método chamado na criação
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects) // Define layout

        projectsContainer = findViewById(R.id.projects_container) // Container onde os projetos serão inseridos
        userId = intent.getIntExtra("user_id", -1) // Id do utilizador recebido
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador" // Tipo de perfil

        if (userId == -1) { // Caso não haja utilizador válido
            Toast.makeText(this, "Utilizador não autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadProjects() // Carrega projetos existentes

        findViewById<Button>(R.id.btn_add_project).setOnClickListener { // Botão para criar novo projeto
            val intent = Intent(this, CreateProjectActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivityForResult(intent, 1001)
        }


        val btnHome = findViewById<ImageView>(R.id.btn_home) // Botão home

        findViewById<ImageView>(R.id.btn_home).setOnClickListener { // Voltar ao dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }
        val btnProfile = findViewById<ImageView>(R.id.btn_profile) // Botão perfil
        val btnSettings = findViewById<ImageView>(R.id.btn_settings) // Botão definições

        btnProfile.setOnClickListener { // Vai para o perfil
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        btnSettings.setOnClickListener { // Abre definições
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }


    }

    private fun loadProjects() { // Chama a API para obter projetos
        val data = mapOf("utilizador_id" to userId)
        RetrofitClient.instance.getProjects(data).enqueue(object : Callback<ProjectResponse> {
            override fun onResponse(call: Call<ProjectResponse>, response: Response<ProjectResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val projetos = response.body()?.projetos?.filter {
                        it.estado.equals("Ativo", ignoreCase = true)
                    } ?: emptyList()
                    renderProjects(projetos) // Mostra apenas projetos ativos
                } else {
                    Toast.makeText(this@ProjectsActivity, "Erro ao carregar projetos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProjectResponse>, t: Throwable) {
                Toast.makeText(this@ProjectsActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun renderProjects(projetos: List<Project>) { // Insere cards de projeto
        val inflater = LayoutInflater.from(this)
        projectsContainer.removeAllViews()

        for (projeto in projetos) { // Percorre lista de projetos
            val view = inflater.inflate(R.layout.item_project, projectsContainer, false)
            val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

            view.findViewById<TextView>(R.id.tv_project_name).text = projeto.nome
            view.findViewById<TextView>(R.id.tv_project_manager).text = "Gestor: ${projeto.gestor_nome}"
            view.findViewById<TextView>(R.id.tv_project_status).text = "Estado: ${projeto.estado}"
            view.findViewById<TextView>(R.id.tv_task_count).text = "${projeto.total_tarefas} tarefas"

            val btn = view.findViewById<Button>(R.id.btn_project_action) // Botão principal do card
            btn.text = if (userId == 1) "Editar" else "Ver"
            btn.setOnClickListener {
                val intent = Intent(this, ProjectDetailsActivity::class.java)
                intent.putExtra("projeto", projeto)
                intent.putExtra("user_id", userId) // <- importante
                intent.putExtra("tipo_perfil", tipoPerfil) // <-- ADICIONA ISTO!

                startActivity(intent)
            }

            val btnVerTarefas = view.findViewById<Button>(R.id.btn_ver_tarefas) // Botão para ver tarefas
            btnVerTarefas.setOnClickListener {
                val intent = Intent(this, ProjectTasksActivity::class.java)
                intent.putExtra("project_id", projeto.id)
                intent.putExtra("project_name", projeto.nome)
                intent.putExtra("user_id", userId)
                intent.putExtra("tipo_perfil", tipoPerfil)

                startActivity(intent)
            }



            projectsContainer.addView(view) // Adiciona card ao container
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // Resultado da criação
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val created = data?.getBooleanExtra("project_created", false) ?: false
            if (created) {
                loadProjects() // Atualiza a lista ao voltar
            }
        }

    }


}
