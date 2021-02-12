package com.tonmatsu.gles3raytracing.commons;

public class Ticker {
    private final Metric deltaMetric = new Metric(60);
    public float delta;
    public float elapsedTime;
    private long lastTickTime;

    public void tick() {
        if (lastTickTime == 0) {
            lastTickTime = System.nanoTime();
            return;
        }

        final long currentTime = System.nanoTime();
        final long delta = currentTime - lastTickTime;
        lastTickTime = currentTime;

        this.delta = 0.000000001f * delta;
        this.elapsedTime += this.delta;
        deltaMetric.put(this.delta);
    }

    public float getAverageDelta() {
        return deltaMetric.getAverage();
    }
}
