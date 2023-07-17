package com.example.startcoroutines

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.startcoroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonDownload.setOnClickListener {

            binding.progressBar.isVisible = true
            binding.buttonDownload.isEnabled = false
            val jobCity = lifecycleScope.launch {
                val city = loadCity()
                binding.textViewCity.text = city
            }
            val jobTemperature = lifecycleScope.launch {
                val temperature = loadTemperature()
                binding.textViewTemperature.text = temperature.toString()
            }
            lifecycleScope.launch {
                jobCity.join()
                jobTemperature.join()
                binding.progressBar.isVisible = false
                binding.buttonDownload.isEnabled = true
            }
        }
    }

    // метода без использования короутин для понимая, как они работают под капотом
    private fun loadDataWithoutCoroutines(step: Int = 0, obj: Any? = null) {
        when (step) {
            0 -> {
                Log.d("test", "Load started: $this")
                binding.progressBar.isVisible = true
                binding.buttonDownload.isEnabled = false
                loadCityWithoutCoroutines {
                    loadDataWithoutCoroutines(1, it)
                }
            }

            1 -> {
                val city = obj as String
                binding.textViewCity.text = city
                loadTemperatureWithoutCoroutines(city) {
                    loadDataWithoutCoroutines(2, it)
                }
            }

            2 -> {
                val temp = obj as Int
                binding.textViewTemperature.text = temp.toString()
                binding.progressBar.isVisible = false
                binding.buttonDownload.isEnabled = true
                Log.d("test", "Load finished: $this")
            }
        }
    }

    private fun loadCityWithoutCoroutines(callback: (String) -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            callback.invoke("Moscow")
        }, 5000)
    }

    private fun loadTemperatureWithoutCoroutines(city: String, callback: (Int) -> Unit) {
        Toast.makeText(
            this@MainActivity,
            "Загрузка погоды в городе: $city",
            Toast.LENGTH_SHORT
        ).show()
        Handler(Looper.getMainLooper()).postDelayed({
            callback.invoke(20)
        }, 5000)
    }


    private suspend fun loadData() {
        // 0
        Log.d("test", "Load started: $this")
        binding.progressBar.isVisible = true
        binding.buttonDownload.isEnabled = false
        val city = loadCity()
        // 1
        binding.textViewCity.text = city
        val temp = loadTemperature()
        // 2
        binding.textViewTemperature.text = temp.toString()
        binding.progressBar.isVisible = false
        binding.buttonDownload.isEnabled = true
        Log.d("test", "Load finished: $this")
    }

    private suspend fun loadCity(): String {
        delay(1000)
        return "Moscow"
    }

    private suspend fun loadTemperature(): Int {
        delay(5000)
        return 20
    }
}

