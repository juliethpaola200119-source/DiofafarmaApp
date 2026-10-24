package com.diofafarma.ui.auth

import androidx.lifecycle.*
import com.diofafarma.data.model.LoginResponse
import com.diofafarma.data.repository.AuthRepository
import com.diofafarma.util.TokenManager
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(usuario: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(usuario, password)
                response.data?.token?.let { token ->
                    TokenManager.saveToken(token)
                }
                _loginResult.value = Result.success(response)
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }
}
