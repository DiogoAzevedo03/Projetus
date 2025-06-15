package com.example.projetus // Pacote da aplicação

import android.app.DatePickerDialog // Diálogo de seleção de data
import android.content.Intent // Navegação entre Activities
import android.os.Bundle // Estado da Activity
import android.widget.* // Widgets Android
import androidx.appcompat.app.AppCompatActivity // Activity base
import com.example.projetus.network.GenericResponse // Resposta genérica da API
import com.example.projetus.network.Gestor // Modelo de gestor
import com.example.projetus.network.GestoresResponse // Resposta com gestores
import retrofit2.Call // Chamada HTTP
import retrofit2.Callback // Callback do Retrofit
import retrofit2.Response // Resposta do Retrofit
import java.util.* // Utilidades de data

class CreateProjectActivity : AppCompatActivity() { // Activity para criar projetos

    private lateinit var spinnerGestor: Spinner // Dropdown de gestores
    private var listaGestores: List<Gestor> = emptyList() // Lista carregada da API

    override fun onCreate(savedInstanceState: Bundle?) { // Metodo inicial
        super.onCreate(savedInstanceState) // Super
        setContentView(R.layout.activity_create_project) // Layout

        val etNome = findViewById<EditText>(R.id.et_nome_projeto) // Campo nome
        val etDescricao = findViewById<EditText>(R.id.et_descricao_projeto) // Campo descrição
        val dpInicio = findViewById<EditText>(R.id.et_data_inicio) // Data início
        val dpFim = findViewById<EditText>(R.id.et_data_fim) // Data fim
        val btnGuardar = findViewById<Button>(R.id.btn_guardar_projeto) // Botão guardar
        val btnCancelar = findViewById<Button>(R.id.btn_cancelar_projeto) // Botão cancelar
        spinnerGestor = findViewById(R.id.spinner_gestor) // Spinner de gestores

        // Date pickers
        val calendar = Calendar.getInstance() // Instância de calendário
        val setDateListener = { target: EditText ->
            DatePickerDialog(this, { _, year, month, day ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, day)
                target.setText(date) // Define data escolhida
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        dpInicio.setOnClickListener { setDateListener(dpInicio) } // Escolher data início
        dpFim.setOnClickListener { setDateListener(dpFim) } // Escolher data fim

        carregarGestores() // Carrega gestores disponíveis

        btnGuardar.setOnClickListener { // Ao guardar projeto
            val nome = etNome.text.toString().trim() // Nome inserido
            val descricao = etDescricao.text.toString().trim() // Descrição inserida
            val dataInicio = dpInicio.text.toString().trim() // Data início
            val dataFim = dpFim.text.toString().trim() // Data fim

            val gestorSelecionado = spinnerGestor.selectedItemPosition // Posição selecionada
            val gestorId = listaGestores.getOrNull(gestorSelecionado)?.id ?: -1 // Id do gestor

            if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataFim.isEmpty() || gestorId == -1) {
                Toast.makeText(this, "Preencha todos os campos e selecione um gestor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.instance.addProject(
                nome = nome,
                descricao = descricao,
                gestorId = gestorId,
                dataInicio = dataInicio,
                dataFim = dataFim
            ).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@CreateProjectActivity, "Projeto criado!", Toast.LENGTH_SHORT).show()
                        val resultIntent = Intent()
                        resultIntent.putExtra("project_created", true)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Toast.makeText(this@CreateProjectActivity, response.body()?.message ?: "Erro", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@CreateProjectActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnCancelar.setOnClickListener { // Cancela criação
            finish()
        }
    }

    private fun carregarGestores() { // Carrega lista de gestores do servidor
        RetrofitClient.instance.getGestores().enqueue(object : Callback<GestoresResponse> {
            override fun onResponse(call: Call<GestoresResponse>, response: Response<GestoresResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    listaGestores = response.body()?.gestores ?: emptyList()
                    val nomesGestores = listaGestores.map { it.nome }
                    val adapter = ArrayAdapter(this@CreateProjectActivity, android.R.layout.simple_spinner_item, nomesGestores)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerGestor.adapter = adapter
                } else {
                    Toast.makeText(this@CreateProjectActivity, "Erro ao carregar gestores", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GestoresResponse>, t: Throwable) {
                Toast.makeText(this@CreateProjectActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

