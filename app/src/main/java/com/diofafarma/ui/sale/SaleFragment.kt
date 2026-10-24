package com.diofafarma.ui.sale

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.diofafarma.data.model.*
import com.diofafarma.data.repository.ClientRepository
import com.diofafarma.data.repository.ProductRepository
import com.diofafarma.data.repository.SaleRepository
import com.diofafarma.databinding.DialogNewSaleBinding
import com.diofafarma.databinding.DialogSaleDetailBinding
import com.diofafarma.databinding.FragmentSalesBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class SaleFragment : Fragment() {

    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!

    private val saleRepository = SaleRepository()
    private val clientRepository = ClientRepository()
    private val productRepository = ProductRepository()

    private lateinit var adapter: SaleAdapter
    private var listaVentas: List<Sale> = emptyList()
    private val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SaleAdapter(
            ventas = emptyList(),
            onVerDetalleClick = { venta -> cargarYMostrarDetalle(venta.id) }
        )

        binding.rvVentas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVentas.adapter = adapter

        binding.fabNuevaVenta.setOnClickListener {
            abrirDialogoNuevaVenta()
        }

        binding.etBuscarVentas.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    adapter.updateData(listaVentas)
                } else {
                    val filtradas = listaVentas.filter {
                        it.codigo.contains(query, ignoreCase = true) ||
                        (it.clienteNombre?.contains(query, ignoreCase = true) == true)
                    }
                    adapter.updateData(filtradas)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cargarVentas()
    }

    fun cargarVentas() {
        binding.progressBarVentas.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                listaVentas = saleRepository.getVentas()
                adapter.updateData(listaVentas)
                binding.tvEmptyVentas.visibility = if (listaVentas.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar ventas: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBarVentas.visibility = View.GONE
            }
        }
    }

    private fun cargarYMostrarDetalle(ventaId: Int) {
        binding.progressBarVentas.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val venta = saleRepository.getVentaPorId(ventaId)
                mostrarModalDetalle(venta)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al obtener detalle: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBarVentas.visibility = View.GONE
            }
        }
    }

    private fun mostrarModalDetalle(venta: Sale) {
        val dialogBinding = DialogSaleDetailBinding.inflate(layoutInflater)

        dialogBinding.tvDetalleTitulo.text = "Transacción: ${venta.codigo}"
        val fechaStr = if (!venta.fecha.isNullOrEmpty() && venta.fecha.length >= 10) venta.fecha.substring(0, 10) else "Hoy"
        dialogBinding.tvDetalleCodigoFecha.text = "Fecha: $fechaStr | Estado: ${venta.estado} | Pago: ${venta.tipoPago}"
        val doc = if (!venta.clienteDoc.isNullOrEmpty()) " (${venta.clienteDoc})" else ""
        dialogBinding.tvDetalleCliente.text = "Cliente: ${venta.clienteNombre ?: "Ocasional"}$doc"

        dialogBinding.llItemsContainer.removeAllViews()

        val items = venta.items ?: emptyList()
        if (items.isEmpty()) {
            val tvEmpty = TextView(requireContext())
            tvEmpty.text = "No hay ítems registrados en esta venta"
            tvEmpty.setTextColor(android.graphics.Color.GRAY)
            dialogBinding.llItemsContainer.addView(tvEmpty)
        } else {
            for (item in items) {
                val tvItem = TextView(requireContext())
                val subtotalStr = formatoMoneda.format(item.subtotal ?: (item.precioUnitario * item.cantidad))
                tvItem.text = "• ${item.nombreProducto ?: "Producto"} x${item.cantidad} (${formatoMoneda.format(item.precioUnitario)} c/u) = $subtotalStr"
                tvItem.setTextColor(android.graphics.Color.DKGRAY)
                tvItem.textSize = 14f
                tvItem.setPadding(0, 4, 0, 4)
                dialogBinding.llItemsContainer.addView(tvItem)
            }
        }

        dialogBinding.tvDetalleSubtotal.text = formatoMoneda.format(venta.subtotal)
        dialogBinding.tvDetalleIva.text = formatoMoneda.format(venta.iva)
        dialogBinding.tvDetalleTotal.text = formatoMoneda.format(venta.total)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Cerrar", null)

        if (!venta.estado.equals("Anulada", ignoreCase = true)) {
            builder.setNeutralButton("Anular Venta") { _, _ ->
                anularVenta(venta.id)
            }
        }

        builder.show()
    }

    private fun anularVenta(ventaId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Anulación")
            .setMessage("¿Deseas anular esta venta? Se reintegrará el stock de los productos.")
            .setPositiveButton("Sí, Anular") { _, _ ->
                lifecycleScope.launch {
                    try {
                        saleRepository.anularVenta(ventaId)
                        Toast.makeText(requireContext(), "Venta anulada correctamente", Toast.LENGTH_SHORT).show()
                        cargarVentas()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error al anular: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun abrirDialogoNuevaVenta() {
        binding.progressBarVentas.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val clientes = clientRepository.getClientes()
                val productos = productRepository.getProductos().filter { it.stock > 0 }

                if (productos.isEmpty()) {
                    Toast.makeText(requireContext(), "No hay productos disponibles con stock", Toast.LENGTH_LONG).show()
                    return@launch
                }

                mostrarFormularioVenta(clientes, productos)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBarVentas.visibility = View.GONE
            }
        }
    }

    private fun mostrarFormularioVenta(clientes: List<Client>, productos: List<Product>) {
        val dialogBinding = DialogNewSaleBinding.inflate(layoutInflater)

        // Spinner Clientes
        val listaNombresClientes = mutableListOf("Cliente Ocasional")
        listaNombresClientes.addAll(clientes.map { "${it.nombreCompleto} (${it.documento})" })
        val adapterClientes = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listaNombresClientes)
        dialogBinding.spClienteVenta.adapter = adapterClientes

        // Spinner Productos
        val listaNombresProductos = productos.map { "${it.nombre} [Stock: ${it.stock}] - ${formatoMoneda.format(it.precioVenta)}" }
        val adapterProductos = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listaNombresProductos)
        dialogBinding.spProductoVenta.adapter = adapterProductos

        // Spinner Métodos de Pago
        val metodosPago = arrayOf("Efectivo", "Tarjeta de Débito", "Tarjeta de Crédito", "Transferencia")
        val adapterPagos = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, metodosPago)
        dialogBinding.spTipoPagoVenta.adapter = adapterPagos

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Registrar Venta") { _, _ ->
                val clientePos = dialogBinding.spClienteVenta.selectedItemPosition
                val clienteId = if (clientePos == 0) null else clientes[clientePos - 1].id

                val prodPos = dialogBinding.spProductoVenta.selectedItemPosition
                val productoSeleccionado = productos[prodPos]

                val cantidad = dialogBinding.etCantidadVenta.text.toString().toIntOrNull() ?: 1
                if (cantidad <= 0 || cantidad > productoSeleccionado.stock) {
                    Toast.makeText(requireContext(), "Cantidad inválida o superior al stock disponible", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val tipoPago = dialogBinding.spTipoPagoVenta.selectedItem.toString()
                val observaciones = dialogBinding.etObservacionesVenta.text.toString().trim()

                val request = SaleCreateRequest(
                    clienteId = clienteId,
                    tipoPago = tipoPago,
                    observaciones = observaciones,
                    items = listOf(SaleItemInput(productoId = productoSeleccionado.id, cantidad = cantidad))
                )

                registrarVenta(request)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun registrarVenta(request: SaleCreateRequest) {
        binding.progressBarVentas.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val venta = saleRepository.crearVenta(request)
                Toast.makeText(requireContext(), "Venta registrada con éxito: ${venta.codigo}", Toast.LENGTH_SHORT).show()
                cargarVentas()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al registrar venta: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBarVentas.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
