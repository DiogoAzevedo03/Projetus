package com.example.projetus // Pacote principal

import android.content.Intent // Para navegação
import android.os.Bundle // Estado da Activity
import android.widget.* // Widgets
import androidx.appcompat.app.AppCompatActivity // Activity base
import com.example.projetus.RetrofitClient // Cliente Retrofit
import com.example.projetus.network.HelpRequest // Modelo do pedido de ajuda
import com.example.projetus.network.SimpleResponse // Resposta simples da API
import retrofit2.Call // Chamada HTTP
import retrofit2.Callback // Callback do Retrofit
import retrofit2.Response // Resposta do Retrofit

class HelpLoggedActivity : AppCompatActivity() { // Ecrã de ajuda para utilizadores autenticados
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_logged) // Define o layout

        val userId = intent.getIntExtra("user_id", -1) // ID do utilizador
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador" // Perfil

        val etMessage = findViewById<EditText>(R.id.et_message) // Campo de mensagem
        val btnSend = findViewById<Button>(R.id.btn_send) // Botão enviar

        btnSend.setOnClickListener { // Envio da mensagem
            val msg = etMessage.text.toString()
            if (msg.isNotEmpty()) {
                val request = HelpRequest(msg, userId)
                val userId = intent.getIntExtra("user_id", -1)

                RetrofitClient.instance.enviarDuvida(request).enqueue(object : Callback<SimpleResponse> {
                    override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@HelpLoggedActivity, getString(R.string.message_sent), Toast.LENGTH_SHORT).show()
                            etMessage.text.clear()
                        } else {
                            Toast.makeText(this@HelpLoggedActivity, getString(R.string.error_sending_message), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                        Toast.makeText(this@HelpLoggedActivity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, getString(R.string.error_write_question), Toast.LENGTH_SHORT).show()
            }
        }

        // Navegação inferior
        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btn_settings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("tipo_perfil", tipoPerfil)

            startActivity(intent)
        }
    }
}
