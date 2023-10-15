
package com.example.serviteca.ui.historial

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.serviteca.databinding.FragmentHistorialBinding
import com.example.serviteca.ui.consultar.ClienteModel
import com.example.serviteca.ui.consultar.DetalleServicioActivity
import com.example.serviteca.ui.consultar.ServicioAdapter
import com.example.serviteca.ui.consultar.ServicioPrestado
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HistorialFragment : Fragment() {
    private var _binding: FragmentHistorialBinding? = null
    private lateinit var viewModel: HistorialViewModel
    private lateinit var binding: FragmentHistorialBinding
    private lateinit var servicioAdapter: ServicioAdapter

    // Retrofit
    private val apiService: ApiServiceHistorial by lazy {
        val BASE_URL = "http://servitecaopita2.pythonanywhere.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiServiceHistorial::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistorialBinding.inflate(inflater, container, false)
        binding = _binding!!

        viewModel = ViewModelProvider(this).get(HistorialViewModel::class.java)

        servicioAdapter = ServicioAdapter(mutableListOf()) { servicio ->
            // Maneja el clic en un servicio
            val intent = Intent(requireContext(), DetalleServicioActivity::class.java)
            intent.putExtra("servicioId", servicio.id) // Pasa el ID del servicio
            startActivity(intent)
        }

        // Configurar una línea divisoria vertical entre elementos del RecyclerView
        val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerViewHistorial.addItemDecoration(dividerItemDecoration)

        binding.recyclerViewHistorial.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = servicioAdapter
        }

        binding.buttonConsultarHistorial.setOnClickListener {
            val cedula = binding.txtCedula.text.toString().trim()
            if (cedula.isNotEmpty()) {
                consultarCliente(cedula)
            } else {
                val cedulaEditText = binding.txtCedula
                val cedula = cedulaEditText.text.toString().trim()
                if (cedula.length > 10 || cedula.length < 7) {
                    cedulaEditText.error = "¡Por Favor Ingresa Su Documento de Identidad!"
                }
                servicioAdapter.clearData() // Limpiar la lista si no se ingresa una cédula
            }
        }

        return binding.root
    }

    private fun consultarCliente(cedula: String) {
        // Limpiar el contenido anterior
        binding.txtHistorial.text = ""
        val cedulaEditText = binding.txtCedula
        val cedula = cedulaEditText.text.toString().trim()
        if (cedula.length > 10 || cedula.length < 7) {
            cedulaEditText.error = "¡La cédula no es válida!\n" +
                    "Verifique su número de cédula"
            return
        }
        // Limpiar la lista antes de realizar la consulta
        servicioAdapter.clearData()

        // Realiza la solicitud para obtener datos del cliente
        val callCliente = apiService.getClienteConCedula(cedula)
        callCliente.enqueue(object : Callback<ClienteModel> {
            override fun onResponse(call: Call<ClienteModel>, response: Response<ClienteModel>) {
                if (response.isSuccessful) {
                    val cliente = response.body()
                    if (cliente != null) {
                        // Llama a la función para mostrar el cuadro de diálogo con la información del cliente
                        mostrarInformacionClienteDialog(cliente)

                        // Obtener servicios del cliente
                        obtenerServiciosDelCliente(cedula)
                    } else {
                        binding.txtHistorial.text = "Cliente no encontrado."
                    }
                } else {
                    binding.txtHistorial.text = "Cliente no encontrado."
                }
            }

            override fun onFailure(call: Call<ClienteModel>, t: Throwable) {
                binding.txtHistorial.text = "Error al realizar la solicitud del cliente."
            }
        })
    }


    private fun mostrarInformacionClienteDialog(clienteInfo: ClienteModel) {
        val nombreCompleto = "${clienteInfo.cliPersona_info.perNombres} ${clienteInfo.cliPersona_info.perApellidos}"
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Bienvenido Señor o Señora")
            .setMessage("Nombre Completo: $nombreCompleto")
            .setPositiveButton("Cerrar") { _, _ ->
                // Acción cuando el usuario presiona el botón "Cerrar"
            }
            .create()

        dialog.show()
    }


    private fun obtenerServiciosDelCliente(cedula: String) {
        // Realiza la solicitud para obtener los servicios del cliente
        val callServicio = apiService.getServiciosPrestadosConClienteId(cedula)
        callServicio.enqueue(object : Callback<List<ServicioPrestado>> {
            override fun onResponse(
                call: Call<List<ServicioPrestado>>,
                response: Response<List<ServicioPrestado>>
            ) {
                if (response.isSuccessful) {
                    val serviciosPrestados = response.body()
                    if (serviciosPrestados != null && serviciosPrestados.isNotEmpty()) {
                        // Filtrar los servicios según los estados "Terminado" o "Cancelado"
                        val filteredServicios = serviciosPrestados.filter {
                            it.serpEstado.equals("Terminado", ignoreCase = true) ||
                                    it.serpEstado.equals("Cancelado", ignoreCase = true)
                        }
                        if (filteredServicios.isNotEmpty()) {
                            servicioAdapter.updateData(filteredServicios)
                        } else {
                            // No se encontraron servicios con los estados "Terminado" o "Cancelado"
                            binding.txtHistorial.text =
                                "No se encontraron servicios con estado 'Terminado' o 'Cancelado'."
                            servicioAdapter.clearData()
                        }
                    } else {
                        // Maneja el caso de que no haya servicios o no cumplan el criterio
                        binding.txtHistorial.text = "Señor Usuario como usted todavía no posee servicios con nuestra serviteca, entonces por el momento no podemos mostrar tu historial"
                        servicioAdapter.clearData()
                    }
                } else {
                    // Maneja el error de la respuesta de la API de servicios
                    binding.txtHistorial.text = "Error en la respuesta de la API de servicios."
                    servicioAdapter.clearData()
                }
            }

            override fun onFailure(call: Call<List<ServicioPrestado>>, t: Throwable) {
                // Maneja el error al realizar la solicitud de servicios
                binding.txtHistorial.text = "Error al realizar la solicitud de servicios."
                servicioAdapter.clearData()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
