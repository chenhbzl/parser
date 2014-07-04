package com.bole.resumeparser.message;

public class SpiderResumeMessage {
	/**
	 * 处理来自爬虫的消息
	 */
	String resumeId = "";   //简历id
	String collection = "collected_resume";   //简历所在表
	String source = "";    //简历来源
	
	public String getResumeId() {
		return resumeId;
	}
	public void setResumeId(String resumeId) {
		this.resumeId = resumeId;
	}
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
}
