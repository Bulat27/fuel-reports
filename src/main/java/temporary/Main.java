package temporary;

import business.models.PetrolStations;
import business.services.SFTPDownloader;
import business.services.XMLParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String LOCAL_DIRECTORY = "src/main/resources/data";

    public static void main(String[] args) {
        SFTPDownloader.downloadFiles();
        printPetrolStations();
    }

    private static void printPetrolStations() {
        List<PetrolStations> petrolStations = new ArrayList<>();
        File[] files = new File(LOCAL_DIRECTORY).listFiles();
        for (File file : files) {
            petrolStations.add(XMLParser.parsePetrolStationsXML(LOCAL_DIRECTORY + "/" + file.getName()));
        }
        for (PetrolStations p: petrolStations)
            System.out.println(p);
    }
}
