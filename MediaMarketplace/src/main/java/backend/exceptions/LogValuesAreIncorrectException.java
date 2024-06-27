package backend.exceptions;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import DataStructures.UserLogInfo;
import backend.DataUtils;
import backend.dto.mediaProduct.MovieReviewDto;
import backend.dto.users.UserInformationDto;
import net.bytebuddy.asm.Advice.Local;

public class LogValuesAreIncorrectException extends Exception {

	private Set<UserLogInfo> logInfoSet;

	public LogValuesAreIncorrectException(Set<UserLogInfo> logInfoSet, String message) {
		super(message);
		this.logInfoSet = logInfoSet;
	}
	
	public Set<UserLogInfo> getUserLogInfo() {
		return this.logInfoSet;
	}
	
	public static void checkForException(String username, String password) throws LogValuesAreIncorrectException {
		Set<UserLogInfo> logInfoSet = new HashSet<>();
		if(username == null || username.isEmpty()) {
			logInfoSet.add(UserLogInfo.NAME);
		}
		if(password == null || password.isEmpty())
			logInfoSet.add(UserLogInfo.PASWORD);
		if(!logInfoSet.isEmpty())
			throw new LogValuesAreIncorrectException(logInfoSet, "one or more values are missing");
	}
	
	public static void checkForException(UserInformationDto userInformationDto) throws LogValuesAreIncorrectException {
		Set<UserLogInfo> logInfoSet = new HashSet<>();
		String username = userInformationDto.getUsername();
		String password = userInformationDto.getPassword();
		String passwordConfirm = userInformationDto.getPasswordConfirm();
		if(username == null || username.isEmpty()) {
			logInfoSet.add(UserLogInfo.NAME);
		}
		if(password == null || password.isEmpty())
			logInfoSet.add(UserLogInfo.PASWORD);
		if(passwordConfirm == null || passwordConfirm.isEmpty())
			logInfoSet.add(UserLogInfo.PASSWORD_CONFIRM);
		if(!logInfoSet.isEmpty())
			throw new LogValuesAreIncorrectException(logInfoSet, "one or more values are missing");
	}

}
