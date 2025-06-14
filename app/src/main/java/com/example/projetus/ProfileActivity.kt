package com.example.projetus // Pacote da aplicação

import android.content.Intent // Para navegar entre atividades
import android.os.Bundle // Para gerir o ciclo de vida da Activity
import android.util.Log // Para registos de depuração
import android.widget.* // Importa todos os widgets utilizados
import androidx.appcompat.app.AppCompatActivity // Activity base da app
import com.example.projetus.RetrofitClient // Cliente Retrofit definido na app
import com.example.projetus.network.Utilizador // Modelo de Utilizador vindo da API
import com.example.projetus.network.GenericResponse // Resposta genérica da API
import retrofit2.Call // Chamada Retrofit
import retrofit2.Callback // Callback do Retrofit
import retrofit2.Response // Resposta do Retrofit
import com.bumptech.glide.Glide // Biblioteca para carregar imagens

class ProfileActivity : AppCompatActivity() { // Activity que mostra e edita o perfil do utilizador

    private lateinit var etNome: EditText // Campo de texto para o nome
    private lateinit var etUsername: EditText // Campo de texto para o username
    private lateinit var etEmail: EditText // Campo de texto para o email
    private lateinit var etPassword: EditText // Campo de texto para a password
    private lateinit var etFoto: EditText // Campo de texto para a URL da foto
    private lateinit var ivFoto: ImageView // Imagem do utilizador
    private lateinit var btnGuardar: Button // Botão para guardar alterações
    private var userId: Int = -1 // ID do utilizador corrente
    private var tipoPerfil: String = "utilizador" // Tipo de perfil do utilizador

    override fun onCreate(savedInstanceState: Bundle?) { // Método chamado na criação da Activity
        super.onCreate(savedInstanceState) // Chama implementação da superclasse
        setContentView(R.layout.activity_profile) // Define o layout a usar

        etNome = findViewById(R.id.et_nome) // Obtém referência ao campo nome
        etUsername = findViewById(R.id.et_username) // Obtém referência ao campo username
        etEmail = findViewById(R.id.et_email) // Obtém referência ao campo email
        etPassword = findViewById(R.id.et_password) // Obtém referência ao campo password
        etFoto = findViewById(R.id.et_foto) // Obtém referência ao campo foto
        ivFoto = findViewById(R.id.iv_foto) // Referência à imagem de perfil
        btnGuardar = findViewById(R.id.btn_guardar) // Botão para guardar

        userId = intent.getIntExtra("user_id", -1) // Lê o ID do utilizador passado na Intent

        tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador" // Lê o tipo de perfil

        if (userId == -1) { // Se não vier ID válido, mostra erro e termina
            Toast.makeText(this, "ID de utilizador inválido", Toast.LENGTH_SHORT).show()
            finish() // Fecha a activity
            return
        }

        carregarDadosUtilizador() // Pede dados do utilizador à API

        btnGuardar.setOnClickListener { // Quando clica em guardar
            val nomeTxt = etNome.text.toString() // Obtém texto do nome
            val userTxt = etUsername.text.toString() // Obtém texto do username
            val emailTxt = etEmail.text.toString() // Obtém texto do email
            val passTxt = etPassword.text.toString() // Obtém texto da password
            val fotoTxt = etFoto.text.toString()  // Captura a URL da foto
            Log.d("ProfileActivity", "Foto URL: $fotoTxt")  // Mostra URL no log

            // Verifica se todos os campos estão preenchidos
            if (nomeTxt.isEmpty() || userTxt.isEmpty() || emailTxt.isEmpty() || passTxt.isEmpty() || fotoTxt.isEmpty()) { // Validação simples
                Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show() // Mostra aviso
                return@setOnClickListener // Sai do listener
            }

            // Agora chama o método para atualizar os dados do utilizador
            atualizarDadosUtilizador(nomeTxt, userTxt, emailTxt, passTxt, fotoTxt) // Chama API para atualizar
        }

        findViewById<ImageView>(R.id.btn_home).setOnClickListener { // Botão home
            val intent = Intent(this, DashboardActivity::class.java) // Intent para dashboard
            intent.putExtra("user_id", userId) // Passa ID
            intent.putExtra("tipo_perfil", tipoPerfil) // Passa tipo de perfil

            startActivity(intent) // Abre dashboard
        }

        findViewById<ImageView>(R.id.btn_perfil).setOnClickListener { // Botão perfil
            // Já estás no perfil, apenas avisa
            Toast.makeText(this, "Já estás no Perfil", Toast.LENGTH_SHORT).show()
        }

        val btnSettings = findViewById<ImageView>(R.id.btn_settings) // Botão de definições

        btnSettings.setOnClickListener { // Abrir ecrã de definições
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        val btnSair = findViewById<Button>(R.id.btn_sair) // Botão para sair da conta

        btnSair.setOnClickListener { // Ao clicar em sair
            val intent = Intent(this, LoginActivity::class.java) // Vai para ecrã de login
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Limpa a pilha
            startActivity(intent) // Inicia atividade
            finish() // Fecha a atual
        }




    }

    private fun carregarDadosUtilizador() { // Obtém dados do utilizador na API
        RetrofitClient.instance.getUser(mapOf("user_id" to userId)) // Chamada à API
            .enqueue(object : Callback<Utilizador> { // Callback assíncrono
                override fun onResponse(call: Call<Utilizador>, response: Response<Utilizador>) { // Resposta da API
                    val user = response.body() // Corpo da resposta
                    if (response.isSuccessful && user != null) { // Se sucesso
                        etNome.setText(user.nome) // Preenche campo nome
                        etUsername.setText(user.username) // Preenche username
                        etEmail.setText(user.email) // Preenche email
                        etPassword.setText(user.password) // Preenche password
                        etFoto.setText(user.foto) // Preenche foto
                        if (user.foto.isNotBlank()) { // Se tem foto
                            Glide.with(this@ProfileActivity).load(user.foto).into(ivFoto) // Carrega imagem
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show() // Erro de API
                    }
                }

                override fun onFailure(call: Call<Utilizador>, t: Throwable) { // Falha na chamada
                    Log.e("ProfileActivity", "Erro: ${t.message}") // Log do erro
                    Toast.makeText(this@ProfileActivity, "Erro de rede", Toast.LENGTH_SHORT).show() // Mensagem de erro
                }
            })
    }

    private fun atualizarDadosUtilizador(nome: String, username: String, email: String, password: String, foto: String) { // Atualiza dados na API
        val body = mapOf( // Corpo da requisição
            "id" to userId.toString(),
            "nome" to nome,
            "username" to username,
            "email" to email,
            "password" to password,
            "foto" to foto,  // Passando a URL da foto corretamente
            "tipo_perfil" to tipoPerfil
        )
        Log.d("ProfileActivity", "Body enviado: $body") // Log do corpo enviado


        RetrofitClient.instance.updateUser(body) // Chamada para atualizar
            .enqueue(object : Callback<GenericResponse> { // Callback
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) { // Resposta
                    Log.d("ProfileActivity", "Código de resposta: ${response.code()}")
                    Log.d("ProfileActivity", "Resposta: ${response.body()}")
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@ProfileActivity, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        val fotoUrl = foto
                        if (fotoUrl.isNotBlank()) {
                            Glide.with(this@ProfileActivity).load(fotoUrl).into(ivFoto) // Atualiza imagem
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show()
                    }
                }


                override fun onFailure(call: Call<GenericResponse>, t: Throwable) { // Falha
                    Log.e("ProfileActivity", "Erro: ${t.message}")
                    Toast.makeText(this@ProfileActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
