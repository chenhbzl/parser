package com.bole.resumeparser.models;

import java.util.ArrayList;

import java.util.List;

public class TrainingExperienceData {
	//培训开始
	String startTime = "";
	
	//培训结束时间
	String endTime = "";
	
	//培训机构
	String instituation = "";
	
	//培训地点
	String location = "";
	
	//培训课程
	String course = "";
	
	//获得证书
	String certificate = "";
	//培训描述
	String trainDesc = "";
		
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

	public String getInstituation() {
		return instituation;
	}

	public void setInstituation(String instituation) {
		this.instituation = instituation;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getTrainDesc() {
		return trainDesc;
	}

	public void setTrainDesc(String trainDesc) {
		this.trainDesc = trainDesc;
	}

	@Override
	public String toString(){
		return "WorkExperience [startTime=" + startTime + ", endTime=" + endTime + ", instituation=" + instituation + ", location="
		        + location + ", course = " + course + ", certificate=" + certificate + ", trainDesc=" + trainDesc
				+"]";
	}
	
	public String getString(){
		return startTime + " "+ endTime +" "+ instituation +" "+ location +" "+ course +" "+ certificate +" "+ trainDesc;
	}
	
}
