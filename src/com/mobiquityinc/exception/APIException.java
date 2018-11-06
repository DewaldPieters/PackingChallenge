package com.mobiquityinc.exception;

/**
 * @author Dewald Pieters
 *
 */
/*
 * Custom Exception thrown by the program
 */
public class APIException extends Exception {
	/*
	 * Exception is instantiated by providing a custom error message as well as
	 * the cause of the issue if available
	 */
	public APIException(String errorMessage, Throwable rootError) {
		super(errorMessage, rootError);
	}

	// Exception is instantiated by providing a custom error message
	public APIException(String errorMessage) {
		super(errorMessage);
	}

}
