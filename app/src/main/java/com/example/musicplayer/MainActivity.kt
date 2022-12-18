package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musicplayer.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory


class MainActivity : AppCompatActivity() {

    private var player: SimpleExoPlayer? = null
    private val songUrl: String = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"

    private var playbackPosition = 0L
    private var currentWindow = 0
    private var playWhenReady = true

    private val mediaReceiver = MediaReceiver()

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityMainBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializePlayer()
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainPcv.player = player
        binding.mainPcv.showTimeoutMs = 0

    }

    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this)
//            main_pcv.player = player
//            main_pcv.showTimeoutMs = 0
            val defaultHttpDataSourceFactory =
                DefaultHttpDataSourceFactory(getString(R.string.app_name))
            val mediaSource = ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory)
                .createMediaSource(Uri.parse(songUrl))
            player!!.prepare(mediaSource)
            player!!.seekTo(currentWindow, playbackPosition)
            player!!.playWhenReady = playWhenReady
            setAudioFocus()
        }
    }

    private fun releasePlayer() {
        player?.let {
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            playWhenReady = it.playWhenReady
            it.release()
            player = null
        }
    }

    private fun setAudioFocus() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val afChangeListener: AudioManager.OnAudioFocusChangeListener =
            AudioManager.OnAudioFocusChangeListener {
                when (it) {
                    AudioManager.AUDIOFOCUS_LOSS -> {
                        player?.playWhenReady = false
                    }
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                        player?.playWhenReady = false
                    }
                    AudioManager.AUDIOFOCUS_GAIN -> {
                        player?.playWhenReady = true

                    }
                }
            }
        val result: Int = audioManager.requestAudioFocus(
            afChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        player?.playWhenReady = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    inner class MediaReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                player?.playWhenReady = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initializePlayer()
        registerReceiver(mediaReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
    }

    override fun onRestart() {
        super.onRestart()
        initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
        unregisterReceiver(mediaReceiver)
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

}
