package controller.pop3;

import java.util.ArrayList;
import java.util.List;

public enum POP3Command {
	CAPA, QUIT, USER, PASS, STAT, LIST, RETR, DELE, NOOP, RSET, TOP, UIDL, UNSUPPORTED, EMPTY;
	
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
		}else if(value.equals(UIDL.toString())){
			return UIDL;
		}else{
			return UNSUPPORTED;
		}
	}
	
	@Override
	public String toString(){
		return super.toString().toUpperCase();
	}
	
	public static List<String> getPOP3CapaCommands(){
		
		List<String> pop3CapaCommands = new ArrayList<String>();
		
		pop3CapaCommands.add(CAPA.toString());
		pop3CapaCommands.add(USER.toString());
		pop3CapaCommands.add(PASS.toString());
		pop3CapaCommands.add(QUIT.toString());
		pop3CapaCommands.add(STAT.toString());
		pop3CapaCommands.add(LIST.toString());
		pop3CapaCommands.add(RETR.toString());
		pop3CapaCommands.add(DELE.toString());
		pop3CapaCommands.add(TOP.toString());
		pop3CapaCommands.add(NOOP.toString());
		pop3CapaCommands.add(RSET.toString());
		pop3CapaCommands.add(UIDL.toString());
		
		return pop3CapaCommands;
	}
}
