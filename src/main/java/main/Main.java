package main;

import cli.view_models.ConfigCommand;
//import cli.view_models.FuelReportsArgs;
import cli.view_models.ProcessCommand;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    
   private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws SQLException {
        JCommander jc = null;
        LOGGER.addHandler(new ConsoleHandler());

        try {
            //FuelReportsArgs mainCommand = new FuelReportsArgs();
            ProcessCommand processCommand = new ProcessCommand();
            ConfigCommand configCommand = new ConfigCommand();

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
                    LOGGER.log(Level.FINE, "Invalid command : {0}", parsedCmdStr);
            }
        } catch (JSchException | SAXException | ParserConfigurationException | IOException | SftpException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }catch (ParameterException e){
            if(jc != null) jc.usage();
        }
    }
}

