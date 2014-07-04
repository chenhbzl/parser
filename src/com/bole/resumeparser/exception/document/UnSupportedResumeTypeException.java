package com.bole.resumeparser.exception.document;

public class UnSupportedResumeTypeException extends Exception {
	/**
	 * 不支持的简历
	 */
	private static final long serialVersionUID = 4718810515107491013L;

	public UnSupportedResumeTypeException() {
		super();
	}

	public UnSupportedResumeTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnSupportedResumeTypeException(String message) {
		super(message);
	}

	public UnSupportedResumeTypeException(Throwable cause) {
		super(cause);
	}
}
