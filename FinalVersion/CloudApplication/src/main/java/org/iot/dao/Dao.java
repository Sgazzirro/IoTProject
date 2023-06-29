package org.iot.dao;

import org.iot.models.Actuator;
import org.iot.models.Measurement;

import java.sql.*;

public class Dao {
    private final String CONNECTION_URI = "jdbc:mysql://172.17.0.2:3306/IoTProject?user=root&password=1234&useUnicode=true&characterEncoding=UTF-8";
    private final String USERNAME = "root";
    private final String PASSWORD = "1234";

    public boolean deleteActuatorIP(String IP){

        String sqlStatement = "DELETE FROM Actuators WHERE IPActuator = ?";
        boolean result = false;
        try(Connection conn = DriverManager.getConnection(CONNECTION_URI, USERNAME, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sqlStatement);
        )
        {
            ps.setString(1, IP);
            int RowsAffected = ps.executeUpdate();

            if(RowsAffected==0){
                System.out.println("C'è stato un errore!");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
	System.out.println("Correctly DELETED");
        return true;
}


            public void writeMeasurement(Measurement m){
       
        String sqlStatement = "INSERT INTO Measurements VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(CONNECTION_URI, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlStatement);
        )
        {
            ps.setInt(1, m.getIDSensor());
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
	System.out.println("Success: measurement written");
    }

    public void writeActuator(Actuator a) {
        String sqlStatement = "REPLACE INTO Actuators VALUES (?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(CONNECTION_URI, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlStatement);
        )
        {
            ps.setInt(1, a.getIDActuator());
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
	System.out.println("Success: Actuator registered");
    }
}
