package com.bole.resumeparser.html.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;

import com.bole.resumeparser.exception.ResumeParseException;
import com.bole.resumeparser.exception.document.HtmlResumeParseException;
import com.bole.resumeparser.exception.html.DocResumeParseException;
import com.bole.resumeparser.models.*;

public abstract class AbstractResumeParser implements HtmlResumeParserInterface{
	

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
	public ResumeData parse() throws HtmlResumeParseException{
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
	public String extractSelfEvaluation(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobTarget extractJobTarget(Node node) {
		// TODO Auto-generated method stub
		return null;
	}	
	
	@Override
	public String getResumeKeywords(Node node) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ArrayList<WorkExperienceData> extractWorkExperience(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<EducationExperienceData> extractEducationExperience(
			Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<TrainingExperienceData> extractTrainingExperience(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<LanguageSkillData> extractLanguageSkill(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<CertificateData> extractCertficate(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ProjectExperienceData> extractProjectExperience(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ProfessionalSkillData> extractProfessionalSkill(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<PracticalExperienceData> extractPracticalExperience(
			Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<OtherInfoData> extractOtherInfo(Node node) {
		// TODO Auto-generated method stub
		return null;
	}	
	
	public Date transUpdateTime(String updateTime){
		Date update = null;
		try
		{
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			update = sdf.parse(updateTime);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return update;
	}
	
}
