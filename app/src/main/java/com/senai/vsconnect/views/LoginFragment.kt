package com.senai.vsconnect.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

    private val retrofitClient = NetworkUtils.getRetrofitInstance("http://192.168.1.102:8099/")
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
            val idEmail = root.findViewById<EditText>(R.id.campo_email)
            val idSenha = root.findViewById<EditText>(R.id.campo_senha)

            // Para obter o texto digitado
            val emailDigitado = idEmail.text.toString()
            val senhaDigitada = idSenha.text.toString()

            val user = Login(email = emailDigitado, password = senhaDigitada)

            val usuarioLogado = logado(user);

            if (usuarioLogado){
                // Navegar para a tela de serviços ao clicar no botão
                findNavController().navigate(R.id.nav_servicos)
            }else{
                Toast.makeText(requireContext(), "Usuário não encontrado!", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    fun logado(user: Login): Boolean{

        var retornoAPI: Boolean = false

        // Fazendo uma requisição de login
        endpointFile.login(user).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable) {
                // Tratamento de falha ao fazer a requisição
                println("Falha na requisição de login: ${t.message}")
                retornoAPI = false

            }

            override fun onResponse(call: retrofit2.Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    // Tratamento bem-sucedido da resposta
                    val jsonObject = response.body()
                    // Processar o objeto JSON
                    println(jsonObject)

                    retornoAPI = true
                } else {
                    // Tratamento de erro na resposta
                    println("Erro na resposta de login: ${response.code()}")
                    println("Corpo da resposta: ${response.errorBody()?.string()}")
                    retornoAPI = false
                }
            }

        })

        return retornoAPI
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}