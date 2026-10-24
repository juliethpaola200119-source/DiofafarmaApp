package com.diofafarma.ui.user

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diofafarma.data.model.User
import com.diofafarma.databinding.ItemUserBinding

class UserAdapter(
    private var usuarios: List<User>,
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    fun updateData(nuevosUsuarios: List<User>) {
        this.usuarios = nuevosUsuarios
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(usuarios[position])
    }

    override fun getItemCount(): Int = usuarios.size

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(usuario: User) {
            binding.tvUsername.text = usuario.usuario
            binding.tvUserRol.text = usuario.rol ?: "Cajero"
            binding.tvUserEmail.text = if (usuario.email.isNullOrEmpty()) "Sin correo registrado" else usuario.email

            val activo = usuario.estado == 1 || usuario.estado == null
            binding.tvUserEstado.text = if (activo) "Activo" else "Inactivo"
            binding.tvUserEstado.setTextColor(if (activo) Color.parseColor("#2E7D32") else Color.parseColor("#C62828"))

            binding.btnEditarUser.setOnClickListener { onEditClick(usuario) }
            binding.btnEliminarUser.setOnClickListener { onDeleteClick(usuario) }
        }
    }
}
