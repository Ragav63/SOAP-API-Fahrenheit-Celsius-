package com.ragav63.soapapi.presentation.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ragav63.soapapi.databinding.ActivityMainBinding
import com.ragav63.soapapi.presentation.viewmodel.TempViewModel
import com.ragav63.soapapi.presentation.viewmodel.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val vm: TempViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val etF =binding.etFahrenheit
        val btn = binding.btnConvert
        val tv = binding.tvResult

        lifecycleScope.launchWhenStarted {
            vm.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> tv.text = "Loading..."
                    is UiState.Success -> tv.text = "Result: ${state.result} °C"
                    is UiState.Error -> tv.text = "Error: ${state.message}"
                    else -> {}
                }
            }
        }

        btn.setOnClickListener {
            val value = etF.text.toString()
            if (value.isNotBlank()) vm.convert(value)
        }


    }
}
