package com.bole.resumeparser.models;

import java.util.ArrayList;

import java.util.List;

import com.bole.resumeparser.models.ProfessionalSkillData;
import com.bole.resumeparser.models.TrainingExperienceData;

public class EducationExperienceData {
	//开始时间
	public String startTime = "";
	
	//结束时间
	public String endTime = "";
	
	//学校
	public String school = "";
	
	//专业
	public String major = "";
	
	//学位
	public String degree = "";

	//
	public String seriesIncurs = "";
	
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

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getSeriesIncurs() {
		return seriesIncurs;
	}

	public void setSeriesIncurs(String seriesIncurs) {
		this.seriesIncurs = seriesIncurs;
	}

	@Override
	public String toString(){
		return "EducationExperience [startTime="
				+ startTime + ", endTime=" + endTime + ", school=" + school 
				+ ", major=" + major + ", degree=" + degree 
				+ "]";
	}
	
	public String getString(){
		return startTime + " " + endTime + " " + school + " " + major + " " + degree ;
	}
}
