package com.example.ensayorv.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ensayorv.databinding.ItemGatitoBinding
import com.example.ensayorv.domain.model.Gatito

class GatitoAdapter (
    val delGatito : (Gatito) -> Unit,
)
    : ListAdapter<Gatito, GatitoAdapter.GatitoViewHolder>(GatitoDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GatitoViewHolder {
        val binding = ItemGatitoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GatitoViewHolder(binding, delGatito)
    }

    override fun onBindViewHolder(holder: GatitoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class GatitoViewHolder(

        private val binding: ItemGatitoBinding,
        private val onClickDelete: (Gatito) -> Unit,

        ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(gatito: Gatito) {

            with(binding) {

                tvNombreGatito.text = gatito.nombre

                root.setOnClickListener {

                    onClickDelete(gatito)

                }

            }
        }

    }

    class GatitoDiffCallback : DiffUtil.ItemCallback<Gatito>() {

        override fun areItemsTheSame(oldItem: Gatito, newItem: Gatito): Boolean =
            oldItem.nombre == newItem.nombre

        override fun areContentsTheSame(oldItem: Gatito, newItem: Gatito): Boolean =
            oldItem == newItem
    }
}




