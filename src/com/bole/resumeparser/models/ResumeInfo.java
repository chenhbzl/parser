package com.bole.resumeparser.models;

import java.util.ArrayList;

public class ResumeInfo {
	//该结构主要用于存放简历预处理之后的信息，包括简历所属网站，简历的类型，以及简历转换并读取之后的源文件信息等
	String resumeSource = "";  //简历的来源
	String resumeType = "";   //简历的类型
	
	public ArrayList<String> sourceDataList = new ArrayList<String>();  //文本内容链表
	public String txtFilePath = "";   //文本转换后的txt文件
	
	public String getResumeSource() {
		return resumeSource;
	}
	public void setResumeSource(String resumeSource) {
		this.resumeSource = resumeSource;
	}
	public String getResumeType() {
		return resumeType;
	}
	public void setResumeType(String resumeType) {
		this.resumeType = resumeType;
	}
	public ArrayList<String> getSourceDataList() {
		return sourceDataList;
	}
	public void setSourceDataList(ArrayList<String> sourceDataList) {
		this.sourceDataList = sourceDataList;
	}
	public String getTxtFilePath() {
		return txtFilePath;
	}
	public void setTxtFilePath(String txtFilePath) {
		this.txtFilePath = txtFilePath;
	}
	
	
}
