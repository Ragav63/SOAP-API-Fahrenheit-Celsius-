package com.ragav63.soapapi.data.repository

import com.ragav63.soapapi.data.remote.soap.TempConvertSoapServiceImpl
import com.ragav63.soapapi.domain.model.TemperatureResult
import com.ragav63.soapapi.domain.repository.TempRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TempRepositoryImpl(
    private val service: TempConvertSoapServiceImpl
) : TempRepository {
    override suspend fun convertFahrenheit(fahrenheit: String): TemperatureResult = withContext(Dispatchers.IO) {
        val celsius = service.convertFahrenheitToCelsius(fahrenheit)
        TemperatureResult(celsius)
    }
}