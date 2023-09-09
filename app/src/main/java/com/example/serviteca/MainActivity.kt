
package com.example.serviteca

import android.os.Bundle
import android.view.MenuItem
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

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_custom_header, menu) // Infla el menú de opciones
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_cliente_info -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }*/



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
        val dialog = AlertDialog.Builder(this@MainActivity)
            .setTitle("Datos del Cliente")
            .setMessage("Nombre: ${clienteInfo.cliPersona_info.perNombres}\n" +
                    "Apellidos: ${clienteInfo.cliPersona_info.perApellidos}\n" +
                    "Correo: ${clienteInfo.cliPersona_info.perCorreo}\n" +
                    "Dirección: ${clienteInfo.cliDireccion}\n" +
                    "Número de Celular: ${clienteInfo.cliPersona_info.perNumeroCelular}")
            .setPositiveButton("Cerrar") { _, _ ->
                // Acción cuando el usuario presiona el botón "Cerrar"
            }
            .create()

        dialog.show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                finish() // Cierra la actividad actual, lo que termina la aplicación.
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
