package controller.smtp;

public enum SMTPCode {
	GREETINGS, OK, BAD_SEQUENCE, INTERMEDIATE_REPLY, NO_VALID_RECIPIENTS, ADDRESS_UPDATING_OK, ADDRESS_UPDATING_ERR, MESSAGE_UNDELIVERABLE, USER_AMBIGUOUS, UNABLE_TO_VERIFY_MAILING_LIST_MEMBERS,
	TERMINATION_NEEDED, QUIT_OK_RESPONSE, UNSUPPORTED_COMMAND;

	@Override
	public String toString(){
		
		String value = "";
		
		if(this.equals(GREETINGS)){
			value = "220";
		}else if(this.equals(OK)){
			value = "250";
		}else if(this.equals(BAD_SEQUENCE)){
			value = "503";
		}else if(this.equals(INTERMEDIATE_REPLY)){
			value = "354";
		}else if(this.equals(NO_VALID_RECIPIENTS)){
			value = "554";
		}else if(this.equals(ADDRESS_UPDATING_OK)){
			value = "251";
		}else if(this.equals(ADDRESS_UPDATING_ERR)){
			value = "551";
		}else if(this.equals(MESSAGE_UNDELIVERABLE)){
			value = "550";
		}else if(this.equals(USER_AMBIGUOUS)){
			value = "553";
		}else if(this.equals(UNABLE_TO_VERIFY_MAILING_LIST_MEMBERS)){
			value = "252";
		}else if(this.equals(TERMINATION_NEEDED)){
			value = "421";
		}else if(this.equals(QUIT_OK_RESPONSE)){
			value = "221";
		}else if(this.equals(UNSUPPORTED_COMMAND)){
			value = "500";
		}
		
		return value;
	}
}