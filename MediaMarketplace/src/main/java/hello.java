import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;

import org.springframework.beans.BeansException;

public class hello {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter//.ofLocalizedDateTime(FormatStyle.LONG)
																		.ofPattern("dd/MM/yyyy HH:mm:ss")
																		.withZone(ZoneId.systemDefault());;

	public static void main(String... args) throws BeansException, Exception {
		LocalDate localDate = LocalDate.of(2019, 8, 1);
		System.out.println(localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
		
		String path = "https:/www.themoviedb.org/t/p/original/1Ci05BbrRgc8qwKSvliJH6gqzol.jpg";
		
		String fullPath = new File(path).toURI().toURL().toExternalForm();
		System.out.println(fullPath);
		
		//System.out.println(new URL("/gandalaf"));
		
		System.out.println(LocalDateTime.of(LocalDate.of(0, 1, 3), LocalTime.of(12, 0)));
		
		
		Duration initialDuration = Duration.ofHours(21).plusMinutes(34);
		
		
		Instant now = Instant.now();
		System.out.println(formatter.format(now));
		
	}

}
