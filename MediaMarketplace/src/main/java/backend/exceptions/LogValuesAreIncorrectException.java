package backend.exceptions;

import java.util.HashSet;
import java.util.Set;

import DataStructures.UserLogInfo;

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

}
