package org.iot.models;

public class Measurement {
    private int IDSensor;
    private int sector;
    private double value;
    private String topic;
    private String timestamp;

    public Measurement(int IDSensor, int sector, double value, String topic, String timestamp) {
        this.IDSensor = IDSensor;
        this.sector = sector;
        this.value = value;
        this.topic = topic;
        this.timestamp = timestamp;
    }

    public int getIDSensor() {
        return IDSensor;
    }

    public void setIDSensor(int IDSensor) {
        this.IDSensor = IDSensor;
    }

    public int getSector() {
        return sector;
    }

    public void setSector(int sector) {
        this.sector = sector;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Measurements{" +
                "sensorID='" + IDSensor + '\'' +
                ", sector=" + sector +
                ", value=" + value +
                ", topic='" + topic + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
