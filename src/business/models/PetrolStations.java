package business.models;

import business.services.XMLParsingUtilities;

import java.time.LocalDate;
import java.util.List;

public class PetrolStations {

    private List<PetrolStation> petrolStationList;
    private LocalDate date;

    public PetrolStations() { }

    public PetrolStations(List<PetrolStation> petrolStationList, LocalDate date) {
        this.petrolStationList = petrolStationList;
        this.date = date;
    }

    public List<PetrolStation> getPetrolStationList() {
        return petrolStationList;
    }

    public void setPetrolStationList(List<PetrolStation> petrolStationList) {
        this.petrolStationList = petrolStationList;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    //This main method is only temporary. I've used it to test my mapping.
    public static void main(String[] args) {
        PetrolStations p = XMLParsingUtilities.parsePetrolStationsXML();
        System.out.println("Date: " + p.getDate());
        System.out.println();
        for (int i = 0; i < p.getPetrolStationList().toArray().length; i++) {
            System.out.println(p.getPetrolStationList().get(i).toString());
        }
    }
}
