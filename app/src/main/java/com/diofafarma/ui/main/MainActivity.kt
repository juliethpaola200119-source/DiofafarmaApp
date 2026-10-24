package com.diofafarma.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.diofafarma.R
import com.diofafarma.databinding.ActivityMainBinding
import com.diofafarma.ui.auth.LoginActivity
import com.diofafarma.ui.client.ClientFragment
import com.diofafarma.ui.product.ProductFragment
import com.diofafarma.ui.report.ReportFragment
import com.diofafarma.ui.sale.SaleFragment
import com.diofafarma.ui.user.UserFragment
import com.diofafarma.util.TokenManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val productFragment = ProductFragment()
    private val clientFragment = ClientFragment()
    private val saleFragment = SaleFragment()
    private val reportFragment = ReportFragment()
    private val userFragment = UserFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Cargar vista inicial de Productos
        if (savedInstanceState == null) {
            cambiarFragment(productFragment, "Catálogo de Productos")
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_products -> {
                    cambiarFragment(productFragment, "Catálogo de Productos")
                    true
                }
                R.id.nav_clients -> {
                    cambiarFragment(clientFragment, "Gestión de Clientes")
                    true
                }
                R.id.nav_sales -> {
                    cambiarFragment(saleFragment, "Transacciones y Ventas")
                    true
                }
                R.id.nav_reports -> {
                    cambiarFragment(reportFragment, "Reportes y Estadísticas")
                    true
                }
                R.id.nav_users -> {
                    cambiarFragment(userFragment, "Gestión de Usuarios")
                    true
                }
                else -> false
            }
        }

        binding.btnLogout.setOnClickListener {
            confirmarCierreSesion()
        }
    }

    private fun cambiarFragment(fragment: Fragment, titulo: String) {
        binding.toolbar.title = titulo
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun confirmarCierreSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas salir del sistema?")
            .setPositiveButton("Salir") { _, _ ->
                TokenManager.clear()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
