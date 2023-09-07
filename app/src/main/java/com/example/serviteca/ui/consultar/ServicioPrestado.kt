package com.example.serviteca.ui.consultar

data class ServicioPrestado(
    val id: Int,
    val serpCli: Int,
    val serpVehi: Int,
    val serpEstado: String,
    val serpObservaciones: String,
    val serpFechaServicio: String
)
