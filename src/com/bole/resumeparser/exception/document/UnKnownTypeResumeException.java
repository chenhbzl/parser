package com.bole.resumeparser.exception.document;

public class UnKnownTypeResumeException extends Exception {

	/**
	 * 不能识别的简历
	 */
	
	private static final long serialVersionUID = 6084462207586849526L;

	public UnKnownTypeResumeException() {
		super();
	}

	public UnKnownTypeResumeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnKnownTypeResumeException(String message) {
		super(message);
	}

	public UnKnownTypeResumeException(Throwable cause) {
		super(cause);
	}
}
