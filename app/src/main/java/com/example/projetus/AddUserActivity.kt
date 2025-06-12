package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.network.GenericResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddUserActivity : AppCompatActivity() {

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        userId = intent.getIntExtra("user_id", -1)

        val nome = findViewById<EditText>(R.id.et_nome)
        val username = findViewById<EditText>(R.id.et_username)
        val email = findViewById<EditText>(R.id.et_email)
        val password = findViewById<EditText>(R.id.et_password)
        val foto = findViewById<EditText>(R.id.et_foto).text.toString()

        val spinner = findViewById<Spinner>(R.id.spinner_tipo_perfil)

        val btnCancelar = findViewById<Button>(R.id.btn_cancelar)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar)

        val opcoes = listOf("utilizador", "administrador", "gestor")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcoes)

        btnCancelar.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            val nomeTxt = nome.text.toString()
            val userTxt = username.text.toString()
            val emailTxt = email.text.toString()
            val passTxt = password.text.toString()
            val perfil = spinner.selectedItem.toString()
            val foto = findViewById<EditText>(R.id.et_foto).text.toString()  // Mover a captura da foto aqui

            if (nomeTxt.isEmpty() || userTxt.isEmpty() || emailTxt.isEmpty() || passTxt.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Enviar os dados para o servidor, incluindo a foto
            RetrofitClient.instance.registerUser(nomeTxt, userTxt, emailTxt, passTxt, perfil, foto)
                .enqueue(object : Callback<GenericResponse> {
                    override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@AddUserActivity, "Utilizador adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@AddUserActivity, "Erro: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                        Toast.makeText(this@AddUserActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

    }
}
