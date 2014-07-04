package com.bole.resumeparser.models;

import java.util.ArrayList;

public class StudyInfoData {
	//奖学金
	ArrayList<String> scholarShipList = new ArrayList<String>();
	//校内活动/职务描述
	String activityDesc = "";
	
	ArrayList<RewardData> rewardDataList = new ArrayList<RewardData>();
	
	public ArrayList<String> getScholarShipList() {
		return scholarShipList;
	}
	public void setScholarShipList(ArrayList<String> scholarShipList) {
		this.scholarShipList = scholarShipList;
	}
	public void addScholarShip(String scholarShip){
		scholarShipList.add(scholarShip);
	}
	public String getActivityDesc() {
		return activityDesc;
	}
	public void setActivityDesc(String activityDesc) {
		this.activityDesc = activityDesc;
	}
	public ArrayList<RewardData> getRewardDataList() {
		return rewardDataList;
	}
	public void setRewardDataList(ArrayList<RewardData> rewardDataList) {
		this.rewardDataList = rewardDataList;
	}
	public void addRewarData(RewardData rewardData){
		rewardDataList.add(rewardData);
	}
	
	@Override
	public String toString(){
		return " [scholarShipList="
				+ scholarShipList.toString() + ", activityDesc=" + activityDesc + ", activityDesc=" + activityDesc 
				+ "]";
	}
	
	public String getString(){
		String text = "";
		text = activityDesc;
		for(int i=0;i<scholarShipList.size();i++){
			text += " " +scholarShipList.get(i).toString();
		}
		
		for(int i=0;i<rewardDataList.size();i++){
			text += " " +rewardDataList.get(i).getString();
		}
		return text;
	}
}


