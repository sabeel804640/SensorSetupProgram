package com.discoveritech.sensorsetupprogram.LibraryClasses;

public class TokenExpiredException extends FernetException {
	private static final long serialVersionUID = 1L;

	public TokenExpiredException(String message) {
		super(message);
	}
}
