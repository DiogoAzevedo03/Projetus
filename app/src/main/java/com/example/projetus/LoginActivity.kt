package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.projetus.network.LoginResponse
import com.example.projetus.network.Utilizador
import com.example.projetus.data.local.AppDatabase
import com.example.projetus.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val btnGoToRegister = findViewById<Button>(R.id.btn_goto_register)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(applicationContext, "Preenche todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chamar a API via Retrofit
            RetrofitClient.instance.loginUser(email, password)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        Log.d("Login", "Código: ${response.code()} - Body: ${response.body()?.message}")

                        if (response.isSuccessful && response.body()?.success == true) {
                            val user = response.body()?.user
                            Toast.makeText(
                                applicationContext,
                                "Bem-vindo, ${user?.nome}",
                                Toast.LENGTH_SHORT
                            ).show()

                            user?.let { userData ->
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
                                                    AppDatabase.getDatabase(this@LoginActivity).userDao().insert(localUser)
                                                }
                                            }
                                        }

                                        override fun onFailure(call: Call<Utilizador>, t: Throwable) {
                                            Log.e("LoginActivity", "Erro ao obter detalhes: ${t.message}")
                                        }
                                    })
                            }

                            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                            intent.putExtra("user_id", user?.id)
                            intent.putExtra("tipo_perfil", user?.tipo_perfil)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                response.body()?.message ?: "Erro de login",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
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

                })
        }



        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val tvForgotPassword = findViewById<TextView>(R.id.tv_forgot_password)

        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, PasswordRecoveryActivity::class.java)
            startActivity(intent)
        }

        val tvForgotEmail = findViewById<TextView>(R.id.tv_forgot_email)

        tvForgotEmail.setOnClickListener {
            val intent = Intent(this, UsernameRecoveryActivity::class.java)
            startActivity(intent)
        }

        val helpIcon = findViewById<ImageView>(R.id.help_icon)

        helpIcon.setOnClickListener {
            val intent = Intent(this, AjudaActivity::class.java)
            startActivity(intent)
        }


    }
}

