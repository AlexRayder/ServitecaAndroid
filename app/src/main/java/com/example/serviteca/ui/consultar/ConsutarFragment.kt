package com.example.serviteca.ui.consultar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    // Agrega una lista para almacenar los servicios
    private val serviciosList = mutableListOf<ServicioPrestado>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var servicioAdapter: ServicioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsultarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Encuentra los elementos de la vista inflada (root) y configura el RecyclerView
        recyclerView = root.findViewById(R.id.recyclerViewServicios)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        servicioAdapter = ServicioAdapter(serviciosList)
        recyclerView.adapter = servicioAdapter

        // Encuentra el botón de consulta y configura el listener
        btnConsulta = root.findViewById(R.id.button)
        txtCliente = root.findViewById(R.id.txtCliente)
        btnConsulta.setOnClickListener {
            fetchDataFromAPI()
        }

        return root
    }

    private fun fetchDataFromAPI() {
        val cedulaEditText = binding.txtCedula
        val cedula = cedulaEditText.text.toString().trim()

        if (cedula.length > 10 || cedula.length < 7) {
            cedulaEditText.error = "¡La cédula no es válida!\n" +
                    "Verifique su número de cédula"
            return
        }

        // Realiza la llamada a la API para obtener los datos del cliente
        val callCliente = apiService.getServiceByCedula(cedula)

        callCliente.enqueue(object : Callback<List<ServiceModel>> {
            override fun onResponse(call: Call<List<ServiceModel>>, response: Response<List<ServiceModel>>) {
                if (response.isSuccessful) {
                    val serviceModels = response.body()
                    if (serviceModels != null && serviceModels.isNotEmpty()) {
                        val clienteEncontrado = serviceModels.find { it.perIdentificacion == cedula }
                        if (clienteEncontrado != null) {
                            // Mostrar los datos del cliente en el TextView
                            txtCliente.text = "Cliente encontrado:\n" +
                                    "Nombre: ${clienteEncontrado.perNombres} ${clienteEncontrado.perApellidos}\n" +
                                    "Correo: ${clienteEncontrado.perCorreo}\n" +
                                    "Número de celular: ${clienteEncontrado.perNumeroCelular}"

                            // Ahora puedes realizar la solicitud para obtener los servicios prestados usando el ID del cliente
                            val callServicio = apiService.getServicePrestadoById(clienteEncontrado.id)
                            callServicio.enqueue(object : Callback<List<ServicioPrestado>> {
                                override fun onResponse(call: Call<List<ServicioPrestado>>, response: Response<List<ServicioPrestado>>) {
                                    if (response.isSuccessful) {
                                        val serviciosPrestados = response.body()
                                        if (serviciosPrestados != null && serviciosPrestados.isNotEmpty()) {
                                            // Limpiar la lista actual de servicios
                                            serviciosList.clear()

                                            // Agregar los servicios prestados a la lista
                                            serviciosList.addAll(serviciosPrestados)

                                            // Notificar al adaptador que los datos han cambiado
                                            servicioAdapter.notifyDataSetChanged()
                                        } else {
                                            // No se encontraron servicios prestados para este cliente
                                            txtCliente.text = "Cliente encontrado, pero no se encontraron servicios prestados."
                                        }
                                    } else {
                                        txtCliente.text = "Error en la respuesta de la API de servicios."
                                    }
                                }

                                override fun onFailure(call: Call<List<ServicioPrestado>>, t: Throwable) {
                                    txtCliente.text = "Error al realizar la solicitud de servicios."
                                }
                            })

                        } else {
                            txtCliente.text = "Cliente no encontrado."
                            serviciosList.clear()
                            servicioAdapter.notifyDataSetChanged()
                        }
                    } else {
                        txtCliente.text = "Cliente no encontrado."
                        serviciosList.clear()
                        servicioAdapter.notifyDataSetChanged()
                    }
                } else {
                    txtCliente.text = "Error en la respuesta de la API del cliente."
                    serviciosList.clear()
                    servicioAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<ServiceModel>>, t: Throwable) {
                txtCliente.text = "Error al realizar la solicitud del cliente."
                serviciosList.clear()
                servicioAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
