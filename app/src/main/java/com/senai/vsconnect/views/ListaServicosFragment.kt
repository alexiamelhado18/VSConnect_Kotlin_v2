package com.senai.vsconnect.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.senai.vsconnect.R
import com.senai.vsconnect.adapters.ServicoAdapter
import com.senai.vsconnect.apis.Endpoint
import com.senai.vsconnect.databinding.FragmentListaServicosBinding
import com.senai.vsconnect.databinding.FragmentServicoBinding
import com.senai.vsconnect.models.Servico
import com.senai.vsconnect.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaServicosFragment : Fragment() {
    private val retrofitClient = NetworkUtils.getRetrofitInstance();

    private val endpointFile = retrofitClient.create(Endpoint::class.java)

    private var _binding: FragmentListaServicosBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaServicosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // endereçando os dados do recicler
//        binding.recyclerServico.layoutManager = LinearLayoutManager(requireContext())
//        var reciclerList = root.findViewById<RecyclerView>(R.id.recyclerServico)


        // Listando os serviços cadastrados
//        endpointFile.listarServicos().enqueue(object : Callback<List<Servico>> {
//            override fun onFailure(call: Call<List<Servico>>, t: Throwable) {
//                // Tratamento de falha ao fazer a requisição
//                println("Falha na requisição: ${t.message}")
//            }
//
//            override fun onResponse(call: Call<List<Servico>>, response: Response<List<Servico>>) {
//                if (response.isSuccessful) {
//                    // Tratamento bem-sucedido da resposta (para uma lista de objetos JSON)
//                    val jsonArray = response.body()
//                    jsonArray?.forEach { jsonObject ->
//                        // Processar cada objeto JSON na lista
//                        println(jsonObject)
//                    }
//
//                    binding.recyclerServico.adapter = jsonArray?.let { ServicoAdapter(requireContext(), it) }
//
//                } else {
//                    // Tratamento de erro na resposta
//                    println("Erro na resposta: ${response.code()}")
//                }
//            }
//        })

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}