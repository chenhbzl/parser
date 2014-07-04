package com.bole.resumeparser.exception.document;

public class ConVertException extends Exception{

	/**
	 * 文档简历转换异常
	 */
	private static final long serialVersionUID = -9071916396619747709L;

	public ConVertException() {
		super();
	}

	public ConVertException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConVertException(String message) {
		super(message);
	}

	public ConVertException(Throwable cause) {
		super(cause);
	}
}
