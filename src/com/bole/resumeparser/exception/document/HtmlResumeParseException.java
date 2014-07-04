package com.bole.resumeparser.exception.document;

import com.bole.resumeparser.exception.ResumeParseException;

public class HtmlResumeParseException extends ResumeParseException {

	/**
	 * 简历处理异常
	 */
	private static final long serialVersionUID = -5411598418222453486L;

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
