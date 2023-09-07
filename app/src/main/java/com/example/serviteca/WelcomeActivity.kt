package com.example.serviteca

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.serviteca.databinding.ActivityWelcomeBinding // Importa la clase de ViewBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding // Declaración de la variable de ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater) // Inicializa ViewBinding
        val view = binding.root
        setContentView(view)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // Animación de transición para las vistas
        binding.logoImageView.animate().alpha(1f).setDuration(2000).start()
        binding.logoImageView2.animate().alpha(1f).setDuration(2000).start()
        binding.welcomeMessageTextView.animate().alpha(1f).setDuration(2000).start()

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Cierra la actividad de bienvenida
        }, 2000) // Espera 2 segundos antes de abrir la actividad principal
    }
}
