package com.example.projetus // Define o pacote da aplicação

import android.app.DatePickerDialog // Para mostrar o seletor de data
import android.os.Bundle // Estado da Activity
import android.util.Log // Utilizado para logs de depuração
import android.widget.* // Importa widgets do Android
import androidx.appcompat.app.AppCompatActivity // Activity base com suporte a ActionBar
import com.example.projetus.network.GenericResponse // Modelo de resposta genérica da API
import com.example.projetus.network.ProjectResponse // Modelo de resposta de projetos
import com.example.projetus.network.UtilizadoresResponse // Modelo de resposta de utilizadores
import retrofit2.Call // Chamada da API
import retrofit2.Callback // Callback de resposta
import retrofit2.Response // Resposta da API
import java.util.* // Utilitários para datas e listas
import com.example.projetus.network.Project // Modelo de projeto
import com.example.projetus.network.Utilizador // Modelo de utilizador

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var etNome: EditText // Campo para o nome da tarefa
    private lateinit var etDescricao: EditText // Campo para a descrição
    private lateinit var spinnerProjetos: Spinner // Lista de projetos existentes
    private lateinit var spinnerUtilizadores: Spinner // Lista de utilizadores do projeto
    private lateinit var spinnerEstado: Spinner // Estado da tarefa
    private lateinit var etDataEntrega: EditText // Campo para data de entrega
    private lateinit var btnGuardar: Button // Botão para guardar a tarefa
    private lateinit var btnCancelar: Button // Botão para cancelar a criação

    private var projetosList = listOf<Project>() // Lista de projetos obtidos da API
    private var utilizadoresList = listOf<Utilizador>() // Lista de utilizadores associados ao projeto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task) // Define o layout da Activity

        // Ligações ao layout
        etNome = findViewById(R.id.et_nome) // Campo nome
        etDescricao = findViewById(R.id.et_descricao) // Campo descrição
        spinnerProjetos = findViewById(R.id.spinner_projetos) // Dropdown projetos
        spinnerUtilizadores = findViewById(R.id.spinner_utilizadores) // Dropdown utilizadores
        spinnerEstado = findViewById(R.id.spinner_estado) // Dropdown estado
        etDataEntrega = findViewById(R.id.et_data_entrega) // Campo data
        btnGuardar = findViewById(R.id.btn_guardar) // Botão guardar
        btnCancelar = findViewById(R.id.btn_cancelar) // Botão cancelar

        val userId = intent.getIntExtra("user_id", -1) // ID do utilizador recebido
        Log.d("CreateTaskActivity", "Recebido user_id: $userId") // Log de depuração

        setupEstadoSpinner() // Prepara spinner de estados
        setupDatePicker() // Prepara seletor de data
        carregarProjetos(userId) // Carrega projetos disponíveis

        btnGuardar.setOnClickListener { // Ação ao clicar em Guardar
            val nome = etNome.text.toString().trim() // Obtém o nome
            val descricao = etDescricao.text.toString().trim() // Obtém a descrição
            val dataEntrega = etDataEntrega.text.toString().trim() // Obtém a data
            val estado = spinnerEstado.selectedItem.toString() // Obtém o estado
            val projetoSelecionado = projetosList[spinnerProjetos.selectedItemPosition] // Projeto escolhido
            if (utilizadoresList.isEmpty() || spinnerUtilizadores.selectedItemPosition == -1) {
                Toast.makeText(this, getString(R.string.error_no_user_selected), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val utilizadorSelecionado = utilizadoresList[spinnerUtilizadores.selectedItemPosition] // Utilizador escolhido

            if (nome.isEmpty() || descricao.isEmpty() || dataEntrega.isEmpty()) { // Valida campos obrigatórios
                Toast.makeText(this, getString(R.string.error_fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dados = mapOf( // Cria mapa com os dados a enviar
                "nome" to nome,
                "descricao" to descricao,
                "projeto_id" to projetoSelecionado.id.toString(),
                "utilizador_id" to utilizadorSelecionado.id.toString(), // Se fores usar Atribuições
                "data_entrega" to dataEntrega,
                "estado" to estado
            )

            RetrofitClient.instance.addTask(dados).enqueue(object : Callback<GenericResponse> { // Envia para API
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) { // Resposta
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@CreateTaskActivity, getString(R.string.success_task_created), Toast.LENGTH_SHORT).show()
                        finish() // Fecha Activity após sucesso
                    } else {
                        Toast.makeText(this@CreateTaskActivity, response.body()?.message ?: "Erro", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) { // Erro de rede
                    Toast.makeText(this@CreateTaskActivity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
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
        val estados = resources.getStringArray(R.array.task_states)
        spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)
    }

    private fun setupDatePicker() { // Configura o DatePicker
        val calendar = Calendar.getInstance() // Data atual
        etDataEntrega.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val date = String.format("%04d-%02d-%02d", year, month + 1, day) // Formata a data
                    etDataEntrega.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show() // Mostra o seletor
        }
    }

    private fun carregarProjetos(userId: Int) { // Obtém projetos do utilizador
        val data = mapOf("utilizador_id" to userId)
        RetrofitClient.instance.getProjects(data).enqueue(object : Callback<ProjectResponse> {
            override fun onResponse(call: Call<ProjectResponse>, response: Response<ProjectResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    projetosList = response.body()?.projetos ?: emptyList() // Guarda lista
                    val nomes = projetosList.map { it.nome } // Nomes para o spinner
                    spinnerProjetos.adapter = ArrayAdapter(this@CreateTaskActivity, android.R.layout.simple_spinner_dropdown_item, nomes)
                } else {
                    Toast.makeText(this@CreateTaskActivity, getString(R.string.error_loading_projects), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProjectResponse>, t: Throwable) { // Falha na requisição
                Toast.makeText(this@CreateTaskActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun carregarUtilizadoresDoProjeto(projetoId: Int) { // Carrega colaboradores
        val data = mapOf("projeto_id" to projetoId)
        RetrofitClient.instance.getColaboradoresDoProjeto(data).enqueue(object : Callback<UtilizadoresResponse> {
            override fun onResponse(call: Call<UtilizadoresResponse>, response: Response<UtilizadoresResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    utilizadoresList = response.body()?.utilizadores ?: emptyList() // Guarda utilizadores
                    val nomes = utilizadoresList.map { it.nome }
                    spinnerUtilizadores.adapter = ArrayAdapter(this@CreateTaskActivity, android.R.layout.simple_spinner_dropdown_item, nomes)
                } else {
                    Toast.makeText(this@CreateTaskActivity, getString(R.string.error_loading_collaborators), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UtilizadoresResponse>, t: Throwable) { // Falha na obtenção
                Toast.makeText(this@CreateTaskActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
