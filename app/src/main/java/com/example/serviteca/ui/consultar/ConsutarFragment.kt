package com.example.serviteca.ui.consultar
import android.content.Intent
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

    private fun fetchDataFromAPI() {
        val cedulaEditText = binding.txtCedula
        val cedula = cedulaEditText.text.toString().trim()

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
                        txtCliente.text = "Cliente encontrado:\n" +
                                "ID: ${cliente.id}\n" +
                                "Dirección: ${cliente.cliDireccion}"

                        val callServicio = apiService.getServiciosPrestadosByClienteId(cedula)
                        callServicio.enqueue(object : Callback<List<ServicioPrestado>> {
                            override fun onResponse(
                                call: Call<List<ServicioPrestado>>,
                                response: Response<List<ServicioPrestado>>
                            ) {
                                if (response.isSuccessful) {
                                    val serviciosPrestados = response.body()
                                    if (serviciosPrestados != null && serviciosPrestados.isNotEmpty()) {
                                        serviciosList.clear()
                                        serviciosList.addAll(serviciosPrestados)
                                        servicioAdapter.notifyDataSetChanged()
                                    } else {
                                        txtCliente.append("\nCliente existe pero no tiene servicios.")
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
