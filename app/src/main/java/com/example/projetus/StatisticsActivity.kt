package com.example.projetus // pacote da aplicação

import android.content.ContentValues // para definir metadados ao guardar ficheiros
import android.content.Intent // utilizado para navegar entre activities
import android.os.Bundle // estado guardado da activity
import android.provider.MediaStore // acesso ao armazenamento
import android.util.Log // logs de debug
import android.widget.* // widgets do layout
import androidx.appcompat.app.AppCompatActivity // classe base das activities
import com.example.projetus.network.StatisticsResponse // modelo de resposta das estatísticas de utilizador
import retrofit2.Call // chamada retrofit
import retrofit2.Callback // callback retrofit
import retrofit2.Response // resposta retrofit
import android.graphics.pdf.PdfDocument // para gerar PDF
import java.io.File // representação de ficheiro
import java.io.FileOutputStream // escrita em ficheiros
import android.os.Build // verificação de versão Android
import android.os.Environment // acesso a pastas públicas
import java.io.OutputStream // fluxo de saída
import com.example.projetus.network.StatisticsProjetoResponse // resposta de estatísticas de projeto
import com.example.projetus.network.ProjectStat // modelo de projeto
import android.content.res.ColorStateList // lista de estados de cor
import android.graphics.Color // utilitários de cor
import com.example.projetus.network.TaskStat // modelo de tarefa
import com.example.projetus.network.StatisticsTarefaResponse // resposta de estatísticas de tarefa

class StatisticsActivity : AppCompatActivity() { // activity que apresenta estatísticas

    private var filtroAtual: String = "utilizador" // filtro atualmente selecionado
    private lateinit var statsContainer: LinearLayout // contêiner para as estatísticas

    // Variáveis estatísticas para utilizador
    private var nome = "" // nome do utilizador
    private var tarefasAtribuidas = 0 // total de tarefas atribuídas
    private var tarefasConcluidas = 0 // tarefas concluídas
    private var taxaConclusao = 0 // percentagem de conclusão
    private var projetosConcluidos = 0 // projetos concluídos

    // Lista para armazenar todos os projetos
    private var listaProjetos: List<ProjectStat> = emptyList() // projetos obtidos da API

    // Lista para armazenar todas as tarefas
    private var listaTarefas: List<TaskStat> = emptyList() // tarefas obtidas da API

    override fun onCreate(savedInstanceState: Bundle?) { // metodo chamado na criação da activity
        super.onCreate(savedInstanceState) // inicializa a activity pai
        setContentView(R.layout.activity_statistics) // define o layout desta activity

        statsContainer = findViewById(R.id.stats_container) // obtém o contêiner onde as estatísticas serão mostradas

        val userId = intent.getIntExtra("user_id", -1) // id recebido da activity anterior
        if (userId == -1) { // verifica se foi enviado um id válido
            Toast.makeText(this, getString(R.string.invalid_user), Toast.LENGTH_SHORT).show() // informa erro
            finish() // encerra a activity
            return // sai do metodo
        }
        val tipoPerfil = intent.getStringExtra("tipo_perfil") ?: "utilizador" // tipo de perfil do utilizador

        val btnUtilizador = findViewById<Button>(R.id.btn_utilizador) // botão para filtro de utilizador
        val btnProjeto = findViewById<Button>(R.id.btn_projeto) // botão para filtro de projetos
        val btnTarefa = findViewById<Button>(R.id.btn_tarefa) // botão para filtro de tarefas

        // Carregar estatísticas por defeito
        carregarEstatisticasUtilizador(userId) // mostra estatísticas do utilizador inicialmente

        btnUtilizador.setOnClickListener { // clique no botão utilizador
            filtroAtual = "utilizador" // define filtro
            carregarEstatisticasUtilizador(userId) // carrega estatísticas do utilizador
        }

        btnProjeto.setOnClickListener { // clique no botão projeto
            filtroAtual = "projeto" // define filtro
            carregarEstatisticasProjeto(userId) // carrega estatísticas por projeto
        }

        btnTarefa.setOnClickListener { // clique no botão tarefa
            filtroAtual = "tarefa" // define filtro
            carregarEstatisticasTarefa(userId) // carrega estatísticas por tarefa
        }
// Navegação
        findViewById<ImageView>(R.id.btn_home).setOnClickListener { // clique no ícone home
            val intent = Intent(this, DashboardActivity::class.java) // cria intent para o dashboard
            intent.putExtra("user_id", userId) // envia id do utilizador
            intent.putExtra("tipo_perfil", tipoPerfil) // envia tipo de perfil
            startActivity(intent) // abre o dashboard
        }

        findViewById<ImageView>(R.id.btn_profile).setOnClickListener { // clique no ícone perfil
            val intent = Intent(this, ProfileActivity::class.java) // abre ProfileActivity
            intent.putExtra("user_id", userId) // envia id
            intent.putExtra("tipo_perfil", tipoPerfil) // envia tipo de perfil
            startActivity(intent) // inicia activity de perfil
        }

        findViewById<ImageView>(R.id.btn_settings).setOnClickListener { // clique em definições
            val intent = Intent(this, SettingsActivity::class.java) // abre SettingsActivity
            intent.putExtra("user_id", userId) // envia id
            intent.putExtra("tipo_perfil", tipoPerfil) // envia tipo perfil
            startActivity(intent) // inicia activity
        }

    } // fim do onCreate

    private fun carregarEstatisticasUtilizador(userId: Int) { // obtém estatísticas do utilizador
        statsContainer.removeAllViews() // limpa o contêiner
        layoutInflater.inflate(R.layout.layout_user_stats, statsContainer, true) // infla layout com estatísticas de utilizador

        // Configurar o botão exportar APÓS inflatar o layout
        configurarBotaoExportar() // associa o botão exportar

        RetrofitClient.instance.getEstatisticas(mapOf("user_id" to userId)) // chamada à API
            .enqueue(object : Callback<StatisticsResponse> { // callback da resposta
                override fun onResponse(call: Call<StatisticsResponse>, response: Response<StatisticsResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) { // se sucesso
                        val stats = response.body()!! // obtém dados
                        nome = stats.nome // guarda nome
                        tarefasAtribuidas = stats.tarefas_atribuidas // guarda tarefas atribuídas
                        tarefasConcluidas = stats.tarefas_concluidas // guarda concluídas
                        taxaConclusao = stats.taxa_conclusao // guarda taxa
                        projetosConcluidos = stats.projetos_concluidos // guarda projetos concluídos

                        updateUI() // actualiza a interface
                    }
                }

                override fun onFailure(call: Call<StatisticsResponse>, t: Throwable) { // em caso de falha
                    Toast.makeText(this@StatisticsActivity, getString(R.string.error_loading_statistics), Toast.LENGTH_SHORT).show()
                }
            }) // fim da chamada
    }

    private fun carregarEstatisticasProjeto(userId: Int) {
        RetrofitClient.instance.getEstatisticasProjeto(mapOf("user_id" to userId))
            .enqueue(object : Callback<StatisticsProjetoResponse> {
                override fun onResponse(
                    call: Call<StatisticsProjetoResponse>,
                    response: Response<StatisticsProjetoResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val stats = response.body()!! // corpo da resposta

                        statsContainer.removeAllViews() // limpa o contêiner

                        // Armazenar TODOS os projetos
                        listaProjetos = stats.projetos ?: emptyList() // guarda lista de projetos

                        // Adicionar informações dos projetos
                        listaProjetos.forEachIndexed { index, projeto -> // para cada projeto
                            val tvProjeto = TextView(this@StatisticsActivity).apply {
                                text = getString(
                                    R.string.statistics_project_item,
                                    index + 1,
                                    projeto.nome,
                                    projeto.tarefas_atribuidas,
                                    projeto.tarefas_concluidas,
                                    projeto.taxa_conclusao
                                )
                                setPadding(0, 0, 0, 32)
                            }
                            statsContainer.addView(tvProjeto)
                        }

                        // Adicionar botão exportar no final
                        adicionarBotaoExportar() // botão para exportar dados

                    } else {
                        Toast.makeText(this@StatisticsActivity, getString(R.string.error_project_not_found), Toast.LENGTH_SHORT).show() // mensagem de erro
                    }
                }

                override fun onFailure(
                    call: Call<StatisticsProjetoResponse>,
                    t: Throwable
                ) {
                    Toast.makeText(this@StatisticsActivity, getString(R.string.error_loading_project_stats), Toast.LENGTH_SHORT).show() // erro na chamada
                }
            })
    }

    private fun carregarEstatisticasTarefa(userId: Int) { // obtém estatísticas das tarefas
        statsContainer.removeAllViews() // limpa contêiner

        // CORRIGIDO: Usar endpoint específico para tarefas
        RetrofitClient.instance.getEstatisticasTarefas(mapOf("user_id" to userId)) // chamada à API
            .enqueue(object : Callback<StatisticsTarefaResponse> { // callback
                override fun onResponse(
                    call: Call<StatisticsTarefaResponse>,
                    response: Response<StatisticsTarefaResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) { // se sucesso
                        val stats = response.body()!! // corpo da resposta

                        // Armazenar TODAS as tarefas
                        listaTarefas = stats.tarefas ?: emptyList() // guarda lista de tarefas

                        if (listaTarefas.isNotEmpty()) {
                            // Mostrar todas as tarefas
                            listaTarefas.forEachIndexed { index, tarefa ->
                                val tvTarefa = TextView(this@StatisticsActivity).apply {
                                    text = getString(
                                        R.string.statistics_task_item,
                                        index + 1,
                                        tarefa.nome,
                                        tarefa.projeto_nome,
                                        tarefa.estado,
                                        tarefa.data_criacao ?: getString(R.string.not_available),
                                        tarefa.data_conclusao ?: getString(R.string.pending)
                                    )
                                    setPadding(0, 0, 0, 32)
                                }
                                statsContainer.addView(tvTarefa)
                            }
                        } else {
                            val tvSemTarefas = TextView(this@StatisticsActivity).apply {
                                text = getString(R.string.no_tasks_found)
                                setPadding(0, 0, 0, 32)
                            }
                            statsContainer.addView(tvSemTarefas) // mensagem caso não haja tarefas
                        }

                        // Adicionar botão exportar
                        adicionarBotaoExportar() // adiciona botão de exportação
                    } else {
                        Toast.makeText(this@StatisticsActivity, getString(R.string.error_loading_tasks), Toast.LENGTH_SHORT).show() // erro de resposta
                    }
                }

                override fun onFailure(call: Call<StatisticsTarefaResponse>, t: Throwable) { // falha na chamada
                    Toast.makeText(this@StatisticsActivity, getString(R.string.error_loading_tasks_stats), Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun configurarBotaoExportar() { // procura botão de exportar existente
        val btnExportar = statsContainer.findViewById<Button>(R.id.btn_exportar) // referência ao botão
        btnExportar?.setOnClickListener { // define clique
            exportarEstatisticas() // executa exportação
        }
    }

    private fun adicionarBotaoExportar() { // adiciona botão de exportar dinamicamente
        val btnExportar = layoutInflater.inflate(R.layout.layout_export_button, statsContainer, false) as Button // infla layout do botão
        btnExportar.setOnClickListener {
            exportarEstatisticas() // acção de exportar
        }
        statsContainer.addView(btnExportar) // insere botão no contêiner
    }


    private fun exportarEstatisticas() { // compõe dados e gera PDF
        val fileContents = when (filtroAtual) { // verifica filtro activo
            "utilizador" -> {
                val header = getString(R.string.statistics_user_header)
                val corpo = getString(
                    R.string.statistics_user_content,
                    nome,
                    tarefasAtribuidas,
                    tarefasConcluidas,
                    taxaConclusao,
                    projetosConcluidos
                )
                "$header\n\n$corpo"
            }

            "projeto" -> {
                val headerProjetos = getString(R.string.statistics_projects_header)
                val dadosProjetos = if (listaProjetos.isNotEmpty()) {
                    listaProjetos.mapIndexed { index, projeto ->
                        getString(
                            R.string.statistics_project_item,
                            index + 1,
                            projeto.nome,
                            projeto.tarefas_atribuidas,
                            projeto.tarefas_concluidas,
                            projeto.taxa_conclusao
                        )
                    }.joinToString("\n\n")
                } else {
                    getString(R.string.no_projects_found)
                }
                headerProjetos + dadosProjetos // junta cabeçalho aos dados
            }

            "tarefa" -> {
                // CORRIGIDO: Exportar TODAS as tarefas
                val headerTarefas = getString(R.string.statistics_tasks_header)
                val dadosTarefas = if (listaTarefas.isNotEmpty()) {
                    listaTarefas.mapIndexed { index, tarefa ->
                        getString(
                            R.string.statistics_task_item,
                            index + 1,
                            tarefa.nome,
                            tarefa.projeto_nome,
                            tarefa.estado,
                            tarefa.data_criacao ?: getString(R.string.not_available),
                            tarefa.data_conclusao ?: getString(R.string.pending)
                        )
                    }.joinToString("\n\n")
                } else {
                    getString(R.string.no_tasks_found)
                }
                headerTarefas + dadosTarefas // resultado final
            }

            else -> getString(R.string.no_data_available)
        }

        val filename = "estatisticas_${filtroAtual}_${System.currentTimeMillis()}" // nome do ficheiro
        gerarPDF(fileContents, filename) // gera o PDF
    }

    private fun updateUI() { // actualiza os textos do layout
        val tvNome = statsContainer.findViewById<TextView?>(R.id.tv_nome) // campo nome
        val tvTarefas = statsContainer.findViewById<TextView?>(R.id.tv_tarefas) // campo tarefas
        val tvTaxa = statsContainer.findViewById<TextView?>(R.id.tv_taxa) // campo taxa
        val tvProjetos = statsContainer.findViewById<TextView?>(R.id.tv_projetos) // campo projetos

        if (tvNome == null || tvTarefas == null || tvTaxa == null || tvProjetos == null) {
            Log.e("updateUI", "Views não estão presentes no layout atual. Ignorando update.") // erro caso views não estejam presentes
            return // sai se não houver views
        }

        tvNome.text = nome
        tvTarefas.text = getString(R.string.stats_tasks_summary, tarefasAtribuidas, tarefasConcluidas)
        tvTaxa.text = getString(R.string.stats_completion_rate, taxaConclusao)
        tvProjetos.text = getString(R.string.stats_projects_completed, projetosConcluidos)
    }

    private fun gerarPDF(content: String, filename: String) { // geração do ficheiro PDF
        val pdfDocument = PdfDocument() // documento PDF
        var pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Tamanho A4
        var page = pdfDocument.startPage(pageInfo) // primeira página

        var canvas = page.canvas // área de desenho
        val paint = android.graphics.Paint() // pincel
        paint.textSize = 12f // tamanho da fonte

        val lines = content.split("\n") // texto dividido em linhas
        var y = 50 // posição inicial
        val pageHeight = 842 // altura total
        val bottomMargin = 50 // margem inferior

        for (line in lines) { // percorre cada linha
            // Verificar se precisamos de uma nova página
            if (y + 20 > pageHeight - bottomMargin) {
                pdfDocument.finishPage(page) // termina página actual
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, pdfDocument.pages.size + 1).create() // nova página
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 50 // reinicia posição vertical
            }

            canvas.drawText(line, 30f, y.toFloat(), paint) // desenha texto
            y += 20 // avança para próxima linha
        }

        pdfDocument.finishPage(page) // finaliza última página

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // dispositivos recentes
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, "$filename.pdf") // nome do ficheiro
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf") // tipo MIME
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS) // pasta Downloads
                }

                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues) // cria URI

                uri?.let {
                    val outputStream: OutputStream? = contentResolver.openOutputStream(it) // abre stream
                    pdfDocument.writeTo(outputStream!!) // escreve PDF
                    outputStream?.close() // fecha stream
                    Toast.makeText(this, getString(R.string.pdf_saved), Toast.LENGTH_LONG).show() // informa utilizador
                }

            } else { // para versões antigas
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) // pasta downloads
                val file = File(downloadsDir, "$filename.pdf") // ficheiro destino
                val outputStream = FileOutputStream(file) // abre ficheiro
                pdfDocument.writeTo(outputStream) // escreve no ficheiro
                outputStream.close() // fecha
                Toast.makeText(this, getString(R.string.pdf_saved), Toast.LENGTH_LONG).show() // informa utilizador
            }

        } catch (e: Exception) { // em caso de erro
            Toast.makeText(this, getString(R.string.error_saving_pdf, e.message), Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close() // encerra documento
        }
    } // fim gerarPDF
} // fim da classe
