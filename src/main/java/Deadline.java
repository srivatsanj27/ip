import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class Deadline extends Task {
    private LocalDateTime byWhen;

    // how the user is expected to input the time
    public static final DateTimeFormatter INPUT_TYPE = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    // how the time will be displayed in the chatbox
    public static final DateTimeFormatter OUTPUT_TYPE = DateTimeFormatter.ofPattern("MMM dd yyyy 'at' h:mma");

    /* another option for the users to not input a timing at all and just give the date. the deadline will be auto
       defaulted to 2359 of that date */
    public static final DateTimeFormatter DATE_ONLY_INPUT = DateTimeFormatter.ofPattern("d/M/yyyy");
    public static final LocalTime DEFAULT_TIME = LocalTime.of(23, 59);

    public Deadline(String description, LocalDateTime byWhen) {
        super(description);
        this.byWhen = byWhen;
    }

    // overloaded constructor
    public Deadline(String description, String byWhen) throws WrongDateFormatException {
        this(description, parseDate(byWhen));
    }

    public LocalDateTime getByWhen() {
        return this.byWhen;
    }

    private static LocalDateTime parseDate(String dateString) throws WrongDateFormatException {
        try {
            return LocalDateTime.parse(dateString, INPUT_TYPE);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
            } catch (DateTimeParseException e2) {
                try {
                    LocalDate dateOnly = LocalDate.parse(dateString, DATE_ONLY_INPUT);
                    return dateOnly.atTime(DEFAULT_TIME);
                } catch (DateTimeParseException e3) {
                    throw new WrongDateFormatException(dateString);
                }
            }
        }
    }

    @Override
    public String loadFormat() {
        return "[D]" + super.toString() + " (by: " + byWhen.format(INPUT_TYPE) + ")";
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + byWhen.format(OUTPUT_TYPE) + ")";
    }
}