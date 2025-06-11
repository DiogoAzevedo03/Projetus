package com.example.projetus

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.RetrofitClient
import com.example.projetus.network.GenericResponse
import com.example.projetus.network.Utilizador
import com.example.projetus.network.UtilizadoresResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AlertDialog



class UserManagementActivity : AppCompatActivity() {

    private lateinit var usersContainer: LinearLayout
    private lateinit var btnAddUser: Button
    private var userId: Int = -1  // ✅ ADICIONAR AQUI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        userId = intent.getIntExtra("user_id", -1)  // ✅ ATRIBUIÇÃO AQUI

        usersContainer = findViewById(R.id.users_container)
        btnAddUser = findViewById(R.id.btn_add_user)

        btnAddUser.setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }


        carregarUtilizadores()

        val btnHome = findViewById<ImageView>(R.id.btn_home)

        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }
        val btnProfile = findViewById<ImageView>(R.id.btn_profile)
        val btnSettings = findViewById<ImageView>(R.id.btn_settings)

        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }
    }

    private fun carregarUtilizadores() {
        RetrofitClient.instance.getUtilizadores().enqueue(object : Callback<UtilizadoresResponse> {
            override fun onResponse(call: Call<UtilizadoresResponse>, response: Response<UtilizadoresResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val lista = response.body()?.utilizadores ?: emptyList()
                    mostrarUtilizadores(lista)
                } else {
                    Toast.makeText(this@UserManagementActivity, "Erro ao carregar utilizadores", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UtilizadoresResponse>, t: Throwable) {
                Toast.makeText(this@UserManagementActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarUtilizadores(lista: List<Utilizador>) {
        usersContainer.removeAllViews()

        for (user in lista) {
            val card = layoutInflater.inflate(R.layout.user_card_item, usersContainer, false)

            card.findViewById<TextView>(R.id.tv_nome).text = user.nome
            card.findViewById<TextView>(R.id.tv_email).text = "Email: ${user.email}"
            card.findViewById<TextView>(R.id.tv_perfil).text = "Perfil: ${user.tipo_perfil}"

            card.findViewById<Button>(R.id.btn_edit).setOnClickListener {
                val intent = Intent(this, EditUserActivity::class.java)
                intent.putExtra("utilizador", user) // user deve ser serializable
                startActivity(intent)
            }


            card.findViewById<Button>(R.id.btn_remove).setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Remover Utilizador")
                    .setMessage("Tens a certeza que queres remover ${user.nome}?")
                    .setPositiveButton("Sim") { _, _ ->
                        val data = mapOf("utilizador_id" to user.id.toString())
                        RetrofitClient.instance.deleteUser(data).enqueue(object : Callback<GenericResponse> {
                            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                                if (response.isSuccessful && response.body()?.success == true) {
                                    Toast.makeText(this@UserManagementActivity, "Utilizador removido!", Toast.LENGTH_SHORT).show()
                                    carregarUtilizadores()  // Atualiza lista
                                } else {
                                    Toast.makeText(this@UserManagementActivity, response.body()?.message ?: "Erro ao remover", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                                Toast.makeText(this@UserManagementActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }


            usersContainer.addView(card)
        }
    }
}
