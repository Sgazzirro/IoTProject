package org.iot.models;

public class Actuator {
    private int IDActuator;
    private String IPActuator;
    private int sector;
    private String type;
    private String status;

    public Actuator(int IDActuator, String IPActuator, int sector,  String type,String status) {
        this.IDActuator = IDActuator;
        this.IPActuator = IPActuator;
        this.sector = sector;
        this.type = type;
        this.status = status;
    }

    public int getIDActuator() {
        return IDActuator;
    }

    public void setIDActuator(int IDActuator) {
        this.IDActuator = IDActuator;
    }

    public String getIPActuator() {
        return IPActuator;
    }

    public void setIPActuator(String IPActuator) {
        this.IPActuator = IPActuator;
    }

    public int getSector() {
        return sector;
    }

    public void setSector(int sector) {
        this.sector = sector;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Actuator{" +
                "IDActuator='" + IDActuator + '\'' +
                ", IPActuator='" + IPActuator + '\'' +
                ", Sector=" + sector +
                ", Type=" + type +
                ", Status=" + status +
                '}';
    }

}
