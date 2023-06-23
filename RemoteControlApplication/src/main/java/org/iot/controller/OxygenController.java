package org.iot.controller;


import org.iot.ClientCOAP;
import org.iot.models.Actuator;
import org.iot.models.CriticalSector;
import org.iot.dao.DAO;

import java.util.ArrayList;

public class OxygenController extends Thread{
    private double OXYGEN_MAX_THRESHOLD = 12.5;
    private double OXYGEN_MIN_THRESHOLD = 7.0;

    public void run(){

        while(true){

            try {
                ArrayList<CriticalSector> ms = new DAO().readCriticalMeasure("oxygen", OXYGEN_MAX_THRESHOLD, OXYGEN_MIN_THRESHOLD);
                if (ms == null) {
                    System.out.println("Maybe SQL Error");
                    sleep(5000);
                    continue;
                }
                if(ms.isEmpty()){
                    System.out.println("No critical Measures! :D");
                    sleep(60000);
                    continue;
                }

                System.out.println("Critical Sectors Found!");
                for(CriticalSector cs : ms){
                    helpSector(cs);
                }



            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private void helpSector(CriticalSector cs) {
        int sector = cs.getSector();
        double value = cs.getValue();
        System.out.println("Ossigeno nel settore : " + sector + " : " + value + "%");
        System.out.println("Provo a contattare gli attuatori di quel settore");

        if(value > OXYGEN_MAX_THRESHOLD){
            reduceOxygen(sector);
        }

        if(value < OXYGEN_MIN_THRESHOLD){
            increaseOxygen(sector);
        }
    }

    private void increaseOxygen(int sector) {
        ArrayList<Actuator> actuators = new DAO().readActuatorS("nitro", sector);
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

        ArrayList<Actuator> actuators_fan = new DAO().readActuatorS("fan", sector);
        for(int i = 0; i < actuators_fan.size(); i++){
            // Logica di aumento ossigeno
                ClientCOAP.incrDecrParam(actuators_fan.get(i), "power", 1);
            }
        }


    private void reduceOxygen(int sector) {
        ArrayList<Actuator> actuators = new DAO().readActuatorS("fan", sector);
        boolean fanOn = false;
        for(int i = 0; i < actuators.size(); i++){
            // Logica di aumento ossigeno
            if(Integer.parseInt(actuators.get(i).getStatus()) > 0){
                ClientCOAP.changeParam(actuators.get(i), "power", 0);
                fanOn = true;
            }
        }
        if(fanOn)
            return;

        ArrayList<Actuator> actuators_nitro = new DAO().readActuatorS("nitro", sector);
        for(int i = 0; i < actuators_nitro.size(); i++){
            // Logica di aumento ossigeno
            ClientCOAP.enable(actuators_nitro.get(i));
        }

    }

}
