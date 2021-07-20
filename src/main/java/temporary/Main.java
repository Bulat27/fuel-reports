package temporary;


import business.models.PetrolStations;
import business.services.SFTPDownloader;
import business.services.XMLParser;
import cli.view_models.FuelReportsArgs;
import com.beust.jcommander.JCommander;
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
//        try {
//            SFTPDownloader.downloadFiles();
//            Repositories.writeIntoDataBase(returnPetrolStations(), 5000);
//        } catch (SQLException | IOException | SAXException | ParserConfigurationException | JSchException | SftpException e) {
//            e.printStackTrace();
//        }
        FuelReportsArgs mainCommand = new FuelReportsArgs();
        FuelReportsArgs.ProcessCommand processCommand = mainCommand.new ProcessCommand();
        FuelReportsArgs.ConfigCommand configCommand = mainCommand.new ConfigCommand();
        FuelReportsArgs.ReportCommand reportCommand = mainCommand.new ReportCommand();

        JCommander jc = JCommander.newBuilder().addCommand(configCommand).addCommand(processCommand).addCommand(reportCommand).build();
        jc.parse(args);
        String parsedCmdStr = jc.getParsedCommand();

        switch (parsedCmdStr){
            case "config":
                break;
            case "process":
                try {
                    processCommand.process(configCommand.getDestinationDir());
                } catch (JSchException | SQLException | SAXException | ParserConfigurationException | IOException | SftpException e) {
                    e.printStackTrace();
                }
            case "report":

        }


    }
    //TODO: Put this is some other class!
    public static List<PetrolStations> returnPetrolStations(String path) throws IOException, SAXException, ParserConfigurationException {
        List<PetrolStations> petrolStations = new ArrayList<>();
        File[] files = new File(path).listFiles();
        for (File file : files) {
            petrolStations.add(XMLParser.parsePetrolStationsXML(path + "/" + file.getName()));
        }
        return petrolStations;
    }
}

