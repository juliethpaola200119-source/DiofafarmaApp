package com.diofafarma.ui.sale

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diofafarma.data.model.Sale
import com.diofafarma.databinding.ItemSaleBinding
import java.text.NumberFormat
import java.util.Locale

class SaleAdapter(
    private var ventas: List<Sale>,
    private val onVerDetalleClick: (Sale) -> Unit
) : RecyclerView.Adapter<SaleAdapter.SaleViewHolder>() {

    private val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    fun updateData(nuevasVentas: List<Sale>) {
        this.ventas = nuevasVentas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val binding = ItemSaleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SaleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        holder.bind(ventas[position])
    }

    override fun getItemCount(): Int = ventas.size

    inner class SaleViewHolder(private val binding: ItemSaleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(venta: Sale) {
            binding.tvCodigoVenta.text = venta.codigo
            binding.tvClienteVenta.text = "Cliente: ${venta.clienteNombre ?: "Cliente Ocasional"}"

            val fechaStr = if (!venta.fecha.isNullOrEmpty() && venta.fecha.length >= 10) {
                venta.fecha.substring(0, 10)
            } else {
                "Fecha no registrada"
            }
            binding.tvFechaTipoPago.text = "$fechaStr | ${venta.tipoPago}"
            binding.tvTotalVenta.text = "Total: ${formatoMoneda.format(venta.total)}"

            binding.tvEstadoVenta.text = venta.estado
            if (venta.estado.equals("Anulada", ignoreCase = true)) {
                binding.tvEstadoVenta.setTextColor(Color.parseColor("#C62828"))
                binding.tvEstadoVenta.setBackgroundColor(Color.parseColor("#FFEBEE"))
            } else {
                binding.tvEstadoVenta.setTextColor(Color.parseColor("#2E7D32"))
                binding.tvEstadoVenta.setBackgroundColor(Color.parseColor("#E8F5E9"))
            }

            binding.btnVerDetalle.setOnClickListener { onVerDetalleClick(venta) }
        }
    }
}
