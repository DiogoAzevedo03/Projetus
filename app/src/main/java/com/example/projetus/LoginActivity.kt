package com.example.projetus // declaração do pacote

import android.content.Intent // para navegar entre ecrãs
import android.os.Bundle // fornece estado guardado
import android.util.Log // utilitário de registo
import android.widget.* // widgets como Button e EditText
import androidx.appcompat.app.AppCompatActivity // classe base das activities
import androidx.lifecycle.lifecycleScope // scope de corrotinas ligado ao ciclo de vida
import com.example.projetus.network.LoginResponse // modelo de resposta da API
import com.example.projetus.network.Utilizador // modelo de utilizador detalhado
import com.example.projetus.data.local.AppDatabase // base de dados Room
import com.example.projetus.data.model.User // entidade de utilizador local
import kotlinx.coroutines.Dispatchers // despachantes de corrotinas
import kotlinx.coroutines.launch // construtor de corrotinas
import kotlinx.coroutines.withContext // muda o contexto da corrotina
import retrofit2.Call // chamada Retrofit
import retrofit2.Callback // callback do Retrofit
import retrofit2.Response // invólucro de resposta do Retrofit

class LoginActivity : AppCompatActivity() { // trata do login do utilizador

    override fun onCreate(savedInstanceState: Bundle?) { // callback do ciclo de vida
        super.onCreate(savedInstanceState) // chama a implementação pai
        setContentView(R.layout.activity_login) // define o layout do ecrã

        val etEmail = findViewById<EditText>(R.id.et_email) // campo do email
        val etPassword = findViewById<EditText>(R.id.et_password) // campo da palavra-passe
        val btnLogin = findViewById<Button>(R.id.btn_login) // botão de login
        val btnGoToRegister = findViewById<Button>(R.id.btn_goto_register) // botão de registo

        btnLogin.setOnClickListener { // ao premir o botão de login
            val email = etEmail.text.toString().trim() // obtém o email
            val password = etPassword.text.toString().trim() // obtém a palavra-passe

            if (email.isEmpty() || password.isEmpty()) { // validação simples
                Toast.makeText(applicationContext, "Preenche todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chamar a API via Retrofit
            RetrofitClient.instance.loginUser(email, password) // envia o pedido
                .enqueue(object : Callback<LoginResponse> { // callback assíncrono
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        Log.d("Login", "Código: ${response.code()} - Body: ${response.body()?.message}") // registo da resposta

                        if (response.isSuccessful && response.body()?.success == true) { // caminho de sucesso
                            val user = response.body()?.user // informação do utilizador
                            Toast.makeText(
                                applicationContext,
                                "Bem-vindo, ${user?.nome}",
                                Toast.LENGTH_SHORT
                            ).show()

                            user?.let { userData -> // obter detalhes completos
                                RetrofitClient.instance.getUser(mapOf("user_id" to userData.id))
                                    .enqueue(object : Callback<Utilizador> {
                                        override fun onResponse(
                                            call: Call<Utilizador>,
                                            resp: Response<Utilizador>
                                        ) {
                                            resp.body()?.let { fullUser ->
                                                lifecycleScope.launch(Dispatchers.IO) {
                                                    val localUser = User(
                                                        id = fullUser.id,
                                                        nome = fullUser.nome,
                                                        username = fullUser.username,
                                                        email = fullUser.email,
                                                        password = fullUser.password,
                                                        tipo_perfil = fullUser.tipo_perfil,
                                                        estado = "Ativo",
                                                        foto = fullUser.foto
                                                    )
                                                    AppDatabase.getDatabase(this@LoginActivity).userDao().insert(localUser) // guarda localmente
                                                }
                                            }
                                        }

                                        override fun onFailure(call: Call<Utilizador>, t: Throwable) {
                                            Log.e("LoginActivity", "Erro ao obter detalhes: ${t.message}")
                                        }
                                    })
                            }

                            val intent = Intent(this@LoginActivity, DashboardActivity::class.java) // abre o dashboard
                            intent.putExtra("user_id", user?.id) // passa o id do utilizador
                            intent.putExtra("tipo_perfil", user?.tipo_perfil) // passa o perfil
                            startActivity(intent)
                            finish() // fecha o login
                        } else {
                            Toast.makeText(
                                applicationContext,
                                response.body()?.message ?: "Erro de login",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) { // erro de rede ou servidor
                        lifecycleScope.launch(Dispatchers.IO) {
                            val localUser = AppDatabase.getDatabase(this@LoginActivity).userDao().getByEmail(email)
                            if (localUser != null && localUser.password == password) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(applicationContext, "Login offline com sucesso", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                                    intent.putExtra("user_id", localUser.id)
                                    intent.putExtra("tipo_perfil", localUser.tipo_perfil)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(applicationContext, "Erro: sem internet e utilizador não encontrado localmente", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }

                }) // fim do pedido de login
        }



        btnGoToRegister.setOnClickListener { // abrir ecrã de registo
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val tvForgotPassword = findViewById<TextView>(R.id.tv_forgot_password) // link para recuperação de palavra-passe

        tvForgotPassword.setOnClickListener { // abrir recuperação de palavra-passe
            val intent = Intent(this, PasswordRecoveryActivity::class.java)
            startActivity(intent)
        }

        val tvForgotEmail = findViewById<TextView>(R.id.tv_forgot_email) // link para recuperação de utilizador

        tvForgotEmail.setOnClickListener { // abrir recuperação de utilizador
            val intent = Intent(this, UsernameRecoveryActivity::class.java)
            startActivity(intent)
        }

        val helpIcon = findViewById<ImageView>(R.id.help_icon) // ícone de ajuda

        helpIcon.setOnClickListener { // abrir ecrã de ajuda
            val intent = Intent(this, AjudaActivity::class.java)
            startActivity(intent)
        }


    }
}

