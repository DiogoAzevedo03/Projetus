package com.example.projetus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.projetus.data.model.User
import com.example.projetus.data.model.Project
import com.example.projetus.data.model.Task

@Database(
    entities = [User::class, Project::class, Task::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun projectDao(): ProjectDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "projetus_db"
                )
                    .fallbackToDestructiveMigration()  // ← adicionado aqui
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
