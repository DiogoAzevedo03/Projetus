package com.example.projetus // Pacote da aplicação

import android.os.Bundle // Dados do ciclo de vida de uma Activity
import android.widget.* // Componentes de interface do Android
import androidx.appcompat.app.AppCompatActivity // Activity base com ActionBar
import com.example.projetus.RetrofitClient // Cliente para chamadas Retrofit
import com.example.projetus.network.GenericResponse // Modelo de resposta
import retrofit2.Call // Representa a chamada HTTP
import retrofit2.Callback // Callback assíncrono do Retrofit
import retrofit2.Response // Resposta HTTP do Retrofit

// Activity que permite registar novos utilizadores
class RegisterActivity : AppCompatActivity() {
    // Executado quando a Activity é criada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Chama implementação da superclasse
        setContentView(R.layout.activity_register) // Define o layout desta Activity

        // Referências aos campos de texto e botão de registo
        val fullName = findViewById<EditText>(R.id.et_fullname)
        val username = findViewById<EditText>(R.id.et_username)
        val email = findViewById<EditText>(R.id.et_email)
        val password = findViewById<EditText>(R.id.et_password)
        val btnRegister = findViewById<Button>(R.id.btn_register)

        // Ação executada quando o utilizador clica no botão
        btnRegister.setOnClickListener {
            // Obtém o texto de cada campo
            val nome = fullName.text.toString().trim()
            val user = username.text.toString().trim()
            val mail = email.text.toString().trim()
            val pass = password.text.toString().trim()

            // Verifica se todos os campos foram preenchidos
            if (nome.isEmpty() || user.isEmpty() || mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Encerra se faltarem dados
            }

            // Chamada à API para registar o utilizador
            RetrofitClient.instance.registerUser(
                nome = nome,
                username = user,
                email = mail,
                password = pass,
                tipoPerfil = "utilizador", // Perfil por defeito
                foto = ""
            ).enqueue(object : Callback<GenericResponse> {
                // Chamado quando o servidor responde
                override fun onResponse(
                    call: Call<GenericResponse>, // chamada efetuada
                    response: Response<GenericResponse> // resposta obtida
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@RegisterActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                        finish() // Termina Activity em caso de sucesso
                    } else {
                        Toast.makeText(this@RegisterActivity, response.body()?.message ?: "Erro no registo", Toast.LENGTH_LONG).show()
                    }
                }

                // Chamado quando ocorre um erro de rede
                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        // Permite regressar ao ecrã de login
        findViewById<TextView>(R.id.tv_go_to_login).setOnClickListener {
            finish()
        }
    }
}


