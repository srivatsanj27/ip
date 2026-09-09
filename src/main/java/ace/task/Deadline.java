package ace.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Function;

import ace.exception.WrongDateFormatException;

/**
 * A task with a due date and time. Accepts several input formats when constructed
 * from a String (see {@link #parseDate(String)}), and keeps separate formats for
 * how the deadline is saved to disk versus how it's shown to the user.
 */
public class Deadline extends Task {
    // how the user is expected to input the time
    private static final DateTimeFormatter INPUT_TYPE = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    // how the time will be displayed in the chatbox
    private static final DateTimeFormatter OUTPUT_TYPE = DateTimeFormatter.ofPattern("MMM dd yyyy 'at' h:mma");

    // an ISO-like alternative to INPUT_TYPE, accepted as a fallback
    private static final DateTimeFormatter ALTERNATE_INPUT_TYPE = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /* another option for the users to not input a timing at all and just give the date. the deadline will be auto
       defaulted to 2359 of that date */
    private static final DateTimeFormatter DATE_ONLY_INPUT = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final LocalTime DEFAULT_TIME = LocalTime.of(23, 59);

    /* tried in order until one parses successfully; each entry is a small parser for one
       accepted input format, so parseDate can just loop over these instead of nesting a
       try/catch per format */
    private static final List<Function<String, LocalDateTime>> DATE_PARSERS = List.of(
            dateString -> LocalDateTime.parse(dateString, INPUT_TYPE),
            dateString -> LocalDateTime.parse(dateString, ALTERNATE_INPUT_TYPE),
            dateString -> LocalDate.parse(dateString, DATE_ONLY_INPUT).atTime(DEFAULT_TIME));

    private final LocalDateTime byWhen;

    /**
     * Creates a new, incomplete deadline with the given description and due
     * date/time.
     *
     * @param description the text describing what this deadline is.
     * @param byWhen the date and time this deadline is due.
     */
    public Deadline(String description, LocalDateTime byWhen) {
        super(description);
        this.byWhen = byWhen;
    }

    /**
     * Creates a new, incomplete deadline by parsing its due date/time from text.
     * Overloads the {@link #Deadline(String, LocalDateTime)} constructor for
     * callers (such as the command parser and save-file loader) that only have the
     * date as a raw String.
     *
     * @param description the text describing what this deadline is.
     * @param byWhen the due date/time as text, in any format {@link #parseDate(String)} accepts.
     * @throws WrongDateFormatException if byWhen cannot be parsed as a date/time.
     */
    public Deadline(String description, String byWhen) throws WrongDateFormatException {
        this(description, parseDate(byWhen));
    }

    /**
     * Returns this deadline's due date and time.
     *
     * @return the date and time this deadline is due.
     */
    public LocalDateTime getByWhen() {
        return this.byWhen;
    }

    /**
     * Parses a due date/time from text, trying progressively looser formats in
     * order: the full "d/M/yyyy HHmm" format, a "yyyy-MM-dd HHmm" fallback, and
     * finally a date-only "d/M/yyyy" format (with the time defaulted to
     * {@link #DEFAULT_TIME}) for users who don't want to specify a time at all.
     *
     * @param dateString the due date/time as text.
     * @return the parsed date and time.
     * @throws WrongDateFormatException if dateString matches none of the accepted formats.
     */
    private static LocalDateTime parseDate(String dateString) throws WrongDateFormatException {
        for (Function<String, LocalDateTime> parser : DATE_PARSERS) {
            try {
                return parser.apply(dateString);
            } catch (DateTimeParseException e) {
                // this format didn't match; fall through and try the next one
            }
        }

        throw new WrongDateFormatException(dateString);
    }

    /**
     * Parses a date-only string into a {@link LocalDate}, using the same
     * "d/M/yyyy" format {@link #parseDate(String)} accepts when a deadline is
     * given a date with no time. Exposed for the command parser's "date"
     * command, which looks up tasks due on a given day and needs to parse
     * that day using the same format Deadline itself understands, without
     * exposing the {@link #DATE_ONLY_INPUT} formatter directly.
     *
     * @param dateString the date as text, in "d/M/yyyy" format.
     * @return the parsed date.
     * @throws WrongDateFormatException if dateString doesn't match "d/M/yyyy".
     */
    public static LocalDate parseDateOnly(String dateString) throws WrongDateFormatException {
        try {
            return LocalDate.parse(dateString, DATE_ONLY_INPUT);
        } catch (DateTimeParseException e) {
            throw new WrongDateFormatException(dateString);
        }
    }

    /**
     * Returns the representation of this deadline used when saving it to disk,
     * with the due date/time in the same machine-parseable format it was read in
     * as, so it can be loaded back exactly.
     *
     * @return the save-file representation of this deadline.
     */
    @Override
    public String loadFormat() {
        return "[D]" + super.toString() + " (by: " + byWhen.format(INPUT_TYPE) + ")";
    }

    /**
     * Returns this deadline's display representation, with the due date/time in a
     * human-readable format, e.g. "[D][ ] return book (by: Dec 02 2019 at 6:00pm)".
     *
     * @return this deadline formatted for display.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + byWhen.format(OUTPUT_TYPE) + ")";
    }
}
