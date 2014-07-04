package com.bole.resumeparser.message;

import java.util.ArrayList;

public class ResumePreProcessInfo {
	//该结构主要用于存放简历预处理之后的信息，包括简历所属网站，简历的类型，以及简历转换并读取之后的源文件信息等
	public ResumeSourceInfo resumeSourceInfo = new ResumeSourceInfo();
	
	public ResumeMetaData resumeMetaData = new ResumeMetaData();
	
	public ResumeSourceInfo getResumeSourceInfo() {
		return resumeSourceInfo;
	}

	public void setResumeSourceInfo(ResumeSourceInfo resumeSourceInfo) {
		this.resumeSourceInfo = resumeSourceInfo;
	}

	public ResumeMetaData getResumeMetaData() {
		return resumeMetaData;
	}

	public void setResumeMetaData(ResumeMetaData resumeMetaData) {
		this.resumeMetaData = resumeMetaData;
	}

	
	public class ResumeSourceInfo{
		String resumeSource = "";  //简历的来源
		String resumeType = "";   //简历的类型
		
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
	}

	public class ResumeMetaData{
		public ArrayList<String> sourceDataList = new ArrayList<String>();
		public String txtFilePath = "";
		
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


}