package cli.view_models;

import business.services.SFTPDownloader;
import business.services.XMLParser;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import data.Repositories;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

public class FuelReportsArgs {

//    @Parameters(
//            commandNames = {"process"},
//            commandDescription = "Deserializing all new data"
//    )
//    public class ProcessCommand{
//        public void process(String path) throws JSchException, SftpException, IOException, ParserConfigurationException, SAXException, SQLException {
//            SFTPDownloader.downloadFiles(path);
//            Repositories.writeIntoDataBase(XMLParser.returnPetrolStations(path), 5000);
//        }
//    }

//    @Parameters(commandNames = {"config"},
//                commandDescription = "Configures your local directory")
//    public class ConfigCommand{
//
//        @Parameter(
//                names = "--data-dir",
//                required = true,
//                validateWith = DirectoryParameterValidator.class,
//                description = "A directory where the date files are stored locally"
//        )
//        private String destinationDir;
//
//        public String getDestinationDir() {
//            return destinationDir;
//        }
//
//        public ConfigCommand() throws SQLException {
//            destinationDir = Repositories.getDefaultDestination();
//        }
//
//        public void config() throws SQLException {
//            Repositories.updateDefaultDestination(destinationDir);
//        }
//    }
}
