import java.io.File;
import java.net.URL;
import java.time.LocalDate;import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.springframework.beans.BeansException;

public class hello {

	public static void main(String... args) throws BeansException, Exception {
		LocalDate localDate = LocalDate.of(2019, 8, 1);
		System.out.println(localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
		
		String path = "https:/www.themoviedb.org/t/p/original/1Ci05BbrRgc8qwKSvliJH6gqzol.jpg";
		
		String fullPath = new File(path).toURI().toURL().toExternalForm();
		System.out.println(fullPath);
		
		System.out.println(new URL("/gandalaf"));
		
	}

}
