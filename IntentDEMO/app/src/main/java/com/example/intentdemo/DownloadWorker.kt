package com.example.intentdemo

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class DownloadWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("DownloadWorker", "Starting background download simulation...")
        
        // Simulate a long download process
        try {
            for (i in 1..5) {
                Thread.sleep(1000)
                Log.d("DownloadWorker", "Downloading... $i/5")
            }
        } catch (e: InterruptedException) {
            return Result.failure()
        }

        Log.d("DownloadWorker", "Download complete!")
        return Result.success()
    }
}
