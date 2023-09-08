    package com.example.serviteca.ui.consultar

    import retrofit2.Call
    import retrofit2.http.GET
    import retrofit2.http.Path
    import retrofit2.http.Query

    interface ApiService {
        @GET("persona")
        fun getServiceByCedula(@Query("perIdentificacion") cedula: String): Call<List<ServiceModel>>
        @GET("cliente")
        fun getClientesByCedula(@Query("perIdentificacion") cedula: String): Call<List<ClienteModel>>

        @GET("cliente/{perIdentificacion}")
        fun getClienteByCedula(@Path("perIdentificacion") cedula: String): Call<ClienteModel>

        @GET("servicioPrestado/{perIdentificacion}")
        fun getServiciosPrestadosByClienteId(@Path("perIdentificacion") cedula: String): Call<List<ServicioPrestado>>
        @GET("detalleServicioPrestado/{id}")
        fun getDetalleServicioPrestado(@Path("id") detalleId: Int): Call<List<DetalleServicioPrestado>>

    }