package com.bole.resumeparser.document.impl;

import java.util.ArrayList;

import com.bole.resumeparser.ResumeParseInterface;
import com.bole.resumeparser.exception.ResumeParseException;
import com.bole.resumeparser.models.*;


/**
 * j
 * @version 1.0,2013-08-10
 * @author 李瑶
 * @since JDK1.7
 */
public interface DocumentResumeParserInterface{
		
	public TextResumeData parse() throws ResumeParseException;
	//简历基本信息
	public String getSourceID(String html); //获取简历id
	public String getWebsite();     //获取简历所属网站
	public String getUpdateTime();   //获取简历更新时间
	public String getResumeKeywords(int start,int end); //获取简历关键字
	
	//个人基本信息
	public String getName();//获取用户名
	public String getPhone();  //用户电话
	public String getEmail();  //email
	public String getAge();    //年龄
	public String Gender();    //性别
	
	//基本信息
	public void extractContactInfo(int start,int end);
	//自我评价
	public String extractSelfEvaluation(int start,int end);  
	
	//求职意向Node
	public JobTarget extractJobTarget(int start,int end);
	
	//工作经历
	public ArrayList<WorkExperienceData> extractWorkExperience(int start,int end);
	
	//教育经历
	public ArrayList<EducationExperienceData> extractEducationExperience(int start,int end);
	
	//培训经历
	public ArrayList<TrainingExperienceData> extractTrainingExperience(int start,int end);
	
	//语言能力
	public ArrayList<LanguageSkillData> extractLanguageSkill(int start,int end);
	
	//证书
	public ArrayList<CertificateData> extractCertficate(int start,int end);
	
	//项目经历
	public ArrayList<ProjectExperienceData> extractProjectExperience(int start,int end);
	
	//专业技能
	public ArrayList<ProfessionalSkillData> extractProfessionalSkill(int start,int end);
	
	//在校实践经验
	public ArrayList<PracticalExperienceData> extractPracticalExperience(int start,int end);
	
	//其他信息
	public ArrayList<OtherInfoData> extractOtherInfo(int start,int end);
	
}
