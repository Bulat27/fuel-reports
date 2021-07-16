package temporary;

import business.models.PetrolStation;
import business.models.PetrolStations;
import business.services.SFTPDownloader;
import business.services.XMLParser;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            SFTPDownloader.downloadFiles();
            printPetrolStations();
        } catch (SAXException | IOException | ParserConfigurationException | SftpException | JSchException e) {
            e.printStackTrace();
        }
    }

    private static void printPetrolStations() throws IOException, SAXException, ParserConfigurationException {
        List<PetrolStations> petrolStations = new ArrayList<>();
        File[] files = new File(SFTPDownloader.LOCAL_DIRECTORY).listFiles();
        for (File file : files) {
            petrolStations.add(XMLParser.parsePetrolStationsXML(SFTPDownloader.LOCAL_DIRECTORY + "/" + file.getName()));
        }
        for (PetrolStations p: petrolStations)
            System.out.println(p);
    }
}
