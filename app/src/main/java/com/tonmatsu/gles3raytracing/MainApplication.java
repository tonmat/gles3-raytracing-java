package com.tonmatsu.gles3raytracing;

import android.app.*;

import com.tonmatsu.gles3raytracing.utils.*;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AssetUtils.initialize(this);
    }
}
