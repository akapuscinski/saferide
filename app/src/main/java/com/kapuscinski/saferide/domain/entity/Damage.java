package com.kapuscinski.saferide.domain.entity;

import com.google.gson.annotations.SerializedName;
import com.kapuscinski.saferide.domain.persistence.DamageDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */
@Table(database = DamageDatabase.class)
public class Damage extends BaseModel implements Serializable {

    public static final int SMALL_DAMAGE_VALUE = 1;
    public static final int MEDIUM_DAMAGE_VALUE = 2;
    public static final int BIG_DAMAGE_VALUE = 3;

    @Column
    private float xAcceleration;
    @Column
    private float yAcceleration;
    @Column
    private float zAcceleration;
    @Column
    private int damageValue;
    @PrimaryKey
    @SerializedName("lat")
    private double latitude;
    @PrimaryKey
    @SerializedName("lon")
    private double longitude;
    @Column
    private float accuracy;
    @Column
    private long timestamp;
    @Column
    private float speed;
    @Column
    private boolean synced; //true if damage was sent to server
    private int entriesCount;

    public Damage() {
    }

    public Damage(float xAcceleration, float yAcceleration, float zAcceleration,
                  int damageValue, double latitude, double longitude,
                  float accuracy, long timestamp, float speed) {
        this.xAcceleration = xAcceleration;
        this.yAcceleration = yAcceleration;
        this.zAcceleration = zAcceleration;
        this.damageValue = damageValue;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
        this.speed = speed;
    }

    public static Damage create(AccelerometerEvent event, Position position, int damageValue) {
        return new Damage(event.getXAcceleration(), event.getYAcceleration(), event.getZAcceleration(),
                damageValue, position.getLatitude(), position.getLongitude(),
                position.getAccuracy(), position.getTimestamp(), position.getSpeed());
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

    public void setZAcceleration(float zAcceleration){this.zAcceleration = zAcceleration;}

    public int getDamageValue() {
        return damageValue;
    }

    public void setDamageValue(int damageValue) {
        if (damageValue != SMALL_DAMAGE_VALUE && damageValue != MEDIUM_DAMAGE_VALUE && damageValue != BIG_DAMAGE_VALUE) {
            throw new IllegalArgumentException("damageValue must be equal to one of three " +
                    "constant values: SMALL_DAMAGE_VALUE, MEDIUM_DAMAGE_VALUE, BIG_DAMAGE_VALUE");
        }
        this.damageValue = damageValue;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public int getEntriesCount() {
        return entriesCount;
    }

    public void setEntriesCount(int entriesCount) {
        this.entriesCount = entriesCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Damage damage = (Damage) o;

        if (Float.compare(damage.xAcceleration, xAcceleration) != 0) return false;
        if (Float.compare(damage.yAcceleration, yAcceleration) != 0) return false;
        if (Float.compare(damage.zAcceleration, zAcceleration) != 0) return false;
        if (damageValue != damage.damageValue) return false;
        if (Double.compare(damage.latitude, latitude) != 0) return false;
        if (Double.compare(damage.longitude, longitude) != 0) return false;
        if (Float.compare(damage.accuracy, accuracy) != 0) return false;
        if (timestamp != damage.timestamp) return false;
        if (Float.compare(damage.speed, speed) != 0) return false;
        if (synced != damage.synced) return false;
        return entriesCount == damage.entriesCount;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (xAcceleration != +0.0f ? Float.floatToIntBits(xAcceleration) : 0);
        result = 31 * result + (yAcceleration != +0.0f ? Float.floatToIntBits(yAcceleration) : 0);
        result = 31 * result + (zAcceleration != +0.0f ? Float.floatToIntBits(zAcceleration) : 0);
        result = 31 * result + damageValue;
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (accuracy != +0.0f ? Float.floatToIntBits(accuracy) : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (speed != +0.0f ? Float.floatToIntBits(speed) : 0);
        result = 31 * result + (synced ? 1 : 0);
        result = 31 * result + entriesCount;
        return result;
    }

    @Override
    public String toString() {
        return "Damage{" +
                "xAcceleration=" + xAcceleration +
                ", yAcceleration=" + yAcceleration +
                ", zAcceleration=" + zAcceleration +
                ", damageValue=" + damageValue +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", accuracy=" + accuracy +
                ", timestamp=" + timestamp +
                ", speed=" + speed +
                ", synced=" + synced +
                ", entriesCount=" + entriesCount +
                '}';
    }
}
