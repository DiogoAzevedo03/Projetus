package com.example.projetus // Pacote principal da aplicação

import com.example.projetus.network.ApiService // Interface com definições das rotas
import retrofit2.Retrofit // Biblioteca Retrofit para chamadas HTTP
import retrofit2.converter.gson.GsonConverterFactory // Conversor JSON para objetos Kotlin

// Objeto que fornece uma instância única de Retrofit
object RetrofitClient {
    // Endereço base da API (10.0.2.2 = localhost do computador no emulador)
    private const val BASE_URL = "http://10.0.2.2/projetus_api/"

    // Instância do serviço criada preguiçosamente
    val instance: ApiService by lazy {
        Retrofit.Builder() // Constrói o objeto Retrofit
            .baseUrl(BASE_URL) // Define a URL base
            .addConverterFactory(GsonConverterFactory.create()) // Usa Gson para converter JSON
            .build() // Cria a instância Retrofit
            .create(ApiService::class.java) // Cria a implementação da interface ApiService
    }
}

