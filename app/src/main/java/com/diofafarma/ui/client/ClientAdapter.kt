package com.diofafarma.ui.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diofafarma.data.model.Client
import com.diofafarma.databinding.ItemClientBinding

class ClientAdapter(
    private var clientes: List<Client>,
    private val onEditClick: (Client) -> Unit,
    private val onDeleteClick: (Client) -> Unit
) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    fun updateData(nuevosClientes: List<Client>) {
        this.clientes = nuevosClientes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val binding = ItemClientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.bind(clientes[position])
    }

    override fun getItemCount(): Int = clientes.size

    inner class ClientViewHolder(private val binding: ItemClientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cliente: Client) {
            binding.tvNombreCliente.text = cliente.nombreCompleto
            val tipo = cliente.tipoDocumento ?: "CC"
            binding.tvDocumento.text = "$tipo ${cliente.documento}"

            val tel = if (cliente.telefono.isNullOrEmpty()) "Sin teléfono" else cliente.telefono
            val email = if (cliente.email.isNullOrEmpty()) "Sin email" else cliente.email
            binding.tvContacto.text = "Tel: $tel | $email"

            val dir = if (cliente.direccion.isNullOrEmpty()) "Sin dirección registrada" else cliente.direccion
            binding.tvDireccion.text = "Dir: $dir"

            binding.btnEditarCliente.setOnClickListener { onEditClick(cliente) }
            binding.btnEliminarCliente.setOnClickListener { onDeleteClick(cliente) }
        }
    }
}
