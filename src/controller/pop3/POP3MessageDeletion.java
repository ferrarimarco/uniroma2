package controller.pop3;

public enum POP3MessageDeletion {

	YES, NO, UNKNOWN;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

	public static POP3MessageDeletion parseValue(String value) {
		if(value.equals(YES.toString())) {
			return YES;
		}else if(value.equals(NO.toString())) {
			return NO;
		} else {
			return UNKNOWN;
		}
	}
	
}
