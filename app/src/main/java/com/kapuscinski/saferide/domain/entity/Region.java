/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.entity;

public class Region {

    private double topLat;
    private double rightLon;
    private double bottomLat;
    private double leftLon;

    public Region() {
    }

    public Region(double topLat, double rightLon, double bottomLat, double leftLon) {
        this.topLat = topLat;
        this.rightLon = rightLon;
        this.bottomLat = bottomLat;
        this.leftLon = leftLon;
    }

    public double getWidth(){
        return rightLon-leftLon;
    }

    public double getHeight(){
        return topLat-bottomLat;
    }

    public double getSize(){
        return (rightLon-leftLon) * (topLat-bottomLat);
    }

    public double getTopLat() {
        return topLat;
    }

    public void setTopLat(double topLat) {
        this.topLat = topLat;
    }

    public double getRightLon() {
        return rightLon;
    }

    public void setRightLon(double rightLon) {
        this.rightLon = rightLon;
    }

    public double getBottomLat() {
        return bottomLat;
    }

    public void setBottomLat(double bottomLat) {
        this.bottomLat = bottomLat;
    }

    public double getLeftLon() {
        return leftLon;
    }

    public void setLeftLon(double leftLon) {
        this.leftLon = leftLon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Region region = (Region) o;

        if (Double.compare(region.topLat, topLat) != 0) return false;
        if (Double.compare(region.rightLon, rightLon) != 0) return false;
        if (Double.compare(region.bottomLat, bottomLat) != 0) return false;
        return Double.compare(region.leftLon, leftLon) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(topLat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(rightLon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(bottomLat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(leftLon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String toCoordinatesString() {
        StringBuilder b = new StringBuilder();
        b.append(topLat);
        b.append(";");
        b.append(rightLon);
        b.append(";");
        b.append(bottomLat);
        b.append(";");
        b.append(leftLon);
        return b.toString();
    }

    @Override
    public String toString() {
        return "Region{" +
                "topLat=" + topLat +
                ", rightLon=" + rightLon +
                ", bottomLat=" + bottomLat +
                ", leftLon=" + leftLon +
                '}';
    }
}
