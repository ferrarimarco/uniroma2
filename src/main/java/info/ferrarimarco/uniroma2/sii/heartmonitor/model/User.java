package info.ferrarimarco.uniroma2.sii.heartmonitor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
	
	@Id
	private String userName;
	private String hashedPassword;
	
	@Override
	public String toString() {
		return "User [userName=" + userName + ", hashedPassword=" + hashedPassword + "]";
	}

	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getHashedPassword() {
		return hashedPassword;
	}
	
	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}
}
