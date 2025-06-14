package com.example.projetus // declaração do pacote

import android.os.Bundle // bundle para estado guardado
import androidx.activity.enableEdgeToEdge // utilitário para conteúdo edge-to-edge
import androidx.appcompat.app.AppCompatActivity // classe base de activity
import androidx.core.view.ViewCompat // utilidades de compatibilidade
import androidx.core.view.WindowInsetsCompat // lida com as barras do sistema

class MainActivity : AppCompatActivity() { // activity gerada por defeito
    override fun onCreate(savedInstanceState: Bundle?) { // ponto de entrada do ciclo de vida
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // desenha atrás das barras do sistema
        setContentView(R.layout.activity_main) // define o layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // obtém margens das barras do sistema
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom) // aplica margens
            insets
        }
    }
}