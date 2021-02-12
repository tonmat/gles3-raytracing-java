package com.tonmatsu.gles3raytracing.commons;

public class Metric {
    private final float[] data;
    private int size;
    private int index;
    private boolean dirty;
    private float average;

    public Metric(int size) {
        this.data = new float[size];
    }

    public void put(float value) {
        if (size < data.length)
            size++;
        data[index] = value;
        index = ++index % data.length;
        dirty = true;
    }

    public float getAverage() {
        if (dirty) {
            average = 0;
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    average += data[i];
                }
                average /= size;
            }
            dirty = false;
        }
        return average;
    }
}
