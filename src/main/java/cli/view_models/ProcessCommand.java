package cli.view_models;

import business.services.SFTPDownloader;
import business.services.XMLParser;
import com.beust.jcommander.Parameters;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import data.Repositories;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

@Parameters(
        commandNames = {"process"},
        commandDescription = "Deserializing all new data")
public class ProcessCommand {
    public void process(String path) throws JSchException, SftpException, IOException, ParserConfigurationException, SAXException, SQLException {
        SFTPDownloader.downloadFiles(path);
        Repositories.writeIntoDataBase(XMLParser.returnPetrolStations(path), 5000);
    }
}
