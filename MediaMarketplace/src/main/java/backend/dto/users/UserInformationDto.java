package backend.dto.users;

public class UserInformationDto {
	
	private String username;
	
	private String name;
	
	private String password;
	
	private String passwordConfirm;
	
	public UserInformationDto() {
		
	}

	public UserInformationDto(String username, String name, String password, String passwordConfirm) {
		super();
		this.username = username;
		this.name = name;
		this.password = password;
		this.passwordConfirm = passwordConfirm;
	}

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}
}
