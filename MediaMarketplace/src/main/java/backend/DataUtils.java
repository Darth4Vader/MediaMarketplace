package backend;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import backend.dto.mediaProduct.MoviePurchasedDto;
import javafx.scene.control.Label;

public class DataUtils {
	
	public static boolean isBlank(String str) {
		return !isNotBlank(str);
	}

	public static boolean isNotBlank(String str) {
		return str != null && !str.isBlank();
	}
	
	public static boolean isListEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}
	
	public static boolean equalsIgnoreCase(String str1, String str2) {
		return str1 != null && str1.equalsIgnoreCase(str2);
	}

	public static Double getNumber(String str) {
		try {
			return Double.parseDouble(str);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
	
	public static Integer getIntegerNumber(String str) {
		try {
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
	
	public static BigDecimal getBigDecimal(String str) {
		if(DataUtils.isNotBlank(str))
			return new BigDecimal(getNumber(str));
		return null;
	}
	
	public static String getLocalDateTimeInCurrentZone(LocalDateTime localDateTime) {
		ZonedDateTime date = localDateTime.atZone(ZoneId.systemDefault());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy HH:mm:ss");
		return date.format(formatter);
	}
	
	public static String durationToString(Duration duration) {
        String time  = String.format("%02d days: %02d:%02d:%02d", duration.toDays(), duration.toHoursPart(), duration.toMinutesPart(),
                duration.toSecondsPart());
        return time;
	}
	
	public static boolean isUseable(MoviePurchasedDto moviePurchased) {
		return isUseable(moviePurchased.isRented(), moviePurchased.getRentTimeSincePurchase());
	}
	
	public static boolean isUseable(boolean isRented, LocalDateTime timeSince) {
		if(!isRented)
			return true;
		LocalDateTime now = LocalDateTime.now();
		return now.isBefore(timeSince);
	}
}
