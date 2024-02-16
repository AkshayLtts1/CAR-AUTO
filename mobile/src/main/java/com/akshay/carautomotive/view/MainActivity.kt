package com.akshay.carautomotive.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.akshay.carautomotive.R
import com.akshay.carautomotive.databinding.ActivityMainBinding
import com.akshay.carautomotive.viewModel.ViewPagerAdapter
import com.akshay.carautomotive.fragment.AudioFragment
import com.akshay.carautomotive.fragment.VideoFragment
import com.akshay.carautomotive.model.Audio
import com.akshay.carautomotive.model.Folder
import com.akshay.carautomotive.model.Video
import com.google.android.material.tabs.TabLayout
import java.io.File
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager // creating object of ViewPager
    private lateinit var tab: TabLayout
    private lateinit var bar: Toolbar
    private lateinit var binding: ActivityMainBinding


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set the references of the declared objects above
        pager = findViewById(R.id.viewPager)
        tab = findViewById(R.id.tabs)
        bar = findViewById(R.id.toolbar)
        tab.setupWithViewPager(pager)

        // To make our toolbar show the application
        // we need to give it to the ActionBar
        setSupportActionBar(bar)
        // Initializing the ViewPagerAdapter
        val adapter = ViewPagerAdapter(supportFragmentManager)
        // add fragment to the list
        adapter.addFragment(AudioFragment(), getString(R.string.audio))
        adapter.addFragment(VideoFragment(), getString(R.string.video))
        // Adding the Adapter to the ViewPager
        pager.adapter = adapter
        // bind the viewPager with the TabLayout.
        tab.setupWithViewPager(pager)

        if (checkPermission()){
            folderList = ArrayList()
            videoList = getAllVideos()
            audioList = getAllAudios()
        }
        else{
            // Permission is not granted, request it from the user
            requestPermission()
        }
    }

    // Function to check if the permission is granted
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Function to request permission from the user
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            123
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                recreate()
            }
            else{
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // for get all Audio List
    @SuppressLint("Recycle", "Range", "SuspiciousIndentation")
    private fun getAllAudios(): ArrayList<Audio>{
        val tempList = ArrayList<Audio>()
        val tempFolderList = ArrayList<String>()
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.BUCKET_DISPLAY_NAME, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.BUCKET_ID)

        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC")

        if (cursor != null)
            if (cursor.moveToNext())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val folderC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME))
//                    val folderIdC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.BUCKET_ID))
                    val sizeC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)).toLong()

                    try {
                        val file = File(pathC)
                        val artUriC = Uri.fromFile(file)
                        val audio = Audio(title = titleC, id = idC, folderName = folderC, size = sizeC, path = pathC, duration = durationC, artUri = artUriC)

                        if (file.exists()) tempList.add(audio)
                        //for adding folders
                        if (!tempFolderList.contains(folderC)){
                            tempFolderList.add(folderC)
                            folderList.add(Folder(id = idC, folderName = folderC))
                        }
                    }catch (e: Exception){}
                }while (cursor.moveToNext())
        cursor?.close()
        return tempList
    }

    @SuppressLint("Recycle", "Range", "SuspiciousIndentation")
    fun getAllVideos(): ArrayList<Video>{
        val tempList = ArrayList<Video>()
        val tempFolderList = ArrayList<String>()
        val projection = arrayOf(MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.BUCKET_ID)

        val cursor = this.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null,
            MediaStore.Video.Media.DATE_ADDED + " DESC")

        if (cursor != null)
            if (cursor.moveToNext())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                    val folderC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                    val folderIdC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
                    val sizeC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                    val durationC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)).toLong()

                    try {
                        val file = File(pathC)
                        val artUriC = Uri.fromFile(file)
                        val video = Video(title = titleC, id = idC, folderName = folderC, size = sizeC, path = pathC, duration = durationC, artUri = artUriC)

                        if (file.exists()) tempList.add(video)
                        //for adding folders
                        if (!tempFolderList.contains(folderC)){
                            tempFolderList.add(folderC)
                            folderList.add(Folder(folderIdC,folderC))
                        }


                    }catch (e: Exception){}
                }while (cursor.moveToNext())
        cursor?.close()
        return tempList
    }

    companion object{
        var audioList: ArrayList<Audio> = ArrayList()
        var videoList: ArrayList<Video> = ArrayList()
        var folderList: ArrayList<Folder> = ArrayList()
    }
}