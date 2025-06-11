package com.example.projetus

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.network.GenericResponse
import com.example.projetus.network.Utilizador
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditUserActivity : AppCompatActivity() {

    private lateinit var nomeET: EditText
    private lateinit var usernameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var btnAtualizar: Button
    private lateinit var perfilSpinner: Spinner

    private val perfis = listOf("utilizador", "administrador", "gestor")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        nomeET = findViewById(R.id.et_nome)
        usernameET = findViewById(R.id.et_username)
        emailET = findViewById(R.id.et_email)
        passwordET = findViewById(R.id.et_password)
        btnAtualizar = findViewById(R.id.btn_atualizar)
        perfilSpinner = findViewById(R.id.spinner_perfil)

        // Preencher spinner com os tipos de perfil
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, perfis)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        perfilSpinner.adapter = adapter

        // Receber dados do utilizador passado por Intent
        val user = intent.getSerializableExtra("utilizador") as? Utilizador

        if (user != null) {
            nomeET.setText(user.nome)
            usernameET.setText(user.username)
            emailET.setText(user.email)

            // Selecionar o tipo_perfil atual no spinner
            val index = perfis.indexOf(user.tipo_perfil)
            if (index >= 0) {
                perfilSpinner.setSelection(index)
            }
        }

        btnAtualizar.setOnClickListener {
            val nome = nomeET.text.toString()
            val username = usernameET.text.toString()
            val email = emailET.text.toString()
            val password = passwordET.text.toString()
            val tipoPerfil = perfilSpinner.selectedItem.toString()

            if (nome.isEmpty() || username.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = mutableMapOf(
                "id" to user?.id.toString(),
                "nome" to nome,
                "username" to username,
                "email" to email,
                "tipo_perfil" to tipoPerfil
            )

            if (password.isNotEmpty()) {
                data["password"] = password
            }

            RetrofitClient.instance.updateUser(data).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@EditUserActivity, "Atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditUserActivity, "Erro: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@EditUserActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
