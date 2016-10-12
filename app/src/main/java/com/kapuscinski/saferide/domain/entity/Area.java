/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.entity;

public class Area {

    private int entriesCount;
    private Region region;

    public Area() {

    }

    public Area(int entriesCount, Region region) {
        this.entriesCount = entriesCount;
        this.region = region;
    }

    public double getRegionHeight(){
        return region.getTopLat()-region.getBottomLat();
    }

    public double getRegionWidth(){
        return region.getRightLon()-region.getLeftLon();
    }

    public int getEntriesCount() {
        return entriesCount;
    }

    public void setEntriesCount(int entriesCount) {
        this.entriesCount = entriesCount;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Area area = (Area) o;

        if (entriesCount != area.entriesCount) return false;
        return region != null ? region.equals(area.region) : area.region == null;

    }

    @Override
    public int hashCode() {
        int result = entriesCount;
        result = 31 * result + (region != null ? region.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Area{" +
                "entriesCount=" + entriesCount +
                ", region=" + region +
                '}';
    }
}
