package com.example.serviteca.ui.consultar

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface ApiService {
    @GET("persona")
    fun getServiceByCedula(@Query("perIdentificacion") cedula: String): Call<List<ServiceModel>>
    @GET("servicioPrestado/{id}")
    fun getServicePrestadoById(@Path("id") id: Int): Call<ServicioPrestado>


}
