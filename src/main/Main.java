package main;

import business.models.PetrolStations;
import business.services.XMLParser;

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
