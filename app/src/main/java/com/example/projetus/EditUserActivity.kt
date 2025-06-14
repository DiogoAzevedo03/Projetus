package com.example.projetus // Pacote principal

import android.os.Bundle // Bundle para Activity
import android.widget.* // Widgets padrão
import androidx.appcompat.app.AppCompatActivity // Activity com ActionBar
import com.example.projetus.network.GenericResponse // Modelo de resposta genérica
import com.example.projetus.network.Utilizador // Modelo de utilizador
import retrofit2.Call // Chamada da API
import retrofit2.Callback // Callback da API
import retrofit2.Response // Resposta da API
import android.util.Patterns // Padrões para validação de email


class EditUserActivity : AppCompatActivity() { // Activity para editar utilizadores

    private lateinit var nomeET: EditText // Campo nome
    private lateinit var usernameET: EditText // Campo username
    private lateinit var emailET: EditText // Campo email
    private lateinit var passwordET: EditText // Campo password
    private lateinit var btnAtualizar: Button // Botão atualizar
    private lateinit var fotoET: EditText // Campo foto

    private lateinit var perfilSpinner: Spinner // Spinner de perfis
    private lateinit var perfis: List<String> // Opções de perfil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user) // Define o layout

        nomeET = findViewById(R.id.et_nome) // Liga campo nome
        usernameET = findViewById(R.id.et_username) // Liga campo username
        emailET = findViewById(R.id.et_email) // Liga campo email
        passwordET = findViewById(R.id.et_password) // Liga campo password
        fotoET = findViewById(R.id.et_foto) // Liga campo foto

        btnAtualizar = findViewById(R.id.btn_atualizar) // Botão atualizar
        perfilSpinner = findViewById(R.id.spinner_perfil) // Spinner de perfil

        // Preencher spinner com os tipos de perfil
        perfis = listOf(
            getString(R.string.profile_option_user),
            getString(R.string.profile_option_admin),
            getString(R.string.profile_option_manager)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, perfis)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        perfilSpinner.adapter = adapter

        // Receber dados do utilizador passado por Intent
        val user = intent.getSerializableExtra("utilizador") as? Utilizador

        if (user != null) { // Preenche campos se veio utilizador
            nomeET.setText(user.nome)
            usernameET.setText(user.username)
            emailET.setText(user.email)
            fotoET.setText(user.foto)


            // Selecionar o tipo_perfil atual no spinner
            val index = perfis.indexOf(user.tipo_perfil)
            if (index >= 0) {
                perfilSpinner.setSelection(index)
            }
        }

        btnAtualizar.setOnClickListener { // Ao clicar atualizar
            val nome = nomeET.text.toString()
            val username = usernameET.text.toString()
            val email = emailET.text.toString()
            val password = passwordET.text.toString()
            val foto = fotoET.text.toString()

            val tipoPerfil = perfilSpinner.selectedItem.toString()

            if (nome.isEmpty() || username.isEmpty() || email.isEmpty()) { // Valida campos
                Toast.makeText(this, getString(R.string.error_fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isNotEmpty()) {
                val passwordPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}")
                if (!passwordPattern.containsMatchIn(password)) {
                    Toast.makeText(this, getString(R.string.error_weak_password), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val data = mutableMapOf(
                "id" to user?.id.toString(),
                "nome" to nome,
                "username" to username,
                "email" to email,
                "tipo_perfil" to tipoPerfil,
                "foto" to foto
            ) // Dados a enviar

            if (password.isNotEmpty()) { // Se mudou a password
                data["password"] = password
            }

            RetrofitClient.instance.updateUser(data).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@EditUserActivity, getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@EditUserActivity,
                            getString(R.string.error_generic, response.body()?.message ?: ""),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) { // Erro de rede
                    Toast.makeText(
                        this@EditUserActivity,
                        getString(R.string.error_generic, t.message ?: ""),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}
