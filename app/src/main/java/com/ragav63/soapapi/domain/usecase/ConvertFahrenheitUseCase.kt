package com.ragav63.soapapi.domain.usecase

import com.ragav63.soapapi.domain.model.TemperatureResult
import com.ragav63.soapapi.domain.repository.TempRepository
import javax.inject.Inject

class ConvertFahrenheitUseCase @Inject constructor(
    private val repository: TempRepository
) {
    suspend operator fun invoke(fahrenheit: String): TemperatureResult {
        return repository.convertFahrenheit(fahrenheit)
    }
}