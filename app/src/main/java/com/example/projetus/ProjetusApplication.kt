package com.example.projetus // Define o pacote onde esta classe reside

import android.app.Application // Importa a classe base Application do Android

// Classe de aplicação que será inicializada antes de qualquer Activity
class ProjetusApplication : Application() {
    // Método chamado quando a aplicação é criada
    override fun onCreate() {
        super.onCreate() // Chama o método onCreate da superclasse
        // Aplica o idioma previamente guardado nas preferências
        LocaleManager.applySavedLocale(this)
    }
}
