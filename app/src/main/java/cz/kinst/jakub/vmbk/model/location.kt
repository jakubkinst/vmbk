package cz.kinst.jakub.vmbk.model

import android.Manifest
import android.arch.lifecycle.LiveData
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.support.v4.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class LocationLiveData(context: Context) : LiveData<Location>() {
    private var isTracking = false
    private val context = context.applicationContext
    private val client = LocationServices.getFusedLocationProviderClient(this.context)
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            value = result.lastLocation
        }
    }

    override fun onActive() {
        val locationRequest = LocationRequest.create()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            isTracking = true
        }
    }

    fun onPermissionsChanged() {
        if (hasActiveObservers() && !isTracking)
            onActive()
    }

    override fun onInactive() {
        client.removeLocationUpdates(locationCallback)
        isTracking = false
    }
}
