package cz.kinst.jakub.vmbk

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat


class PermissionManager(val activity: FragmentActivity) {
    private var lastRequestId = 0
    private val permissionRequests = HashMap<Int, PermissionRequest>()
    fun requestPermission(permissionRequest: PermissionRequest) {
        val notGranted = ArrayList<PermissionRequest>()

        permissionRequest.permissions.forEach {
            if (checkPermission(it))
                permissionRequest.grantedCallback.invoke(listOf(it))
            else
                notGranted.add(permissionRequest)

        }


        val requestId = ++lastRequestId
        permissionRequests[requestId] = permissionRequest
        askForPermissions(permissionRequest.permissions, requestId)
    }

    fun onPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val permissionRequest = permissionRequests[requestCode]
        permissionRequest?.let {
            val granted = ArrayList<String>()
            val denied = ArrayList<String>()

            (0 until permissions.size).forEach {
                if (grantResults[it] == PackageManager.PERMISSION_GRANTED)
                    granted.add(permissions[it])
                else
                    denied.add(permissions[it])
            }
            if (granted.isNotEmpty())
                permissionRequest.grantedCallback.invoke(granted)
            if (denied.isNotEmpty())
                permissionRequest.deniedCallback.invoke(denied)

            permissionRequests.remove(requestCode)
        }
    }

    fun checkPermission(permission: String) = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED

    private fun askForPermissions(permissions: List<String>, requestId: Int) {
        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), requestId)
    }
}


open class PermissionRequest(
        val permissions: List<String>,
        val grantedCallback: (grantedPermissions: List<String>) -> Unit = {},
        val deniedCallback: (deniedPermissions: List<String>) -> Unit = {})

class SinglePermissionRequest(
        permission: String,
        grantedCallback: (grantedPermission: String) -> Unit = {},
        deniedCallback: (deniedPermission: String) -> Unit = {}) : PermissionRequest(listOf(permission), { grantedCallback(it[0]) }, { deniedCallback(it[0]) })