package backend;

public class DataUtils {
	
	public static boolean isBlank(String str) {
		return !isNotBlank(str);
	}

	public static boolean isNotBlank(String str) {
		return str != null && !str.isBlank();
	}

}
