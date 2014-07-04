package com.bole.resumeparser.exception;

public class DBConnectException extends Exception {

	//处理数据库连接异常	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7042098382699552863L;

	public DBConnectException() {
		super();
	}

	public DBConnectException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBConnectException(String message) {
		super(message);
	}

	public DBConnectException(Throwable cause) {
		super(cause);
	}
}
