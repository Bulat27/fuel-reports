package cli.view_models;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import data.Repositories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Parameters(commandNames = {"report"},
            commandDescription = "Executing a report. Reports include the average prices for the given flags.")
public class ReportCommand {

    @Parameter(names = "--period",
               description = "Reports will be printed for the desired period.",
               required = true,
               validateWith = DateParameterValidator.class)
    private String period;

    @Parameter(names = "--fuel-type",
               description = "The type of fuel for which the reports will be printed.")
    private String fuelType;

    @Parameter(names = "--petrol-station",
               description = "The name of the petrol station for which the reports will be printed",
               variableArity = true)
    private List<String> petrolStation = new ArrayList<>();

    @Parameter(names = "--city",
               description = "The name of the city for which the reports will be printed",
               variableArity = true)
    private List<String> city = new ArrayList<>();

    public String getPeriod() {
        return period;
    }

    private String getJoinedList(List<String> list){
        return list.isEmpty() ? null : String.join(" ", list);
    }

    public void report() throws SQLException {
        Repositories.printTheReport(period, fuelType, getJoinedList(petrolStation), getJoinedList(city));
    }
}
