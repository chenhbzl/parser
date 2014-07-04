package com.bole.resumeparser.models;

import java.util.ArrayList;

import java.util.List;

public class LanguageSkillData {
	//语言能力
	
	//语言类别
	String catagory = "";
	
	//读写能力
	String readAndWriteAbility = "";
	
	//听说能力
	String listenAndSpeakAbility = "";
	
	//等级
	String level;

	public String getCatagory() {
		return catagory;
	}

	public void setCatagory(String catagory) {
		this.catagory = catagory;
	}

	public String getReadAndWriteAbility() {
		return readAndWriteAbility;
	}

	public void setReadAndWriteAbility(String readAndWriteAbility) {
		this.readAndWriteAbility = readAndWriteAbility;
	}

	public String getListenAndSpeakAbility() {
		return listenAndSpeakAbility;
	}

	public void setListenAndSpeakAbility(String listenAndSpeakAbility) {
		this.listenAndSpeakAbility = listenAndSpeakAbility;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@Override
	public String toString(){
		return "LanguageSkill [catagory="
				+ catagory + ", readAndWriteAbility=" + readAndWriteAbility + ", listenAndSpeakAbility=" + listenAndSpeakAbility 
				+ ", level=" + level +"]";
	}
	
	public String getString(){
		return catagory + " " + readAndWriteAbility + " " +listenAndSpeakAbility + " " +level;
	}
	
}
