package com.senai.vsconnect.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.senai.vsconnect.R
import com.senai.vsconnect.models.Servico

class ServicoAdapter
    ( private val context : Context, private val listaServicos : List<Servico> ) : RecyclerView.Adapter< ServicoAdapter.ViewHolder >()
{
        class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
            fun VincularCardDados(servico: Servico){
                val tituloServico = itemView.findViewById<TextView>(R.id.tituloTextView)
                tituloServico.text = servico.titulo

                val valorServico = itemView.findViewById<TextView>(R.id.valorTextView)
                valorServico.text = servico.proposta

                val descricaoServico = itemView.findViewById<TextView>(R.id.descricaoTextView)
                descricaoServico.text = servico.descricao
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicoAdapter.ViewHolder {
        val inflater = LayoutInflater.from(context);
        val view = inflater.inflate(R.layout.fragment_servico, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServicoAdapter.ViewHolder, position: Int) {
        val itemServico = listaServicos[position]
        holder.VincularCardDados( itemServico )
    }

    override fun getItemCount(): Int {
        return listaServicos.size
    }
}