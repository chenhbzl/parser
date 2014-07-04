package com.bole.resumeparser.exception;

public class HtmlResumeParseException extends ResumeParseException {
	
	/**
	 * 解析html简历时候的异常
	 */
	private static final long serialVersionUID = -2422053352719143037L;

	public HtmlResumeParseException() {
		super();
	}

	public HtmlResumeParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public HtmlResumeParseException(String message) {
		super(message);
	}

	public HtmlResumeParseException(Throwable cause) {
		super(cause);
	}
}
