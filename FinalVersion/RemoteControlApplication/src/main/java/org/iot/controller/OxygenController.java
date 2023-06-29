package org.iot.controller;


import org.iot.ClientCOAP;
import org.iot.models.Actuator;
import org.iot.models.CriticalSector;
import org.iot.dao.DAO;

import java.util.ArrayList;

public class OxygenController extends Controller{

    public OxygenController(double min, double max){
        super(min, max,"oxygen");
    }

    public void helpSector(CriticalSector cs) {
        int sector = cs.getSector();
        double value = cs.getValue();
        System.out.println("Sector Oxygen : " + sector + " : " + value + "%");
       

        if(value > MAX_THRESHOLD){
	    System.out.println("Trying to reduce oxygen");
            reduceOxygen(sector);
        }

        if(value < MIN_THRESHOLD){
	    System.out.println("Trying to increase oxygen");
            increaseOxygen(sector);
        }
    }

    private void increaseOxygen(int sector) {
        ArrayList<Actuator> actuators = new DAO().readActuatorST("nitro", sector);
        boolean nitroOn = false;
        for(int i = 0; i < actuators.size(); i++){
            // Logica di aumento ossigeno
            if(actuators.get(i).getStatus().equals("ON")){
                ClientCOAP.disable(actuators.get(i));
                nitroOn = true;
            }
        }
        if(nitroOn)
            return;

        ArrayList<Actuator> actuators_fan = new DAO().readActuatorST("fan", sector);
        for(int i = 0; i < actuators_fan.size(); i++){
            // Logica di aumento ossigeno
                 System.out.println(Integer.parseInt(actuators_fan.get(i).getStatus().substring(0,1)));
                if(Integer.parseInt(actuators_fan.get(i).getStatus().substring(0,1)) == 5){
                    System.out.println("ALERT! No more contain measures available!");
                    break;
                }
                ClientCOAP.incrDecrParam(actuators_fan.get(i), "power", 1);
            }
        }


    private void reduceOxygen(int sector) {
        ArrayList<Actuator> actuators = new DAO().readActuatorST("fan", sector);
        boolean fanOn = false;
        for(int i = 0; i < actuators.size(); i++){
            // Logica di aumento ossigeno
            if(Integer.parseInt(actuators.get(i).getStatus().substring(0,1)) > 0){
                ClientCOAP.changeParam(actuators.get(i), "power", 0);
                fanOn = true;
            }
        }
        if(fanOn)
            return;

        ArrayList<Actuator> actuators_nitro = new DAO().readActuatorST("nitro", sector);
        for(int i = 0; i < actuators_nitro.size(); i++){
            // Logica di aumento ossigeno
            if(actuators_nitro.get(i).getStatus().equals("ON")){
                System.out.println("ALERT! No contain measures available!");
                break;
            }
            ClientCOAP.enable(actuators_nitro.get(i));
        }

    }


}
