package com.example.serviteca.ui.consultar

data class ServicioPrestado(
    val id: Int,
    val serpCli: String, // Esta propiedad almacenará el nombre del cliente
    val serpVehi: Int,
    val serpEstado: String,
    val serpObservaciones: String,
    val serpFechaServicio: String
)
