package com.bole.resumeparser.models;

import java.util.ArrayList;

import java.util.List;

public class WorkExperienceData implements Comparable<WorkExperienceData>{
	
	//开始时间
	String startTime = "";
	//结束时间
	String endTime = "";
	
	//获取工作时间年限
	String druation = "";
	
	//公司名称
	String companyName = "";
	
	//企业性质
	String companyCatagory = "";
	//企业规模
	String companyScale = "";
	
	//行业类别
	String industryCatagory = "";
	
	//职位类别
	String positionCatagory = "";
	
	//职位名称
	String positionTitle = "";
	
	//月薪
	String salary = "";
	
	//工作描述
	String jobDesc = "";
	
	//所属部门
	String department = "";
	
	//工作地点
	String location = "";
	
	//公司描述
	String companyDesc = "";
	
	
	//管理经验
	public ArrayList<ManageExperienceData> manageExperienceDataList = new ArrayList<ManageExperienceData>();
	

	public ArrayList<ManageExperienceData> getManageExperienceList() {
		return manageExperienceDataList;
	}

	public void setManageExperienceList(ArrayList<ManageExperienceData> manageExperienceList) {
		this.manageExperienceDataList = manageExperienceList;
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

	
	public String getDruation() {
		return druation;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setDruation(String druation) {
		this.druation = druation;
	}

	public String getCompanyName() {
		return companyName;
	}
	

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyCatagory() {
		return companyCatagory;
	}

	public void setCompanyCatagory(String companyCatagory) {
		this.companyCatagory = companyCatagory;
	}

	public String getCompanyScale() {
		return companyScale;
	}

	public void setCompanyScale(String companyScale) {
		this.companyScale = companyScale;
	}

	public String getIndustryCatagory() {
		return industryCatagory;
	}

	public void setIndustryCatagory(String industryCatagory) {
		this.industryCatagory = industryCatagory;
	}

	public String getPositionCatagory() {
		return positionCatagory;
	}

	public void setPositionCatagory(String positionCatagory) {
		this.positionCatagory = positionCatagory;
	}

	public String getPositionTitle() {
		return positionTitle;
	}

	public void setPositionTitle(String positionTitle) {
		this.positionTitle = positionTitle;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}
	public void addManageExperience(ManageExperienceData manageExperienceData){
		manageExperienceDataList.add(manageExperienceData);
	}

	public String getCompanyDesc() {
		return companyDesc;
	}

	public void setCompanyDesc(String companyDesc) {
		this.companyDesc = companyDesc;
	}

	public ArrayList<ManageExperienceData> getManageExperienceDataList() {
		return manageExperienceDataList;
	}

	public void setManageExperienceDataList(
			ArrayList<ManageExperienceData> manageExperienceDataList) {
		this.manageExperienceDataList = manageExperienceDataList;
	}

	@Override
	public int compareTo(WorkExperienceData work) {
		// TODO Auto-generated method stub
		
		if(work.getStartTime() == "" || this.getStartTime() == "" || work.getStartTime() == null || this.getStartTime() == null){
			//参数错误: 返回 -2
			return -2;
		}else{
			int startYear1 =  Integer.parseInt(this.getStartTime().split(".")[0]);
			int startYear2 =  Integer.parseInt(work.getStartTime().split(".")[0]);
			
			int startMonth1 = Integer.parseInt(this.getEndTime().split(".")[0]);
			int startMonth2 = Integer.parseInt(work.getEndTime().split(".")[0]);
			
			if(startYear1 < startYear2){
				return -1;
			}else if(startYear1 == startYear2){
				if(startMonth1 > startMonth2){
					return 1;
				}else if(startMonth1 == startMonth2){
					return 0;
				}else{
					return -1;
				}
			}else{
				return 1;
			}
		}
		
	}

	@Override
	public String toString() {
		return "WorkExperience [start="
				+ startTime + ", end=" + endTime + ", duration=" + druation 
				+ ", companyName=" + companyName + 	", companyCatagory=" + companyCatagory +", companyScale=" + companyScale 
				+ ", industryCatagory=" + industryCatagory + ", positionCatagory=" + positionCatagory + ", pisitionTitle= " + positionTitle 
				+ ", salary=" + salary + ", jobDesc= " + jobDesc +  
		        "]";
	}
	
	public String getString(){
		String text = "";
		text = endTime +" "+ startTime +" "+ druation +" "+ companyName +" "
				+ companyCatagory +" "+ companyScale +" "+ industryCatagory +" "+ positionTitle 
				+" "+ salary +" "+ jobDesc +" "+ department +" "+  location;
		
		for(int i=0;i<manageExperienceDataList.size();i++){
			text = text + " "+ manageExperienceDataList.get(i).getString();
		}
		return text;
	}

}
