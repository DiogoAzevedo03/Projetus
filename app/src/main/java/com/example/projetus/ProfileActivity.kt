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

class ProfileActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnGuardar: Button
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        etNome = findViewById(R.id.et_nome)
        etUsername = findViewById(R.id.et_username)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnGuardar = findViewById(R.id.btn_guardar)

        userId = intent.getIntExtra("user_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "ID de utilizador inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarDadosUtilizador()

        btnGuardar.setOnClickListener {
            atualizarDadosUtilizador()
        }
        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
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

    private fun atualizarDadosUtilizador() {
        val body = mapOf(
            "id" to userId.toString(),
            "nome" to etNome.text.toString(),
            "username" to etUsername.text.toString(),
            "email" to etEmail.text.toString(),
            "password" to etPassword.text.toString()
        )

        RetrofitClient.instance.updateUser(body)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@ProfileActivity, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
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
