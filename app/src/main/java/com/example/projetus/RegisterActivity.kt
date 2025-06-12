package com.example.projetus

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.RetrofitClient
import com.example.projetus.network.GenericResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val fullName = findViewById<EditText>(R.id.et_fullname)
        val username = findViewById<EditText>(R.id.et_username)
        val email = findViewById<EditText>(R.id.et_email)
        val password = findViewById<EditText>(R.id.et_password)
        val btnRegister = findViewById<Button>(R.id.btn_register)

        btnRegister.setOnClickListener {
            val nome = fullName.text.toString().trim()
            val user = username.text.toString().trim()
            val mail = email.text.toString().trim()
            val pass = password.text.toString().trim()

            if (nome.isEmpty() || user.isEmpty() || mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.instance.registerUser(
                nome = nome,
                username = user,
                email = mail,
                password = pass,
                tipoPerfil = "utilizador",
                foto = ""
            ).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@RegisterActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, response.body()?.message ?: "Erro no registo", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        findViewById<TextView>(R.id.tv_go_to_login).setOnClickListener {
            finish()
        }
    }
}

