package org.iot.models;

public class CriticalSector {
    private int Sector;
    private double value;

    public CriticalSector(int sector, double value) {
        Sector = sector;
        this.value = value;
    }

    public int getSector() {
        return Sector;
    }

    public void setSector(int sector) {
        Sector = sector;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
