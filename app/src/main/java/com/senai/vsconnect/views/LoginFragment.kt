package com.senai.vsconnect.views

import android.content.Context
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
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.StandardCharsets
import java.util.Base64

class LoginFragment : Fragment() {

    private val retrofitClient = NetworkUtils.getRetrofitInstance()
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


        // Obtém o controlador de navegação associado ao fragmento
        val navController = findNavController()

        // Obtém a entrada anterior na pilha de retrocesso
        val previousBackStackEntry = navController.previousBackStackEntry

        // Verifica se há uma entrada anterior e obtém informações sobre ela
        if (previousBackStackEntry != null) {
            // Obtém o ID do destino anterior
            val previousDestinationId = previousBackStackEntry.destination.id

            // Faça algo com o ID do destino anterior
            // Por exemplo, você pode verificar qual era a tela anterior
            when (previousDestinationId) {
                R.id.nav_servicos, R.id.nav_editar_imagem -> {

                    val sharedPreferences = requireContext().getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    // Substitua "sua_chave" pelo nome da chave que você deseja excluir
                    editor.remove("idUsuario")

                    // Aplica as alterações
                    editor.apply()

                    println(sharedPreferences.getString("idUsuario", "Chave não encontrada"))
                }

            }
        }

        btnEntrar.setOnClickListener {
            //autenticarUsuario()
            findNavController().navigate(R.id.action_nav_sair_to_nav_servicos)

        }

        return root
    }

    private fun autenticarUsuario() {

        val root: View = binding.root

        val idEmail = root.findViewById<EditText>(R.id.campo_email)
        val idSenha = root.findViewById<EditText>(R.id.campo_senha)

        // obtem o texto digitado
        val emailDigitado = idEmail.text.toString()
        val senhaDigitada = idSenha.text.toString()

        val usuario = Login(emailDigitado, senhaDigitada)

        // Fazendo uma requisição de login
        endpointFile.login(usuario).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable) {
                tratarFalhaAutenticacao(t.message)
            }

            override fun onResponse(call: retrofit2.Call<JsonObject>, response: Response<JsonObject>) {

                when (response.code()) {
                    200 -> tratarAutenticacaoBemSucedida(response.body().toString())
                    400 -> tratarFalhaAutenticacao("E-mail/Senha inválidos")
                    404 -> tratarFalhaAutenticacao("Usuário não encontrado!")
                    403 -> tratarFalhaAutenticacao("Usuário sem permissão para se logar!")
                    else -> tratarFalhaAutenticacao("Erro desconhecido: ${response.code()}")
                }
            }

        })
    }

    private fun tratarAutenticacaoBemSucedida(response: String) {
        val id = decodificarToken(response)

        val sharedPreferences = requireContext()
            .getSharedPreferences("idUsuario", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putString("idUsuario", id.toString())

        editor.apply()

        println(sharedPreferences.getString("idUsuario", "Chave não encontrada"))

        findNavController().navigate(R.id.action_nav_sair_to_nav_servicos)
    }

    private fun tratarFalhaAutenticacao(mensagemErro: String?) {
        println("Falha na requisição de login: $mensagemErro")
        Toast.makeText(requireContext(), "Falha ao se logar!", Toast.LENGTH_SHORT).show()
    }

    private fun decodificarToken(token: String): Any {
        val partes = token.split(".")
        val payloadBase64 = partes[1]

        val payloadBytes = Base64.getUrlDecoder().decode(payloadBase64)
        val payloadJson = String(payloadBytes, StandardCharsets.UTF_8)

        val json = JSONObject(payloadJson)
        return json["idUsuario"].toString()
//        return json.optString("idUsuario", null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}