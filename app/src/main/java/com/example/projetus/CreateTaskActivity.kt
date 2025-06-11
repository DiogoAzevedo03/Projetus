package com.example.projetus

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.network.GenericResponse
import com.example.projetus.network.ProjectResponse
import com.example.projetus.network.UtilizadoresResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import com.example.projetus.network.Project
import com.example.projetus.network.Utilizador

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etDescricao: EditText
    private lateinit var spinnerProjetos: Spinner
    private lateinit var spinnerUtilizadores: Spinner
    private lateinit var spinnerEstado: Spinner
    private lateinit var etDataEntrega: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private var projetosList = listOf<Project>() // Cria modelo Project se ainda não tiveres
    private var utilizadoresList = listOf<Utilizador>() // Cria modelo Utilizador se ainda não tiveres

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        // Ligações ao layout
        etNome = findViewById(R.id.et_nome)
        etDescricao = findViewById(R.id.et_descricao)
        spinnerProjetos = findViewById(R.id.spinner_projetos)
        spinnerUtilizadores = findViewById(R.id.spinner_utilizadores)
        spinnerEstado = findViewById(R.id.spinner_estado)
        etDataEntrega = findViewById(R.id.et_data_entrega)
        btnGuardar = findViewById(R.id.btn_guardar)
        btnCancelar = findViewById(R.id.btn_cancelar)

        val userId = intent.getIntExtra("user_id", -1)
        Log.d("CreateTaskActivity", "Recebido user_id: $userId")

        setupEstadoSpinner()
        setupDatePicker()
        carregarProjetos(userId)

        btnGuardar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val descricao = etDescricao.text.toString().trim()
            val dataEntrega = etDataEntrega.text.toString().trim()
            val estado = spinnerEstado.selectedItem.toString()
            val projetoSelecionado = projetosList[spinnerProjetos.selectedItemPosition]
            if (utilizadoresList.isEmpty() || spinnerUtilizadores.selectedItemPosition == -1) {
                Toast.makeText(this, "Nenhum utilizador selecionado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val utilizadorSelecionado = utilizadoresList[spinnerUtilizadores.selectedItemPosition]

            if (nome.isEmpty() || descricao.isEmpty() || dataEntrega.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dados = mapOf(
                "nome" to nome,
                "descricao" to descricao,
                "projeto_id" to projetoSelecionado.id.toString(),
                "utilizador_id" to utilizadorSelecionado.id.toString(), // Se fores usar Atribuições
                "data_entrega" to dataEntrega,
                "estado" to estado
            )

            RetrofitClient.instance.addTask(dados).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@CreateTaskActivity, "Tarefa criada com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@CreateTaskActivity, response.body()?.message ?: "Erro", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@CreateTaskActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
                }
            })
        }
        spinnerProjetos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                spinnerUtilizadores.adapter = null // Limpa o spinner antes de carregar novos
                val projetoSelecionado = projetosList[position]
                carregarUtilizadoresDoProjeto(projetoSelecionado.id)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nada a fazer
            }
        }


        btnCancelar.setOnClickListener { finish() }
    }

    private fun setupEstadoSpinner() {
        val estados = arrayOf("Pendente", "Em andamento", "Concluída")
        spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        etDataEntrega.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val date = String.format("%04d-%02d-%02d", year, month + 1, day)
                    etDataEntrega.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun carregarProjetos(userId: Int) {
        val data = mapOf("utilizador_id" to userId)
        RetrofitClient.instance.getProjects(data).enqueue(object : Callback<ProjectResponse> {
            override fun onResponse(call: Call<ProjectResponse>, response: Response<ProjectResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    projetosList = response.body()?.projetos ?: emptyList()
                    val nomes = projetosList.map { it.nome }
                    spinnerProjetos.adapter = ArrayAdapter(this@CreateTaskActivity, android.R.layout.simple_spinner_dropdown_item, nomes)
                } else {
                    Toast.makeText(this@CreateTaskActivity, "Erro ao carregar projetos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProjectResponse>, t: Throwable) {
                Toast.makeText(this@CreateTaskActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun carregarUtilizadoresDoProjeto(projetoId: Int) {
        val data = mapOf("projeto_id" to projetoId)
        RetrofitClient.instance.getColaboradoresDoProjeto(data).enqueue(object : Callback<UtilizadoresResponse> {
            override fun onResponse(call: Call<UtilizadoresResponse>, response: Response<UtilizadoresResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    utilizadoresList = response.body()?.utilizadores ?: emptyList()
                    val nomes = utilizadoresList.map { it.nome }
                    spinnerUtilizadores.adapter = ArrayAdapter(this@CreateTaskActivity, android.R.layout.simple_spinner_dropdown_item, nomes)
                } else {
                    Toast.makeText(this@CreateTaskActivity, "Erro ao carregar colaboradores", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UtilizadoresResponse>, t: Throwable) {
                Toast.makeText(this@CreateTaskActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
