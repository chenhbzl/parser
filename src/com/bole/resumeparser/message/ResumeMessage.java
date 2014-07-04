package com.bole.resumeparser.message;

public class ResumeMessage {
	private String resumeId;

	private String fileId;

	private String path;

	private String contentType;

	private String convertedFilePath;
	
	private String startProcess;
	

	public String getStartProcess() {
		return startProcess;
	}

	public void setStartProcess(String startProcess) {
		this.startProcess = startProcess;
	}

	public String getConvertedFilePath() {
		return convertedFilePath;
	}

	public void setConvertedFilePath(String convertedFilePath) {
		this.convertedFilePath = convertedFilePath;
	}

	/**
	 * @return the resumeId
	 */
	public String getResumeId() {
		return resumeId;
	}

	/**
	 * @param resumeId
	 *            the resumeId to set
	 */
	public void setResumeId(String resumeId) {
		this.resumeId = resumeId;
	}

	/**
	 * @return the fileId
	 */
	public String getFileId() {
		return fileId;
	}

	/**
	 * @param fileId
	 *            the fileId to set
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}