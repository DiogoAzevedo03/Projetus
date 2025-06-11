package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.network.LoginResponse
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
                Toast.makeText(this, "Preenche todos os campos", Toast.LENGTH_SHORT).show()
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
                                this@LoginActivity,
                                "Bem-vindo, ${user?.nome}",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                            intent.putExtra("user_id", user?.id)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                response.body()?.message ?: "Erro de login",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Erro: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
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

