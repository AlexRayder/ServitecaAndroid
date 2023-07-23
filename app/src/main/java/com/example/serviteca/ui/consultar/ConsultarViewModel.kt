package com.example.serviteca.ui.consultar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConsultarViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Consultar Servicio"
    }
    val text: LiveData<String> = _text
}