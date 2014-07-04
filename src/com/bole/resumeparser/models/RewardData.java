package com.bole.resumeparser.models;

public class RewardData{
	//获取奖项
	String rewards = "";	
	//奖项级别
	String rewardsLevel = "";
	//获奖时间
	String time = "";
	//奖项描述
	String desc = "";
	
	public String getRewards() {
		return rewards;
	}
	public void setRewards(String rewards) {
		this.rewards = rewards;
	}
	public String getRewardsLevel() {
		return rewardsLevel;
	}
	public void setRewardsLevel(String rewardsLevel) {
		this.rewardsLevel = rewardsLevel;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String toString(){
		return "[rewards="
				+ rewards + ", rewardsLevel=" + rewardsLevel + ", time=" + time 
				+ ", desc=" + desc + "]";
	}
	
	public String getString(){
		return rewards + " "+ rewardsLevel +" "+ time +" "+ desc;
	}
}
