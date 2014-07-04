package com.bole.resumeparser.models;

public class OtherInfoData {
	String title = "";
	String content = "";
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString(){
		return "OtherInfoData [title="
				+ title + ", content=" + content +"]";
	}
	
	public String getString(){
		return title + " " + content;
	}
}
