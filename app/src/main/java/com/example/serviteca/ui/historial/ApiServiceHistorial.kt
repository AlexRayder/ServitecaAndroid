package com.example.serviteca.ui.historial

import com.example.serviteca.ui.consultar.ClienteModel
import com.example.serviteca.ui.consultar.DetalleServicioPrestado
import com.example.serviteca.ui.consultar.ServiceModel
import com.example.serviteca.ui.consultar.ServicioPrestado
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceHistorial {
    @GET("persona")
    fun getServiceConCedula(@Query("perIdentificacion") cedula: String): Call<List<ServiceModel>>
    @GET("cliente")
    fun getClientesConCedula(@Query("perIdentificacion") cedula: String): Call<List<ClienteModel>>

    @GET("cliente/{perIdentificacion}")
    fun getClienteConCedula(@Path("perIdentificacion") cedula: String): Call<ClienteModel>

    @GET("servicioPrestado/{perIdentificacion}")
    fun getServiciosPrestadosConClienteId(@Path("perIdentificacion") cedula: String): Call<List<ServicioPrestado>>
    @GET("detalleServicioPrestado/{id}")
    fun getDetalleServicioPrestado2(@Path("id") detalleId: Int): Call<List<DetalleServicioPrestado>>
}