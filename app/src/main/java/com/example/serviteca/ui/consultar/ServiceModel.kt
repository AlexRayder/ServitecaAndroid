package com.example.serviteca.ui.consultar

data class ServiceModel(
    val id: Int,
    val perIdentificacion: String,
    val perNombres: String,
    val perApellidos: String,
    val perCorreo: String?,
    val perNumeroCelular: String,
)
