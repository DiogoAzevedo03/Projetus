package com.example.projetus // declaração do pacote

import android.content.Intent // usado para iniciar activities
import android.os.Bundle // permite aceder ao estado guardado
import android.widget.Button // widget de botão
import androidx.appcompat.app.AppCompatActivity // classe base das activities

class IntroActivity : AppCompatActivity() { // primeira tela mostrada ao iniciar
    override fun onCreate(savedInstanceState: Bundle?) { // callback do ciclo de vida
        super.onCreate(savedInstanceState) // chama o pai
        setContentView(R.layout.activity_intro) // define o layout

        val btnStart = findViewById<Button>(R.id.btn_start) // botão para continuar
        btnStart.setOnClickListener { // abre a activity de login ao clicar
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
