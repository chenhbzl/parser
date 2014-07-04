package com.bole.resumeparser.models;

import java.util.ArrayList;

import java.util.List;

public class ProfessionalSkillData {

	String skillDesc = "";
	
	String proficiency  = "";
	
	String months = "";
	
	//专业技能原文本（文本简历）
	public String sourceText = "";

	public String getSkillDesc() {
		return skillDesc;
	}

	public void setSkillDesc(String skillDesc) {
		this.skillDesc = skillDesc;
	}

	public String getProficiency() {
		return proficiency;
	}

	public void setProficiency(String proficiency) {
		this.proficiency = proficiency;
	}

	public String getMonths() {
		return months;
	}

	public void setMonths(String months) {
		this.months = months;
	}	
	
	public String getSourceText() {
		return sourceText;
	}

	public void setSourceText(String sourceText) {
		this.sourceText = sourceText;
	}

	@Override
	public String toString(){
		return "ProfessionalSkill [skillDesc="
				+ skillDesc + ", proficiency=" + proficiency + ", months=" + months 
				+ "]";
	}
	
	public String getString(){
		return skillDesc + " " + proficiency + " " + months;
	}
	
}
