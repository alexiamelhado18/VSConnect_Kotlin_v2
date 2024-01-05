package com.senai.vsconnect.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.senai.vsconnect.R
import com.senai.vsconnect.apis.Endpoint
import com.senai.vsconnect.databinding.FragmentLoginBinding
import com.senai.vsconnect.models.Login
import com.senai.vsconnect.utils.NetworkUtils
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {

    private val retrofitClient = NetworkUtils.getRetrofitInstance("http://IP:8099/")
    private val endpointFile = retrofitClient.create(Endpoint::class.java)
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val btnEntrar = root.findViewById<Button>(R.id.btn_entrar)

        btnEntrar.setOnClickListener {
            // Navegar para a tela de serviços ao clicar no botão
            findNavController().navigate(R.id.action_nav_sair_to_nav_servicos)
        }

        val user = Login(email = "thiago3@email.com", password = "senai")

        // Fazendo uma requisição de login
        endpointFile.login(user).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable) {
                // Tratamento de falha ao fazer a requisição
                println("Falha na requisição de login: ${t.message}")
            }

            override fun onResponse(call: retrofit2.Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    // Tratamento bem-sucedido da resposta
                    val jsonObject = response.body()
                    // Processar o objeto JSON
                    println(jsonObject)
                } else {
                    // Tratamento de erro na resposta
                    println("Erro na resposta de login: ${response.code()}")
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