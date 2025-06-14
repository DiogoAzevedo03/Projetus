package com.example.projetus // Pacote principal

import android.content.Intent // Para iniciar atividades
import android.os.Bundle // Estado da atividade
import android.widget.* // Componentes visuais
import androidx.appcompat.app.AppCompatActivity // Activity base

class AjudaActivity : AppCompatActivity() { // Tela de ajuda ao utilizador

    override fun onCreate(savedInstanceState: Bundle?) { // Inicialização
        super.onCreate(savedInstanceState) // Chamada à superclasse
        setContentView(R.layout.activity_ajuda) // Define o layout

        val etMensagem = findViewById<EditText>(R.id.et_email_ajuda) // Campo de mensagem
        val btnEnviar = findViewById<Button>(R.id.btn_enviar_ajuda) // Botão de envio
        val btnVoltar = findViewById<Button>(R.id.btn_voltar_login) // Botão de voltar ao login

        btnEnviar.setOnClickListener { // Ao clicar em enviar
            val mensagem = etMensagem.text.toString().trim() // Obtém o texto
            if (mensagem.isEmpty()) {
                Toast.makeText(this, "Escreva a sua dúvida", Toast.LENGTH_SHORT).show() // Valida
            } else {
                // TODO: Enviar mensagem via API (futuramente)
                Toast.makeText(this, "Mensagem enviada", Toast.LENGTH_SHORT).show() // Confirma
                etMensagem.text.clear() // Limpa o campo
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
