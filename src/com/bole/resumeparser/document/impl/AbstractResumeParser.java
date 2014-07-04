package com.bole.resumeparser.document.impl;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.bole.resumeparser.exception.ResumeParseException;
import com.bole.resumeparser.models.*;

public abstract class AbstractResumeParser implements DocumentResumeParserInterface{
	

	public String removeHtmlTag(String htmlStr){
		
		String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式
       
        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //过滤script标签
       
        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //过滤style标签
       
        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //过滤html标签

       return htmlStr.trim(); //返回文本字符串 
	}

	@Override
	public TextResumeData parse() throws ResumeParseException{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSourceID(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWebsite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPhone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String Gender() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extractSelfEvaluation(int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobTarget extractJobTarget(int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}	
	
	@Override
	public String getResumeKeywords(int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ArrayList<WorkExperienceData> extractWorkExperience(int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<EducationExperienceData> extractEducationExperience(
			int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<TrainingExperienceData> extractTrainingExperience(int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<LanguageSkillData> extractLanguageSkill(int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<CertificateData> extractCertficate(int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ProjectExperienceData> extractProjectExperience(int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ProfessionalSkillData> extractProfessionalSkill(int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<PracticalExperienceData> extractPracticalExperience(
			int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<OtherInfoData> extractOtherInfo(int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	class SectionInfo{
		int start;
		int end;	

		public int getStart() {
			return start;

		}
		public void setStart(int start) {
			this.start = start;
		}
		public int getEnd() {
			return end;
		}
		public void setEnd(int end) {
			this.end = end;
		}
		public SectionInfo(int start,int end){
			this.start = start;
			this.end = end;
		}

	}

		
	
}
