package org.iot.controller;

import org.iot.dao.DAO;
import org.iot.models.CriticalSector;

import java.util.ArrayList;

public class Controller extends Thread{
    public double MIN_THRESHOLD;
    public double MAX_THRESHOLD;

    public String topic;

    public Controller(double min, double max, String topic){
        MIN_THRESHOLD = min;
        MAX_THRESHOLD = max;
        this.topic = topic;
    }

    public void run(){
        while(true){

            try {
                ArrayList<CriticalSector> ms = new DAO().readCriticalMeasure(topic, MIN_THRESHOLD, MAX_THRESHOLD);
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
                sleep(10000);
                System.out.println("Critical Sectors Found!");
                for(CriticalSector cs : ms){
                    this.helpSector(cs);
                }



            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void helpSector(CriticalSector cs) {
        System.out.println("Overloaded by sub classes");
    }

    public void changeThreshold(double value, int type) {
        if (type==-1){
            MIN_THRESHOLD = value;

        }else if(type==1) {
            MAX_THRESHOLD = value;
        }
        return;

    }
}
