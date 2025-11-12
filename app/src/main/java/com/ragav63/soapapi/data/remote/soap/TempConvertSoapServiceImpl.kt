package com.ragav63.soapapi.data.remote.soap

import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE

class TempConvertSoapServiceImpl {
    private val namespace = "https://www.w3schools.com/xml/"
    private val url = "https://www.w3schools.com/xml/tempconvert.asmx"
    private val methodName = "FahrenheitToCelsius"
    private val soapAction = "https://www.w3schools.com/xml/FahrenheitToCelsius"

    fun convertFahrenheitToCelsius(fahrenheit: String): String {
        val request = SoapObject(namespace, methodName)
        request.addProperty("Fahrenheit", fahrenheit)

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER12)
        envelope.dotNet = true
        envelope.setOutputSoapObject(request)

        val transport = HttpTransportSE(url)
        transport.call(soapAction, envelope)

        val response = envelope.response
        return response.toString()
    }
}