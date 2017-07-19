package dev.exception;

public class CalculException extends RuntimeException {

	public CalculException() {
		super();
	}

	public CalculException(String message, Throwable cause) {
		super(message, cause);
	}

	public CalculException(String message) {
		super(message);
	}

	public CalculException(Throwable cause) {
		super(cause);
	}

}
