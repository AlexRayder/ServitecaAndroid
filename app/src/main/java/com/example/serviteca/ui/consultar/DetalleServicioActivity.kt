    package com.example.serviteca.ui.consultar
    
    import android.content.Intent
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.view.View
    import android.widget.TextView
    import com.example.serviteca.MainActivity
    import com.example.serviteca.R
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    
    class DetalleServicioActivity : AppCompatActivity() {
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_detalle_servicio)
    
            val servicioId = intent.getIntExtra("servicioId", -1)
    
            if (servicioId != -1) {
                // Configura Retrofit
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://servitecaopita2.pythonanywhere.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
    
                // Crea una instancia de ApiService
                val apiService = retrofit.create(ApiService::class.java)
    
                // Realiza la solicitud para obtener los detalles del servicio
                val call = apiService.getDetalleServicioPrestado(servicioId)
    
                call.enqueue(object : Callback<List<DetalleServicioPrestado>> {
                    override fun onResponse(
                        call: Call<List<DetalleServicioPrestado>>,
                        response: Response<List<DetalleServicioPrestado>>
                    ) {
                        if (response.isSuccessful) {
                            val detalleServicios = response.body()
                            if (detalleServicios != null && detalleServicios.isNotEmpty()) {
                                // Crear un StringBuilder para almacenar todos los servicios
                                val serviciosStringBuilder = StringBuilder()

                                for (detalleServicio in detalleServicios) {
                                    serviciosStringBuilder.append("Servicio: ${detalleServicio.detServicio}\n\n")
                                    serviciosStringBuilder.append("Empleado: ${detalleServicio.detEmpleado}\n\n")
                                    serviciosStringBuilder.append("Estado Servicio: ${detalleServicio.detEstadoServicio}\n\n")
                                    serviciosStringBuilder.append("Observacion Técnico: ${detalleServicio.detObservaciones}\n\n")
                                    serviciosStringBuilder.append("---------------------------------------\n\n\n") // Línea divisoria
                                }


                                // Actualiza el TextView con los detalles de todos los servicios
                                val detalleTextView = findViewById<TextView>(R.id.detalleTextView)
                                detalleTextView.text = serviciosStringBuilder.toString()
                            } else {
                                // Maneja el caso en el que la lista está vacía
                                // Puedes mostrar un mensaje o realizar alguna otra acción aquí
                            }
                        } else {
                            // Maneja el caso de respuesta no exitosa
                            // Puedes mostrar un mensaje de error o realizar alguna otra acción aquí
                        }
                    }
    
                    override fun onFailure(call: Call<List<DetalleServicioPrestado>>, t: Throwable) {
                        // Maneja el caso de error de red
                        // Puedes mostrar un mensaje de error o realizar alguna otra acción aquí
                    }
                })
            } else {
                // Maneja el caso en el que no se proporciona un ID válido
            }
        }
        fun redirigirAInicio(view: View) {
            // Redirige a la actividad de inicio
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    
        fun redirigirAConsultar(view: View) {
            // Redirige a la actividad de consultar
            val intent = Intent(this, ConsutarFragment::class.java)
            startActivity(intent)
        }
    }
