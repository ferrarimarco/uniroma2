package controller.smtp;

public enum SMTPCommand {

	EHLO, HELO, MAIL, RCPT, DATA, QUIT, RSET, NOOP, EMPTY, UNSUPPORTED;
	
	public static SMTPCommand parseCommand(String value){
		
		value = value.toUpperCase();
		
		if(value.equals(EHLO.toString())){
			return EHLO;
		}else if(value.equals(HELO.toString())){
			return HELO;
		}else if(value.equals(MAIL.toString())){
			return MAIL;
		}else if(value.equals(RCPT.toString())){
			return RCPT;
		}else if(value.equals(DATA.toString())){
			return DATA;
		}else if(value.equals(QUIT.toString())){
			return QUIT;
		}else if(value.equals(RSET.toString())){
			return RSET;
		}else if(value.equals(NOOP.toString())){
			return NOOP;
		}else if(value.equals(EMPTY.toString())){
			return EMPTY;
		}else{
			return UNSUPPORTED;
		}
	}
	
}
