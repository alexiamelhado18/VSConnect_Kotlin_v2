package com.senai.vsconnect.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import com.senai.vsconnect.R
import com.senai.vsconnect.apis.Endpoint
import com.senai.vsconnect.databinding.FragmentEditarImagemBinding
import com.senai.vsconnect.utils.NetworkUtils
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import java.util.UUID
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class EditarImagemFragment : Fragment() {

    // Configuração do cliente Retrofit para comunicação com o servidor
    private val retrofitClient = NetworkUtils.getRetrofitInstance()
    private val endpointFile = retrofitClient.create(Endpoint::class.java)

    // Código de requisição para a escolha da imagem
    private val IMAGEM_PERFIL_REQUEST_CODE = 123

    // Objeto de binding para vincular elementos do layout
    private var _binding: FragmentEditarImagemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflar o layout utilizando View Binding
        _binding = FragmentEditarImagemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Obter referência para a ImageView do ícone de lápis
        val iconeLapis = root.findViewById<ImageView>(R.id.icone_lapis)

        // Recuperar o ID do usuário armazenado nas preferências compartilhadas
        val sharedPreferences = requireContext().getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getString("idUsuario", "")

        // Chamar a função para buscar informações do usuário com base no ID
        //buscarUsuarioPorId(idUsuario.toString())

        // Adicionar um clique ao ícone de lápis para permitir a escolha ou captura de imagem
//        iconeLapis.setOnClickListener {
//            mostrarOpcoesEscolhaImagem()
//        }

        return root
    }

    private fun buscarUsuarioPorId(idString: String) {
        // Fazer uma chamada assíncrona para o servidor para obter informações do usuário
        endpointFile.buscarUsuarioPorID(UUID.fromString(idString)).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                when (response.code()) {
                    200 -> {
                        // Obter o layout root novamente a partir do objeto de binding
                        val root: View = binding.root

                        // Converter a resposta JSON em um objeto JSONObject
                        val objUsuario = JSONObject(response.body().toString())

                        // Obter referência para a ImageView onde a imagem do perfil será exibida
                        val viewImagemUsuario: ImageView = root.findViewById(R.id.imagem_perfil)

                        // Construir a URL da imagem do usuário no servidor
                        val urlImagemUsuario = "http://172.16.20.226:8099/img/" + objUsuario.getString("url_img")

                        // Usar Picasso para carregar e exibir a imagem na ImageView
                        Picasso.get().load(urlImagemUsuario).into(viewImagemUsuario)
                    }
                    400 -> tratarFalhaNaRequisicao("Id de usuário inválido")
                    404 -> tratarFalhaNaRequisicao("Usuário não encontrado.")
                    else -> tratarFalhaNaRequisicao("Erro ao encontrar usuário")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                // Tratar falha na requisição
                tratarFalhaNaRequisicao(t.message)
            }
        })
    }

    private fun tratarFalhaNaRequisicao(mensagemErro: String?) {
        // Imprimir mensagem de erro no console e exibir um Toast
        println(mensagemErro)
        Toast.makeText(requireContext(), mensagemErro, Toast.LENGTH_SHORT).show()
    }

    private fun mostrarOpcoesEscolhaImagem() {
        // Criar Intents para escolher uma imagem da galeria ou capturar uma nova pela câmera
        val escolherImagemIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val capturarImagemIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Obter os títulos para as opções de escolha de imagem
        val escolherImagemTitle = resources.getString(R.string.escolher_imagem)
        val capturarImagemTitle = resources.getString(R.string.capturar_imagem)

        // Criar um Intent Chooser para oferecer opções entre galeria e câmera
        val intentEscolhaImagem = Intent.createChooser(escolherImagemIntent, escolherImagemTitle)
        intentEscolhaImagem.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(capturarImagemIntent))

        // Iniciar a atividade esperando um resultado
        startActivityForResult(intentEscolhaImagem, IMAGEM_PERFIL_REQUEST_CODE)
    }

    private fun atualizarNovaImagemDePerfil(imagemSelecionadaBitmap: Bitmap) {
        // Obter o ID do usuário armazenado nas preferências compartilhadas
        val idUsuario = requireContext().getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
            .getString("idUsuario", "")

        // Criar um arquivo temporário para armazenar a imagem
        val file = File(requireContext().cacheDir, "temp_image.png")
        file.createNewFile()

        // Salvar a imagem Bitmap no arquivo temporário
        val outputStream = FileOutputStream(file)
        imagemSelecionadaBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()

        // Criar partes Multipart para a imagem
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val imagemPart = MultipartBody.Part.createFormData("imagem", file.name, requestFile)

        // Enviar a nova imagem para o servidor e tratar a resposta
        endpointFile.editarImagem(imagemPart, UUID.fromString(idUsuario)).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                when (response.code()) {
                    200 -> {
                        // Exibir mensagem de sucesso
                        Toast.makeText(requireContext(), "Foto atualizada com sucesso!!!", Toast.LENGTH_SHORT).show()
                    }
                    400 -> tratarFalhaNaRequisicao("404")
                    404 -> tratarFalhaNaRequisicao("Usuário não encontrado.")
                    else -> tratarFalhaNaRequisicao("Erro ao salvar imagem")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                // Tratar falha na requisição
                tratarFalhaNaRequisicao(t.message)
            }
        })
    }

    // Manipular o resultado da escolha de imagem
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val profile_image = view?.findViewById<ImageView>(R.id.imagem_perfil)

        if (requestCode == IMAGEM_PERFIL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (data?.data != null) {
                // Imagem escolhida da galeria
                val imagemSelecionadaUri = data.data

                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(imagemSelecionadaUri!!)
                val imagemSelecionadaBitmap = BitmapFactory.decodeStream(inputStream)

                // Exibir a imagem na ImageView e enviar para o servidor
                profile_image?.setImageURI(imagemSelecionadaUri)
                atualizarNovaImagemDePerfil(imagemSelecionadaBitmap)

            } else if (data?.action == "inline-data") {
                // Imagem capturada pela câmera
                val imagemCapturada = data.extras?.get("data") as Bitmap
                profile_image?.setImageBitmap(imagemCapturada)
                atualizarNovaImagemDePerfil(imagemCapturada)

            }
        }
    }

    override fun onDestroyView() {
        // Liberar a referência ao objeto de binding ao destruir o fragmento
        super.onDestroyView()
        _binding = null
    }
}
