package frontend.admin.createMovieLogView;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Represents a log record for displaying in the movie creation log view.
 * <p>This class encapsulates information from a {@link LogRecord} such as the timestamp,
 * log level, and message, and formats the timestamp for display.</p>
 */
public class LoggerRecord {
    
    /** DateTimeFormatter for formatting the timestamp of log records. */
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                                                                        .withZone(ZoneId.systemDefault());

    /** The timestamp of the log record in a formatted string. */
    private String timestamp;
    
    /** The level of the log record. */
    private Level level;
    
    /** The message of the log record. */
    private String message;
    
    /**
     * Constructs a {@code LoggerRecord} from a {@link LogRecord}.
     * <p>Formats the timestamp of the log record and initializes the level and message fields.</p>
     * 
     * @param logRecord The {@link LogRecord} to convert into a {@code LoggerRecord}.
     */
    public LoggerRecord(LogRecord logRecord) {
        Instant instant = logRecord.getInstant();
        String timestamp = "";
        if (instant != null) {
            timestamp = formatter.format(instant);
        }
        this.timestamp = timestamp;
        this.level = logRecord.getLevel();
        this.message = logRecord.getMessage();
    }

    /**
     * Gets the timestamp of the log record.
     * 
     * @return The timestamp of the log record.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the level of the log record.
     * 
     * @return The level of the log record.
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Gets the message of the log record.
     * 
     * @return The message of the log record.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the timestamp of the log record.
     * 
     * @param timestamp The new timestamp for the log record.
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Sets the level of the log record.
     * 
     * @param level The new level for the log record.
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * Sets the message of the log record.
     * 
     * @param message The new message for the log record.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}