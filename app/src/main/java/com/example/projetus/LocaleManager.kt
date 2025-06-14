package com.example.projetus // declaração do pacote

import android.content.Context // contexto para acesso às preferências
import androidx.appcompat.app.AppCompatDelegate // gere a aplicação da localidade
import androidx.core.os.LocaleListCompat // compatibilidade de lista de localidades

object LocaleManager { // objeto auxiliar para gerir a localidade da app
    private const val PREFS_NAME = "locale_prefs" // nome do ficheiro de preferências
    private const val KEY_LANGUAGE = "language" // chave para a língua guardada

    fun setLocale(context: Context, language: String) { // atualiza a localidade e persiste
        // Guarda a língua
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
        // Aplica a localidade
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(language)
        )
    }

    fun applySavedLocale(context: Context) { // aplica a língua escolhida previamente
        val lang = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, "pt") ?: "pt" // padrão para português
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(lang)
        )
    }
}