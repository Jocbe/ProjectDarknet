package uk.ac.cam.darknet.exceptions;

public class AuthorizationFailedException extends Exception {
	public AuthorizationFailedException() {super();}
	public AuthorizationFailedException(String message) {
		super(message);
	}
}
