package cli.view_models;

import business.models.Fuel;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import data.Repositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Parameters(commandNames = {"report"},
            commandDescription = "Executing a report. Reports include the average prices for the given flags.")
public class ReportCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportCommand.class);

    @Parameter(names = {"--period", "--day", "--month", "--year"},
               description = "Reports will be printed for the desired period.",
               required = true,
               validateWith = DateParameterValidator.class)
    private String period;

    @Parameter(names = "--fuel-type",
               description = "The type of fuel for which the reports will be printed.",
               variableArity = true)
    private List<String> fuelType = new ArrayList<>();

    @Parameter(names = "--petrol-station",
               description = "The name of the petrol station for which the reports will be printed",
               variableArity = true)
    private List<String> petrolStation = new ArrayList<>();

    @Parameter(names = "--city",
               description = "The name of the city for which the reports will be printed",
               variableArity = true)
    private List<String> city = new ArrayList<>();


    private String getJoinedList(String delimiter, List<String> list){
        return list.isEmpty() ? null : String.join(delimiter, list);
    }

    public void report() throws SQLException {
        List<Fuel> reportList = Repositories.getTheReport(period, getJoinedList("", fuelType), getJoinedList(" ", petrolStation), getJoinedList(" ", city));

        if(reportList == null) return;

        printTheResultSet(reportList);
    }

    private static void printTheResultSet(List<Fuel> reportList){
        if(reportList.isEmpty()){
            LOGGER.info("There are no reports data for the given flags!");
            return;
        }

        for (Fuel r : reportList) {
            String report = r.toString();
            LOGGER.info(report);
        }
    }
}
