package com.ragav63.soapapi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ragav63.soapapi.domain.usecase.ConvertFahrenheitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TempViewModel @Inject constructor(
    private val useCase: ConvertFahrenheitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState: StateFlow<UiState> = _uiState

    fun convert(fahrenheit: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = useCase(fahrenheit)
                _uiState.value = UiState.Success(result.celsiusValue)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error occurred")
            }
        }
    }
}