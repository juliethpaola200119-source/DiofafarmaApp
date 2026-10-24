package com.diofafarma.ui.product

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diofafarma.data.model.Product
import com.diofafarma.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private var productos: List<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    fun updateData(nuevosProductos: List<Product>) {
        this.productos = nuevosProductos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productos[position])
    }

    override fun getItemCount(): Int = productos.size

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Product) {
            binding.tvNombreProducto.text = producto.nombre
            val lab = if (producto.laboratorio.isNullOrEmpty()) "Sin laboratorio" else producto.laboratorio
            binding.tvCodigoLaboratorio.text = "Cód: ${producto.codigo} | Lab: $lab"
            binding.tvDescripcion.text = producto.descripcion ?: "Sin descripción"
            binding.tvPrecio.text = formatoMoneda.format(producto.precioVenta)

            binding.tvStock.text = "Stock: ${producto.stock}"
            if (producto.stock <= 10) {
                binding.tvStock.setTextColor(Color.parseColor("#C62828"))
                binding.tvStock.setBackgroundColor(Color.parseColor("#FFEBEE"))
            } else {
                binding.tvStock.setTextColor(Color.parseColor("#2E7D32"))
                binding.tvStock.setBackgroundColor(Color.parseColor("#E8F5E9"))
            }

            binding.btnEditar.setOnClickListener { onEditClick(producto) }
            binding.btnEliminar.setOnClickListener { onDeleteClick(producto) }
        }
    }
}
