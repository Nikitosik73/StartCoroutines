package com.example.startcoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.startcoroutines.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonDownload.setOnClickListener {
            loadData()
        }
    }

    private fun loadData() {
        Log.d("MainActivity", "Load started: $this")
        binding.progressBar.isVisible = true
        binding.buttonDownload.isEnabled = false
        loadCity { city ->
            binding.textViewCity.text = city
            loadTemperature(city) { temp ->
                binding.textViewTemperature.text = temp.toString()
                binding.progressBar.isVisible = false
                binding.buttonDownload.isEnabled = true
                Log.d("MainThread", "Load finished: $this")
            }
        }
    }

    private fun loadCity(callback: (String) -> Unit) {
        thread {
            Thread.sleep(5000)
            runOnUiThread {
                callback.invoke("Moscow")
            }
        }
    }

    private fun loadTemperature(city: String, callback: (Int) -> Unit) {
        thread {
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "Загрузка погоды в городе: $city",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Thread.sleep(5000)
            runOnUiThread {
                callback.invoke(20)
            }
        }
    }
}