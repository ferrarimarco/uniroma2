package controller.net;

public enum Domain {

	LOCAL_HOST, UNKNOWN;
	
	public static Domain parseDomain(String value) {

		value = value.toLowerCase();

		if (value.equals(LOCAL_HOST.toString())) {
			return LOCAL_HOST;
		}else{
			return UNKNOWN;
		}
	}
	
	@Override
	public String toString() {

		String value = "";

		if (this.equals(LOCAL_HOST)) {
			value = "ec2-54-243-216-96.compute-1.amazonaws.com";
		}else if(this.equals(UNKNOWN)){
			value = ""; 
		}

		return value;
	}
	
}
