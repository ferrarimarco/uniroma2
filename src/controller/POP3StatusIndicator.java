package controller;

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
