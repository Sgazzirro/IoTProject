package org.iot;

import org.iot.controller.Controller;
import org.iot.controller.OxygenController;
import org.iot.controller.TemperatureController;
import org.iot.dao.DAO;
import org.iot.models.Actuator;
import org.iot.models.Measurement;

import java.util.Scanner;


import java.util.ArrayList;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    private static double TEMPERATURE_MIN_THRESHOLD = 18.0;
    private static double TEMPERATURE_MAX_THRESHOLD = 21.0;
    private static double OXYGEN_MIN_THRESHOLD = 10.0;
    private static double OXYGEN_MAX_THRESHOLD = 15.0;
    private static Controller ctemp = new TemperatureController(TEMPERATURE_MIN_THRESHOLD, TEMPERATURE_MAX_THRESHOLD);

    private static Controller cox= new OxygenController(OXYGEN_MIN_THRESHOLD, OXYGEN_MAX_THRESHOLD);

    public static void main(String[] args) {
        ctemp.start();
        cox.start();
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("OPTIONS MENU:");
	    System.out.println("-------------------------------------------------------------------------");
            System.out.println("1. Show Actuators List by TYPE and/or SECTOR");
            System.out.println("2. Show Oxigen/Temperature Measurements of the last 3 minutes");
            System.out.println("3. Change Oxygen Threshold ([MIN, MAX], value");
            System.out.println("4. Change Temperature Threshold ([MIN, MAX], value)");
	    System.out.println("-------------------------------------------------------------------------");
            int k = scanner.nextInt();
            switch (k) {
                case 1:

                    System.out.println("Insert SECTOR: (type -1 is you want to search in all sectors)");
                    int sector = scanner.nextInt();
                    String type;
                    System.out.println("Insert TYPE: --options are \'fan\' \'nitro\' \'all\'");
                    type = new Scanner(System.in).nextLine();


                    if (sector == -1 && !type.equals("all")) {
                        // solo per tipo
                        ArrayList<Actuator> result = new DAO().readActuatorT(type);
                        System.out.println("list of all actuator of type" + type);
                        for (Actuator a : result) {
                            System.out.println(a.toString());
                        }
                        break;
                    }
                    if (sector == -1 && type.equals("all")) {
                        // tutti
                        ArrayList<Actuator> result = new DAO().readAllActuator();
                        System.out.println("list of all actuator in the system");
                        for (Actuator a : result) {
                            System.out.println(a.toString());
                        }
                        break;
                    }

                    // solo settore
                    if (sector != -1 && type.equals("all")) {
                        // solo settore
                        ArrayList<Actuator> result = new DAO().readActuatorS(sector);
                        System.out.println("list of all actuator in sector" + sector);
                        for (Actuator a : result) {
                            System.out.println(a.toString());
                        }
                        break;
                    }

                    // settore e tipo
                    ArrayList<Actuator> result = new DAO().readActuatorST(type, sector);
                    System.out.println("list of all actuator of type" + type + " in sector" + sector);
                    for (Actuator a : result) {
                        System.out.println(a.toString());
                    }
                    break;

                case 2:
                    System.out.println("Insert the topic you want to inspect");
                    String topic = new Scanner(System.in).nextLine();
                    System.out.println(topic);
                    ArrayList<Measurement> measurements = new DAO().readMeasure(topic);
                    if (measurements==null){
                        System.out.println("No measurements available");
                    }
                    for (Measurement a : measurements) {
                        System.out.println(a.toString());
                    }
                    break;

                case 3:
                    System.out.println("Want to modify MAX or MIN threshold?");
                    String typeCH = new Scanner(System.in).nextLine();
                    System.out.println("New Threshold value :");
                    int newThr = scanner.nextInt();
                    changeThreshold(typeCH, newThr, cox);
                    break;

                case 4:
                    System.out.println("Want to modify MAX or MIN threshold?");
                    String typeCHt = new Scanner(System.in).nextLine();
                    System.out.println("New Threshold value :");
                    int newThrt = scanner.nextInt();
                    changeThreshold(typeCHt, newThrt, ctemp);
                    break;
            }
        }



    }


    public static void showActuators(String type, int sector){
        System.out.println("AVAILABLE ACTUATORS");
        if(sector == -1){
            ArrayList<Actuator> result = new DAO().readActuatorT(type);
            for(Actuator a: result){
                System.out.println(a.toString());
            }
            return;
        }

        ArrayList<Actuator> result = new DAO().readActuatorST(type, sector);
        for(Actuator a: result){
            System.out.println(a.toString());
        }

    }


    public static void changeThreshold(String type, double new_threshold, Controller actuator){

        if (type.equals("MIN")){
            actuator.changeThreshold(new_threshold, -1);
        }else if(type.equals("MAX")){
            actuator.changeThreshold(new_threshold, 1);
        }

    }


}
