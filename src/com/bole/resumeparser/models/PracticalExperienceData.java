package com.bole.resumeparser.models;

import java.util.ArrayList;

import java.util.List;

public class PracticalExperienceData {
	//实践经验
	String practiceTitle = "";
	String startTime = "";
	String endTime = "";
	String practiceDesc = "";
	
	String loacation = ""; //实践经验地点
	
	public String getPracticeTitle() {
		return practiceTitle;
	}
	public void setPracticeTitle(String practiceTitle) {
		this.practiceTitle = practiceTitle;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getPracticeDesc() {
		return practiceDesc;
	}
	public void setPracticeDesc(String practiceDesc) {
		this.practiceDesc = practiceDesc;
	}
	
	@Override
	public String toString(){
		return "[practiceTitle="
				+ practiceTitle + ", startTime=" + startTime + ", endTime=" + endTime 
				+ ", practiceDesc=" + practiceDesc + "]";
	}
	
	public String getString(){
		return practiceTitle +" "+ startTime +" "+ endTime +" "+ practiceDesc;
	}
}
