package com.nrv.pipdemo.activity;

import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Rational;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nrv.pipdemo.model.VideoItem;
import com.nrv.pipdemo.widget.MovieView;
import com.nrv.pipdemo.R;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {

    /** Intent action for media controls from Picture-in-Picture mode. */
    private static final String ACTION_MEDIA_CONTROL = "media_control";

    /** Intent extra for media controls from Picture-in-Picture mode. */
    private static final String EXTRA_CONTROL_TYPE = "control_type";

    /** The request code for play action PendingIntent. */
    private static final int REQUEST_PLAY = 1;

    /** The request code for pause action PendingIntent. */
    private static final int REQUEST_PAUSE = 2;

    /** The intent extra value for play action. */
    private static final int CONTROL_TYPE_PLAY = 1;

    /** The intent extra value for pause action. */
    private static final int CONTROL_TYPE_PAUSE = 2;

    /** The arguments to be used for Picture-in-Picture mode. */
    private final PictureInPictureParams.Builder mPictureInPictureParamsBuilder = new PictureInPictureParams.Builder();

    private String mPlay;
    private String mPause;

    private MovieView mMovieView;
    private Button pip;
    private TextView title;

    private BroadcastReceiver mReceiver;

    private VideoItem videoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        mPlay = getString(R.string.play);
        mPause = getString(R.string.pause);

        title = findViewById(R.id.title);

        if(savedInstanceState == null){
            videoItem = (VideoItem) getIntent().getSerializableExtra("videoItem");
        }

        mMovieView = findViewById(R.id.movie);
        if(videoItem != null){
            mMovieView.setVideoResourceId(videoItem.getResourceID());
            mMovieView.setTitle(videoItem.getTitle());
            title.setText(videoItem.getTitle());
        }

        mMovieView.setMovieListener(mMovieListener);
        pip = findViewById(R.id.pip);
        pip.setOnClickListener(mOnClickListener);

    }

    private final View.OnClickListener mOnClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.pip:
                            minimize();
                            break;
                    }
                }
            };

    /** Callbacks from the {@link MovieView} showing the video playback. */
    private MovieView.MovieListener mMovieListener =
            new MovieView.MovieListener() {

                @Override
                public void onMovieStarted() {
                    // We are playing the video now. In PiP mode, we want to show an action item to pause the video.
                    updatePictureInPictureActions(R.drawable.ic_pause_24dp, mPause, CONTROL_TYPE_PAUSE, REQUEST_PAUSE);
                }

                @Override
                public void onMovieStopped() {
                    // The video stopped or reached its end. In PiP mode, we want to show an action item to play the video.
                    updatePictureInPictureActions(R.drawable.ic_play_arrow_24dp, mPlay, CONTROL_TYPE_PLAY, REQUEST_PLAY);
                }

                @Override
                public void onMovieMinimized() {
                    // The MovieView wants us to minimize it. We enter Picture-in-Picture mode now.
                    minimize();
                }
            };

    /**
     * Update the state of pause/resume action item in Picture-in-Picture mode.
     *
     * @param iconId The icon to be used.
     * @param title The title text.
     * @param controlType The type of the action. either {@link #CONTROL_TYPE_PLAY} or {@link #CONTROL_TYPE_PAUSE}.
     * @param requestCode The request code for the {@link PendingIntent}.
     */
    void updatePictureInPictureActions(@DrawableRes int iconId, String title, int controlType, int requestCode) {
        final ArrayList<RemoteAction> actions = new ArrayList<>();

        final PendingIntent intent =
                PendingIntent.getBroadcast(
                        VideoActivity.this,
                        requestCode,
                        new Intent(ACTION_MEDIA_CONTROL).putExtra(EXTRA_CONTROL_TYPE, controlType),
                        0);
        final Icon icon = Icon.createWithResource(VideoActivity.this, iconId);
        actions.add(new RemoteAction(icon, title, title, intent));
        mPictureInPictureParamsBuilder.setActions(actions);
        setPictureInPictureParams(mPictureInPictureParamsBuilder.build());
    }

    /** Enters Picture-in-Picture mode. */
    void minimize() {
        if (mMovieView == null) {
            return;
        }
        // Hide the controls in picture-in-picture mode.
        mMovieView.hideControls();
        // Calculate the aspect ratio of the PiP screen.
        Rational aspectRatio = new Rational(mMovieView.getWidth(), mMovieView.getHeight());
        mPictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
        enterPictureInPictureMode(mPictureInPictureParamsBuilder.build());
    }

    @Override
    protected void onStop() {
        mMovieView.pause();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isInPictureInPictureMode()) {
            mMovieView.showControls();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adjustFullScreen(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            adjustFullScreen(getResources().getConfiguration());
        }
    }

    private void adjustFullScreen(Configuration config) {
        final View decorView = getWindow().getDecorView();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            pip.setVisibility(View.GONE);
            mMovieView.setAdjustViewBounds(false);
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            pip.setVisibility(View.VISIBLE);
            mMovieView.setAdjustViewBounds(true);
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, configuration);
        if (isInPictureInPictureMode) {
            // Starts receiving events from action items in PiP mode.
            mReceiver =
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (intent == null || !ACTION_MEDIA_CONTROL.equals(intent.getAction())) {
                                return;
                            }

                            // This is where we are called back from Picture-in-Picture action items.
                            final int controlType = intent.getIntExtra(EXTRA_CONTROL_TYPE, 0);
                            switch (controlType) {
                                case CONTROL_TYPE_PLAY:
                                    mMovieView.play();
                                    break;
                                case CONTROL_TYPE_PAUSE:
                                    mMovieView.pause();
                                    break;
                            }
                        }
                    };
            registerReceiver(mReceiver, new IntentFilter(ACTION_MEDIA_CONTROL));
        } else {
            // We are out of PiP mode. We can stop receiving events from it.
            unregisterReceiver(mReceiver);
            mReceiver = null;
            // Show the video controls if the video is not playing
            if (mMovieView != null && !mMovieView.isPlaying()) {
                mMovieView.showControls();
            }
        }
    }

    @Override
    public void onUserLeaveHint () {
        if (!isInPictureInPictureMode() && mMovieView != null && mMovieView.isPlaying()) {
            minimize();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isInPictureInPictureMode() && mMovieView != null && mMovieView.isPlaying()) {
            minimize();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        videoItem = (VideoItem) getIntent().getSerializableExtra("videoItem");
        if(videoItem != null){
            mMovieView.pause();
            mMovieView.setVideoResourceId(videoItem.getResourceID());
            mMovieView.setTitle(videoItem.getTitle());
            mMovieView.startVideo();
        }

    }

}
