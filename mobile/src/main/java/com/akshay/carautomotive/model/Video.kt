package com.akshay.carautomotive.model

import android.net.Uri

data class Video(val id: String, val title: String, val duration: Long,
                 val folderName: String, val size: String, val path: String, val artUri: Uri)

data class Folder(val id: String, val folderName: String)

data class Audio(val id: String, val title: String, val duration: Long,
                 val folderName: String, val size: String, val path: String, val artUri: Uri)