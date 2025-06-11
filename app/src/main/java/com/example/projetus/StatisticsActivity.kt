package com.example.projetus

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.network.StatisticsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import android.os.Build
import android.os.Environment
import java.io.OutputStream
import com.example.projetus.network.StatisticsProjetoResponse
import com.example.projetus.network.ProjectStat

class StatisticsActivity : AppCompatActivity() {

    private var filtroAtual: String = "utilizador"
    private lateinit var statsContainer: LinearLayout

    // Variáveis estatísticas para utilizador
    private var nome = ""
    private var tarefasAtribuidas = 0
    private var tarefasConcluidas = 0
    private var taxaConclusao = 0
    private var projetosConcluidos = 0

    // Lista para armazenar todos os projetos
    private var listaProjetos: List<ProjectStat> = emptyList()

    // Lista para armazenar todas as tarefas
    private var listaTarefas: List<TaskStat> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        statsContainer = findViewById(R.id.stats_container)

        val userId = intent.getIntExtra("user_id", -1)
        if (userId == -1) {
            Toast.makeText(this, "Utilizador inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val btnUtilizador = findViewById<Button>(R.id.btn_utilizador)
        val btnProjeto = findViewById<Button>(R.id.btn_projeto)
        val btnTarefa = findViewById<Button>(R.id.btn_tarefa)

        // Carregar estatísticas por defeito
        carregarEstatisticasUtilizador(userId)

        btnUtilizador.setOnClickListener {
            filtroAtual = "utilizador"
            carregarEstatisticasUtilizador(userId)
        }

        btnProjeto.setOnClickListener {
            filtroAtual = "projeto"
            carregarEstatisticasProjeto(userId)
        }

        btnTarefa.setOnClickListener {
            filtroAtual = "tarefa"
            carregarEstatisticasTarefa(userId)
        }

        // Navegação
        findViewById<ImageView>(R.id.btn_home).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java).putExtra("user_id", userId))
        }
        findViewById<ImageView>(R.id.btn_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).putExtra("user_id", userId))
        }
        findViewById<ImageView>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java).putExtra("user_id", userId))
        }
    }

    private fun carregarEstatisticasUtilizador(userId: Int) {
        statsContainer.removeAllViews()
        layoutInflater.inflate(R.layout.layout_user_stats, statsContainer, true)

        // Configurar o botão exportar APÓS inflatar o layout
        configurarBotaoExportar()

        RetrofitClient.instance.getEstatisticas(mapOf("user_id" to userId))
            .enqueue(object : Callback<StatisticsResponse> {
                override fun onResponse(call: Call<StatisticsResponse>, response: Response<StatisticsResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val stats = response.body()!!
                        nome = stats.nome
                        tarefasAtribuidas = stats.tarefas_atribuidas
                        tarefasConcluidas = stats.tarefas_concluidas
                        taxaConclusao = stats.taxa_conclusao
                        projetosConcluidos = stats.projetos_concluidos

                        updateUI()
                    }
                }

                override fun onFailure(call: Call<StatisticsResponse>, t: Throwable) {
                    Toast.makeText(this@StatisticsActivity, "Erro ao carregar estatísticas", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun carregarEstatisticasProjeto(userId: Int) {
        RetrofitClient.instance.getEstatisticasProjeto(mapOf("user_id" to userId))
            .enqueue(object : Callback<StatisticsProjetoResponse> {
                override fun onResponse(
                    call: Call<StatisticsProjetoResponse>,
                    response: Response<StatisticsProjetoResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val stats = response.body()!!

                        statsContainer.removeAllViews()

                        // Armazenar TODOS os projetos
                        listaProjetos = stats.projetos ?: emptyList()

                        // Adicionar informações dos projetos
                        listaProjetos.forEach { projeto ->
                            val tvProjeto = TextView(this@StatisticsActivity).apply {
                                text = """
                                Nome: ${projeto.nome}
                                Tarefas Atribuídas: ${projeto.tarefas_atribuidas}
                                Tarefas Concluídas: ${projeto.tarefas_concluidas}
                                Taxa de Conclusão: ${projeto.taxa_conclusao}%
                                """.trimIndent()
                                setPadding(0, 0, 0, 32)
                            }
                            statsContainer.addView(tvProjeto)
                        }

                        // Adicionar botão exportar no final
                        adicionarBotaoExportar()

                    } else {
                        Toast.makeText(this@StatisticsActivity, "Erro: projeto não encontrado", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(
                    call: Call<StatisticsProjetoResponse>,
                    t: Throwable
                ) {
                    Toast.makeText(this@StatisticsActivity, "Erro ao carregar estatísticas de projeto", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun carregarEstatisticasTarefa(userId: Int) {
        statsContainer.removeAllViews()

        // CORRIGIDO: Usar endpoint específico para tarefas
        RetrofitClient.instance.getEstatisticasTarefas(mapOf("user_id" to userId))
            .enqueue(object : Callback<StatisticsTarefaResponse> {
                override fun onResponse(
                    call: Call<StatisticsTarefaResponse>,
                    response: Response<StatisticsTarefaResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val stats = response.body()!!

                        // Armazenar TODAS as tarefas
                        listaTarefas = stats.tarefas ?: emptyList()

                        if (listaTarefas.isNotEmpty()) {
                            // Mostrar todas as tarefas
                            listaTarefas.forEach { tarefa ->
                                val tvTarefa = TextView(this@StatisticsActivity).apply {
                                    text = """
                                    Nome: ${tarefa.nome}
                                    Projeto: ${tarefa.projeto_nome}
                                    Estado: ${tarefa.estado}
                                    Data Criação: ${tarefa.data_criacao ?: "N/A"}
                                    Data Conclusão: ${tarefa.data_conclusao ?: "Pendente"}
                                    """.trimIndent()
                                    setPadding(0, 0, 0, 32)
                                }
                                statsContainer.addView(tvTarefa)
                            }
                        } else {
                            val tvSemTarefas = TextView(this@StatisticsActivity).apply {
                                text = "Nenhuma tarefa encontrada."
                                setPadding(0, 0, 0, 32)
                            }
                            statsContainer.addView(tvSemTarefas)
                        }

                        // Adicionar botão exportar
                        adicionarBotaoExportar()
                    } else {
                        Toast.makeText(this@StatisticsActivity, "Erro ao carregar tarefas", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StatisticsTarefaResponse>, t: Throwable) {
                    Toast.makeText(this@StatisticsActivity, "Erro ao carregar estatísticas das tarefas", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun configurarBotaoExportar() {
        val btnExportar = statsContainer.findViewById<Button>(R.id.btn_exportar)
        btnExportar?.setOnClickListener {
            exportarEstatisticas()
        }
    }

    private fun adicionarBotaoExportar() {
        val btnExportar = Button(this).apply {
            id = R.id.btn_exportar
            text = "Exportar"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 64
            }
            setBackgroundColor(resources.getColor(android.R.color.holo_blue_bright))
            setTextColor(resources.getColor(android.R.color.white))
            setOnClickListener {
                exportarEstatisticas()
            }
        }
        statsContainer.addView(btnExportar)
    }

    private fun exportarEstatisticas() {
        val fileContents = when (filtroAtual) {
            "utilizador" -> """
                Estatísticas do Utilizador:

                Nome: $nome
                Tarefas Atribuídas: $tarefasAtribuidas
                Tarefas Concluídas: $tarefasConcluidas
                Taxa de Conclusão: $taxaConclusao%
                Projetos Concluídos: $projetosConcluidos
            """.trimIndent()

            "projeto" -> {
                val headerProjetos = "Estatísticas dos Projetos:\n\n"
                val dadosProjetos = if (listaProjetos.isNotEmpty()) {
                    listaProjetos.mapIndexed { index, projeto ->
                        """
                        Projeto ${index + 1}:
                        Nome: ${projeto.nome}
                        Tarefas Atribuídas: ${projeto.tarefas_atribuidas}
                        Tarefas Concluídas: ${projeto.tarefas_concluidas}
                        Taxa de Conclusão: ${projeto.taxa_conclusao}%
                        """.trimIndent()
                    }.joinToString("\n\n")
                } else {
                    "Nenhum projeto encontrado."
                }
                headerProjetos + dadosProjetos
            }

            "tarefa" -> {
                // CORRIGIDO: Exportar TODAS as tarefas
                val headerTarefas = "Estatísticas das Tarefas:\n\n"
                val dadosTarefas = if (listaTarefas.isNotEmpty()) {
                    listaTarefas.mapIndexed { index, tarefa ->
                        """
                        Tarefa ${index + 1}:
                        Nome: ${tarefa.nome}
                        Projeto: ${tarefa.projeto_nome}
                        Estado: ${tarefa.estado}
                        Data Criação: ${tarefa.data_criacao ?: "N/A"}
                        Data Conclusão: ${tarefa.data_conclusao ?: "Pendente"}
                        """.trimIndent()
                    }.joinToString("\n\n")
                } else {
                    "Nenhuma tarefa encontrada."
                }
                headerTarefas + dadosTarefas
            }

            else -> "Nenhum dado disponível."
        }

        val filename = "estatisticas_${filtroAtual}_${System.currentTimeMillis()}"
        gerarPDF(fileContents, filename)
    }

    private fun updateUI() {
        val tvNome = statsContainer.findViewById<TextView?>(R.id.tv_nome)
        val tvTarefas = statsContainer.findViewById<TextView?>(R.id.tv_tarefas)
        val tvTaxa = statsContainer.findViewById<TextView?>(R.id.tv_taxa)
        val tvProjetos = statsContainer.findViewById<TextView?>(R.id.tv_projetos)

        if (tvNome == null || tvTarefas == null || tvTaxa == null || tvProjetos == null) {
            Log.e("updateUI", "Views não estão presentes no layout atual. Ignorando update.")
            return
        }

        tvNome.text = nome
        tvTarefas.text = "Tarefas Atribuídas: $tarefasAtribuidas\nTarefas Concluídas: $tarefasConcluidas"
        tvTaxa.text = "Taxa Média de Conclusão: $taxaConclusao%"
        tvProjetos.text = "Projetos Concluídos: $projetosConcluidos"
    }

    private fun gerarPDF(content: String, filename: String) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Tamanho A4
        val page = pdfDocument.startPage(pageInfo)

        var canvas = page.canvas
        val paint = android.graphics.Paint()
        paint.textSize = 12f

        val lines = content.split("\n")
        var y = 50
        val pageHeight = 842
        val bottomMargin = 50

        for (line in lines) {
            // Verificar se precisamos de uma nova página
            if (y + 20 > pageHeight - bottomMargin) {
                pdfDocument.finishPage(page)
                val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, pdfDocument.pages.size + 1).create()
                val newPage = pdfDocument.startPage(newPageInfo)
                canvas = newPage.canvas
                y = 50
            }

            canvas.drawText(line, 30f, y.toFloat(), paint)
            y += 20
        }

        pdfDocument.finishPage(page)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, "$filename.pdf")
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    val outputStream: OutputStream? = contentResolver.openOutputStream(it)
                    pdfDocument.writeTo(outputStream!!)
                    outputStream?.close()
                    Toast.makeText(this, "PDF guardado na pasta Downloads", Toast.LENGTH_LONG).show()
                }

            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, "$filename.pdf")
                val outputStream = FileOutputStream(file)
                pdfDocument.writeTo(outputStream)
                outputStream.close()
                Toast.makeText(this, "PDF guardado na pasta Downloads", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao guardar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }
}

// Data classes necessárias para as tarefas
data class TaskStat(
    val nome: String,
    val projeto_nome: String,
    val estado: String,
    val data_criacao: String?,
    val data_conclusao: String?
)

data class StatisticsTarefaResponse(
    val success: Boolean,
    val tarefas: List<TaskStat>?
)