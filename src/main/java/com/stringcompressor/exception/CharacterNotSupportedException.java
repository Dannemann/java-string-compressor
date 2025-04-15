package com.stringcompressor.exception;

public class CharacterNotSupportedException extends RuntimeException {

	public CharacterNotSupportedException(byte character) {
		super("Character " + character + " is not supported.");
	}

}
