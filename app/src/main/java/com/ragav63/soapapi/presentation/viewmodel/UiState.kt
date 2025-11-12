package com.ragav63.soapapi.presentation.viewmodel

sealed class UiState {
    object Empty : UiState()
    object Loading : UiState()
    data class Success(val result: String) : UiState()
    data class Error(val message: String) : UiState()
}
