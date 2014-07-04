package com.bole.resumeparser.exception;

public class UnkownResumeException extends HtmlResumeParseException {

	/**
	 * 未知错误的解析失败
	 */
	private static final long serialVersionUID = 8025197471221453699L;

	public UnkownResumeException() {
		super();
	}

	public UnkownResumeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnkownResumeException(String message) {
		super(message);
	}

	public UnkownResumeException(Throwable cause) {
		super(cause);
	}
}
