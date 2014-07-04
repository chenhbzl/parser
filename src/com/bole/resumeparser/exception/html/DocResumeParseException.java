package com.bole.resumeparser.exception.html;

import com.bole.resumeparser.exception.ResumeParseException;

public class DocResumeParseException extends ResumeParseException {

	/**
	 * 简历处理异常
	 */
	private static final long serialVersionUID = -5411598418222453486L;

	public DocResumeParseException() {
		super();
	}

	public DocResumeParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DocResumeParseException(String message) {
		super(message);
	}

	public DocResumeParseException(Throwable cause) {
		super(cause);
	}

}
