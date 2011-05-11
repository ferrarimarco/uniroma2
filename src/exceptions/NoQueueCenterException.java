package exceptions;

public class NoQueueCenterException extends RuntimeException {

	public NoQueueCenterException(){
		super();
	}
	
	public NoQueueCenterException(String message){
		super(message);
	}
	
}
