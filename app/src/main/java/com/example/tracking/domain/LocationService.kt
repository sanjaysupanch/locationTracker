package com.example.tracking.domain

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit
import com.example.tracking.data.local.LocationEntity
import com.example.tracking.data.repository.LocationRepository
import com.example.tracking.worker.SyncWorker
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Foreground service for continuous location tracking.
 *
 * Collects high-accuracy location updates, stores them locally
 * to ensure zero data loss, and triggers background sync using
 * WorkManager when network is available.
 *
 * @author Sanjay Kumar
 */
@AndroidEntryPoint
class LocationService : Service() {

    @Inject lateinit var repository: LocationRepository
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }
    private val channelId = "tracking_channel"
    private val notificationId = 1

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
        startForeground(notificationId, createNotification())
        requestLocationUpdates()
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.forEach { loc ->
                serviceScope.launch {
                    repository.saveLocation(
                        LocationEntity(
                            latitude = loc.latitude,
                            longitude = loc.longitude,
                            accuracy = loc.accuracy,
                            speed = loc.speed,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    updateNotification(loc.latitude, loc.longitude)
                    triggerSync()
                }
            }
        }
    }

    private fun triggerSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        WorkManager.getInstance(this).enqueueUniqueWork("SyncWork", ExistingWorkPolicy.KEEP, request)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Location tracking service"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Kredily Tracking")
            .setContentText("Starting location tracking...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .build()
    }

    @SuppressLint("DefaultLocale")
    private fun updateNotification(latitude: Double, longitude: Double) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Kredily Tracking")
            .setContentText("Current Location - Lat: ${String.format("%.6f", latitude)}, Lng: ${String.format("%.6f", longitude)}")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}