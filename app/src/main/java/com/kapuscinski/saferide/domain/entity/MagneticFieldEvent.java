/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.entity;

import java.util.Arrays;

public class MagneticFieldEvent {

    private float values[];

    public MagneticFieldEvent(float[] values) {
        this.values = values;
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MagneticFieldEvent that = (MagneticFieldEvent) o;

        return Arrays.equals(values, that.values);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public String toString() {
        return "MagneticFieldEvent{" +
                "values=" + Arrays.toString(values) +
                '}';
    }
}
