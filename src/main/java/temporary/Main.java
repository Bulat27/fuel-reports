package temporary;

import cli.view_models.FuelReportsArgs;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        JCommander jc = null;

        try {
            FuelReportsArgs mainCommand = new FuelReportsArgs();
            FuelReportsArgs.ProcessCommand processCommand = mainCommand.new ProcessCommand();
            FuelReportsArgs.ConfigCommand configCommand = mainCommand.new ConfigCommand();

            jc = JCommander.newBuilder().addCommand(configCommand).addCommand(processCommand).build();
            jc.parse(args);
            String parsedCmdStr = jc.getParsedCommand();

            switch (parsedCmdStr){
                case "config":
                    configCommand.config();
                    break;
                case "process":
                    processCommand.process(configCommand.getDestinationDir());
                    break;
                default:
                    System.err.println("Invalid command " + parsedCmdStr);
            }
        } catch (JSchException | SAXException | ParserConfigurationException | IOException | SftpException e) {
            e.printStackTrace();
        }catch (ParameterException e){
            if(jc != null) jc.usage();
            System.out.println(e.getMessage());
        }
    }
}

