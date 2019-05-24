package it.mp.claudianiferrari.parserjson;


public class Contact {

	private String id;
	private String name;
	private String email;
	private String address;
	private String gender;
	private PhoneNumbers pNumbers;

	
	public Contact(String id, String name, String email, String address, String gender, PhoneNumbers pNumbers) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.address = address;
		this.gender = gender;
		this.pNumbers = pNumbers;
		
	}
	
	@Override
	public String toString(){
		return name + System.getProperty("line.separator") + email + System.getProperty("line.separator") + pNumbers.getMobile();
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public PhoneNumbers getpNumbers() {
		return pNumbers;
	}
	
	public void setpNumbers(PhoneNumbers pNumbers) {
		this.pNumbers =  pNumbers;
	}
	

	
}
