package controller.pop3;

public enum POP3StatusIndicator {
	OK, ERR;

	public String toString(){
		if(this.equals(OK)){
			return "+OK";
		}else{
			return "-ERR";
		}			
	}
}
