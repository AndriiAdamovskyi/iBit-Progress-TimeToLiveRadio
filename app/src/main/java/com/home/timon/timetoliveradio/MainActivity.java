package com.home.timon.timetoliveradio;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
import com.home.timon.timetoliveradio.fragments.FavoriteFragment;
import com.home.timon.timetoliveradio.fragments.MoreFragment;
import com.home.timon.timetoliveradio.fragments.OrderSongFragment;
import com.home.timon.timetoliveradio.fragments.ProgramFragment;
import com.home.timon.timetoliveradio.fragments.RadioFragment;

public class MainActivity extends AppCompatActivity {

    //region Vars
    //UI Elements
    private Button bt_play_pause;
    private PlayerView playerView;
    private Toolbar toolbar;
    private DrawerLayout mDrawer;

    //ExoPlayer
    ExoPlayer player;
    private long playbackPosition = 0;
    private int currentWindow = 0;
    private boolean playWhenReady = false;

    //Logic & etc
    private String TAG = MainActivity.class.getName();
    boolean paused = true;
    private DrawerLayout.DrawerListener drawerToggle;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: started.");

        //loading the default fragment
        loadFragment(new RadioFragment());

        //region Set the toolbar as ActionBar
        //Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24px);
        //actionBar.hide();
        //endregion

        //region NavigationDrawer

        //getting  navigation view and attaching the listener
        mDrawer = findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        // Find our drawer view
        NavigationView navigationView = findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(navigationView);

        //Setup drawer listener
        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Respond when the drawer's position changes
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // Respond when the drawer is opened
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Respond when the drawer is closed
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Respond when the drawer motion state changes
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

    //region ExoPlayer
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
    //endregion

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).createMediaSource(uri);
    }

    //region Drawer
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem item) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(item.getItemId()) {
            case R.id.action_program:
                fragmentClass = ProgramFragment.class;
                break;
            case R.id.action_order_song:
                fragmentClass = OrderSongFragment.class;
                break;
            case R.id.action_radio:
                fragmentClass = RadioFragment.class;
                break;
            case R.id.action_favorite:
                fragmentClass = FavoriteFragment.class;
                break;
            case R.id.action_more:
                fragmentClass = MoreFragment.class;
                break;
            default:
                fragmentClass = MoreFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();

    }
    //endregion

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

}
