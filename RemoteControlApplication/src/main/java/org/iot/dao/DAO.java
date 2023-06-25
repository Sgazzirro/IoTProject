package org.iot.dao;

import org.iot.models.Actuator;
import org.iot.models.CriticalSector;
import org.iot.models.Measurement;

import java.time.LocalDateTime;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DAO {
    private final String CONNECTION_URI = "jdbc:mysql://localhost:3306/IoTProject";
    private final String USERNAME = "root";
    private final String PASSWORD = "1234";


    public void writeMeasurement(Measurement m){
        String sqlStatement = "REPLACE INTO Measurements VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(CONNECTION_URI, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlStatement);
        )
        {
            ps.setString(1, m.getIDSensor());
            ps.setInt(2, m.getSector());
            ps.setDouble(3, m.getValue());
            ps.setString(4, m.getTopic());
            ps.setString(5, m.getTimestamp());
            int affected_rows = ps.executeUpdate();
            if(affected_rows == 0)
                throw new RuntimeException("Something wrong during registration");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeActuator(Actuator a) {
        String sqlStatement = "REPLACE INTO Actuators VALUES (?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(CONNECTION_URI, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlStatement);
        )
        {
            ps.setString(1, a.getIDActuator());
            ps.setString(2, a.getIPActuator());
            ps.setInt(3, a.getSector());
            ps.setString(4, a.getType());
            ps.setString(5, a.getStatus());
            int affected_rows = ps.executeUpdate();
            if(affected_rows == 0)
                throw new RuntimeException("Something wrong during registration");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Reads all actuator with the specified type
    public ArrayList<Actuator> readActuatorT(String type){
        String sqlStatement = "SELECT * FROM Actuators WHERE Type = ?";
        ArrayList<Actuator> result = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(CONNECTION_URI, USERNAME, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sqlStatement);
            )
        {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Actuator toAdd = new Actuator(rs.getString(1), rs.getString(2), rs.getInt(3),
                                                rs.getString(4), rs.getString(5));
                result.add(toAdd);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public ArrayList<Actuator> readAllActuator(){
        String sqlStatement = "SELECT * FROM Actuators ";
        ArrayList<Actuator> result = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(CONNECTION_URI, USERNAME, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sqlStatement);
        )
        {
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Actuator toAdd = new Actuator(rs.getString(1), rs.getString(2), rs.getInt(3),
                        rs.getString(4), rs.getString(5));
                result.add(toAdd);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public ArrayList<Actuator> readActuatorS(int sector){
        String sqlStatement = "SELECT * FROM Actuators WHERE Sector = ?";
        ArrayList<Actuator> result = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(CONNECTION_URI, USERNAME, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sqlStatement);
        )
        {
            ps.setInt(1, sector);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Actuator toAdd = new Actuator(rs.getString(1), rs.getString(2), rs.getInt(3),
                        rs.getString(4), rs.getString(5));
                result.add(toAdd);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    // Reads all actuator with the specified type and sector
    public ArrayList<Actuator> readActuatorST(String type, int sector){
        String sqlStatement = "SELECT * FROM Actuators WHERE Type = ? AND Sector = ?";
        ArrayList<Actuator> result = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(CONNECTION_URI, USERNAME, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sqlStatement);
        )
        {
            ps.setString(1, type);
            ps.setInt(2, sector);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Actuator toAdd = new Actuator(rs.getString(1), rs.getString(2), rs.getInt(3),
                        rs.getString(4), rs.getString(5));
                result.add(toAdd);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    // Reads all measurements about a specific topic in the last 3 minutes
    public ArrayList<Measurement> readMeasure(String topic){
        String shiftedTimestamp = readTime();
        String sqlStatement = "SELECT * FROM Measurements WHERE Topic = ? AND TIMESTAMP TimeStamp > TIMESTAMP ? GROUP BY Sector";
        ArrayList<Measurement> result = new ArrayList<Measurement>();

        try(Connection conn = DriverManager.getConnection(CONNECTION_URI, USERNAME, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sqlStatement);
        )
        {
            ps.setString(1, topic);
            ps.setString(2, shiftedTimestamp);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Measurement toAdd = new Measurement(rs.getString(1), rs.getInt(2), rs.getDouble(3),
                        rs.getString(4), rs.getString(5));
                result.add(toAdd);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    // Returns all CRITICAL SECTORS about a speficic topic in the last 3 minutes
    public ArrayList<CriticalSector> readCriticalMeasure(String topic, double threshold_M, double threshold_m){
        String shiftedTimestamp = readTime();
        String sqlStatement = "SELECT Sector, AVG(Value) FROM Measurements WHERE Topic = ? AND TIMESTAMP TimeStamp > TIMESTAMP ? GROUP BY Sector";
        ArrayList<CriticalSector> result = new ArrayList<CriticalSector>();

        try(Connection conn = DriverManager.getConnection(CONNECTION_URI, USERNAME, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sqlStatement);
        )
        {
            ps.setString(1, topic);
            ps.setString(2, shiftedTimestamp);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getDouble(2) > threshold_M || rs.getDouble(2) < threshold_m)
                    result.add(new CriticalSector(rs.getInt(1), rs.getDouble(2)));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    private String readTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        LocalDateTime now = LocalDateTime.now().minusMinutes(3);
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public boolean deleteActuator(int ID){

        String sqlStatement = "DELETE FROM Actuators WHERE IDActuator = ?";
        boolean result = false;
        try(Connection conn = DriverManager.getConnection(CONNECTION_URI, USERNAME, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sqlStatement);
        )
        {
            ps.setInt(1, ID);
            int RowsAffected = ps.executeUpdate();

            if(RowsAffected==0){
                System.out.println("C'è stato un errore!");
                return false;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }



}
