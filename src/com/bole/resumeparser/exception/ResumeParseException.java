package com.bole.resumeparser.exception;

public class ResumeParseException extends Exception {

	/**
	 * 简历处理异常
	 */
	private static final long serialVersionUID = -5411598418222453486L;

	public ResumeParseException() {
		super();
	}

	public ResumeParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResumeParseException(String message) {
		super(message);
	}

	public ResumeParseException(Throwable cause) {
		super(cause);
	}

}
