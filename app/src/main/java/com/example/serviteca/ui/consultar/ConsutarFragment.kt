package com.example.serviteca.ui.consultar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.serviteca.databinding.FragmentConsultarBinding

class ConsutarFragment : Fragment(){
    private  var _binding: FragmentConsultarBinding? = null

    private  val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val consultarViewModel =
            ViewModelProvider(this).get(ConsultarViewModel::class.java)

        _binding = FragmentConsultarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.ConsultarServi
        consultarViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

