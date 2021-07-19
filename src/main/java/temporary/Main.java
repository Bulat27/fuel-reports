package temporary;


import business.models.PetrolStations;
import business.services.SFTPDownloader;
import business.services.XMLParser;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import data.Repositories;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            SFTPDownloader.downloadFiles();
            Repositories.writeIntoDataBase(returnPetrolStations(), 5000);
        } catch (SQLException | IOException | SAXException | ParserConfigurationException | JSchException | SftpException e) {
            e.printStackTrace();
        }
    }

    private static List<PetrolStations> returnPetrolStations() throws IOException, SAXException, ParserConfigurationException {
        List<PetrolStations> petrolStations = new ArrayList<>();
        File[] files = new File(SFTPDownloader.LOCAL_DIRECTORY).listFiles();
        for (File file : files) {
            petrolStations.add(XMLParser.parsePetrolStationsXML(SFTPDownloader.LOCAL_DIRECTORY + "/" + file.getName()));
        }
        return petrolStations;
    }
}

