package Business.Models;

import Business.Services.XMLParsingUtilities;

import java.time.LocalDate;
import java.util.ArrayList;

public class PetrolStationList {

    private ArrayList<PetrolStation> petrolStations;
    private LocalDate date;

    public PetrolStationList() {
    }

    public PetrolStationList(ArrayList<PetrolStation> petrolStations, LocalDate date) {
        this.petrolStations = petrolStations;
        this.date = date;
    }

    public ArrayList<PetrolStation> getPetrolStations() {
        return petrolStations;
    }

    public void setPetrolStations(ArrayList<PetrolStation> petrolStations) {
        this.petrolStations = petrolStations;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    //This main method is only temporary. I've used it to test my mapping.
    public static void main(String[] args) {
        PetrolStationList p = XMLParsingUtilities.parsePetrolStationListXML();
        System.out.println("Date: " + p.getDate());
        System.out.println();
        for (int i = 0; i < p.getPetrolStations().toArray().length; i++) {
            System.out.println(p.getPetrolStations().get(i).toString());

        }
    }

}
