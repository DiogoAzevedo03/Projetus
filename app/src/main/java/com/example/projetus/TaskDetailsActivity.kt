package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.network.TaskDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.projetus.network.GenericResponse
import com.example.projetus.network.UpdateTaskRequest

class TaskDetailsActivity : AppCompatActivity() {
    private var tarefaId: Int = -1
    private var utilizadorId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DEBUG", "tarefaId: $tarefaId | utilizadorId: $utilizadorId")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        tarefaId = intent.getIntExtra("task_id", -1)
        utilizadorId = intent.getIntExtra("user_id", -1) // este também deve vir da intent

        val nome = findViewById<TextView>(R.id.tv_nome)
        val dataEntrega = findViewById<TextView>(R.id.tv_entrega)
        val progresso = findViewById<ProgressBar>(R.id.progressBar)
        val percentagem = findViewById<TextView>(R.id.tv_percent)
        val tempo = findViewById<EditText>(R.id.et_tempo)
        val observacoes = findViewById<EditText>(R.id.et_obs)
        val foto = findViewById<EditText>(R.id.et_foto)
        val btnConcluir = findViewById<Button>(R.id.btn_concluir)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar)

        if (tarefaId == -1 || utilizadorId == -1) {
            Toast.makeText(this, "Dados inválidos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        val data = mapOf("tarefa_id" to tarefaId)
        RetrofitClient.instance.getTaskDetails(data).enqueue(object : Callback<TaskDetailsResponse> {
            override fun onResponse(call: Call<TaskDetailsResponse>, response: Response<TaskDetailsResponse>) {
                Log.d("DEBUG", "Resposta do servidor: ${response.body()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    val t = response.body()!!.tarefa

                    nome.text = "Nome: ${t["nome"]}"
                    dataEntrega.text = "Entrega: ${t["data_entrega"]}"
                    tempo.setText((t["tempo_gasto"] ?: "").toString())
                    observacoes.setText((t["observacoes"] ?: "").toString())
                    progresso.progress = (t["taxa_conclusao"] as? Double)?.toInt() ?: 0
                    percentagem.text = "${progresso.progress}%"
                    foto.setText((t["foto"] ?: "").toString())


                } else {
                    Toast.makeText(this@TaskDetailsActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TaskDetailsResponse>, t: Throwable) {
                Toast.makeText(this@TaskDetailsActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
            }
        })

        btnGuardar.setOnClickListener {
            val tempoGasto = tempo.text.toString()
            val obs = observacoes.text.toString()
            val fotoUrl = foto.text.toString()
            val progressoAtual = progresso.progress

            val data = UpdateTaskRequest(
                tarefa_id = tarefaId,
                utilizador_id = utilizadorId,
                tempo_gasto = tempoGasto,
                observacoes = obs,
                taxa_conclusao = progressoAtual,
                foto = fotoUrl
            )

            RetrofitClient.instance.updateTaskDetails(data).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@TaskDetailsActivity, "Alterações guardadas!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@TaskDetailsActivity, "Erro ao guardar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@TaskDetailsActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                }
            })
        }


        btnConcluir.setOnClickListener {
            val data = mapOf("tarefa_id" to tarefaId)

            RetrofitClient.instance.marcarTarefaConcluida(data).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@TaskDetailsActivity, "Tarefa marcada como concluída!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@TaskDetailsActivity, "Erro ao concluir tarefa", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@TaskDetailsActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val userId = intent.getIntExtra("user_id", -1)

        val btnHome = findViewById<ImageView>(R.id.btn_home)
        val btnProfile = findViewById<ImageView>(R.id.btn_profile)
        val btnSettings = findViewById<ImageView>(R.id.btn_settings)

        btnHome.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }


    }
}
