package it.mp.claudianiferrari.parserjson;

public class PhoneNumbers {
	private String mobile;
	private String home;
	private String office;
	
	public PhoneNumbers(String mobile, String home, String office) {
		super();
		this.mobile = mobile;
		this.home = home;
		this.office = office;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}
	

}
