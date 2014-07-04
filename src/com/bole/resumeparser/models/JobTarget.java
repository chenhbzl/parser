package com.bole.resumeparser.models;

import java.util.ArrayList;

import java.util.List;

public class JobTarget {
	//期望工作性质
	String jobCatagory = "";
	
	//期望工作地点
	String jobLocation = "";
	
	//期望从事职业
	String jobCareer = "";
	
	//期望从事行业
	String jobIndustry = "";
	
	//期望月薪
	String salary = "";
	
	//目前状态
	String status = "";
	
	//到岗时间
	String enrollTime = "";
	
	//勿推荐企业
	String notRecomandCompanyName = "";
	
	public String getJobCatagory() {
		return jobCatagory;
	}

	public void setJobCatagory(String jobCatagory) {
		this.jobCatagory = jobCatagory;
	}

	public String getJobLocation() {
		return jobLocation;
	}

	public void setJobLocation(String jobLocation) {
		this.jobLocation = jobLocation;
	}

	public String getJobCareer() {
		return jobCareer;
	}

	public void setJobCareer(String jobCareer) {
		this.jobCareer = jobCareer;
	}

	public String getJobIndustry() {
		return jobIndustry;
	}

	public void setJobIndustry(String jobIndustry) {
		this.jobIndustry = jobIndustry;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getEnrollTime() {
		return enrollTime;
	}

	public void setEnrollTime(String enrollTime) {
		this.enrollTime = enrollTime;
	}
	
	public String getNotRecomandCompanyName() {
		return notRecomandCompanyName;
	}

	public void setNotRecomandCompanyName(String notRecomandCompanyName) {
		this.notRecomandCompanyName = notRecomandCompanyName;
	}

	@Override
	public String toString(){
		return "JobTarget [jobCatagory="
				+ jobCatagory + ", jobLocation=" + jobLocation + ", jobCareer=" + jobCareer 
				+ ", jobIndustry=" + jobIndustry + 	", salary=" + salary +", status=" + status 
				+ "]";
	}
	
	public String getString(){
		return jobCatagory + " " + jobLocation + jobCareer + " " + jobIndustry + " " + salary + " " + status + " " + enrollTime;
	}
}
