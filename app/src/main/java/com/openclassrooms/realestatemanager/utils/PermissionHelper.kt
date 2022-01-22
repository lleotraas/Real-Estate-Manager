package com.openclassrooms.realestatemanager.utils

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class PermissionHelper(private val activity: FragmentActivity) {

    private val REQUEST_CODE = 600

    private fun isPermissionsAllowed(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity , permission) == PackageManager.PERMISSION_GRANTED
    }

    fun askForPermissions(permission: String): Boolean {
        if (!isPermissionsAllowed(permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity as Activity, permission
                )) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(activity as Activity,arrayOf(permission),REQUEST_CODE)
            }
            return false
        }
        return true
    }



    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(activity)
                .setTitle("Permission Denied")
                .setMessage("Permission is denied, Please allow permissions from App Settings.")
                .setPositiveButton("App Settings",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                // send to app settings if permission is denied permanently
                val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity.getPackageName(), null)
            intent.data = uri
            activity.startActivity(intent)
        })
            .setNegativeButton("Cancel",null)
                .show()
    }
}