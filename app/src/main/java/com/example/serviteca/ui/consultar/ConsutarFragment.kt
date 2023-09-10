package com.example.serviteca.ui.consultar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
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
    interface ConsultarListener {
        fun onClienteDataReceived(clienteInfo: ClienteModel)
    }

    lateinit var consultarListener: ConsultarListener

    private var _binding: FragmentConsultarBinding? = null
    private lateinit var btnConsulta: Button
    private lateinit var txtCliente: TextView

    private val apiService: ApiService by lazy {
        val BASE_URL = "https://alexxoo.pythonanywhere.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }

    private val binding get() = _binding!!

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

        recyclerView = root.findViewById(R.id.recyclerViewServicios)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Agregar una línea divisoria entre los elementos del RecyclerView
        val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        servicioAdapter = ServicioAdapter(serviciosList) { servicio ->
            val intent = Intent(requireContext(), DetalleServicioActivity::class.java)
            intent.putExtra("servicioId", servicio.id)
            startActivity(intent)
        }

        recyclerView.adapter = servicioAdapter

        btnConsulta = root.findViewById(R.id.button)
        txtCliente = root.findViewById(R.id.txtCliente)
        btnConsulta.setOnClickListener {
            fetchDataFromAPI()
        }

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ConsultarListener) {
            consultarListener = context
        } else {
            throw RuntimeException("$context must implement ConsultarListener")
        }
    }

    private fun fetchDataFromAPI() {
        val cedulaEditText = binding.txtCedula
        val cedula = cedulaEditText.text.toString().trim()
        txtCliente.text = ""
        if (cedula.length > 10 || cedula.length < 7) {
            cedulaEditText.error = "¡La cédula no es válida!\n" +
                    "Verifique su número de cédula"
            return
        }

        val callCliente = apiService.getClienteByCedula(cedula)

        callCliente.enqueue(object : Callback<ClienteModel> {
            override fun onResponse(call: Call<ClienteModel>, response: Response<ClienteModel>) {
                if (response.isSuccessful) {
                    val cliente = response.body()
                    if (cliente != null) {
                        val clienteInfo = cliente.cliPersona_info

                        // Notificar a MainActivity con los datos del cliente
                        consultarListener.onClienteDataReceived(cliente)

                        // Puedes también mostrar los datos aquí en el fragmento
                        "Cliente encontrado:\n" +
                                "Nombres: ${clienteInfo.perNombres}\n" +
                                "Apellidos: ${clienteInfo.perApellidos}\n" +
                                "Correo: ${clienteInfo.perCorreo}\n" +
                                "Dirección: ${cliente.cliDireccion}\n" +
                                "Número de Celular: ${clienteInfo.perNumeroCelular}"

                        val callServicio = apiService.getServiciosPrestadosByClienteId(cedula)
                        callServicio.enqueue(object : Callback<List<ServicioPrestado>> {
                            override fun onResponse(
                                call: Call<List<ServicioPrestado>>,
                                response: Response<List<ServicioPrestado>>
                            ) {
                                if (response.isSuccessful) {
                                    val serviciosPrestados = response.body()
                                    if (serviciosPrestados != null && serviciosPrestados.isNotEmpty()) {
                                        // Filtrar solo los servicios "Solicitados"
                                        val filteredServicios = serviciosPrestados.filter {
                                            it.serpEstado.equals("Solicitado", ignoreCase = true)
                                        }
                                        serviciosList.clear()
                                        serviciosList.addAll(filteredServicios)
                                        servicioAdapter.notifyDataSetChanged()
                                    } else {
                                        txtCliente.append("\nCliente existe pero no tiene servicios.")
                                        serviciosList.clear()
                                        servicioAdapter.notifyDataSetChanged()
                                    }
                                } else {
                                    txtCliente.text = "Error en la respuesta de la API de servicios."
                                    serviciosList.clear()
                                    servicioAdapter.notifyDataSetChanged()
                                }
                            }

                            override fun onFailure(
                                call: Call<List<ServicioPrestado>>,
                                t: Throwable
                            ) {
                                txtCliente.text = "Error al realizar la solicitud de servicios."
                                serviciosList.clear()
                                servicioAdapter.notifyDataSetChanged()
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
            }

            override fun onFailure(call: Call<ClienteModel>, t: Throwable) {
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
