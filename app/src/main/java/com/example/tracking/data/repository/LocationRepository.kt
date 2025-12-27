package com.example.tracking.data.repository

import android.util.Log
import com.example.tracking.data.local.LocationDao
import com.example.tracking.data.local.LocationEntity
import com.example.tracking.data.remote.ApiService
import com.example.tracking.data.remote.SyncLocationRequest
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val dao: LocationDao,
    private val api: ApiService
) {
    val pendingCount = dao.getPendingCount()

    suspend fun saveLocation(location: LocationEntity) = dao.insert(location)

    /**
     * Syncs all location logs that are not yet sent to the server.
     *
     * Logs are synced one by one in order. If any sync fails, the process
     * stops immediately so the remaining logs can be retried later.
     * Only logs that are confirmed by the server are marked as synced
     * in the database.
     *
     * @return true if all pending logs were synced successfully,
     *         false if syncing stopped early or failed.
     *
     * @author Sanjay Kumar
     */
    suspend fun syncUnsyncedLogs(): Boolean {
        val unsynced = dao.getUnsyncedLogs()
        if (unsynced.isEmpty()) return true

        return try {
            val syncedIds = mutableListOf<Int>()
            // Iterate sequentially (FIFO)
            for (log in unsynced) {
                val request = SyncLocationRequest(
                    employeeId = log.employeeId,
                    latitude = log.latitude,
                    longitude = log.longitude,
                    accuracy = log.accuracy,
                    timestamp = log.timestamp,
                    speed = log.speed
                )
                val response = api.syncLocation(request)
                if (response.isSuccessful) {
                    syncedIds.add(log.id)
                } else {
                    break
                }
            }
            if (syncedIds.isNotEmpty()) {
                dao.markAsSynced(syncedIds)
            }
            syncedIds.size == unsynced.size
        } catch (e: Exception) {
            Log.e("LocationRepository", "Error syncing logs", e)
            false
        }
    }
}