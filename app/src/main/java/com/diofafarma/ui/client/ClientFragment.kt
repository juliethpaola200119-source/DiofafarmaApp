package com.diofafarma.ui.client

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.diofafarma.data.model.Client
import com.diofafarma.data.model.ClientRequest
import com.diofafarma.data.repository.ClientRepository
import com.diofafarma.databinding.DialogClientBinding
import com.diofafarma.databinding.FragmentClientsBinding
import kotlinx.coroutines.launch

class ClientFragment : Fragment() {

    private var _binding: FragmentClientsBinding? = null
    private val binding get() = _binding!!
    private val repository = ClientRepository()

    private lateinit var adapter: ClientAdapter
    private var listaCompleta: List<Client> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ClientAdapter(
            clientes = emptyList(),
            onEditClick = { cliente -> mostrarDialogoCliente(cliente) },
            onDeleteClick = { cliente -> confirmarEliminacion(cliente) }
        )

        binding.rvClientes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvClientes.adapter = adapter

        binding.fabAgregarCliente.setOnClickListener {
            mostrarDialogoCliente(null)
        }

        binding.etBuscarCliente.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    adapter.updateData(listaCompleta)
                } else {
                    val filtrados = listaCompleta.filter {
                        it.documento.contains(query, ignoreCase = true) ||
                        it.nombre.contains(query, ignoreCase = true) ||
                        (it.apellido?.contains(query, ignoreCase = true) == true)
                    }
                    adapter.updateData(filtrados)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cargarClientes()
    }

    fun cargarClientes() {
        binding.progressBarClientes.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                listaCompleta = repository.getClientes()
                adapter.updateData(listaCompleta)
                binding.tvEmptyClientes.visibility = if (listaCompleta.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBarClientes.visibility = View.GONE
            }
        }
    }

    private fun mostrarDialogoCliente(cliente: Client?) {
        val dialogBinding = DialogClientBinding.inflate(layoutInflater)
        val esEdicion = cliente != null

        dialogBinding.tvDialogTitleCliente.text = if (esEdicion) "Editar Cliente" else "Nuevo Cliente"

        val tiposDoc = arrayOf("CC", "TI", "CE", "NIT", "PAS")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, tiposDoc)
        dialogBinding.spTipoDoc.adapter = spinnerAdapter

        if (esEdicion) {
            val pos = tiposDoc.indexOf(cliente!!.tipoDocumento ?: "CC")
            if (pos >= 0) dialogBinding.spTipoDoc.setSelection(pos)
            dialogBinding.etDocumento.setText(cliente.documento)
            dialogBinding.etNombreCliente.setText(cliente.nombre)
            dialogBinding.etApellidoCliente.setText(cliente.apellido ?: "")
            dialogBinding.etTelefono.setText(cliente.telefono ?: "")
            dialogBinding.etEmail.setText(cliente.email ?: "")
            dialogBinding.etDireccion.setText(cliente.direccion ?: "")
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton(if (esEdicion) "Actualizar" else "Guardar") { _, _ ->
                val tipoDoc = dialogBinding.spTipoDoc.selectedItem.toString()
                val documento = dialogBinding.etDocumento.text.toString().trim()
                val nombre = dialogBinding.etNombreCliente.text.toString().trim()
                val apellido = dialogBinding.etApellidoCliente.text.toString().trim()
                val telefono = dialogBinding.etTelefono.text.toString().trim()
                val email = dialogBinding.etEmail.text.toString().trim()
                val direccion = dialogBinding.etDireccion.text.toString().trim()

                if (documento.isEmpty() || nombre.isEmpty()) {
                    Toast.makeText(requireContext(), "Documento y nombre son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val req = ClientRequest(
                    tipoDocumento = tipoDoc,
                    documento = documento,
                    nombre = nombre,
                    apellido = apellido,
                    telefono = telefono,
                    email = email,
                    direccion = direccion,
                    estado = true
                )

                guardarCliente(req, cliente?.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarCliente(request: ClientRequest, id: Int?) {
        binding.progressBarClientes.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                if (id == null) {
                    repository.crearCliente(request)
                    Toast.makeText(requireContext(), "Cliente registrado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    repository.actualizarCliente(id, request)
                    Toast.makeText(requireContext(), "Cliente actualizado con éxito", Toast.LENGTH_SHORT).show()
                }
                cargarClientes()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBarClientes.visibility = View.GONE
            }
        }
    }

    private fun confirmarEliminacion(cliente: Client) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Cliente")
            .setMessage("¿Deseas eliminar a '${cliente.nombreCompleto}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    try {
                        repository.eliminarCliente(cliente.id)
                        Toast.makeText(requireContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show()
                        cargarClientes()
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
