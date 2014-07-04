package com.bole.resumeparser.exception.document;

public class UnKnownException extends Exception {	

	/**
	 * 未知错误
	 */
	private static final long serialVersionUID = 8679037109554952749L;

	public UnKnownException() {
		super();
	}

	public UnKnownException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnKnownException(String message) {
		super(message);
	}

	public UnKnownException(Throwable cause) {
		super(cause);
	}
}
