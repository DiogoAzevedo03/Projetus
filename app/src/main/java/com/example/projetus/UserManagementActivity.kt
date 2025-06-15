package com.example.projetus // pacote principal da aplicação

import android.content.Intent // para iniciar outras activities
import android.os.Bundle // utilizado no ciclo de vida da activity
import android.view.LayoutInflater // inflar layouts
import android.widget.* // widgets diversos
import androidx.appcompat.app.AppCompatActivity // classe base das activities
import com.example.projetus.RetrofitClient // cliente retrofit para chamadas API
import com.example.projetus.network.GenericResponse // resposta genérica da API
import com.example.projetus.network.Utilizador // modelo de utilizador
import com.example.projetus.network.UtilizadoresResponse // resposta com lista de utilizadores
import retrofit2.Call // chamada retrofit
import retrofit2.Callback // callback retrofit
import retrofit2.Response // resposta retrofit
import androidx.appcompat.app.AlertDialog // dialogos de confirmação



class UserManagementActivity : AppCompatActivity() { // activity para gestão de utilizadores

    private lateinit var usersContainer: LinearLayout // contêiner onde serão listados os utilizadores
    private lateinit var btnAddUser: Button // botão para adicionar utilizador
    private var userId: Int = -1  // armazena o id do utilizador logado

    override fun onCreate(savedInstanceState: Bundle?) { // metodo chamado ao criar a activity
        super.onCreate(savedInstanceState) // inicializa a activity pai
        setContentView(R.layout.activity_user_management) // define o layout associado

        userId = intent.getIntExtra("user_id", -1)  // obtém o id do utilizador a partir da intent

        usersContainer = findViewById(R.id.users_container) // referência ao contêiner de utilizadores
        btnAddUser = findViewById(R.id.btn_add_user) // referência ao botão adicionar

        btnAddUser.setOnClickListener { // ao clicar no botão de adicionar utilizador
            val intent = Intent(this, AddUserActivity::class.java) // cria intent para AddUserActivity
            intent.putExtra("user_id", userId) // passa o id do utilizador
            startActivity(intent) // inicia a atividade de adicionar utilizador
        } // fim do listener do botão adicionar


        carregarUtilizadores() // carrega a lista de utilizadores ao iniciar

        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador" // tipo de perfil do utilizador

        val btnHome = findViewById<ImageView>(R.id.btn_home) // botão home no menu inferior

        findViewById<ImageView>(R.id.btn_home).setOnClickListener { // ação ao clicar no ícone home
            val intent = Intent(this, DashboardActivity::class.java) // abre DashboardActivity
            intent.putExtra("user_id", userId) // envia id
            intent.putExtra("tipo_perfil", tipoPerfil) // envia tipo de perfil

            startActivity(intent) // inicia a activity
        } // fim do listener home
        val btnProfile = findViewById<ImageView>(R.id.btn_profile) // ícone de perfil
        val btnSettings = findViewById<ImageView>(R.id.btn_settings) // ícone de definições

        btnProfile.setOnClickListener { // ao clicar no ícone de perfil
            val intent = Intent(this, ProfileActivity::class.java) // abre ProfileActivity
            intent.putExtra("user_id", userId) // passa id
            intent.putExtra("tipo_perfil", tipoPerfil) // passa o tipo de perfil

            startActivity(intent) // inicia a activity de perfil
        } // fim do listener de perfil

        btnSettings.setOnClickListener { // ao clicar no ícone de definições
            val intent = Intent(this, SettingsActivity::class.java) // abre SettingsActivity
            intent.putExtra("user_id", userId) // envia id
            intent.putExtra("tipo_perfil", tipoPerfil) // envia tipo de perfil

            startActivity(intent) // inicia a activity
        } // fim listener definições
    } // fim do onCreate

    override fun onResume() { // chamado quando a activity volta ao primeiro plano
        super.onResume() // chama implementação da superclasse
        // Refresh the user list whenever we return to this screen
        carregarUtilizadores() // recarrega a lista de utilizadores
    } // fim do onResume
    private fun carregarUtilizadores() { // obtém a lista de utilizadores do servidor
        RetrofitClient.instance.getUtilizadores().enqueue(object : Callback<UtilizadoresResponse> { // chamada assíncrona
            override fun onResponse(call: Call<UtilizadoresResponse>, response: Response<UtilizadoresResponse>) { // resposta da API
                if (response.isSuccessful && response.body()?.success == true) { // verifica sucesso
                    val lista = response.body()?.utilizadores ?: emptyList() // obtém lista de utilizadores
                    mostrarUtilizadores(lista) // apresenta na interface
                } else { // em caso de erro
                    Toast.makeText(this@UserManagementActivity, "Erro ao carregar utilizadores", Toast.LENGTH_SHORT).show() // mostra mensagem
                }
            }

            override fun onFailure(call: Call<UtilizadoresResponse>, t: Throwable) { // erro na chamada
                Toast.makeText(this@UserManagementActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show() // exibe erro
            }
        }) // fim da chamada
    } // fim do metodo carregarUtilizadores

    private fun mostrarUtilizadores(lista: List<Utilizador>) { // apresenta cada utilizador num cartão
        usersContainer.removeAllViews() // limpa o contêiner

        for (user in lista) { // percorre a lista de utilizadores
            val card = layoutInflater.inflate(R.layout.user_card_item, usersContainer, false) // infla o layout do cartão

            card.findViewById<TextView>(R.id.tv_nome).text = user.nome // define o nome
            card.findViewById<TextView>(R.id.tv_email).text = "Email: ${user.email}" // define o email
            card.findViewById<TextView>(R.id.tv_perfil).text = "Perfil: ${user.tipo_perfil}" // define o perfil

            card.findViewById<Button>(R.id.btn_edit).setOnClickListener { // botão de editar utilizador
                val intent = Intent(this, EditUserActivity::class.java) // inicia EditUserActivity
                intent.putExtra("utilizador", user) // user deve ser serializable
                startActivity(intent) // inicia activity
            }


            card.findViewById<Button>(R.id.btn_remove).setOnClickListener { // botão remover
                AlertDialog.Builder(this)
                    .setTitle("Remover Utilizador") // título do diálogo
                    .setMessage("Tens a certeza que queres remover ${user.nome}?") // mensagem
                    .setPositiveButton("Sim") { _, _ -> // acção positiva
                        val data = mapOf("utilizador_id" to user.id.toString()) // prepara parâmetros
                        RetrofitClient.instance.deleteUser(data).enqueue(object : Callback<GenericResponse> { // chamada de remoção
                            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) { // resposta
                                if (response.isSuccessful && response.body()?.success == true) { // sucesso
                                    Toast.makeText(this@UserManagementActivity, "Utilizador removido!", Toast.LENGTH_SHORT).show() // feedback
                                    carregarUtilizadores()  // Atualiza lista
                                } else { // falha na resposta
                                    Toast.makeText(this@UserManagementActivity, response.body()?.message ?: "Erro ao remover", Toast.LENGTH_SHORT).show() // mostra erro
                                }
                            }

                            override fun onFailure(call: Call<GenericResponse>, t: Throwable) { // erro na chamada
                                Toast.makeText(this@UserManagementActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show() // mostra erro
                            }
                        }) // fim da chamada deleteUser
                    }
                    .setNegativeButton("Cancelar", null) // opção cancelar
                    .show() // exibe o diálogo
            }


            usersContainer.addView(card) // adiciona o cartão ao contêiner
        } // fim do loop
    } // fim do metodo mostrarUtilizadores
} // fim da classe
