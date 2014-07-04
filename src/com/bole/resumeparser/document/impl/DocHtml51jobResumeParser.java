package com.bole.resumeparser.document.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.Span;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;

import com.bole.config.Status;
import com.bole.resumeparser.document.impl.AbstractResumeParser.SectionInfo;
import com.bole.resumeparser.exception.ResumeParseException;
import com.bole.resumeparser.exception.document.HtmlResumeParseException;
import com.bole.resumeparser.exception.html.DocResumeParseException;
import com.bole.resumeparser.document.impl.AbstractResumeParser;
import com.bole.resumeparser.html.impl.HtmlResumeParserInterface;
import com.bole.resumeparser.message.ResumePreProcessInfo;
import com.bole.resumeparser.models.CertificateData;
import com.bole.resumeparser.models.EducationExperienceData;
import com.bole.resumeparser.models.JobTarget;
import com.bole.resumeparser.models.LanguageSkillData;
import com.bole.resumeparser.models.ManageExperienceData;
import com.bole.resumeparser.models.OtherInfoData;
import com.bole.resumeparser.models.PracticalExperienceData;
import com.bole.resumeparser.models.ProfessionalSkillData;
import com.bole.resumeparser.models.ProjectExperienceData;
import com.bole.resumeparser.models.ResumeData;
import com.bole.resumeparser.models.RewardData;
import com.bole.resumeparser.models.StudyInfoData;
import com.bole.resumeparser.models.TextResumeData;
import com.bole.resumeparser.models.TrainingExperienceData;
import com.bole.resumeparser.models.WorkExperienceData;
import com.bole.resumeparser.service.ResumePreProcessFactory;

public class DocHtml51jobResumeParser extends AbstractResumeParser{

	/**
	 * 已经完成对基本信息的解析(性别，年龄),求职意向，项目经历，教育经历，自我评价的解析
	 * 需要完善职业技能，培训经历，证书，在校实践经验，其他信息的解析
	 */
	public String htmlcontent = "";
	public String htmlurl = "";
	public TextResumeData resumedata = new TextResumeData();
	public String objectid = null;
	boolean hasNbsp = false;
	public String resumeText = "";
	public String resumeContent = "";
	

	/**
	 * 简历解析
	 */
	@Override
	public TextResumeData parse() throws HtmlResumeParseException {
		// TODO Auto-generated method stub
		//主要负责解析简历将解析之后的数据保存到ResumeData中
		resumedata.url =  htmlurl;
		resumedata.set_id(objectid);
		resumedata.setSource("51job");
		
		Parser parser = Parser.createParser(htmlcontent, "utf-8");
		
		NodeFilter divResumeFilter = new NodeClassFilter(TableTag.class);
//		
//		NodeFilter divResumeFilter = new HasAttributeFilter("id","c_content");
//		NodeFilter divResumeFilter2 = new HasAttributeFilter("class","MsoNormalTable");
		
		
//		NodeFilter[] filters = new NodeFilter[2];
//		filters[0] = divResumeFilter;
//		filters[1] = divResumeFilter2;
//		
//		OrFilter orfilter = new OrFilter();
//		orfilter.setPredicates(filters);
		
		NodeList nodes = null;
		
		try {
			nodes = parser.extractAllNodesThatMatch(divResumeFilter);
			
			for(int i=0;i<nodes.size();i++){
				TableTag node = (TableTag)nodes.elementAt(i);
				if(node.getRowCount()<4){
					continue;
				}
				if(node instanceof TableTag){
					TableTag resumeTable = (TableTag)node;
					for(int j=0;j<resumeTable.getRowCount();j++){
						TableRow tableRow = resumeTable.getRow(j);
						String rowTxt = tableRow.toPlainTextString();
						switch(rowTxt){
						case "个人信息":
							Node basicInfoNode = resumeTable.getRow(j+1).getColumns()[0].getChild(0);
							extractContactInfo(basicInfoNode);
							j++;
							break;
						case "自我评价":
							Node selfEvaluationNode = resumeTable.getRow(j+1).getColumns()[0].getChild(0);
							String selfEvaluation = extractSelfEvaluation(selfEvaluationNode);
							resumedata.setSelfEvaluation(selfEvaluation);
							j++;
							break;
						case "求职意向":
							Node jobTargetNode = resumeTable.getRow(j+1).getColumns()[0].getChild(0);
							JobTarget jobTarget = extractJobTarget(jobTargetNode);
							resumedata.setJobTarget(jobTarget);
							j++;
							break;
						case "工作经验":
							Node workExperienceNode = resumeTable.getRow(j+1).getColumns()[0].getChild(0);
							ArrayList<WorkExperienceData> workExperienceDataList = extractWorkExperience(workExperienceNode);
        					resumedata.setWorkExperience(workExperienceDataList);
							j++;
							break;
						case "项目经验":
							Node projectExperienceNode = resumeTable.getRow(j+1).getColumns()[0].getChild(0);
							ArrayList<ProjectExperienceData> projectExperienceDataList = extractProjectExperience(projectExperienceNode);
							resumedata.setProjectExperience(projectExperienceDataList);
							j++;
							break;
						case "教育经历":
							Node educationNode = resumeTable.getRow(j+1).getColumns()[0].getChild(0);
							ArrayList<EducationExperienceData> educationExperienceList = extractEducationExperience(educationNode);
							resumedata.setEducationExperience(educationExperienceList);
							j++;
							break;
						case "语言能力":
							Node languageSkillNode = resumeTable.getRow(j+1).getColumns()[0].getChild(0);
							ArrayList<LanguageSkillData> languageSkillList = extractLanguageSkill(languageSkillNode);
							resumedata.setLanguageSkill(languageSkillList);
							j++;
							break;
						case "IT技能":
							Node professionalSkillNode = resumeTable.getRow(j+1).getColumns()[0].getChild(0);
							ArrayList<ProfessionalSkillData> professionalSkillList = extractProfessionalSkill(professionalSkillNode);
							resumedata.setProfessionalSkill(professionalSkillList);
							j++;
							break;
						case "证书":
							break;
						case "培训经历":
							break;
						case "所获奖项":
							break;
						case "社会经验":
							break;
						case "校内职务":
							break;
						case "其他信息":
							break;
						}
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new HtmlResumeParseException(e);
		}
		return resumedata;
		
	}	
	
	/**
	 * 获取简历ID
	 * @param html 包含id的html文本
	 * @return 简历ID
	 */
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
	
	/**
	 * 获取简历更新时间
	 * @param 包含简历更新时间的html节点
	 * @return 简历更新时间
	 */
	public String getUpdateTime(Node node) {
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

	/**
	 * 获取简历信息中的网址链接，该链接默认为用户的产品
	 */
	public ArrayList<String> getUrls(){
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

	/**
	 * 获取简历中头像url
	 * @param content 简历的html内容
	 * @return 简历中头像url
	 */
	public String getImageUrl(){
		return null;
	}
	

	public String getResumeKeywords(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获取求职者自我评价
	 * @param 包含自我评价的html节点
	 * @return 自我评价内容
	 */
	public String extractSelfEvaluation(Node node) {
		// TODO Auto-generated method stub
		if(node == null){
			return null;
		}
		String selfEvaluation = "";
		if(node instanceof TableTag){
			TableTag tableTag = (TableTag)node;
			selfEvaluation = tableTag.toPlainTextString();
		}
		return selfEvaluation;
	}

	public void extractContactInfo(Node node) {
		// TODO Auto-generated method stub
		if(node == null){
			return;
		}
		if(node instanceof TableTag){
			TableTag tableNode = (TableTag)node;
			for(int i=0;i<tableNode.getRowCount();i++){
				TableRow tableRow = tableNode.getRow(i);
				String txt = tableRow.toPlainTextString();
				
				String title1 = tableRow.getColumns()[0].toPlainTextString();
				String desc1 = tableRow.getColumns()[1].toPlainTextString();
				
				String title2 = tableRow.getColumns()[0].toPlainTextString();
				String desc2 = tableRow.getColumns()[1].toPlainTextString();
				
				int colNum = tableRow.getColumnCount();
				if(colNum >=4 ){
					title1 = tableRow.getColumns()[0].toPlainTextString();
					desc1 = tableRow.getColumns()[1].toPlainTextString();
					
					title2 = tableRow.getColumns()[2].toPlainTextString();
					desc2 = tableRow.getColumns()[3].toPlainTextString();
				}else if(colNum == 2){
					title1 = tableRow.getColumns()[0].toPlainTextString();
					desc1 = tableRow.getColumns()[1].toPlainTextString();
					
					title2 = "";
					desc2 = "";
				}
				
				switch(title1){
				case "姓    名：":
					resumedata.setName(desc1);
					break;
				case "性    别：":
					resumedata.setGender(desc1);
					break;
				case "出生日期：":
					resumedata.setBirthday(desc1);
					break;
				case "居 住 地：":
					resumedata.setAddress(desc1);
					break;
				case "工作年限：":
					Pattern pattern = Pattern.compile("([一二三四五六七八九十]+)年");
					Matcher matcher = pattern.matcher(desc1);
			        if(matcher.find()){
			        	String worklen = matcher.group(1);
			        	resumedata.setWorkExperienceLength(cn2int(worklen));
			        }
					break;
				case "电子邮件：":
					resumedata.setEmail(desc1);
					break;
				case "手    机：":
					resumedata.setPhone(desc1);
					break;
				case "目前年薪：":
					resumedata.setLatestSalary(desc1);
					break;
				case "身    高：":
					resumedata.setHeight(desc1);
					break;
				case "婚姻状况：":
					resumedata.setMaritalStatus(desc1);
					break;
				case "政治面貌：":
					resumedata.setPoliticalLandscape(desc1);
					break;
				case "地    址：":
					resumedata.setAddress(desc1);
					break;
				case "邮    编：":
					resumedata.setZipCode(desc1);
					break;
				case "个人主页：":
					resumedata.setHomePage(desc1);
					break;
				case "关 键 词：":
					resumedata.setResumeKeyWord(desc1);
					break;
				case "QQ：":
					resumedata.setQq(desc1);
					break;
				}
				
				if(!"".equals(title2)){
					switch(title2){
					case "姓    名：":
						resumedata.setName(desc2);
						break;
					case "性    别：":
						resumedata.setGender(desc2);
						break;
					case "出生日期：":
						resumedata.setBirthday(desc2);
						break;
					case "居 住 地：":
						resumedata.setAddress(desc2);
						break;
					case "工作年限：":
						Pattern pattern = Pattern.compile("([一二三四五六七八九十]+)年");
						Matcher matcher = pattern.matcher(desc2);
				        if(matcher.find()){
				        	String worklen = matcher.group(1);
				        	resumedata.setWorkExperienceLength(cn2int(worklen));
				        }
						break;
					case "电子邮件：":
						resumedata.setEmail(desc2);
						break;
					case "手    机：":
						resumedata.setPhone(desc2);
						break;
					case "目前年薪：":
						resumedata.setLatestSalary(desc2);
						break;
					case "身    高：":
						resumedata.setHeight(desc2);
						break;
					case "婚姻状况：":
						resumedata.setMaritalStatus(desc2);
						break;
					case "政治面貌：":
						resumedata.setPoliticalLandscape(desc2);
						break;
					case "地    址：":
						resumedata.setAddress(desc2);
						break;
					case "邮    编：":
						resumedata.setZipCode(desc2);
						break;
					case "个人主页：":
						resumedata.setHomePage(desc2);
						break;
					case "关 键 词：":
						resumedata.setResumeKeyWord(desc2);
						break;
					case "QQ：":
						resumedata.setQq(desc2);
						break;
					}
				}
			}
		}
		return;
	}

	/**
	 * 获取求职者求职意向
	 * @param 包含求职意向的html节点
	 * @return 求职意向 @see JobTarget 
	 */
	public JobTarget extractJobTarget(Node node) {
		// TODO Auto-generated method stub
		if(node == null){
			return null;
		}
		JobTarget jobTarget = new JobTarget();
		if(node instanceof TableTag){
			TableTag tableNode = (TableTag)node;
			for(int i=0;i<tableNode.getRowCount();i++){
				TableRow tableRow = tableNode.getRow(i);
				
				String title = "";
				String desc = "";
				
				int colNum = tableRow.getColumnCount();
				if(colNum == 2){
					title = tableRow.getColumns()[0].toPlainTextString();
					desc = tableRow.getColumns()[1].toPlainTextString();
				}
				
				switch(title){
				case "到岗时间：":
					jobTarget.setEnrollTime(desc);
					break;
				case "工作性质：":
					jobTarget.setJobCatagory(desc);
					break;
				case "希望行业：":
					jobTarget.setJobIndustry(desc);
					break;
				case "目标地点：":
					jobTarget.setJobLocation(desc);
					break;
				case "期望薪水：":
					jobTarget.setSalary(desc);
					break;
				case "目标职能：":
					jobTarget.setJobCareer(desc);
					break;
				}
			}
		}
		return jobTarget;
	}

	/**
	 * 获取求职者工作经历
	 * @param 包含工作经历的html节点
	 * @return 工作经历 @see WorkExperienceData 
	 */
	public ArrayList<WorkExperienceData> extractWorkExperience(Node node) {
		// TODO Auto-generated method stub
		//暂时不分析公司性质：企业规模 ，企业性质 （不重要）
		if(node == null){
			return null;
		}
		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
		
		if(node instanceof TableTag){
			TableTag tableNode = (TableTag)node;
			int currentnum = 0;
			for(int i=0;i<tableNode.getRowCount();i++){
				
				TableRow tableRow = tableNode.getRow(i);
				int colNum = 0;
				colNum = tableRow.getColumnCount();
				String txt = tableRow.toPlainTextString();
				
				Pattern pattern = Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+)：(.*)");
		        Matcher matcher = pattern.matcher(txt);
		        if(matcher.find()){
		        	currentnum++;
		        	WorkExperienceData workExperienceData = new WorkExperienceData();
		        	
		        	workExperienceData.setStartTime(matcher.group(1).replaceAll("/", "-"));
		        	workExperienceData.setEndTime(matcher.group(2).replaceAll("/", "-"));
		        	workExperienceData.setCompanyName(matcher.group(3));
		        	
		        	pattern = Pattern.compile("(.*)（(.*)）");
			        matcher = pattern.matcher(workExperienceData.getCompanyName());
			        if(matcher.find()){
			        	workExperienceData.setCompanyName(matcher.group(1));
			        	workExperienceData.setCompanyScale(matcher.group(2));
			        }
		        	workExperienceDataList.add(workExperienceData);
		        }else {
		        	if(colNum == 2){
		        		String title = tableRow.getColumns()[0].toPlainTextString();
		        		String desc = tableRow.getColumns()[1].toPlainTextString();
		        		
		        		switch(title){
		        		case "所属行业：":
		        			workExperienceDataList.get(currentnum-1).setIndustryCatagory(desc);
		        			break;
		        		case "汇报对象：":
		        			if(((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.size()==0){
		        				ManageExperienceData manageExperience =  new ManageExperienceData();
		        				manageExperience.setReportTo(desc);
		        				((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.add(manageExperience);
		        			}else{
		        				((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.get(0).setReportTo(desc);       				
		        			}
		        			break;
		        		case "下属人数：":
		        			if(((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.size()==0){
		        				ManageExperienceData manageExperience =  new ManageExperienceData();
		        				manageExperience.setSubordinatesNum(desc);
		        				((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.add(manageExperience);
		        			}else{
		        				((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.get(0).setSubordinatesNum(desc);    				
		        			}
		        			break;
		        		case "证 明 人：":
		        			if(((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.size()==0){
		        				ManageExperienceData manageExperience =  new ManageExperienceData();
		        				manageExperience.setReterence(desc);
		        				((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.add(manageExperience);
		        			}else{        				
		        				((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.get(0).setReterence(desc);
		        			}
		        			break;
		        		case "离职原因：":
		        			if(((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.size()==0){
		        				ManageExperienceData manageExperience =  new ManageExperienceData();
		        				manageExperience.setLeavingReason(desc);
		        				((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.add(manageExperience);
		        			}else{
		        				((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.get(0).setLeavingReason(desc);
		        			}
		        			break;
		        		case "工作业绩：":
		        			if(((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.size()==0){
		        				ManageExperienceData manageExperience =  new ManageExperienceData();
		        				manageExperience.setKeyPerformance(desc);
		        				((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.add(manageExperience);
		        			}else{
		        				((WorkExperienceData)workExperienceDataList.get(currentnum-1)).manageExperienceDataList.get(0).setKeyPerformance(desc);
		        			}
		        			break;
		        		}
		        	}else if(colNum == 1){
		        		int childsNum = tableRow.getColumns()[0].getChildCount();
		        		String html = tableRow.getColumns()[0].getChildrenHTML();
		        		
		        		pattern = Pattern.compile("<strong>(.*?)</strong>");
				        matcher = pattern.matcher(html);
				        int num = 1;
				        while(matcher.find()){
				        	if(num == 1){
				        		(workExperienceDataList.get(currentnum-1)).setDepartment(matcher.group(1));
				        	}else if(num == 2){
				        		(workExperienceDataList.get(currentnum-1)).setPositionTitle(matcher.group(1));
				        	}
				        	num ++;
				        }
			        	String id = tableRow.getColumns()[0].getAttribute("id");
	        			if("Cur_Val".equals(id)){
	        				(workExperienceDataList.get(currentnum-1)).setJobDesc(tableRow.toPlainTextString());
	        			}
		        	}
		        }
			}
		}
		
		return workExperienceDataList;
	}

	/**
	 * 获取求职者教育经历
	 * @param node 包含教育经历的html节点
	 * @return 教育经历 @see EducationExperienceData 
	 */
	public ArrayList<EducationExperienceData> extractEducationExperience(Node node) {
		// TODO Auto-generated method stub
		if(node==null){
			return null;
		}
		ArrayList<EducationExperienceData> educationExperienceDataList = new ArrayList<EducationExperienceData>();
		
		if(node instanceof TableTag){
			TableTag tableNode = (TableTag)node;
			for(int i=0;i<tableNode.getRowCount();i++){
				TableRow tableRow = tableNode.getRow(i);
				if(tableRow.getColumnCount() == 4){
					String time = tableRow.getColumns()[0].toPlainTextString();
					String school = tableRow.getColumns()[1].toPlainTextString();
					String major = tableRow.getColumns()[2].toPlainTextString();
					String degree = tableRow.getColumns()[3].toPlainTextString();
					
					Pattern pattern = Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+)");
			        Matcher matcher = pattern.matcher(time);
			        if(matcher.find()){
			        	EducationExperienceData educationExperienceData = new EducationExperienceData();
			        	
			        	educationExperienceData.setStartTime(matcher.group(1));
						educationExperienceData.setEndTime(matcher.group(2));
						educationExperienceData.setSchool(school);
						educationExperienceData.setMajor(major);
						educationExperienceData.setDegree(degree);
						
						educationExperienceDataList.add(educationExperienceData);
			        }
				}
			}
		}
		return educationExperienceDataList;
	}


	/**
	 * 获取求职者培训经历(暂时没有实现)
	 * @param node 包含培训经历的html节点
	 * @return 培训经历 @see TrainingExperienceData 
	 */
	public ArrayList<TrainingExperienceData> extractTrainingExperience(Node node) {
		return null;
	}

	public HashMap<String,String> extractOtherInfoMap(Node node){
		return null;
	}
	/**
	 * 获取求职者语言能力
	 * @param node 包含语言能力的html节点
	 * @return 语言能力 @see LanguageSkillData 
	 */
	public ArrayList<LanguageSkillData> extractLanguageSkill(Node node) {
		// TODO Auto-generated method stub
		if(null == node){
			return null;
		}
		
		ArrayList<LanguageSkillData> languageSkillDataList = new ArrayList<LanguageSkillData>();
		
		if(node instanceof TableTag){
			node = ((TableTag)node).getRow(0).getColumns()[0].getChild(0);
			if(node instanceof TableTag){
				TableTag tableTag = (TableTag)node;
				for(int j = 0;j < tableTag.getRowCount();j++){
					TableRow tableRow = tableTag.getRow(j);
					int childNum = tableRow.getChildCount();
					if(childNum == 2){
						String title = tableRow.getColumns()[0].toPlainTextString();
						String desc = tableRow.getColumns()[1].toPlainTextString();
						
						Pattern pattern=Pattern.compile("听说（(.*?)）.*?读写（(.*?)）");
				        Matcher matcher=pattern.matcher(desc);
				        if(matcher.find()){
				        	LanguageSkillData languageSkillData = new LanguageSkillData();
				        	String listenAndSpeakAbility = matcher.group(1);
				        	String readAndWriteAbility = matcher.group(2);
				        	languageSkillData.setCatagory(title);
				        	languageSkillData.setListenAndSpeakAbility(listenAndSpeakAbility);
				        	languageSkillData.setReadAndWriteAbility(readAndWriteAbility);
				        }else{
							LanguageSkillData languageSkillData = new LanguageSkillData();
							languageSkillData.setCatagory(title);
							languageSkillData.setLevel(desc);
							languageSkillDataList.add(languageSkillData);
				        }
					}
				}
			}
		}
		return languageSkillDataList;
	}


	public ArrayList<RewardData> extractReward(Node node){
		return null;
	}
	/**
	 * 获取求职者证书信息
	 * @param node 包含证书信息的html节点
	 * @return 证书信息 @see CertificateData 
	 */
	public ArrayList<CertificateData> extractCertficate(Node node) {
		return null;
	}

	/**
	 * 获取求职者项目经历
	 * @param node 包含项目经历的html节点
	 * @return 项目经历 @see ProjectExperienceData 
	 */
	public ArrayList<ProjectExperienceData> extractProjectExperience(Node node) {
		// TODO Auto-generated method stub  项目经验
		if(node == null){
			return null;
		}
		ArrayList<ProjectExperienceData> projectExperienceDataList = new ArrayList<ProjectExperienceData>();
		
		if(node instanceof TableTag){
			TableTag tableNode = (TableTag)node;
			int currentnum = 0;
			for(int i=0;i<tableNode.getRowCount();i++){
				TableRow tableRow = tableNode.getRow(i);
				String txt = tableRow.toPlainTextString();
				int childNum = 0;
				childNum = tableRow.getColumnCount();
				
				if(childNum == 1){
					Pattern pattern = Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+)：(.*)");
			        Matcher matcher = pattern.matcher(txt);
			        
			        while(matcher.find()){
			        	currentnum++;
			        	ProjectExperienceData projectExperienceData = new ProjectExperienceData();
			        	projectExperienceData.setStartTime(matcher.group(1).replaceAll("/", "-"));
			        	projectExperienceData.setEndTime(matcher.group(2).replaceAll("/", "-"));
			        	projectExperienceData.setProjectTitle(matcher.group(3));
			        	
			        	projectExperienceDataList.add(projectExperienceData);
			        }
				}else if(childNum == 2){
					String title = tableRow.getColumns()[0].toPlainTextString();
					String desc = tableRow.getColumns()[1].toPlainTextString();
					
					switch(title){
	        		case " 软件环境：":
	        			projectExperienceDataList.get(currentnum-1).setSoftwareEnvir(desc);
	        			break;
	        		case " 硬件环境：":
	        			projectExperienceDataList.get(currentnum-1).setHardEnvir(desc);
	        			break;
	        		case " 开发工具：":
	        			projectExperienceDataList.get(currentnum-1).setDevelopTool(desc);
	        			break;
	        		case " 项目描述：":
	        			projectExperienceDataList.get(currentnum-1).setProjectDesc(desc);
	        			break;
	        		case " 责任描述：":
	        			projectExperienceDataList.get(currentnum-1).setResponsibleFor(desc);
	        			break;
					}
				}
			}
		}
		return projectExperienceDataList;
	}

	/**
	 * 获取求职者专业技能
	 * @param node 包含专业技能的html节点
	 * @return 专业技能 @see ProfessionalSkillData 
	 */
	public ArrayList<ProfessionalSkillData> extractProfessionalSkill(Node node) {
		// TODO Auto-generated method stub
		if(node == null){
			return null;
		}
		
		ArrayList<ProfessionalSkillData> professionalSkillDataList = new ArrayList<ProfessionalSkillData>();
		
		if(node instanceof TableTag){
			TableTag tableNode = (TableTag)node;
			for(int i=0;i<tableNode.getRowCount();i++){
				TableRow tableRow = tableNode.getRow(i);
				if(tableRow.getColumnCount() == 3){
					String title = tableRow.getColumns()[0].toPlainTextString();
					String proficiency = tableRow.getColumns()[1].toPlainTextString();
					String months = tableRow.getColumns()[2].toPlainTextString();
					
					if(months.indexOf("月")>=0){
						ProfessionalSkillData professionalSkillData = new ProfessionalSkillData();
						professionalSkillData.setSkillDesc(title);
						professionalSkillData.setProficiency(proficiency);
						professionalSkillData.setMonths(months);
						
						professionalSkillDataList.add(professionalSkillData);
					}
				}
			}
		}
		return professionalSkillDataList;
	}


	public ArrayList<PracticalExperienceData> extractPracticalExperience(
			Node node) {
		return null;
	}


	public ArrayList<OtherInfoData> extractOtherInfo(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	public DocHtml51jobResumeParser(String html,String url){

		if(htmlcontent.indexOf("&nbsp;") > 0){
			hasNbsp = true;
		}
		htmlcontent = html;
		htmlcontent = htmlcontent.replaceAll("\r", "").replaceAll("\n", "").replaceAll("#→start→#", "").replaceAll("#←end←#", "");
		
		this.htmlurl = url;
	}
	public DocHtml51jobResumeParser(String html,String url,String objectid){
		
		htmlcontent = html;
		htmlcontent = htmlcontent.replaceAll("\r", "").replaceAll("\n", "").replaceAll("#→start→#", "").replaceAll("#←end←#", "");
		Pattern pattern = Pattern.compile("<font color=\"red\">(.*?)</font>",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlcontent);
        String searchWords = "";
        if(matcher.find()){
        	searchWords = matcher.group(1);
        }
        if(!"".equals(searchWords)){
        	htmlcontent = htmlcontent.replaceAll("<font color=\"red\">.*?</font>", searchWords);
        }
		if(htmlcontent.indexOf("&nbsp;") > 0){
			hasNbsp = true;
		}
		
		this.htmlurl = url;
		this.objectid = objectid;
	}
	
	public boolean isValidResume(){
		if(this.htmlcontent.indexOf("该简历已被求职者删除") >= 0){
			return false;
		}
		return true;
	}
	

	public boolean hasContactInfo(){
		if(this.htmlcontent.indexOf("此简历为未下载简历") >= 0){
			return false;
		}
		return true;
	}
	
	public int cn2int(String chinese){
		int re = 0;
		HashMap<String,Integer> cn2intMap = new HashMap<String,Integer>(); 
		cn2intMap.put("一", 1);
		cn2intMap.put("二", 2);
		cn2intMap.put("三", 3);
		cn2intMap.put("四", 4);
		cn2intMap.put("五", 5);
		cn2intMap.put("六", 6);
		cn2intMap.put("七", 7);
		cn2intMap.put("八", 8);
		cn2intMap.put("九", 9);
		cn2intMap.put("十", 10);
		if(null == chinese){
			return 0;
		}
		int len = chinese.length();
		if(len>3){
			//非合法工作年限
			return 0;
		}else{
			if(len==1){
				re = cn2intMap.get(chinese);
			}else if(len==2){
				String first = String.valueOf(chinese.charAt(0));
				String second = String.valueOf(chinese.charAt(1));
				if(!second.equals("十")){
					re = 10+cn2intMap.get(second);
				}else{
					re = cn2intMap.get(first)*10;
				}
				
			}else if(len==3){
				String first = String.valueOf(chinese.charAt(0));
				String third = String.valueOf(chinese.charAt(2));
				re = cn2intMap.get(first)*10+cn2intMap.get(third);
			}
		}
		return re;
	}

	@Override
	public String getResumeKeywords(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void extractContactInfo(int start, int end) {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public String extractSelfEvaluation(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobTarget extractJobTarget(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<WorkExperienceData> extractWorkExperience(int start,
			int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<EducationExperienceData> extractEducationExperience(
			int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<TrainingExperienceData> extractTrainingExperience(
			int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<LanguageSkillData> extractLanguageSkill(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<CertificateData> extractCertficate(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ProjectExperienceData> extractProjectExperience(int start,
			int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ProfessionalSkillData> extractProfessionalSkill(int start,
			int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<PracticalExperienceData> extractPracticalExperience(
			int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<OtherInfoData> extractOtherInfo(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}
}
