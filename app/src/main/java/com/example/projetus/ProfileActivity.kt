package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.RetrofitClient
import com.example.projetus.network.Utilizador
import com.example.projetus.network.GenericResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.bumptech.glide.Glide

class ProfileActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etFoto: EditText
    private lateinit var ivFoto: ImageView
    private lateinit var btnGuardar: Button
    private var userId: Int = -1
    private var tipoPerfil: String = "utilizador"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        etNome = findViewById(R.id.et_nome)
        etUsername = findViewById(R.id.et_username)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etFoto = findViewById(R.id.et_foto)
        ivFoto = findViewById(R.id.iv_foto)
        btnGuardar = findViewById(R.id.btn_guardar)

        userId = intent.getIntExtra("user_id", -1)

        tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador"

        if (userId == -1) {
            Toast.makeText(this, "ID de utilizador inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarDadosUtilizador()

        btnGuardar.setOnClickListener {
            val nomeTxt = etNome.text.toString()
            val userTxt = etUsername.text.toString()
            val emailTxt = etEmail.text.toString()
            val passTxt = etPassword.text.toString()
            val fotoTxt = etFoto.text.toString()  // Captura a URL da foto
            Log.d("ProfileActivity", "Foto URL: $fotoTxt")  // Log da URL da foto para depuração

            // Verifica se todos os campos estão preenchidos
            if (nomeTxt.isEmpty() || userTxt.isEmpty() || emailTxt.isEmpty() || passTxt.isEmpty() || fotoTxt.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Agora chama o método para atualizar os dados do utilizador
            atualizarDadosUtilizador(nomeTxt, userTxt, emailTxt, passTxt, fotoTxt)
        }

        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_perfil).setOnClickListener {
            // Já estás no perfil, podes deixar vazio ou dar um Toast
            Toast.makeText(this, "Já estás no Perfil", Toast.LENGTH_SHORT).show()
        }

        val btnSettings = findViewById<ImageView>(R.id.btn_settings)

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        val btnSair = findViewById<Button>(R.id.btn_sair)

        btnSair.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // fecha a atividade atual para não ficar na pilha
        }




    }

    private fun carregarDadosUtilizador() {
        RetrofitClient.instance.getUser(mapOf("user_id" to userId))
            .enqueue(object : Callback<Utilizador> {
                override fun onResponse(call: Call<Utilizador>, response: Response<Utilizador>) {
                    val user = response.body()
                    if (response.isSuccessful && user != null) {
                        etNome.setText(user.nome)
                        etUsername.setText(user.username)
                        etEmail.setText(user.email)
                        etPassword.setText(user.password)
                        etFoto.setText(user.foto)
                        if (user.foto.isNotBlank()) {
                            Glide.with(this@ProfileActivity).load(user.foto).into(ivFoto)
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Utilizador>, t: Throwable) {
                    Log.e("ProfileActivity", "Erro: ${t.message}")
                    Toast.makeText(this@ProfileActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun atualizarDadosUtilizador(nome: String, username: String, email: String, password: String, foto: String) {
        val body = mapOf(
            "id" to userId.toString(),
            "nome" to nome,
            "username" to username,
            "email" to email,
            "password" to password,
            "foto" to foto,  // Passando a URL da foto corretamente
            "tipo_perfil" to tipoPerfil
        )
        Log.d("ProfileActivity", "Body enviado: $body")


        RetrofitClient.instance.updateUser(body)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    Log.d("ProfileActivity", "Código de resposta: ${response.code()}")
                    Log.d("ProfileActivity", "Resposta: ${response.body()}")
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@ProfileActivity, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        val fotoUrl = foto
                        if (fotoUrl.isNotBlank()) {
                            Glide.with(this@ProfileActivity).load(fotoUrl).into(ivFoto)
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show()
                    }
                }


                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Log.e("ProfileActivity", "Erro: ${t.message}")
                    Toast.makeText(this@ProfileActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                }
            })
    }




}
