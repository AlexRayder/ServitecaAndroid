package com.example.serviteca.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NosotrosViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Sobre Nosotros\n\n" +
                "\"En nuestra serviteca, nos enorgullece brindar servicios de alta calidad y atención personalizada a nuestros clientes. Con años de experiencia en el cuidado y mantenimiento de vehículos, nuestro equipo de profesionales altamente capacitados se esfuerza por mantener su automóvil en las mejores condiciones.\n" +
                "\n" +
                "En Serviteca Opita, entendemos la importancia de su vehículo en su vida diaria. Ya sea que necesite un cambio de aceite, reparaciones mecánicas, alineación de ruedas o cualquier otro servicio, estamos aquí para ayudarle. Nuestra misión es proporcionar soluciones confiables y asequibles para mantener su automóvil en óptimas condiciones de funcionamiento.\n" +
                "\n" +
                "Nos enorgullece ser su elección de confianza cuando se trata de mantener su vehículo en su mejor estado. Nuestra dedicación a la excelencia y el compromiso con la satisfacción del cliente nos distinguen. Estamos aquí para cuidar de usted y de su automóvil.\n" +
                "\n" +
                "En Serviteca Opita, su seguridad y comodidad son nuestra principal prioridad. Confíe en nosotros para el mantenimiento y servicio de su vehículo, y experimente la diferencia de elegir a verdaderos profesionales del automóvil. Gracias por confiar en nosotros para sus necesidades automotrices.\" "
    }
    val text: LiveData<String> = _text
}