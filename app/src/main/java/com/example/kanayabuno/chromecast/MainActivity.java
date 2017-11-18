package com.example.kanayabuno.chromecast;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteButton;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadOptions;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;

public class MainActivity extends AppCompatActivity {

    private MediaRouteButton mediaRouteButton;
    private SessionManager sessionManager;
    private CastSession castSession;
    //we added these
    private MediaMetadata movieMetadata;

    Handler handler = new Handler();


    //NOT SURE
    private MediaTrack mSelectedMedia;

    private final SessionManagerListener mSessionManagerListener =
            new SessionManagerListenerImpl();

    private final Runnable tenSecRunnable = new Runnable() {
        @Override
        public void run() {
            Log.v("MainActivity", "10 seconds has passed");
            handler.postDelayed(tenSecRunnable, 5000);
        }
    };



    private class SessionManagerListenerImpl implements SessionManagerListener<CastSession> {
        @Override
        public void onSessionStarted(CastSession session, String sessionId) {
            invalidateOptionsMenu();
            castSession = session;
            Log.v("MainActivity", "Session Started");
            MediaMetadata audioMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);

            audioMetadata.putString(MediaMetadata.KEY_TITLE, "Heartrate");
//        audioMetadata.putString(MediaMetadata.KEY_SUBTITLE, mSelectedMedia.getStudio());
//        audioMetadata.addImage(new WebImage(Uri.parse(mSelectedMedia.getImage(0))));
//        audioMetadata.addImage(new WebImage(Uri.parse(mSelectedMedia.getImage(1))));

            //Load Media
            MediaInfo mediaInfo = new MediaInfo.Builder("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
                    .setContentType("audio/mpeg")
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setMetadata(audioMetadata)
                    .build();
            RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
            remoteMediaClient.load(mediaInfo, new MediaLoadOptions.Builder().build());
        }
        @Override
        public void onSessionResumed(CastSession session, boolean wasSuspended) {
            invalidateOptionsMenu();
        }
        @Override
        public void onSessionSuspended(CastSession session, int i) {

        }
        @Override
        public void onSessionStarting(CastSession session) {

        }
        @Override
        public void onSessionStartFailed(CastSession session, int i) {

        }
        @Override
        public void onSessionEnding(CastSession session) {

        }
        @Override
        public void onSessionResuming(CastSession session, String s) {

        }
        @Override
        public void onSessionResumeFailed(CastSession session, int i) {

        }
        @Override
        public void onSessionEnded(CastSession session, int error) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CastContext castContext = CastContext.getSharedInstance(this);

        mediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mediaRouteButton);

        sessionManager = castContext.getSessionManager();
        castSession = sessionManager.getCurrentCastSession();

        handler.postDelayed(tenSecRunnable, 5000);
    }

    @Override
    protected void onResume() {
        castSession = sessionManager.getCurrentCastSession();
        sessionManager.addSessionManagerListener(mSessionManagerListener);
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        sessionManager.removeSessionManagerListener(mSessionManagerListener);
        castSession = null;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);
        return true;
    }
}
