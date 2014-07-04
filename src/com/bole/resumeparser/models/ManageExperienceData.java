package com.bole.resumeparser.models;


public class ManageExperienceData{
	//高级管理人员信息
	//汇报对象
	String reportTo = "";
	//下属人数
	String subordinatesNum = "";
	//直接下属
	String suborinates = "";
	//证明人
	String  reterence = "";
	//离职原因
	String leavingReason = "";
	//主要业绩
	String keyPerformance = "";
	//是否有海外工作经验
	String overseasWorkExperience = "";
	
	public String getReportTo() {
		return reportTo;
	}

	public void setReportTo(String reportTo) {
		this.reportTo = reportTo;
	}

	public String getSubordinatesNum() {
		return subordinatesNum;
	}

	public void setSubordinatesNum(String subordinatesNum) {
		this.subordinatesNum = subordinatesNum;
	}

	public String getReterence() {
		return reterence;
	}

	public void setReterence(String reterence) {
		this.reterence = reterence;
	}

	public String getLeavingReason() {
		return leavingReason;
	}

	public void setLeavingReason(String leavingReason) {
		this.leavingReason = leavingReason;
	}

	public String getKeyPerformance() {
		return keyPerformance;
	}

	public void setKeyPerformance(String keyPerformance) {
		this.keyPerformance = keyPerformance;
	}

	public String getOverseasWorkExperience() {
		return overseasWorkExperience;
	}

	public void setOverseasWorkExperience(String overseasWorkExperience) {
		this.overseasWorkExperience = overseasWorkExperience;
	}
	
	public String getSuborinates() {
		return suborinates;
	}

	public void setSuborinates(String suborinates) {
		this.suborinates = suborinates;
	}

	@Override
	public String toString(){
		return "JobTarget [reportTo="
				+ reportTo + ", subordinatesNum=" + subordinatesNum + ", suborinates=" + suborinates 
				+ ", reterence=" + reterence + 	", leavingReason=" + leavingReason +", keyPerformance=" + keyPerformance 
				+ ", overseasWorkExperience=" + overseasWorkExperience + "]";
	}
	
	public String getString(){
		return reportTo + " " +subordinatesNum +" "+ suborinates +" "+ reterence +" "+ leavingReason +" "+ keyPerformance +" "+ overseasWorkExperience;
	}
}
