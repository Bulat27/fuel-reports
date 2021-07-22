package cli.view_models;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

public class DateParameterValidator implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws ParameterException {
        //DateTimeFormatterBuilder dtfBuilder = new DateTimeFormatterBuilder();
        DateTimeFormatter yearMonthDayDtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();
        DateTimeFormatter yearMonthDtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM").parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter();
        DateTimeFormatter yearDtf = new DateTimeFormatterBuilder().appendPattern("yyyy")
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1).parseDefaulting(ChronoField.DAY_OF_MONTH ,1).toFormatter();

        if(isValid(value, yearMonthDayDtf) || isValid(value, yearMonthDtf) || isValid(value, yearDtf)) return;

        String message = String.format("The [%s] is not in a valid format.",  value);
        throw new ParameterException(message);
    }

    private boolean isValid(String dateStr, DateTimeFormatter dtf){
        try {
            LocalDate.parse(dateStr, dtf);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
