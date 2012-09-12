package controller.pop3;

public enum POP3StatusIndicator {
	OK, ERR, UNKNOWN;

	public static POP3StatusIndicator parseStatusIndicator(String value) {

		value = value.toUpperCase();

		if (value.equals(OK.toString())) {
			return OK;
		} else if (value.equals(ERR.toString())) {
			return ERR;
		} else {
			return UNKNOWN;
		}
	}

	@Override
	public String toString() {
		if (this.equals(OK)) {
			return "+OK";
		} else {
			return "-ERR";
		}
	}
}
