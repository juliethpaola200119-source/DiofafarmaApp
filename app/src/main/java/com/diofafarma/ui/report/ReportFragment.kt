package com.diofafarma.ui.report

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.diofafarma.data.repository.ReportRepository
import com.diofafarma.databinding.FragmentReportsBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ReportFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private val repository = ReportRepository()
    private val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarReportes()
    }

    fun cargarReportes() {
        binding.progressBarReportes.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val resumen = repository.getResumen()
                binding.tvTotalProductos.text = resumen.totalProductos.toString()
                binding.tvProductosActivos.text = resumen.productosActivos.toString()
                binding.tvStockBajo.text = resumen.productosStockBajo.toString()
                binding.tvTotalUsuarios.text = resumen.totalUsuarios.toString()

                val stockBajoList = repository.getStockBajo(10)
                binding.llStockBajoContainer.removeAllViews()

                if (stockBajoList.isEmpty()) {
                    val tv = TextView(requireContext())
                    tv.text = "✅ Todo el inventario tiene stock suficiente"
                    tv.setTextColor(Color.parseColor("#2E7D32"))
                    tv.textSize = 14f
                    binding.llStockBajoContainer.addView(tv)
                } else {
                    for (item in stockBajoList) {
                        val tv = TextView(requireContext())
                        val precioStr = formatoMoneda.format(item.precio_venta)
                        tv.text = "• ${item.nombre} (Cód: ${item.codigo}) - Quedan solo ${item.stock} unidades - $precioStr"
                        tv.setTextColor(Color.parseColor("#C62828"))
                        tv.textSize = 14f
                        tv.setPadding(0, 6, 0, 6)
                        binding.llStockBajoContainer.addView(tv)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar reportes: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBarReportes.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
