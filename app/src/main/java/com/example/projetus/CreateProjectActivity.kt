// CreateProjectActivity.kt
package com.example.projetus

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projetus.network.GenericResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CreateProjectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_project)

        val etNome = findViewById<EditText>(R.id.et_nome_projeto)
        val etDescricao = findViewById<EditText>(R.id.et_descricao_projeto)
        val dpInicio = findViewById<EditText>(R.id.et_data_inicio)
        val dpFim = findViewById<EditText>(R.id.et_data_fim)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar_projeto)
        val btnCancelar = findViewById<Button>(R.id.btn_cancelar_projeto)

        val userId = intent.getIntExtra("user_id", -1)

        // Date pickers
        val calendar = Calendar.getInstance()

        val setDateListener = { target: EditText ->
            DatePickerDialog(this, { _, year, month, day ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, day)
                target.setText(date)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        dpInicio.setOnClickListener { setDateListener(dpInicio) }
        dpFim.setOnClickListener { setDateListener(dpFim) }

        btnGuardar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val descricao = etDescricao.text.toString().trim()
            val dataInicio = dpInicio.text.toString().trim()
            val dataFim = dpFim.text.toString().trim()

            if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataFim.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.instance.addProject(
                nome = nome,
                descricao = descricao,
                gestorId = userId,
                dataInicio = dataInicio,
                dataFim = dataFim
            ).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@CreateProjectActivity, "Projeto criado!", Toast.LENGTH_SHORT).show()

                        val resultIntent = Intent()
                        resultIntent.putExtra("project_created", true)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                } else {
                        Toast.makeText(this@CreateProjectActivity, response.body()?.message ?: "Erro", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@CreateProjectActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}
