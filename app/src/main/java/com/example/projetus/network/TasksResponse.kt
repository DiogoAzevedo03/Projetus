package com.example.projetus.network // Pacote da resposta de tarefas

data class TasksResponse(
    val success: Boolean, // Resultado da consulta
    val tarefas: List<Task> // Lista de tarefas retornadas
) // Estrutura de resposta para várias tarefas
