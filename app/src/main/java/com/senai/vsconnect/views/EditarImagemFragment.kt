package com.senai.vsconnect.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.senai.vsconnect.R
import com.senai.vsconnect.apis.Endpoint
import com.senai.vsconnect.databinding.FragmentEditarImagemBinding
import com.senai.vsconnect.utils.NetworkUtils
import org.json.JSONObject
import retrofit2.Call
import java.util.UUID
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.nio.file.Paths

class EditarImagemFragment : Fragment() {

    private val retrofitClient = NetworkUtils.getRetrofitInstance("http://192.168.0.104:8099/")

    private val endpointFile = retrofitClient.create(Endpoint::class.java)

    private val IMAGEM_PERFIL_REQUEST_CODE = 123

    private var _binding: FragmentEditarImagemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditarImagemBinding.inflate(inflater, container, false)

        val root: View = binding.root

        // Recuperar o ID do usuário
        val sharedPreferences = requireContext()
            .getSharedPreferences("idUsuario", Context.MODE_PRIVATE)

        val id = sharedPreferences.getString("idUsuario", "")

        buscarUsuarioPorId(id.toString())

        val icone_lapis = root.findViewById<ImageView>(R.id.icone_lapis)
        // Adicione um clique ao ícone de lápis
        icone_lapis.setOnClickListener {
            mostrarOpcoesEscolhaImagem()
        }

        return root
    }

    private fun buscarUsuarioPorId(idString: String) {
        val idUUID: UUID = UUID.fromString(idString)

        endpointFile.buscarUsuarioPorID(idUUID).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                when (response.code()) {
                    200 -> tratarBuscaDeUsuarioBemSucedida(response.body().toString())
                    400 -> tratarFalhaNaBuscaDeUsuario("Id de usuário inválido")
                    404 -> tratarFalhaNaBuscaDeUsuario("Usuário não encontrado.")
                    else -> tratarFalhaNaBuscaDeUsuario("Erro ao encontrar usuário")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                tratarFalhaNaBuscaDeUsuario(t.message)
            }

        })
    }

    private fun tratarBuscaDeUsuarioBemSucedida(response: String) {
        val root: View = binding.root

        val json = JSONObject(response)

        // Suponha que você tenha uma ImageView no seu layout com o ID "imagem_perfil"
        val viewImagemUsuario: ImageView = root.findViewById(R.id.imagem_perfil)

        // Substitua a URL abaixo pela URL real da imagem no servidor
//        val pathImagemUsuario = Paths.get(
//            System.getProperty("user.dir"),
//            "src",
//            "main",
//            "resources",
//            "static",
//            "img",
//            json.getString("url_img")
//        ).toString()

        //val pathImagemUsuario = "C:\\Users\\teodo\\OneDrive\\Área de Trabalho\\ApiVsConnect\\apivsconnect\\src\\main\\resources\\static\\img\\" + json.getString("url_img")

        // Carrega a imagem usando Glide
//        Glide.with(this)
//            .load(File(pathImagemUsuario))
//            .into(viewImagemUsuario)
    }

    private fun tratarFalhaNaBuscaDeUsuario(mensagemErro: String?) {
        println("Falha na requisição de login: $mensagemErro")
        Toast.makeText(requireContext(), "Falha ao se logar!", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarOpcoesEscolhaImagem() {
        val escolherImagemIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val capturarImagemIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val escolherImagemTitle = resources.getString(R.string.escolher_imagem)
        val capturarImagemTitle = resources.getString(R.string.capturar_imagem)

        // Crie uma Intent Chooser para escolher entre a galeria e a câmera
        val chooserIntent = Intent.createChooser(escolherImagemIntent, escolherImagemTitle)
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(capturarImagemIntent))

        startActivityForResult(chooserIntent, IMAGEM_PERFIL_REQUEST_CODE)
    }

    // Manipule o resultado da escolha da imagem
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val profile_image = view?.findViewById<ImageView>(R.id.imagem_perfil)

        if (requestCode == IMAGEM_PERFIL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (data?.data != null) {
                // Imagem escolhida da galeria
                val imagemSelecionadaUri = data.data
                profile_image?.setImageURI(imagemSelecionadaUri)


                // Lógica adicional para ação da galeria
            } else if (data?.action == "inline-data") {
                // Imagem capturada pela câmera
                val imagemCapturada = data.extras?.get("data") as Bitmap
                profile_image?.setImageBitmap(imagemCapturada)
                // Lógica adicional para ação da câmera
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}