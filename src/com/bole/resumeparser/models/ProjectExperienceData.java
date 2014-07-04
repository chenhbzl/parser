package com.bole.resumeparser.models;

import java.util.ArrayList;

import java.util.List;

public class ProjectExperienceData {

	//项目名称
	String projectTitle = "";
	
	//项目开始时间
	String startTime = "";
	
	//软件环境
	String softwareEnvir = "";
	
	//硬件环境
	String hardEnvir = "";
	
	//开发工具
	String developTool = "";
	
	//项目介绍时间
	String endTime = "";
	
	//职责描述
	String responsibleFor = "";
	
	//项目描述
	String projectDesc = "";
	
	//项目所在公司 （猎聘专用）
	String company = "";
	//项目职务
	String positionTitle = "";
	//项目业绩
	String projectPerformance = "";
	
	public String getSoftwareEnvir() {
		return softwareEnvir;
	}

	public void setSoftwareEnvir(String softwareEnvir) {
		this.softwareEnvir = softwareEnvir;
	}	

	public String getProjectPerformance() {
		return projectPerformance;
	}

	public void setProjectPerformance(String projectPerformance) {
		this.projectPerformance = projectPerformance;
	}

	public String getCompany() {
		return company;
	}	

	public String getPositionTitle() {
		return positionTitle;
	}

	public void setPositionTitle(String positionTitle) {
		this.positionTitle = positionTitle;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getHardEnvir() {
		return hardEnvir;
	}

	public void setHardEnvir(String hardEnvir) {
		this.hardEnvir = hardEnvir;
	}	

	public String getDevelopTool() {
		return developTool;
	}

	public void setDevelopTool(String developTool) {
		this.developTool = developTool;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
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

	public String getResponsibleFor() {
		return responsibleFor;
	}

	public void setResponsibleFor(String responsibleFor) {
		this.responsibleFor = responsibleFor;
	}

	public String getProjectDesc() {
		return projectDesc;
	}

	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}

	@Override
	public String toString(){
		return "ProjectExperience [projectTitle="
				+ projectTitle + ", startTime=" + startTime + ", endTime=" + endTime 
				+ ", softwareEnvir=" + softwareEnvir + 	", hardEnvir=" + hardEnvir +", developTool=" + developTool 
				+ ", responsibleFor=" + responsibleFor + ", projectDesc=" + projectDesc 
				+ "]";
	}
	
	public String getString(){
		return projectTitle + " " + startTime +" "+ softwareEnvir +" "+ hardEnvir  +" "+ developTool  +" "+ endTime +" "+ responsibleFor  +" "+ projectDesc +" "+ company +" "+ positionTitle +" "+ projectPerformance;
	} 
	
}
