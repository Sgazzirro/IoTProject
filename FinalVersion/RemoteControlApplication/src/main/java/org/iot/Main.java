package org.iot;

import org.iot.controller.OxygenController;
import org.iot.dao.DAO;
import org.iot.models.Actuator;
import org.iot.models.Measurement;

import java.util.Scanner;


import java.util.ArrayList;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {

    private static OxygenController Controller = new OxygenController();

    public static void main(String[] args) {
        Controller.start();
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("OPTIONS MENU:");
            System.out.println("1. Show Actuators List by TYPE and/or SECTOR");
            System.out.println("2. Show Oxigen Measurements of the last 3 minutes");
            System.out.println("2. Show Temperature Measurements of the last 3 minutes");

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

    public static void showLastMeasurements(int sector){

    }

    public static void deleteActuator(int ID){

        if(new DAO().deleteActuator(ID)){
            System.out.println("Cancellazione effettuata");
        }else{
            System.out.println("Cancellazione fallita");
        }



    }

    public static void changeThreshold(String type, double new_threshold, OxygenController actuator){

        if (type.equals("MIN")){
            Controller.changeThreshold(new_threshold, -1);
        }else if(type.equals("MAX")){
            Controller.changeThreshold(new_threshold, 1);
        }

    }


}