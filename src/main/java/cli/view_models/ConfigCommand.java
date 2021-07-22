package cli.view_models;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import data.Repositories;

import java.sql.SQLException;

@Parameters(commandNames = {"config"},
        commandDescription = "Configures your local directory")
public class ConfigCommand {

    @Parameter(
            names = "--data-dir",
            required = true,
            validateWith = DirectoryParameterValidator.class,
            description = "A directory where the date files are stored locally"
    )
    private String destinationDir;

    public String getDestinationDir() {
        return destinationDir;
    }

    public ConfigCommand() throws SQLException {
        destinationDir = Repositories.getDefaultDestination();
    }

    public void config() throws SQLException {
        Repositories.updateDefaultDestination(destinationDir);
    }
}
