package business.models;

import java.util.List;

public class PetrolStations {

    private List<PetrolStation> petrolStationList;

    public PetrolStations(List<PetrolStation> petrolStationList) {
        this.petrolStationList = petrolStationList;
    }

    public List<PetrolStation> getPetrolStationList() {
        return petrolStationList;
    }

    public void setPetrolStationList(List<PetrolStation> petrolStationList) {
        this.petrolStationList = petrolStationList;
    }

    @Override
    public String toString() {
        return printPetrolStationList();
    }

    private String printPetrolStationList() {
        String s = "";
        for (PetrolStation p : this.petrolStationList) {
            s += p.toString() + "\n";
        }
        return s;
    }
}
