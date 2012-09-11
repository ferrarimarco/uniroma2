package controller.smtp;

public enum SMTPCode {
	GREETINGS, OK, INTERMEDIATE_REPLY, 
	
	ADDRESS_UPDATING_OK, ADDRESS_UPDATING_ERR,
	
	BAD_SEQUENCE, NO_VALID_RECIPIENTS, MESSAGE_UNDELIVERABLE, USER_AMBIGUOUS, UNABLE_TO_VERIFY_MAILING_LIST_MEMBERS,
	TERMINATION_NEEDED, QUIT_OK_RESPONSE, SYNTAX_ERROR,
	
	UNSUPPORTED_COMMAND,
	
	// 552
	EXCEEDED_STORAGE_ALLOCATION,
	
	EMPTY, UNKNOWN;

	public static SMTPCode parseCode(String value){
		
		value = value.toUpperCase();
		
		if(value.equals(GREETINGS.toString())){
			return GREETINGS;
		}else if(value.equals(OK.toString())){
			return OK;
		}else if(value.equals(INTERMEDIATE_REPLY.toString())){
			return INTERMEDIATE_REPLY;
		}else if(value.equals(ADDRESS_UPDATING_OK.toString())){
			return ADDRESS_UPDATING_OK;
		}else if(value.equals(ADDRESS_UPDATING_ERR.toString())){
			return ADDRESS_UPDATING_ERR;
		}else if(value.equals(BAD_SEQUENCE.toString())){
			return BAD_SEQUENCE;
		}else if(value.equals(NO_VALID_RECIPIENTS.toString())){
			return NO_VALID_RECIPIENTS;
		}else if(value.equals(MESSAGE_UNDELIVERABLE.toString())){
			return MESSAGE_UNDELIVERABLE;
		}else if(value.equals(USER_AMBIGUOUS.toString())){
			return USER_AMBIGUOUS;
		}else if(value.equals(UNABLE_TO_VERIFY_MAILING_LIST_MEMBERS.toString())){
			return UNABLE_TO_VERIFY_MAILING_LIST_MEMBERS;
		}else if(value.equals(TERMINATION_NEEDED.toString())){
			return TERMINATION_NEEDED;
		}else if(value.equals(QUIT_OK_RESPONSE.toString())){
			return QUIT_OK_RESPONSE;
		}else if(value.equals(EXCEEDED_STORAGE_ALLOCATION.toString())){
			return EXCEEDED_STORAGE_ALLOCATION;
		}else if(value.equals(SYNTAX_ERROR.toString())){
			return SYNTAX_ERROR;
		}else if(value.equals(EMPTY.toString())){
			return EMPTY;
		}else{
			return UNSUPPORTED_COMMAND;
		}
	}
	
	@Override
	public String toString(){
		
		String value = "";
		
		if(this.equals(GREETINGS)){
			value = "220";
		}else if(this.equals(QUIT_OK_RESPONSE)){
			value = "221";
		}else if(this.equals(OK)){
			value = "250";
		}else if(this.equals(ADDRESS_UPDATING_OK)){
			value = "251";
		}else if(this.equals(UNABLE_TO_VERIFY_MAILING_LIST_MEMBERS)){
			value = "252";
		}else if(this.equals(INTERMEDIATE_REPLY)){
			value = "354";
		}else if(this.equals(TERMINATION_NEEDED)){
			value = "421";
		}else if(this.equals(UNSUPPORTED_COMMAND)){
			value = "500";
		}else if(this.equals(SYNTAX_ERROR)){
			value = "501";
		}else if(this.equals(BAD_SEQUENCE)){
			value = "503";
		}else if(this.equals(MESSAGE_UNDELIVERABLE)){
			value = "550";
		}else if(this.equals(ADDRESS_UPDATING_ERR)){
			value = "551";
		}else if(this.equals(EXCEEDED_STORAGE_ALLOCATION)){
			value = "552";
		}else if(this.equals(USER_AMBIGUOUS)){
			value = "553";
		}else if(this.equals(NO_VALID_RECIPIENTS)){
			value = "554";
		}else{
			value = "EMPTY";
		}
		
		return value;
	}
}