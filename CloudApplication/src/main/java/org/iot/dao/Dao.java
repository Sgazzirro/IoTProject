package org.iot.dao;

import org.iot.models.Actuator;
import org.iot.models.Measurement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Dao {
    private final String CONNECTION_URI = "jdbc:mysql://localhost:3306/IoTProject";
    private final String USERNAME = "root";
    private final String PASSWORD = "1234";

    public void writeMeasurement(Measurement m){
        String sqlStatement = "REPLACE INTO Measurements VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(CONNECTION_URI, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlStatement);
        )
        {
            ps.setString(1, m.getIDsensor());
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
}
