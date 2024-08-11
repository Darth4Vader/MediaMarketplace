package frontend.admin.createMovieLogView;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LoggerRecord {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
																		.withZone(ZoneId.systemDefault());;
	
	private String timestamp;
	private Level level;
	private String message;
	
	public LoggerRecord(LogRecord logRecord) {
		Instant instant = logRecord.getInstant();
		String timestamp = "";
		if(instant != null)
			timestamp = formatter.format(instant);
		this.timestamp = timestamp;
		this.level = logRecord.getLevel();
		this.message = logRecord.getMessage();
	}

	public String getTimestamp() {
		return timestamp;
	}

	public Level getLevel() {
		return level;
	}

	public String getMessage() {
		return message;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
