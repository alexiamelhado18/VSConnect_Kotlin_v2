package com.senai.vsconnect.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.JsonArray
import com.senai.vsconnect.apis.Endpoint
import com.senai.vsconnect.databinding.FragmentServicoBinding
import com.senai.vsconnect.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaServicosFragment : Fragment() {
    private val retrofitClient = NetworkUtils.getRetrofitInstance("http://IP:8099/");

    private val endpointFile = retrofitClient.create(Endpoint::class.java)

    private var _binding: FragmentServicoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Listando os serviços cadastrados
        endpointFile.listarServicos().enqueue(object : Callback<JsonArray> {
            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                // Tratamento de falha ao fazer a requisição
                println("Falha na requisição: ${t.message}")
            }

            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    // Tratamento bem-sucedido da resposta (para uma lista de objetos JSON)
                    val jsonArray = response.body()
                    jsonArray?.forEach { jsonObject ->
                        // Processar cada objeto JSON na lista
                        println(jsonObject)
                    }
                } else {
                    // Tratamento de erro na resposta
                    println("Erro na resposta: ${response.code()}")
                }
            }
        })

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}