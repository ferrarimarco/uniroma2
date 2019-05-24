package controller.pop3;

public enum POP3SessionStatus {
	GREETINGS, AUTHORIZATION, TRANSACTION, UPDATE, UNKNOWN;

	public static POP3SessionStatus parseStatus(String value) {

		value = value.toLowerCase();

		if (value.equals(GREETINGS.toString())) {
			return GREETINGS;
		} else if (value.equals(AUTHORIZATION.toString())) {
			return AUTHORIZATION;
		} else if (value.equals(TRANSACTION.toString())) {
			return TRANSACTION;
		} else if (value.equals(UPDATE.toString())) {
			return UPDATE;
		} else {
			return UNKNOWN;
		}
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
