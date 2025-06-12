package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.network.ProjectResponse
import com.example.projetus.network.Project
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectsActivity : AppCompatActivity() {

    private lateinit var projectsContainer: LinearLayout
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)

        projectsContainer = findViewById(R.id.projects_container)
        userId = intent.getIntExtra("user_id", -1)
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

        if (userId == -1) {
            Toast.makeText(this, "Utilizador não autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadProjects()

        findViewById<Button>(R.id.btn_add_project).setOnClickListener {
            val intent = Intent(this, CreateProjectActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivityForResult(intent, 1001)
        }


        val btnHome = findViewById<ImageView>(R.id.btn_home)

        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
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


    }

    private fun loadProjects() {
        val data = mapOf("utilizador_id" to userId)
        RetrofitClient.instance.getProjects(data).enqueue(object : Callback<ProjectResponse> {
            override fun onResponse(call: Call<ProjectResponse>, response: Response<ProjectResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val projetos = response.body()?.projetos?.filter {
                        it.estado.equals("Ativo", ignoreCase = true)
                    } ?: emptyList()
                    renderProjects(projetos)
                } else {
                    Toast.makeText(this@ProjectsActivity, "Erro ao carregar projetos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProjectResponse>, t: Throwable) {
                Toast.makeText(this@ProjectsActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun renderProjects(projetos: List<Project>) {
        val inflater = LayoutInflater.from(this)
        projectsContainer.removeAllViews()

        for (projeto in projetos) {
            val view = inflater.inflate(R.layout.item_project, projectsContainer, false)
            val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

            view.findViewById<TextView>(R.id.tv_project_name).text = projeto.nome
            view.findViewById<TextView>(R.id.tv_project_manager).text = "Gestor: ${projeto.gestor_nome}"
            view.findViewById<TextView>(R.id.tv_project_status).text = "Estado: ${projeto.estado}"
            view.findViewById<TextView>(R.id.tv_task_count).text = "${projeto.total_tarefas} tarefas"

            val btn = view.findViewById<Button>(R.id.btn_project_action)
            btn.text = if (userId == 1) "Editar" else "Ver"
            btn.setOnClickListener {
                val intent = Intent(this, ProjectDetailsActivity::class.java)
                intent.putExtra("projeto", projeto)
                intent.putExtra("user_id", userId) // <- importante
                intent.putExtra("tipo_perfil", tipoPerfil) // <-- ADICIONA ISTO!

                startActivity(intent)
            }

            val btnVerTarefas = view.findViewById<Button>(R.id.btn_ver_tarefas)
            btnVerTarefas.setOnClickListener {
                val intent = Intent(this, ProjectTasksActivity::class.java)
                intent.putExtra("project_id", projeto.id)
                intent.putExtra("project_name", projeto.nome)
                intent.putExtra("user_id", userId)
                startActivity(intent)
            }



            projectsContainer.addView(view)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val created = data?.getBooleanExtra("project_created", false) ?: false
            if (created) {
                loadProjects() // Atualiza a lista ao voltar
            }
        }

    }


}
