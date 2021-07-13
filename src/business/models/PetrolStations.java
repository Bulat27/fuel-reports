package business.models;



import java.time.LocalDate;
import java.util.List;

public class PetrolStations {

    private List<PetrolStation> petrolStationList;
    private LocalDate date;

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
}
