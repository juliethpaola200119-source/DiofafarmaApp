package com.diofafarma.ui.product

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.diofafarma.data.model.Product
import com.diofafarma.data.model.ProductRequest
import com.diofafarma.data.repository.ProductRepository
import com.diofafarma.databinding.DialogProductBinding
import com.diofafarma.databinding.FragmentProductsBinding
import kotlinx.coroutines.launch

class ProductFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private val repository = ProductRepository()

    private lateinit var adapter: ProductAdapter
    private var listaCompleta: List<Product> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductAdapter(
            productos = emptyList(),
            onEditClick = { producto -> mostrarDialogoProducto(producto) },
            onDeleteClick = { producto -> confirmarEliminacion(producto) }
        )

        binding.rvProductos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductos.adapter = adapter

        binding.fabAgregarProducto.setOnClickListener {
            mostrarDialogoProducto(null)
        }

        binding.etBuscarProducto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filtrar(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cargarProductos()
    }

    fun cargarProductos() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                listaCompleta = repository.getProductos()
                adapter.updateData(listaCompleta)
                binding.tvEmpty.visibility = if (listaCompleta.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun filtrar(query: String) {
        val filtrados = if (query.isEmpty()) {
            listaCompleta
        } else {
            listaCompleta.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                it.codigo.contains(query, ignoreCase = true) ||
                (it.laboratorio?.contains(query, ignoreCase = true) == true)
            }
        }
        adapter.updateData(filtrados)
        binding.tvEmpty.visibility = if (filtrados.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun mostrarDialogoProducto(producto: Product?) {
        val dialogBinding = DialogProductBinding.inflate(layoutInflater)
        val esEdicion = producto != null

        dialogBinding.tvDialogTitle.text = if (esEdicion) "Editar Producto" else "Nuevo Producto"

        if (esEdicion) {
            dialogBinding.etCodigo.setText(producto!!.codigo)
            dialogBinding.etNombre.setText(producto.nombre)
            dialogBinding.etLaboratorio.setText(producto.laboratorio ?: "")
            dialogBinding.etDescripcion.setText(producto.descripcion ?: "")
            dialogBinding.etPrecioCompra.setText(producto.precioCompra.toString())
            dialogBinding.etPrecioVenta.setText(producto.precioVenta.toString())
            dialogBinding.etStock.setText(producto.stock.toString())
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton(if (esEdicion) "Actualizar" else "Guardar") { _, _ ->
                val codigo = dialogBinding.etCodigo.text.toString().trim()
                val nombre = dialogBinding.etNombre.text.toString().trim()
                val laboratorio = dialogBinding.etLaboratorio.text.toString().trim()
                val descripcion = dialogBinding.etDescripcion.text.toString().trim()
                val precioCompra = dialogBinding.etPrecioCompra.text.toString().toDoubleOrNull() ?: 0.0
                val precioVenta = dialogBinding.etPrecioVenta.text.toString().toDoubleOrNull() ?: 0.0
                val stock = dialogBinding.etStock.text.toString().toIntOrNull() ?: 0

                if (codigo.isEmpty() || nombre.isEmpty()) {
                    Toast.makeText(requireContext(), "Código y nombre son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val req = ProductRequest(
                    codigo = codigo,
                    nombre = nombre,
                    descripcion = descripcion,
                    laboratorio = laboratorio,
                    precioCompra = precioCompra,
                    precioVenta = precioVenta,
                    stock = stock,
                    estado = true
                )

                guardarProducto(req, producto?.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarProducto(request: ProductRequest, id: Int?) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                if (id == null) {
                    repository.crearProducto(request)
                    Toast.makeText(requireContext(), "Producto creado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    repository.actualizarProducto(id, request)
                    Toast.makeText(requireContext(), "Producto actualizado con éxito", Toast.LENGTH_SHORT).show()
                }
                cargarProductos()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun confirmarEliminacion(producto: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Producto")
            .setMessage("¿Deseas eliminar '${producto.nombre}' del inventario?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    try {
                        repository.eliminarProducto(producto.id)
                        Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                        cargarProductos()
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
