package com.example.startcoroutines

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class MainViewModel : ViewModel() {

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)
    private val childJob = Job()

    fun method() {
        val childJob1 = coroutineScope.launch {
            delay(3000)
            Log.d(LOG_TAG, "first coroutine finished")
            val subChildJob1 = coroutineScope.launch(childJob) {
                delay(2000)
                Log.d(LOG_TAG, "first subChild coroutine finished")
            }
            val subChildJob2 = coroutineScope.launch(childJob) {
                delay(3000)
                Log.d(LOG_TAG, "second subChild coroutine finished")
            }
            Log.d(LOG_TAG, childJob.children.contains(subChildJob1).toString())
            Log.d(LOG_TAG, childJob.children.contains(subChildJob2).toString())
        }

        val childJob2 = coroutineScope.launch {
            delay(2000)
            Log.d(LOG_TAG, "second coroutine finished")
            Log.d(LOG_TAG, "Parent job canceled: ${parentJob.isCancelled}")
        }
        Log.d(LOG_TAG, parentJob.children.contains(childJob1).toString())
        Log.d(LOG_TAG, parentJob.children.contains(childJob2).toString())
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

    companion object {
        private const val LOG_TAG = "MainViewModel"
    }
}
