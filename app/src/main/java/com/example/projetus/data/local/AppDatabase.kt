package com.example.projetus.data.local // Pacote da camada de acesso local a dados

import com.example.projetus.data.local.PendingUserDao // Import do DAO de utilizadores pendentes
import android.content.Context // Necessário para obter o contexto da aplicação
import androidx.room.Database // Anotação que define a base de dados Room
import androidx.room.Room // Classe utilitária para construir a BD
import androidx.room.RoomDatabase // Classe base para a BD Room
import com.example.projetus.data.model.User // Entidade Utilizador
import com.example.projetus.data.model.Project // Entidade Projeto
import com.example.projetus.data.model.Task // Entidade Tarefa
import com.example.projetus.data.local.PendingUser // Entidade Utilizador pendente

@Database(
    entities = [User::class, Project::class, Task::class, PendingUser::class], // Entidades geridas pela Room
    version = 3 // Versão da base de dados
)

abstract class AppDatabase : RoomDatabase() { // Classe abstrata da base de dados

    abstract fun userDao(): UserDao // DAO dos utilizadores
    abstract fun projectDao(): ProjectDao // DAO dos projetos
    abstract fun taskDao(): TaskDao // DAO das tarefas
    abstract fun pendingUserDao(): PendingUserDao // DAO dos utilizadores pendentes

    companion object { // Padrão Singleton para a base de dados
        @Volatile // Visibilidade entre threads
        private var INSTANCE: AppDatabase? = null // Instância única

        fun getDatabase(context: Context): AppDatabase { // Obtém ou cria a instância da BD
            return INSTANCE ?: synchronized(this) { // Garante criação única
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Contexto da aplicação
                    AppDatabase::class.java, // Classe da BD
                    "projetus_db" // Nome da BD
                )
                    .fallbackToDestructiveMigration()  // Destrói e recria em caso de migração incompatível
                    .build() // Constrói a instância
                INSTANCE = instance // Guarda a instância
                instance // Devolve a instância
            }
        }
    }
}
