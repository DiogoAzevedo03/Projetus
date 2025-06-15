package com.example.projetus // Pacote principal

import android.content.Intent // Para iniciar atividades
import android.os.Bundle // Estado da atividade
import android.widget.* // Componentes visuais
import androidx.appcompat.app.AppCompatActivity // Activity base
import com.example.projetus.RetrofitClient // Cliente Retrofit
import com.example.projetus.network.HelpRequest // Modelo de pedido de ajuda
import com.example.projetus.network.SimpleResponse // Resposta simples da API
import retrofit2.Call // Chamada HTTP
import retrofit2.Callback // Callback do Retrofit
import retrofit2.Response // Resposta do Retrofit

class AjudaActivity : AppCompatActivity() { // Tela de ajuda ao utilizador

    override fun onCreate(savedInstanceState: Bundle?) { // Inicialização
        super.onCreate(savedInstanceState) // Chamada à superclasse
        setContentView(R.layout.activity_ajuda) // Define o layout

        val etMensagem = findViewById<EditText>(R.id.et_email_ajuda)
        val btnEnviar = findViewById<Button>(R.id.btn_enviar_ajuda) // Botão de envio
        val btnVoltar = findViewById<Button>(R.id.btn_voltar_login) // Botão de voltar ao login

        btnEnviar.setOnClickListener { // Ao clicar em enviar
            val mensagem = etMensagem.text.toString().trim()
            if (mensagem.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_write_question), Toast.LENGTH_SHORT).show()
            } else {
                val request = HelpRequest(mensagem) // Cria o objeto de envio
                val userId = intent.getIntExtra("user_id", -1)
                RetrofitClient.instance.enviarDuvida(request).enqueue(object : Callback<SimpleResponse> {
                    override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@AjudaActivity, getString(R.string.message_sent), Toast.LENGTH_SHORT).show()
                            etMensagem.text.clear()
                        } else {
                            Toast.makeText(this@AjudaActivity, getString(R.string.error_sending_message), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                        Toast.makeText(this@AjudaActivity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        btnVoltar.setOnClickListener { // Voltar ao login
            val intent = Intent(this, LoginActivity::class.java) // Nova intenção
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Limpa a pilha
            startActivity(intent) // Inicia atividade
            finish() // Fecha a atual
        }
    } // Fim do onCreate
} // Fim da Activity
