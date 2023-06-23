package org.iot;

public class Measurement {
    private String sensorID;
    private int sector;

    private double value;

    private String topic;
    private String timestamp;

    public Measurement(String sensorID, String sensorIP, int sector, double value, String topic, String timestamp) {
        this.sensorID = sensorID;
        this.sector = sector;
        this.value = value;
        this.topic = topic;
        this.timestamp = timestamp;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
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
        return "Measurement{" +
                "sensorID='" + sensorID + '\'' +
                ", sector=" + sector +
                ", value=" + value +
                ", topic='" + topic + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
