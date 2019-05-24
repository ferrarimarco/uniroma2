package controller;

public enum SpecialCharactersSequence {

	LINE_END, POP3_TERMINATION_OCTET, SMTP_DATA_END, SMTP_BYTE_STUFFING, SMTP_BYTE_DESTUFFING;

	@Override
	public String toString() {
		if (this.equals(LINE_END)) {
			return "\r\n";
		} else if (this.equals(POP3_TERMINATION_OCTET)) {
			return ".";
		} else if (this.equals(SMTP_DATA_END) || this.equals(SMTP_BYTE_DESTUFFING)) {
			return "\r\n.\r\n";
		} else if (this.equals(SMTP_BYTE_STUFFING)) {
			return "\r\n..\r\n";
		} else {
			// TODO: check this return statement
			// This should never occur
			return "";
		}
	}
}
