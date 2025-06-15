package com.example.projetus // pacote da aplicação

import android.content.Intent // para navegação entre actividades
import android.os.Bundle // para estado guardado da actividade
import android.widget.* // widgets utilizados no layout
import androidx.appcompat.app.AppCompatActivity // classe base para actividades

class UsernameRecoveryActivity : AppCompatActivity() { // activity para recuperar o nome de utilizador

    override fun onCreate(savedInstanceState: Bundle?) { // metodo chamado na criação da activity
        super.onCreate(savedInstanceState) // inicializa a actividade pai
        setContentView(R.layout.activity_username_recovery) // define o layout da activity

        val etEmail = findViewById<EditText>(R.id.et_username_email) // campo de texto para email
        val btnBack = findViewById<Button>(R.id.btn_back_username) // botão de voltar atrás
        val btnNext = findViewById<Button>(R.id.btn_next_username) // botão para continuar
        val btnResend = findViewById<Button>(R.id.btn_resend_username) // botão para reenviar código

        btnBack.setOnClickListener { // acção ao clicar no botão voltar
            finish() // fecha a activity e regressa ao ecrã anterior
        } // fim do listener do botão voltar

        btnNext.setOnClickListener { // acção ao clicar no botão continuar
            val email = etEmail.text.toString().trim() // obtém o email introduzido
            if (email.isEmpty()) { // verifica se o email está vazio
                Toast.makeText(this, "Introduza o email de recuperação", Toast.LENGTH_SHORT).show() // mostra aviso
            } else { // se o email não estiver vazio
                Toast.makeText(this, "Código de verificação enviado (mock)", Toast.LENGTH_SHORT).show() // feedback de sucesso mock
            }
        } // fim do listener do botão continuar

        btnResend.setOnClickListener { // acção para reenviar o email de verificação
            Toast.makeText(this, "A reenviar email (mock)", Toast.LENGTH_SHORT).show() // mensagem de feedback
        } // fim do listener do botão reenviar

        val helpIcon = findViewById<ImageView>(R.id.help_icon) // ícone de ajuda no ecrã

        helpIcon.setOnClickListener { // quando se clica no ícone de ajuda
            val intent = Intent(this, AjudaActivity::class.java) // cria intenção para abrir AjudaActivity
            startActivity(intent) // inicia a activity de ajuda
        } // fim do listener do ícone de ajuda
    } // fim do onCreate
} // fim da classe
