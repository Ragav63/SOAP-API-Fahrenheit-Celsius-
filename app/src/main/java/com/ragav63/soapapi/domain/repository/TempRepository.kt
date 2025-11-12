package com.ragav63.soapapi.domain.repository

import com.ragav63.soapapi.domain.model.TemperatureResult

interface TempRepository {
    suspend fun convertFahrenheit(fahrenheit: String): TemperatureResult
}