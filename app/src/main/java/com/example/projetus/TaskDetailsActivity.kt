package com.example.projetus // Pacote da aplicação

import android.content.Intent // Necessário para navegação entre Activities
import android.os.Bundle // Fornece informação sobre o estado da Activity
import android.util.Log // Utilizado para logs de depuração
import android.widget.* // Contém TextView, Button, SeekBar, etc.
import androidx.appcompat.app.AppCompatActivity // Activity base com ActionBar
import com.example.projetus.network.TaskDetailsResponse // Modelo da resposta dos detalhes
import retrofit2.Call // Representa uma chamada Retrofit
import retrofit2.Callback // Callback assíncrono do Retrofit
import retrofit2.Response // Resposta HTTP do Retrofit
import com.example.projetus.network.GenericResponse // Resposta genérica
import com.example.projetus.network.UpdateTaskRequest // Modelo para atualização de tarefa
import com.bumptech.glide.Glide // Biblioteca para carregar imagens

// Activity que apresenta e permite editar os detalhes de uma tarefa
class TaskDetailsActivity : AppCompatActivity() {
    // ID da tarefa a ser visualizada
    private var tarefaId: Int = -1
    // ID do utilizador associado
    private var utilizadorId: Int = -1

    // Método executado na criação da Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DEBUG", "tarefaId: $tarefaId | utilizadorId: $utilizadorId") // Log para depuração

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details) // Define o layout

        // Recupera os IDs enviados na intent
        tarefaId = intent.getIntExtra("task_id", -1)
        utilizadorId = intent.getIntExtra("user_id", -1) // também passado na intent

        // Elementos de interface
        val nome = findViewById<TextView>(R.id.tv_nome)
        val dataEntrega = findViewById<TextView>(R.id.tv_entrega)
        val progresso = findViewById<SeekBar>(R.id.seekBarProgress)
        val percentagem = findViewById<TextView>(R.id.tv_percent)

        // Atualiza o texto de percentagem à medida que o utilizador move a barra
        progresso.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                percentagem.text = "$progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


        // Campos editáveis e botões
        val tempo = findViewById<EditText>(R.id.et_tempo)
        val observacoes = findViewById<EditText>(R.id.et_obs)
        val foto = findViewById<EditText>(R.id.et_foto)
        val btnConcluir = findViewById<Button>(R.id.btn_concluir)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar)
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

        // Verifica se os dados necessários foram fornecidos
        if (tarefaId == -1 || utilizadorId == -1) {
            Toast.makeText(this, "Dados inválidos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        // Chamada à API para obter os detalhes da tarefa
        val data = mapOf("tarefa_id" to tarefaId)
        RetrofitClient.instance.getTaskDetails(data).enqueue(object : Callback<TaskDetailsResponse> {
            override fun onResponse(call: Call<TaskDetailsResponse>, response: Response<TaskDetailsResponse>) {
                Log.d("DEBUG", "Resposta do servidor: ${response.body()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    val t = response.body()!!.tarefa

                    // Preenche os campos com os dados recebidos
                    nome.text = "Nome: ${t["nome"]}"
                    dataEntrega.text = "Entrega: ${t["data_entrega"]}"
                    tempo.setText((t["tempo_gasto"] ?: "").toString())
                    observacoes.setText((t["observacoes"] ?: "").toString())

                    // Conversão da taxa de conclusão para inteiro
                    val taxaConclusao = when (val valor = t["taxa_conclusao"]) {
                        is Double -> valor.toInt()
                        is Int -> valor
                        is String -> valor.toIntOrNull() ?: 0
                        else -> 0
                    }

                    // Atualiza a UI com a taxa de conclusão
                    progresso.progress = taxaConclusao
                    percentagem.text = "$taxaConclusao%"
                    foto.setText((t["foto"] ?: "").toString())

                    // Mostrar imagem com Glide
                    val imagePreview = findViewById<ImageView>(R.id.image_preview)
                    val fotoUrl = (t["foto"] ?: "").toString()

                    if (fotoUrl.isNotBlank() && (fotoUrl.startsWith("http://") || fotoUrl.startsWith("https://"))) {
                        Glide.with(this@TaskDetailsActivity)
                            .load(fotoUrl)
                            .into(imagePreview)
                    } else {
                        imagePreview.setImageResource(R.drawable.placeholder)
                    }
                }
                else {
                    Toast.makeText(this@TaskDetailsActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
                }


            }

            override fun onFailure(call: Call<TaskDetailsResponse>, t: Throwable) {
                // Caso a chamada falhe
                Toast.makeText(this@TaskDetailsActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
            }
        })

        // Guarda as alterações efetuadas na tarefa
        btnGuardar.setOnClickListener {
            val tempoGasto = tempo.text.toString()
            val obs = observacoes.text.toString()
            val fotoUrl = foto.text.toString()
            val progressoAtual = progresso.progress

            // Cria objeto com os dados para enviar
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


        // Marca a tarefa como concluída
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

        // Recupera novamente o ID do utilizador para navegação
        val userId = intent.getIntExtra("user_id", -1)

        val btnHome = findViewById<ImageView>(R.id.btn_home)
        val btnProfile = findViewById<ImageView>(R.id.btn_profile)
        val btnSettings = findViewById<ImageView>(R.id.btn_settings)

        // Botão para voltar ao dashboard
        btnHome.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        // Botão para abrir o perfil
        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        // Botão para abrir as definições
        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }


    }
}


