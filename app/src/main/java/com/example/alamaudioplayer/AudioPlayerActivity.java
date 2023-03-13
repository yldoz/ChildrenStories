package com.example.alamaudioplayer;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alamaudioplayer.databinding.ActivityAudioPlayerBinding;
import com.example.alamaudioplayer.model.Rows;

import java.io.IOException;

public class AudioPlayerActivity extends AppCompatActivity {
    private ActivityAudioPlayerBinding binding;
    private String AUDIO_URL = "https://ali1001.com/storage/app/storyaudio/";
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAudioPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Rows mainRow = getIntent().getParcelableExtra("songModel");
        binding.songName.setText(mainRow.getStoryName());
        binding.songLanguage.setText(mainRow.getLanguage());
        MediaPlayer(mainRow.getAudio());
        binding.mediaControlBtn.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                binding.mediaControlBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                pauseMediaPlayer();
            } else {
                binding.mediaControlBtn.setImageResource(R.drawable.ic_baseline_pause_24);
                restartMediaPlayer();
            }
        });
    }

    void MediaPlayer(String AudioName) {
        String url = AUDIO_URL + AudioName; // your URL here
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        Handler mHandler = new Handler();
//Make sure you update Seekbar on UI thread
        AudioPlayerActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    binding.seekbar.setProgress(mCurrentPosition);
                    binding.tvTimeStart.setText(String.valueOf(mediaPlayer.getCurrentPosition()/1000));
                }
                mHandler.postDelayed(this, 1000);
            }
        });
        binding.tvTotalDuration.setText(String.valueOf(mediaPlayer.getDuration()/1000));
    }

    void pauseMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

    void restartMediaPlayer() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
        }
        super.onDestroy();
    }
}