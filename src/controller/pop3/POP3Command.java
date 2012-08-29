package controller.pop3;

public enum POP3Command {
	CAPA, QUIT, USER, PASS, STAT, LIST, RETR, DELE, NOOP, RSET, TOP, UNSUPPORTED, EMPTY;
	
	public static POP3Command parseCommand(String value){
		
		value = value.toUpperCase();
		
		if(value.equals(CAPA.toString())){
			return CAPA;
		}else if(value.equals(QUIT.toString())){
			return QUIT;
		}else if(value.equals(USER.toString())){
			return USER;
		}else if(value.equals(PASS.toString())){
			return PASS;
		}else if(value.equals(STAT.toString())){
			return STAT;
		}else if(value.equals(LIST.toString())){
			return LIST;
		}else if(value.equals(RETR.toString())){
			return RETR;
		}else if(value.equals(DELE.toString())){
			return DELE;
		}else if(value.equals(NOOP.toString())){
			return NOOP;
		}else if(value.equals(RSET.toString())){
			return RSET;
		}else if(value.equals(TOP.toString())){
			return TOP;
		}else{
			return UNSUPPORTED;
		}
	}
	
	@Override
	public String toString(){
		return super.toString().toUpperCase();
	}
}
