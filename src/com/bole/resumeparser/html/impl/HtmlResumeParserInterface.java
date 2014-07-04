package com.bole.resumeparser.html.impl;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.htmlparser.Node;

import com.bole.resumeparser.ResumeParseInterface;
import com.bole.resumeparser.exception.html.DocResumeParseException;
import com.bole.resumeparser.models.*;


/**
 * 该模板是按照智联招聘的简历模板格式定义
 * @version 1.0,2013-08-10
 * @author 李瑶
 * @since JDK1.7
 */
public interface HtmlResumeParserInterface extends ResumeParseInterface{
	static Logger logger = Logger.getLogger(HtmlResumeParserInterface.class); 
		
	//简历基本信息
	public String getSourceID(String html); //获取简历id
	public String getWebsite();     //获取简历所属网站
	public String getUpdateTime();   //获取简历更新时间
	public String getResumeKeywords(Node node); //获取简历关键字
	
	//个人基本信息
	public String getName();//获取用户名
	public String getPhone();  //用户电话
	public String getEmail();  //email
	public String getAge();    //年龄
	public String Gender();    //性别
	public boolean hasContactInfo();
	
	//基本信息
	public void extractContactInfo(Node node);
	//自我评价
	public String extractSelfEvaluation(Node node);  
	
	//求职意向
	public JobTarget extractJobTarget(Node node);
	
	//工作经历
	public ArrayList<WorkExperienceData> extractWorkExperience(Node node);
	
	//教育经历
	public ArrayList<EducationExperienceData> extractEducationExperience(Node node);
	
	//培训经历
	public ArrayList<TrainingExperienceData> extractTrainingExperience(Node node);
	
	//语言能力
	public ArrayList<LanguageSkillData> extractLanguageSkill(Node node);
	
	//证书
	public ArrayList<CertificateData> extractCertficate(Node node);
	
	//项目经历
	public ArrayList<ProjectExperienceData> extractProjectExperience(Node node);
	
	//专业技能
	public ArrayList<ProfessionalSkillData> extractProfessionalSkill(Node node);
	
	//在校实践经验
	public ArrayList<PracticalExperienceData> extractPracticalExperience(Node node);
	
	//其他信息
	public ArrayList<OtherInfoData> extractOtherInfo(Node node);
	
}
