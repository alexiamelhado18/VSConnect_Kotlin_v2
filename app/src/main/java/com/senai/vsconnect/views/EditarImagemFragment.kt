package com.senai.vsconnect.views

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.senai.vsconnect.R
import com.senai.vsconnect.databinding.FragmentEditarImagemBinding

class EditarImagemFragment : Fragment() {

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

        val icone_lapis = root.findViewById<ImageView>(R.id.icone_lapis)
        // Adicione um clique ao ícone de lápis
        icone_lapis.setOnClickListener {
            mostrarOpcoesEscolhaImagem()
        }

        return root
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