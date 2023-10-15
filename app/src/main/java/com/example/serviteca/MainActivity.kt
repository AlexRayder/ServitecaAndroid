package com.example.serviteca

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.serviteca.databinding.ActivityMainBinding
import com.example.serviteca.ui.consultar.ApiService
import com.example.serviteca.ui.consultar.ClienteModel
import com.example.serviteca.ui.consultar.ConsutarFragment
import com.google.android.material.navigation.NavigationView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), ConsutarFragment.ConsultarListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var apiService: ApiService
    private var clienteInfo: ClienteModel? = null

    private val loadingProgressBar: ProgressBar by lazy {
        findViewById(R.id.loadingProgressBar)
    }

    private val networkChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (isNetworkAvailable()) {
                // Si la conexión se restablece, volver a cargar la vista principal
                loadMainView()
            } else {
                // Si no hay conexión, mostrar la vista "No hay conexión a Internet"
                showNoInternetView()
            }
        }
    }

    fun getClienteInfo(): ClienteModel? {
        return clienteInfo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.appBarMain.toolbar
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_historial, R.id.nav_nosotros, R.id.nav_Consultar
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Inicializa Retrofit para realizar llamadas a la API
        val BASE_URL = "https://alexxoo.pythonanywhere.com/"
        apiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onStart() {
        super.onStart()
        // Registrar el BroadcastReceiver para la detección de cambios en la conectividad de red
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        // Desregistrar el BroadcastReceiver cuando la actividad se detenga
        unregisterReceiver(networkChangeReceiver)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onClienteDataReceived(clienteInfo: ClienteModel) {
        // Esta función se llama desde ConsultarFragment cuando se reciben los datos del cliente
        // Puedes implementar aquí la lógica para mostrar los datos en un cuadro de diálogo o en otro lugar
        mostrarInformacionClienteDialog(clienteInfo)
    }

    private fun mostrarInformacionClienteDialog(clienteInfo: ClienteModel) {
        val nombreCompleto = "${clienteInfo.cliPersona_info.perNombres} ${clienteInfo.cliPersona_info.perApellidos}"
        val dialog = AlertDialog.Builder(this@MainActivity)
            .setTitle("Bienvenido Señor o Señora")
            .setMessage("$nombreCompleto")
            .setPositiveButton("Cerrar") { _, _ ->
                // Acción cuando el usuario presiona el botón "Cerrar"
            }
            .create()

        dialog.show()
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun showNoInternetView() {

        // Mostrar la vista de "No hay conexión a Internet"
        setContentView(R.layout.no_internet_layout)

    }

    private fun loadMainView() {
        Handler(Looper.getMainLooper()).postDelayed({

            if (isNetworkAvailable()) {
                // Si hay conexión a Internet, cargar la vista principal
                setContentView(binding.root)
            } else {
                // Si no hay conexión a Internet, mostrar la vista "No hay conexión a Internet"
                showNoInternetView()
            }
        }, 3000) // 5 segundos
    }


}
