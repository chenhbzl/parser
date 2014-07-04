package com.bole.resumeparser.models;

public class ESIndexWriterMessage {
	//请求写ES index的消息
	String id = "";   //消息id
	String json = "";   //json信息
	String index = "";    //需要写入的index
	String type = "";   //写入index的type
	String action = "insert";
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
}
