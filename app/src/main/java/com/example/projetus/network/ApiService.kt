package com.example.projetus.network

import AvaliacoesResponse
import com.example.projetus.StatisticsTarefaResponse
import com.example.projetus.network.GenericResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register.php")
    fun registerUser(
        @Field("nome") nome: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("tipo_perfil") tipoPerfil: String
    ): Call<GenericResponse>

    @POST("dashboard.php")
    fun getDashboardData(
        @Body userIdBody: Map<String, Int>): Call<DashboardResponse>

    @POST("tasks.php")
    fun getTasks(
        @Body body: Map<String, Int>): Call<TasksResponse>

    @POST("get_projects.php")
    fun getProjects(
        @Body body: Map<String, Int>
    ): Call<ProjectResponse>

    @FormUrlEncoded
    @POST("add_project.php")
    fun addProject(
        @Field("nome") nome: String,
        @Field("descricao") descricao: String,
        @Field("gestor_id") gestorId: Int,
        @Field("data_inicio") dataInicio: String,
        @Field("data_fim") dataFim: String
    ): Call<GenericResponse>

    @POST("get_users.php")
    fun getUtilizadores(): Call<UtilizadoresResponse>


    @POST("add_task.php")
    fun addTask(@Body body: Map<String, String>): Call<GenericResponse>


    @POST("get_user.php")
    fun getUser(@Body body: Map<String, Int>): Call<Utilizador>

    @POST("update_user.php")
    fun updateUser(@Body body: Map<String, String>): Call<GenericResponse>

    @POST("get_task_details.php")
    fun getTaskDetails(@Body data: Map<String, Int>): Call<TaskDetailsResponse>

    @POST("update_task_details.php")
    fun updateTaskDetails(@Body data: Map<String, Any>): Call<GenericResponse>

    @POST("concluir_tarefa.php")
    fun marcarTarefaConcluida(@Body data: Map<String, Int>): Call<GenericResponse>


    @POST("update_task_details.php")
    fun updateTaskDetails(@Body data: UpdateTaskRequest): Call<GenericResponse>

    @POST("estatisticas.php")
    fun getEstatisticas(@Body body: Map<String, Int>): Call<StatisticsResponse>

    @POST("getTarefasDoProjeto.php")
    fun getTasksByProject(@Body data: Map<String, Int>): Call<TasksResponse>

    @POST("concluirProjeto.php")
    fun concluirProjeto(@Body data: Map<String, Int>): Call<SimpleResponse>

    @POST("associarColaborador.php")
    fun associarColaborador(@Body request: AssociarRequest): Call<SimpleResponse>

    @POST("colaboradoresProjeto.php")
    fun getColaboradoresDoProjeto(@Body data: Map<String, Int>): Call<UtilizadoresResponse>

    @POST("tarefasDoProjetoComColaboradores.php")
    fun getTarefasComColaboradores(@Body data: Map<String, Int>): Call<AvaliacoesResponse>

    @POST("avaliarTarefa.php")
    fun avaliarTarefa(@Body data: AvaliacaoRequest): Call<SimpleResponse>

    @POST("estatisticasProjeto.php")
    fun getEstatisticasProjeto(@Body body: Map<String, Int>): Call<StatisticsProjetoResponse>


    @POST("estatisticasTarefa.php")
    fun getEstatisticasTarefas(@Body params: Map<String, Int>): Call<StatisticsTarefaResponse>


    @POST("delete_user.php")
    fun deleteUser(@Body data: Map<String, String>): Call<GenericResponse>

    @POST("get_gestores.php")
    fun getGestores(): Call<GestoresResponse>


}
