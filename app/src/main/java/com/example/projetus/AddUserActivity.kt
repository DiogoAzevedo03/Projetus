package com.example.projetus // Define o pacote da aplicação

import android.content.Intent // Necessário para navegação entre atividades
import android.os.Bundle // Permite acessar o estado da Activity
import android.widget.* // Importa componentes de interface do Android
import androidx.appcompat.app.AppCompatActivity // Classe base para Activities
import androidx.lifecycle.lifecycleScope // Escopo para coroutines vinculadas ao ciclo de vida
import com.example.projetus.data.local.AppDatabase // Acesso à base de dados local
import com.example.projetus.data.local.PendingUser // Entidade para utilizadores pendentes
import com.example.projetus.network.GenericResponse // Modelo de resposta genérica da API
import kotlinx.coroutines.Dispatchers // Define em que thread a coroutine irá executar
import kotlinx.coroutines.launch // Lançamento de coroutines
import kotlinx.coroutines.withContext // Mudança de contexto da coroutine
import retrofit2.Call // Representa uma chamada HTTP
import retrofit2.Callback // Callback para o Retrofit
import retrofit2.Response // Resposta de uma chamada Retrofit
import android.util.Patterns // Padrões para validação de email

class AddUserActivity : AppCompatActivity() { // Activity responsável por adicionar um utilizador

    private var userId: Int = -1 // Guarda o ID do utilizador autenticado

    override fun onCreate(savedInstanceState: Bundle?) { // Método chamado ao criar a Activity
        super.onCreate(savedInstanceState) // Chama a implementação da superclasse
        setContentView(R.layout.activity_add_user) // Define o layout da Activity

        userId = intent.getIntExtra("user_id", -1) // Recupera o ID passado na Intent

        val nome = findViewById<EditText>(R.id.et_nome) // Campo de nome
        val username = findViewById<EditText>(R.id.et_username) // Campo de username
        val email = findViewById<EditText>(R.id.et_email) // Campo de email
        val password = findViewById<EditText>(R.id.et_password) // Campo de password
        val foto = findViewById<EditText>(R.id.et_foto).text.toString() // Texto da foto (URL ou caminho)

        val spinner = findViewById<Spinner>(R.id.spinner_tipo_perfil) // Spinner para tipos de perfil

        val btnCancelar = findViewById<Button>(R.id.btn_cancelar) // Botão de cancelar
        val btnGuardar = findViewById<Button>(R.id.btn_guardar) // Botão de guardar

        val opcoes = listOf("utilizador", "administrador", "gestor") // Opções do spinner
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcoes) // Associa opções ao spinner

        btnCancelar.setOnClickListener { // Fecha a Activity sem guardar
            finish() // Termina a Activity
        }

        btnGuardar.setOnClickListener { // Ação ao clicar em guardar
            val nomeTxt = nome.text.toString() // Texto do nome
            val userTxt = username.text.toString() // Texto do username
            val emailTxt = email.text.toString() // Texto do email
            val passTxt = password.text.toString() // Texto da password
            val perfil = spinner.selectedItem.toString() // Perfil selecionado
            val foto = findViewById<EditText>(R.id.et_foto).text.toString()  // Mover a captura da foto aqui

            if (nomeTxt.isEmpty() || userTxt.isEmpty() || emailTxt.isEmpty() || passTxt.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show() // Validação simples
                return@setOnClickListener // Interrompe se algum campo estiver vazio
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
                Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}")
            if (!passwordPattern.containsMatchIn(passTxt)) {
                Toast.makeText(this, "Palavra-passe fraca", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Enviar os dados para o servidor, incluindo a foto
            RetrofitClient.instance.registerUser(nomeTxt, userTxt, emailTxt, passTxt, perfil, foto)
                .enqueue(object : Callback<GenericResponse> {
                    override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@AddUserActivity, "Utilizador adicionado com sucesso!", Toast.LENGTH_SHORT).show() // Sucesso
                            finish() // Fecha a Activity
                        } else {
                            Toast.makeText(this@AddUserActivity, "Erro: ${response.body()?.message}", Toast.LENGTH_SHORT).show() // Mostra mensagem de erro
                        }
                    }

                    override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                        // Falha na rede: guardar localmente
                        val userToSave = PendingUser(
                            nome = nomeTxt,
                            username = userTxt,
                            email = emailTxt,
                            password = passTxt,
                            tipo_perfil = perfil,
                            foto = foto
                        )

                        lifecycleScope.launch(Dispatchers.IO) { // Operação em background
                            AppDatabase.getDatabase(this@AddUserActivity).pendingUserDao().insert(userToSave) // Guarda na BD local
                            withContext(Dispatchers.Main) { // De volta à thread principal
                                Toast.makeText(this@AddUserActivity, "Sem internet! Utilizador guardado localmente.", Toast.LENGTH_LONG).show()
                                finish() // Fecha após guardar
                            }
                        }
                    }

                }) // Fim da chamada Retrofit
        } // Fim do clique do botão Guardar

    } // Fim do onCreate
} // Fim da Activity
