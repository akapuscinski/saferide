package com.kapuscinski.saferide.domain.entity;

/**
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */
public class AccelerometerEvent {

    private float xAcceleration;
    private float yAcceleration;
    private float zAcceleration;
    private long nanos;
    private int accuracy;

    public AccelerometerEvent() {
    }

    public AccelerometerEvent(float xAcceleration, float yAcceleration, float zAcceleration, long nanos, int accuracy) {
        this.xAcceleration = xAcceleration;
        this.yAcceleration = yAcceleration;
        this.zAcceleration = zAcceleration;
        this.nanos = nanos;
        this.accuracy = accuracy;
    }

    public AccelerometerEvent(float[] accValues, long nanos, int accuracy) {
        this.xAcceleration = accValues[0];
        this.yAcceleration = accValues[1];
        this.zAcceleration = accValues[2];
        this.nanos = nanos;
        this.accuracy = accuracy;
    }

    public void setAccValues(float[] accValues){
        this.xAcceleration = accValues[0];
        this.yAcceleration = accValues[1];
        this.zAcceleration = accValues[2];
    }

    public float[] getAccValues(){
        return new float[]{getXAcceleration(), getYAcceleration(), getZAcceleration()};
    }

    public float getXAcceleration() {
        return xAcceleration;
    }

    public void setXAcceleration(float xAcceleration) {
        this.xAcceleration = xAcceleration;
    }

    public float getYAcceleration() {
        return yAcceleration;
    }

    public void setYAcceleration(float yAcceleration) {
        this.yAcceleration = yAcceleration;
    }

    public float getZAcceleration() {
        return zAcceleration;
    }

    public void setZAcceleration(float zAcceleration) {
        this.zAcceleration = zAcceleration;
    }

    public long getNanos() {
        return nanos;
    }

    public void setNanos(long nanos) {
        this.nanos = nanos;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccelerometerEvent event = (AccelerometerEvent) o;

        if (Float.compare(event.xAcceleration, xAcceleration) != 0) return false;
        if (Float.compare(event.yAcceleration, yAcceleration) != 0) return false;
        if (Float.compare(event.zAcceleration, zAcceleration) != 0) return false;
        if (nanos != event.nanos) return false;
        return accuracy == event.accuracy;

    }

    @Override
    public int hashCode() {
        int result = (xAcceleration != +0.0f ? Float.floatToIntBits(xAcceleration) : 0);
        result = 31 * result + (yAcceleration != +0.0f ? Float.floatToIntBits(yAcceleration) : 0);
        result = 31 * result + (zAcceleration != +0.0f ? Float.floatToIntBits(zAcceleration) : 0);
        result = 31 * result + (int) (nanos ^ (nanos >>> 32));
        result = 31 * result + accuracy;
        return result;
    }

    @Override
    public String toString() {
        return "AccelerometerEvent{" +
                "xAcceleration=" + xAcceleration +
                ", yAcceleration=" + yAcceleration +
                ", zAcceleration=" + zAcceleration +
                ", nanos=" + nanos +
                ", accuracy=" + accuracy +
                '}';
    }
}
