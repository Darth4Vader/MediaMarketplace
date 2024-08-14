package backend.dto.users;

import backend.entities.User;

public class LogInResponseDto {
	
	private String jwt;

	public LogInResponseDto(String jwt) {
		super();
		this.jwt = jwt;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

}
