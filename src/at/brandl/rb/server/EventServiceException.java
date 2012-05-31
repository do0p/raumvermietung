package at.brandl.rb.server;



public class EventServiceException extends RuntimeException {

	private static final long serialVersionUID = -3991868386256141941L;

	public EventServiceException(String pMessage, Throwable pThrowable) {
		super(pMessage, pThrowable);
	}


}
