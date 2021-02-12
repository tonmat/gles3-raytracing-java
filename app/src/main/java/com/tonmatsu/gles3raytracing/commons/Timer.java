package com.tonmatsu.gles3raytracing.commons;

public class Timer {
    private final Callback callback;
    private final float interval;
    private float time;

    public Timer(Callback callback, float interval) {
        this.callback = callback;
        this.interval = interval;
    }

    public void update(float delta) {
        int id = 0;
        time += delta;
        while (time >= interval) {
            callback.onTime(id++);
            time -= interval;
        }
    }

    public interface Callback {
        void onTime(int id);
    }
}
