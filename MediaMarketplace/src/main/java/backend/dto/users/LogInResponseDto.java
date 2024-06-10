package backend.dto.users;

import backend.entities.User;

public class LogInResponseDto {
	
	private User user;
	
	private String jwt;

	public LogInResponseDto(User user, String jwt) {
		super();
		this.user = user;
		this.jwt = jwt;
	}

	public User getUser() {
		return user;
	}

	public String getJwt() {
		return jwt;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

}
