package com.akshay.carautomotive.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.akshay.carautomotive.R
import com.akshay.carautomotive.databinding.ActivityPlayerBinding
import com.akshay.carautomotive.model.Audio
import com.akshay.carautomotive.model.Folder
import com.akshay.carautomotive.model.Video
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.MediaPlayer.Event
import org.videolan.libvlc.util.VLCVideoLayout


class PlayerActivity : AppCompatActivity() {
    private val audioList: ArrayList<Audio> = ArrayList()
    private val videoList: ArrayList<Video> = ArrayList()
    private lateinit var binding: ActivityPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeLayout()
    }

    private fun initializeLayout(){

        when(intent.getStringExtra("class")){
            "AllVideos" -> {
                videoList.addAll(MainActivity.videoList)
                createVideoPlayerLib()
            }

            "AllAudios" -> {
                audioList.addAll(MainActivity.audioList)
                createAudioPlayerLib()
            }

            "FolderActivity" -> {
                AllPlayerList = ArrayList()
                AllPlayerList.addAll(MainActivity.folderList)
            }
        }
        //Add Custom Control(play, pause, forward, backward)
        setUpCustomControls()
    }

    private fun createAudioPlayerLib(){
        binding.videoTitle.text = audioList[position].title
        binding.videoTitle.isSelected = true
        libVLC = LibVLC(this)
        mediaPlayer = MediaPlayer(libVLC)
        vlcVideoLayout = findViewById(R.id.libPlayerView)
        vlcVideoLayout.visibility = View.VISIBLE
        val mediaPath = audioList[position].artUri
        val media = Media(libVLC, Uri.parse(mediaPath.toString()))
        mediaPlayer.media = media
        mediaPlayer.play()

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(mediaPath.toString())
        val albumArtBytes = retriever.embeddedPicture
        val albumArtBitmap = albumArtBytes?.let { BitmapFactory.decodeByteArray(albumArtBytes,0, it.size) }
        albumArtImageView= findViewById(R.id.albumArtImageView)
        albumArtImageView.setImageBitmap(albumArtBitmap)
        albumArtImageView.visibility = View.VISIBLE

        mediaPlayer.attachViews(vlcVideoLayout, null, false, false)

        mediaPlayer.setEventListener { event ->
            when (event?.type) {
                Event.EndReached -> {
                    mediaPlayer.stop()
                    playNextAudio()
                }
            }
        }
    }

    private fun createVideoPlayerLib(){
        binding.videoTitle.text = videoList[position].title
        binding.videoTitle.isSelected = true
        libVLC = LibVLC(this)
        mediaPlayer = MediaPlayer(libVLC)
        vlcVideoLayout = findViewById(R.id.libPlayerView)
        val mediaPath = videoList[position].artUri
        val media = Media(libVLC, Uri.parse(mediaPath.toString()))
        mediaPlayer.media = media
        mediaPlayer.attachViews(vlcVideoLayout, null, false, false)
        mediaPlayer.play()

        mediaPlayer.setEventListener { event ->
            when (event?.type) {
                Event.EndReached -> {
                    mediaPlayer.stop()
                    playNextVideo()
                }
            }
        }
    }

    private fun setUpCustomControls(){
        val currentMedia = getCurrentMedia()

        val playButton: ImageButton = findViewById(R.id.playPauseButton)
        val nextButton: ImageButton = findViewById(R.id.nextButton)
        val previousButton: ImageButton = findViewById(R.id.previousButton)
        val fastForwardButton: ImageButton = findViewById(R.id.fastForwardButton)
        val fastRewindButton: ImageButton = findViewById(R.id.fastRewindButton)
        val seekBar: SeekBar = findViewById(R.id.seekBar)
        val currentTime: TextView = findViewById(R.id.currentTimeTv)
        val totalTime: TextView = findViewById(R.id.totalTimeTv)
        val playBackSpeed: ImageButton = findViewById(R.id.playBackSpeed)
        val backBtn : ImageButton = findViewById(R.id.backBtn)

        var buttonVisible = true
        var touchStartTime = 0L

        //set up touch listener
        vlcVideoLayout.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN ->{
                    touchStartTime = System.currentTimeMillis()
                }
                MotionEvent.ACTION_UP ->{
                    val touchDuration = System.currentTimeMillis() - touchStartTime

                    if (touchDuration<3000){
                        buttonVisible = !buttonVisible

                            //Update Visibility of Buttons
                        playButton.visibility = if (buttonVisible) View.VISIBLE else View.INVISIBLE
                        nextButton.visibility = if (buttonVisible) View.VISIBLE else View.INVISIBLE
                        previousButton.visibility = if (buttonVisible) View.VISIBLE else View.INVISIBLE
                        fastForwardButton.visibility = if (buttonVisible) View.VISIBLE else View.INVISIBLE
                        fastRewindButton.visibility = if (buttonVisible) View.VISIBLE else View.INVISIBLE
                        seekBar.visibility = if (buttonVisible) View.VISIBLE else View.INVISIBLE
                        currentTime.visibility = if (buttonVisible) View.VISIBLE else View.INVISIBLE
                        totalTime.visibility = if (buttonVisible) View.VISIBLE else View.INVISIBLE
                    }
                }
            }
            true
        }

        playButton.setOnClickListener{
            if (!mediaPlayer.isPlaying){
                mediaPlayer.play()
                playButton.setImageResource(R.drawable.pause)
                Handler().postDelayed({
                    playButton.visibility = View.INVISIBLE
                    nextButton.visibility = View.INVISIBLE
                    previousButton.visibility = View.INVISIBLE
                    fastForwardButton.visibility = View.INVISIBLE
                    fastRewindButton.visibility = View.INVISIBLE
                    seekBar.visibility = View.INVISIBLE
                    currentTime.visibility = View.INVISIBLE
                    totalTime.visibility = View.INVISIBLE
                }, 3000)
            }
            else{
                mediaPlayer.pause()
                playButton.setImageResource(R.drawable.play_icon)
            }
        }
        nextButton.setOnClickListener {
            if (position >= audioList.size + videoList.size){
                position = 0
            }
            currentMedia.let {
                if (it is Audio){
                    mediaPlayer.stop()
                    playNextAudio()
                    if (mediaPlayer.isPlaying){
                        mediaPlayer.pause()
                        playButton.setImageResource(R.drawable.play_icon)
                    }
                    else{
                        mediaPlayer.play()
                        playButton.setImageResource(R.drawable.pause)
                    }
                }
                else if (it is Video){
                    mediaPlayer.stop()
                    playNextVideo()
                    if (mediaPlayer.isPlaying){
                        mediaPlayer.pause()
                        playButton.setImageResource(R.drawable.play_icon)
                    }
                    else{
                        mediaPlayer.play()
                        playButton.setImageResource(R.drawable.pause)
                    }
                }
            }
        }
        previousButton.setOnClickListener {
            if (position > 0){
                position -= 1
            }
            currentMedia.let {
                if (it is Audio){
                    mediaPlayer.stop()
                    playPreviousAudio()
                }
                else if (it is Video){
                    mediaPlayer.stop()
                    playPreviousVideo()
                }
            }
        }
        // move fast forward
        fastForwardButton.setOnClickListener {
            mediaPlayer.time += 10000
        }
        // fast rewind
        fastRewindButton.setOnClickListener {
            mediaPlayer.time -= 10000
        }

        val handler = Handler(Looper.getMainLooper())
        val updateSeekBar = object : Runnable{
            override fun run() {
                val currentPosition = mediaPlayer.time
                val totalDuration = mediaPlayer.media?.duration?:0
                val progress  = (currentPosition.toFloat() / totalDuration!! * 100).toInt()
                seekBar.progress = progress
                val durationString = convertToMMSS(totalDuration)
                val currentPositionString = convertToMMSS(currentPosition)
//                val durationText = "$currentPositionString / $durationString"
                currentTime.text = currentPositionString
                totalTime.text = durationString
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(updateSeekBar, 1000)
        seekBar?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    seekBar?.let {
                        mediaPlayer.time = calculate(it.progress)
                        mediaPlayer.play()
                    }
                    handler.postDelayed(updateSeekBar, 1000)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer.pause()
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer.play()

            }
        })

        playBackSpeed.setOnClickListener {
            val speeds = arrayOf(1.0f, 2.0f, 4.0f, 8.0f, 16.0f, 32.0f)

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Plack Speed")
            builder.setItems(speeds.map { it.toString() }.toTypedArray()){ _, which ->
                val selectedSpeed = speeds[which]
                mediaPlayer.rate = selectedSpeed
            }
            builder.show()
        }

        backBtn.setOnClickListener {
//            onBackPressed()
//            moveTaskToBack(true)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun convertToMMSS(duration: Long): String {
        val seconds = duration / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
    }

    private fun calculate(progress: Int): Long{
        val newPos = progress.toFloat()/100 * mediaPlayer.media?.duration!!
        return newPos.toLong()
    }


    private fun getCurrentMedia(): Any? {
        return if (position < audioList.size) {
            audioList[position]
        } else if (position < audioList.size + videoList.size) {
            videoList[position - audioList.size]
        } else {
            null
        }
    }
    private fun playNextAudio() {
        if (position < audioList.size - 1) {
            position++
            createAudioPlayerLib()
        } else {
            // All audio have been played
            Toast.makeText(this, "Msg for next audio button for change the song",Toast.LENGTH_SHORT).show()
        }
    }

    private fun playPreviousAudio() {
        if (position >= 0) {
            createAudioPlayerLib()
        }
        else {
            // All audio have been played
            Toast.makeText(this, "MSg for previous audio button for change the song",Toast.LENGTH_SHORT).show()
        }
    }

    private fun playNextVideo(){
        if (position < videoList.size - 1) {
            position++
            createVideoPlayerLib()
        } else {
            // All audio have been played
            Toast.makeText(this, "MSg for forward video button for change the song",Toast.LENGTH_SHORT).show()
        }
    }

    private fun playPreviousVideo() {
        if (position >= 0) {
            createVideoPlayerLib()
        }
        else {
            // All audio have been played
            Toast.makeText(this, "MSg for previous audio button for change the song",Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        libVLC.release()
        mediaPlayer.stop()
    }

    companion object{
        lateinit var playPauseButton: ImageButton
        lateinit var AllPlayerList: ArrayList<Folder>
        lateinit var libVLC: LibVLC
        lateinit var vlcVideoLayout: VLCVideoLayout
        lateinit var mediaPlayer: MediaPlayer
        lateinit var albumArtImageView: ImageView
        var position: Int = 0
    }
}