package com.tonmatsu.gles3raytracing;

import android.app.*;
import android.os.*;
import android.view.*;

import java.util.*;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("GLES 3.1 Ray Tracing");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final SurfaceView surfaceView = new SurfaceView(this, this::handleAverageFPS);
        setContentView(surfaceView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        hide();
    }

    private void hide() {
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(0
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    private void handleAverageFPS(float averageFPS) {
        runOnUiThread(() -> {
            setTitle(String.format(Locale.US, "GLES 3.1 Ray Tracing  %5.1f FPS", averageFPS));
        });
    }
}
