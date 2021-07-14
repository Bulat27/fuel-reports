package main.java;

import main.java.business.models.PetrolStations;
import main.java.business.services.XMLParser;

public class Main {

    public static void main(String[] args) {
        PetrolStations p = XMLParser.parsePetrolStationsXML();
        System.out.println("Date: " + p.getDate());
        System.out.println();
        for (int i = 0; i < p.getPetrolStationList().toArray().length; i++) {
            System.out.println(p.getPetrolStationList().get(i).toString());
        }
    }
}
