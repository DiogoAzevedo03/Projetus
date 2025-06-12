package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.network.Project
import com.example.projetus.network.ProjectResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoricoProjetosActivity : AppCompatActivity() {

    private lateinit var projectsContainer: LinearLayout
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico_projetos)

        projectsContainer = findViewById(R.id.projects_container)
        userId = intent.getIntExtra("user_id", -1)
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

        if (userId == -1) {
            Toast.makeText(this, "Utilizador não autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadProjects()

        /// Navegação
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

    private fun loadProjects() {
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

    private fun renderProjects(projetos: List<Project>) {
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

        val inflater = LayoutInflater.from(this)
        projectsContainer.removeAllViews()

        for (projeto in projetos) {
            val view = inflater.inflate(R.layout.item_project, projectsContainer, false)

            view.findViewById<TextView>(R.id.tv_project_name).text = projeto.nome
            view.findViewById<TextView>(R.id.tv_project_manager).text = "Gestor: ${projeto.gestor_nome}"
            view.findViewById<TextView>(R.id.tv_project_status).text = "Estado: ${projeto.estado}"
            view.findViewById<TextView>(R.id.tv_task_count).text = "${projeto.total_tarefas} tarefas"

            // 🔴 Esconde o botão "Ver"
            val btnVer = view.findViewById<Button>(R.id.btn_project_action)
            btnVer.visibility = Button.GONE

            // 🔵 Mantém o botão "Ver Tarefas"
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
