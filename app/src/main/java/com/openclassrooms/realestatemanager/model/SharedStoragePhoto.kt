package com.openclassrooms.realestatemanager.model

import android.net.Uri

class SharedStoragePhoto(
    val id: Long,
    val name: String,
    val width: Int,
    val height: Int,
    val contentUri: Uri
)