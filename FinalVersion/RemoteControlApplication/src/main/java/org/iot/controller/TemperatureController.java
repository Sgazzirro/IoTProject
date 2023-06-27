package org.iot.controller;

import org.iot.ClientCOAP;
import org.iot.dao.DAO;
import org.iot.models.Actuator;
import org.iot.models.CriticalSector;

import java.util.ArrayList;

public class TemperatureController extends Controller{
    public TemperatureController(double min, double max){
        super(min, max,"temperature");
    }

    public void helpSector(CriticalSector cs) {
        int sector = cs.getSector();
        double value = cs.getValue();
        System.out.println("Sector Temperature : " + sector + " : " + value + "°");
        System.out.println("Trying to call actuators...");

        if(value > MAX_THRESHOLD){
            cool(sector);
        }

        if(value < MIN_THRESHOLD){
            heat(sector);
        }
    }

    private void cool(int sector){
        ArrayList<Actuator> actuators_fan = new DAO().readActuatorST("fan", sector);
        for(int i = 0; i < actuators_fan.size(); i++){
            // Logica di aumento ossigeno
            if(Integer.parseInt(actuators_fan.get(i).getStatus().substring(3)) == 25){
                System.out.println("No more contain measures available!");
                break;
            }
            ClientCOAP.incrDecrParam(actuators_fan.get(i), "temperature", -1);
        }
    }

    private void heat(int sector){
        ArrayList<Actuator> actuators_fan = new DAO().readActuatorST("fan", sector);
        for(int i = 0; i < actuators_fan.size(); i++){
            // Logica di aumento ossigeno
            if(Integer.parseInt(actuators_fan.get(i).getStatus().substring(3)) == 16){
                System.out.println("No more contain measures available!");
                break;
            }
            ClientCOAP.incrDecrParam(actuators_fan.get(i), "temperature", 1);
        }
    }

}
