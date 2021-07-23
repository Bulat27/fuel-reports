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
        DateTimeFormatter format = getFormat(name, value);
        if (isValid(value, format)) return;

        String message = String.format("The [%s] is not in a valid format.", value);
        throw new ParameterException(message);
    }

    private boolean isValid(String dateStr, DateTimeFormatter dtf) {
        try {
            LocalDate.parse(dateStr, dtf);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    private DateTimeFormatter getFormatByValue(String value) {
        if (value.length() <= 4) return new DateTimeFormatterBuilder().appendPattern("yyyy")
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1).parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter();

        if (value.length() <= 7)
            return new DateTimeFormatterBuilder().appendPattern("yyyy-MM").parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter();

        return new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();
    }

    private DateTimeFormatter getFormat(String name, String value){
        if(name.equals("--period")) return getFormatByValue(value);

        if(name.equals("--year")) return new DateTimeFormatterBuilder().appendPattern("yyyy")
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1).parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter();

        if(name.equals("--month")) return new DateTimeFormatterBuilder().appendPattern("yyyy-MM").parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter();

        return new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();
    }
}
