package com.example.tracking.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracking.data.repository.LocationRepository
import com.example.tracking.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    repository: LocationRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    val pendingLogs = repository.pendingCount
    var isOnline by mutableStateOf(true)
        private set

    init {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline = it }
        }
    }
}