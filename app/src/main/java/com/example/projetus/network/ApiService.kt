package com.example.projetus.network // Define o pacote do arquivo

import AvaliacoesResponse // Importa o modelo de resposta de avaliacoes
import com.example.projetus.network.StatisticsTarefaResponse // Importa a resposta para estatisticas de tarefas
import com.example.projetus.network.GenericResponse // Importa a resposta generica
import retrofit2.Call // Classe Call do Retrofit para chamadas HTTP
import retrofit2.http.Body // Anotacao para enviar o corpo da requisicao
import retrofit2.http.Field // Anotacao para enviar um campo em formulários
import retrofit2.http.FieldMap // Anotacao para enviar varios campos em formulários
import retrofit2.http.FormUrlEncoded // Indica que a chamada envia dados codificados em formulário
import retrofit2.http.GET // Anotacao para requisicoes GET
import retrofit2.http.POST // Anotacao para requisicoes POST
import com.example.projetus.network.HelpRequest // Modelo do pedido de ajuda

interface ApiService { // Interface que define os endpoints da API

    @FormUrlEncoded // Envia os dados como formulario
    @POST("login.php") // Endpoint de login
    fun loginUser(
        @Field("email") email: String, // Email do utilizador
        @Field("password") password: String // Password do utilizador
    ): Call<LoginResponse> // Resposta com os dados do login

    @FormUrlEncoded // Envia os dados de registro como formulario
    @POST("register.php") // Endpoint de registro
    fun registerUser(
        @Field("nome") nome: String, // Nome do utilizador
        @Field("username") username: String, // Username do utilizador
        @Field("email") email: String, // Email do utilizador
        @Field("password") password: String, // Password do utilizador
        @Field("tipo_perfil") tipoPerfil: String, // Perfil de acesso
        @Field("foto") foto: String // Foto do utilizador
    ): Call<GenericResponse> // Resposta generica com o resultado

    @POST("dashboard.php") // Endpoint para obter dados do dashboard
    fun getDashboardData(
        @Body userIdBody: Map<String, Int>): Call<DashboardResponse> // Envia o ID do utilizador e recebe o dashboard

    @POST("tasks.php") // Endpoint de lista de tarefas
    fun getTasks(
        @Body body: Map<String, Int>): Call<TasksResponse> // Envia parametros e recebe a lista de tarefas

    @POST("get_projects.php") // Endpoint de obtencao de projetos
    fun getProjects(
        @Body body: Map<String, Int>
    ): Call<ProjectResponse> // Resposta com a lista de projetos

    @FormUrlEncoded // Envia dados do projeto como formulario
    @POST("add_project.php") // Endpoint para adicionar projeto
    fun addProject(
        @Field("nome") nome: String, // Nome do projeto
        @Field("descricao") descricao: String, // Descricao do projeto
        @Field("gestor_id") gestorId: Int, // ID do gestor
        @Field("data_inicio") dataInicio: String, // Data de inicio
        @Field("data_fim") dataFim: String // Data de fim
    ): Call<GenericResponse> // Resposta generica

    @POST("get_users.php") // Endpoint para obter utilizadores
    fun getUtilizadores(): Call<UtilizadoresResponse> // Retorna lista de utilizadores


    @POST("add_task.php") // Endpoint para adicionar tarefa
    fun addTask(@Body body: Map<String, String>): Call<GenericResponse> // Envia dados da tarefa


    @POST("get_user.php") // Endpoint para obter um utilizador
    fun getUser(@Body body: Map<String, Int>): Call<Utilizador> // Devolve os dados do utilizador

    @POST("update_user.php") // Endpoint para atualizar utilizador
    fun updateUser(@Body body: Map<String, String>): Call<GenericResponse> // Envia dados atualizados

    @POST("get_task_details.php") // Endpoint para detalhes de tarefa
    fun getTaskDetails(@Body data: Map<String, Int>): Call<TaskDetailsResponse> // Retorna detalhes completos

    @POST("update_task_details.php") // Atualiza dados da tarefa
    fun updateTaskDetails(@Body data: Map<String, Any>): Call<GenericResponse> // Envia mapa de dados variado

    @POST("concluir_tarefa.php") // Marca tarefa como concluída
    fun marcarTarefaConcluida(@Body data: Map<String, Int>): Call<GenericResponse> // Envia ID da tarefa


    @POST("update_task_details.php") // Outra versao de atualizacao de tarefa
    fun updateTaskDetails(@Body data: UpdateTaskRequest): Call<GenericResponse> // Usa objeto dedicado

    @POST("estatisticas.php") // Endpoint de estatisticas gerais
    fun getEstatisticas(@Body body: Map<String, Int>): Call<StatisticsResponse> // Retorna dados estatisticos

    @POST("getTarefasDoProjeto.php") // Tarefas de um projeto especifico
    fun getTasksByProject(@Body data: Map<String, Int>): Call<TasksResponse> // Envia id do projeto e recebe tarefas

    @POST("concluirProjeto.php") // Concluir um projeto
    fun concluirProjeto(@Body data: Map<String, Int>): Call<SimpleResponse> // Envia id do projeto

    @POST("associarColaborador.php") // Associa colaborador a projeto
    fun associarColaborador(@Body request: AssociarRequest): Call<SimpleResponse> // Usa objeto com ids

    @POST("colaboradoresProjeto.php") // Busca colaboradores de um projeto
    fun getColaboradoresDoProjeto(@Body data: Map<String, Int>): Call<UtilizadoresResponse> // Lista de utilizadores

    @POST("tarefasDoProjetoComColaboradores.php") // Tarefas de um projeto e seus colaboradores
    fun getTarefasComColaboradores(@Body data: Map<String, Int>): Call<AvaliacoesResponse> // Retorna avaliacoes

    @POST("avaliarTarefa.php") // Envia avaliacao da tarefa
    fun avaliarTarefa(@Body data: AvaliacaoRequest): Call<SimpleResponse> // Utiliza objeto de avaliacao

    @POST("estatisticasProjeto.php") // Estatisticas especificas de projeto
    fun getEstatisticasProjeto(@Body body: Map<String, Int>): Call<StatisticsProjetoResponse> // Retorna dados do projeto


    @POST("estatisticasTarefa.php") // Estatisticas das tarefas
    fun getEstatisticasTarefas(@Body params: Map<String, Int>): Call<StatisticsTarefaResponse> // Retorna lista de tarefas


    @POST("delete_user.php") // Remove utilizador
    fun deleteUser(@Body data: Map<String, String>): Call<GenericResponse> // Envia id do utilizador

    @POST("get_gestores.php") // Lista de gestores
    fun getGestores(): Call<GestoresResponse> // Resposta com gestores

    @POST("enviar_duvida.php") // Envia dúvida de suporte
    fun enviarDuvida(@Body request: HelpRequest): Call<SimpleResponse> // Envia mensagem de ajuda


    @POST("enviar_sugestao.php")
    fun enviarSugestao(@Body request: SuggestionRequest): Call<SimpleResponse>

    @POST("delete_project.php")
    fun apagarProjeto(@Body data: Map<String, Int>): Call<SimpleResponse>

} // Fim da interface
