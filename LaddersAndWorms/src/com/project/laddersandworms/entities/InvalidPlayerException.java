package com.project.laddersandworms.entities;

public class InvalidPlayerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InvalidPlayerException() { super(); }
	public InvalidPlayerException(String message) { super(message); }
	public InvalidPlayerException(String message, Throwable cause) { super(message, cause); }
	public InvalidPlayerException(Throwable cause) { super(cause); }


}
