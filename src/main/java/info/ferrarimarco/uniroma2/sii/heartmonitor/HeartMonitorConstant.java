package info.ferrarimarco.uniroma2.sii.heartmonitor;


public enum HeartMonitorConstant {
	GENERIC_USERNAME("genericUser");
	
	private String value;
	
	private HeartMonitorConstant(String value) {
		this.value = value;
	}
	
	public String getStringValue() {
		return value;
	}
}
