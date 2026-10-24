package com.diofafarma.ui.user

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.diofafarma.data.model.User
import com.diofafarma.data.model.UserRequest
import com.diofafarma.data.repository.UserRepository
import com.diofafarma.databinding.DialogUserBinding
import com.diofafarma.databinding.FragmentUsersBinding
import kotlinx.coroutines.launch

class UserFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    private val repository = UserRepository()

    private lateinit var adapter: UserAdapter
    private var listaUsuarios: List<User> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserAdapter(
            usuarios = emptyList(),
            onEditClick = { user -> mostrarDialogoUsuario(user) },
            onDeleteClick = { user -> confirmarEliminacion(user) }
        )

        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter

        binding.fabAgregarUser.setOnClickListener {
            mostrarDialogoUsuario(null)
        }

        cargarUsuarios()
    }

    fun cargarUsuarios() {
        binding.progressBarUsers.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                listaUsuarios = repository.getUsuarios()
                adapter.updateData(listaUsuarios)
                binding.tvEmptyUsers.visibility = if (listaUsuarios.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar usuarios: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBarUsers.visibility = View.GONE
            }
        }
    }

    private fun mostrarDialogoUsuario(usuario: User?) {
        val dialogBinding = DialogUserBinding.inflate(layoutInflater)
        val esEdicion = usuario != null

        dialogBinding.tvDialogTitleUser.text = if (esEdicion) "Editar Usuario" else "Nuevo Usuario"

        val roles = arrayOf("Administrador", "Farmacéutico", "Cajero")
        val adapterRoles = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)
        dialogBinding.spUserRol.adapter = adapterRoles

        if (esEdicion) {
            dialogBinding.etUserUsuario.setText(usuario!!.usuario)
            dialogBinding.etUserEmail.setText(usuario.email ?: "")
            dialogBinding.etUserPassword.hint = "Dejar en blanco para mantener actual"
            val pos = roles.indexOf(usuario.rol ?: "Cajero")
            if (pos >= 0) dialogBinding.spUserRol.setSelection(pos)
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton(if (esEdicion) "Actualizar" else "Guardar") { _, _ ->
                val nombreUsuario = dialogBinding.etUserUsuario.text.toString().trim()
                val email = dialogBinding.etUserEmail.text.toString().trim()
                val password = dialogBinding.etUserPassword.text.toString()
                val rol = dialogBinding.spUserRol.selectedItem.toString()

                if (nombreUsuario.isEmpty()) {
                    Toast.makeText(requireContext(), "El nombre de usuario es obligatorio", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (!esEdicion && password.isEmpty()) {
                    Toast.makeText(requireContext(), "La contraseña es obligatoria para nuevos usuarios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val req = UserRequest(
                    usuario = nombreUsuario,
                    email = if (email.isEmpty()) null else email,
                    password = password,
                    rol = rol
                )

                guardarUsuario(req, usuario?.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarUsuario(request: UserRequest, id: Int?) {
        binding.progressBarUsers.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                if (id == null) {
                    repository.crearUsuario(request)
                    Toast.makeText(requireContext(), "Usuario creado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    repository.actualizarUsuario(id, request)
                    Toast.makeText(requireContext(), "Usuario actualizado con éxito", Toast.LENGTH_SHORT).show()
                }
                cargarUsuarios()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBarUsers.visibility = View.GONE
            }
        }
    }

    private fun confirmarEliminacion(usuario: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Usuario")
            .setMessage("¿Deseas eliminar al usuario '${usuario.usuario}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    try {
                        repository.eliminarUsuario(usuario.id)
                        Toast.makeText(requireContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show()
                        cargarUsuarios()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
