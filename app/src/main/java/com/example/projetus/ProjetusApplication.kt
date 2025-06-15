package com.example.projetus // Define o pacote onde esta classe reside

import android.app.Application // Importa a classe base Application do Android

// Classe de aplicação que será inicializada antes de qualquer Activity
class ProjetusApplication : Application() {
    // Metodo chamado quando a aplicação é criada
    override fun onCreate() {
        super.onCreate() // Chama o metodo onCreate da superclasse
        // Aplica o idioma previamente guardado nas preferências
        LocaleManager.applySavedLocale(this)
    }
}
