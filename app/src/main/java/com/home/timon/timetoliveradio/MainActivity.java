package com.home.timon.timetoliveradio;

import android.media.AsyncPlayer;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //region Vars
    //UI Elements
    private Button bt_play_pause;
    private PlayerView playerView;

    //Logic
    boolean paused = true;

    //ExoPlayer
    ExoPlayer player;
    private long playbackPosition = 0;
    private int currentWindow = 0;
    private boolean playWhenReady = false;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //region Button Play/Pause
        bt_play_pause = (Button) findViewById(R.id.bt_play_pause);
        bt_play_pause.setBackgroundResource(R.drawable.ic_media_play);
        bt_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_play_pause:
                        if (paused) {
                            bt_play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_24px);
                            startPlayer();
                            paused = false;
                        } else {
                            bt_play_pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24px);
                            paused = true;
                            pausePlayer();
                        }
                }
            }
        });
        //endregion

        //region Player
        playerView = findViewById(R.id.video_view);
        initializePlayer();
        //endregion
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
        if((Util.SDK_INT <= 23 || player == null)){
            initializePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());
        Uri uri = Uri.parse(getString(R.string.media_url_mp3));
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
        playerView.setPlayer(player);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getContentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    public void startPlayer(){
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    public void pausePlayer(){
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).createMediaSource(uri);
    }


}
