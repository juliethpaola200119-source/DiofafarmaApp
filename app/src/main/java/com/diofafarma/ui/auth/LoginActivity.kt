package com.diofafarma.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.diofafarma.databinding.ActivityLoginBinding
import com.diofafarma.data.repository.AuthRepository
import com.diofafarma.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, LoginViewModelFactory(AuthRepository()))[LoginViewModel::class.java]

        binding.btnLogin.setOnClickListener { attemptLogin() }
        observeViewModel()
    }

    private fun attemptLogin() {
        val usuario = binding.etUsuario.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.login(usuario, password)
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { response ->
                val nombre = response.data?.usuario ?: "Usuario"
                Toast.makeText(this, "Bienvenido, $nombre", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.onFailure { e ->
                Toast.makeText(this, e.message ?: "Error en la autenticación", Toast.LENGTH_LONG).show()
            }
        }
    }
}
