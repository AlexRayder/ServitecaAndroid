package com.example.serviteca.ui.consultar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.serviteca.R
import com.example.serviteca.databinding.FragmentConsultarBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConsutarFragment : Fragment() {
    private var _binding: FragmentConsultarBinding? = null
    private lateinit var btnConsulta: Button
    private lateinit var txtServicio: TextView
    private lateinit var txtCliente: TextView

    // Creamos una instancia de la interfaz ApiService
    private val apiService: ApiService by lazy {
        val BASE_URL = "https://alexxoo.pythonanywhere.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsultarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Encuentra los TextView dentro de la vista inflada (root)
        txtServicio = root.findViewById(R.id.txtServicio)
        txtCliente = root.findViewById(R.id.txtCliente)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnConsulta = view.findViewById(R.id.button)
        btnConsulta.setOnClickListener {
            fetchDataFromAPI()
        }
    }

    private fun fetchDataFromAPI() {
        val cedulaEditText = binding.txtCedula
        val cedula = cedulaEditText.text.toString().trim()

        if (cedula.isEmpty()) {
            cedulaEditText.error = "Por favor, ingrese su número de cédula."
            return
        }

        val callCliente = apiService.getServiceByCedula(cedula)
        val callServicio = apiService.getServicePrestadoById(cedula.toInt())

        callCliente.enqueue(object : Callback<List<ServiceModel>> {
            override fun onResponse(call: Call<List<ServiceModel>>, response: Response<List<ServiceModel>>) {
                if (response.isSuccessful) {
                    val serviceModels = response.body()
                    if (serviceModels != null && serviceModels.isNotEmpty()) {
                        val clienteEncontrado = serviceModels.find { it.perIdentificacion == cedula }
                        if (clienteEncontrado != null) {
                            // Mostrar los datos del cliente en el TextView
                            txtCliente.text = "Cliente encontrado:\n" +
                                    "Nombre: ${clienteEncontrado.perNombres} ${clienteEncontrado.perApellidos}\n" + // Usar serpCliNombre
                                    "Correo: ${clienteEncontrado.perCorreo}\n" +
                                    "Número de celular: ${clienteEncontrado.perNumeroCelular}"

                            // Ahora puedes realizar la solicitud para obtener el servicio prestado usando el ID del servicio prestado
                            val callServicio = apiService.getServicePrestadoById(clienteEncontrado.id)
                            callServicio.enqueue(object : Callback<ServicioPrestado> {
                                override fun onResponse(call: Call<ServicioPrestado>, response: Response<ServicioPrestado>) {
                                    if (response.isSuccessful) {
                                        val servicioPrestado = response.body()
                                        if (servicioPrestado != null) {
                                            // Mostrar los datos del servicio en el TextView
                                            txtServicio.text = "SerpCli: ${servicioPrestado.serpCli}\n" + // Usar serpCliNombre
                                                    "SerpVehi: ${servicioPrestado.serpVehi}\n" +
                                                    "SerpEstado: ${servicioPrestado.serpEstado}\n" +
                                                    "SerpObservaciones: ${servicioPrestado.serpObservaciones}\n" +
                                                    "SerpFechaServicio: ${servicioPrestado.serpFechaServicio}"
                                        } else {
                                            txtServicio.text = "Servicio Prestado no encontrado."
                                        }
                                    } else {
                                        txtServicio.text = "Error en la respuesta de la API del servicio."
                                    }
                                }

                                override fun onFailure(call: Call<ServicioPrestado>, t: Throwable) {
                                    txtServicio.text = "Error al realizar la solicitud del servicio."
                                }
                            })
                        } else {
                            txtCliente.text = "Cliente no encontrado."
                            txtServicio.text = "" // Limpia el TextView de servicio si no se encuentra el cliente
                        }
                    } else {
                        txtCliente.text = "Cliente no encontrado."
                        txtServicio.text = "" // Limpia el TextView de servicio si no se encuentra el cliente
                    }
                } else {
                    txtCliente.text = "Error en la respuesta de la API del cliente."
                    txtServicio.text = "" // Limpia el TextView de servicio en caso de error
                }
            }

            override fun onFailure(call: Call<List<ServiceModel>>, t: Throwable) {
                txtCliente.text = "Error al realizar la solicitud del cliente."
                txtServicio.text = "" // Limpia el TextView de servicio en caso de error
            }
        })
    }
}