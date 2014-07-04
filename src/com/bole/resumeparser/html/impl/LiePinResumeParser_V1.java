package com.bole.resumeparser.html.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.Bullet;
import org.htmlparser.tags.BulletList;
import org.htmlparser.tags.DefinitionList;
import org.htmlparser.tags.DefinitionListBullet;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.ParagraphTag;
import org.htmlparser.util.NodeList;

import com.bole.config.Status;
import com.bole.resumeparser.exception.document.HtmlResumeParseException;
import com.bole.resumeparser.exception.html.DocResumeParseException;
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
import com.bole.resumeparser.models.TrainingExperienceData;
import com.bole.resumeparser.models.WorkExperienceData;

public class LiePinResumeParser_V1 extends AbstractResumeParser implements HtmlResumeParserInterface{
	/**
	 * 解析猎聘网的简历
	 */
	/**
	 * @author liyao
	 * @version 1.5
	 */
	
	public String htmlcontent = ""; //简历html内容
	public String htmlurl = "";   //简历url
	boolean hasNbsp = false;      //用于区别简历是后台爬虫爬取（爬虫爬取的不包含nbsp;）
	String objectid = null;     //简历的objectid(和collectedResume表中objectid一致)
	ResumeData resumedata = new ResumeData();
	public String resumeText  = "";   //去除html的简历内容（用于提取所有的url使用）
	public String currentStatus = "";  //目前工作状态

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public ResumeData parse() throws HtmlResumeParseException{
		//主要负责解析简历将解析之后的数据保存到ResumeData中
		resumedata.url =  htmlurl;
		resumedata.set_id(objectid);
		resumedata.setSource("liepin");
		resumedata.setUpdateTime(getUpdateTime());
		resumedata.setSourceID(getSourceID(this.htmlcontent));
		extractUrlID();
		
		Parser parser = Parser.createParser(htmlcontent, "utf-8");		
		NodeFilter resumeContentFilter = new HasAttributeFilter("class","content");		
		
		NodeList nodes = null;
		try {
//			NodeList nodes1 = parser.extractAllNodesThatMatch(resumeContentFilter);
			nodes = parser.extractAllNodesThatMatch(resumeContentFilter).elementAt(0).getChildren();
			String arrName = ""; //div node的class属性名
			String idName = ""; //id 属性名，用于处理工作经验
			for(int i = 0;i < nodes.size();i++){
				Node node = nodes.elementAt(i);
				resumeText = resumeText + " " + node.toPlainTextString();
				if(node instanceof Div){
					arrName = ((Div)node).getAttribute("class");
					idName = ((Div)node).getAttribute("id");
					if(arrName == null){
						if(idName.equals("resume-work")){
							NodeList childs = node.getChildren();
							if(childs!=null){
								for(int j=0;j<childs.size();j++){
									Node child = childs.elementAt(j);
									if(child instanceof Div && "profile jobs".equals(((Div)child).getAttribute("class"))){
										resumedata.setWorkExperience(extractWorkExperience(child));
										break;
									}
								}
								continue;
							}
						}
						continue;
					}
					switch(arrName){
					case "profile basic":
						extractBasicInfo(node);
						break;
					case "profile career clearfix":
						if(node.toPlainTextString().indexOf("12个月")>=0){
							resumedata.setCurrentSalary(node.toPlainTextString().trim().replaceAll("&nbsp;", " "));
						}else{
							resumedata.setJobTarget(extractJobTarget(node));
						}						
						break;
					case "profile jobs":
						resumedata.setWorkExperience(extractWorkExperience(node));
						break;
					case "profile current-job clearfix":
						extractCurrentWork(node);
						break;
					case "profile education":
						resumedata.setEducationExperience(extractEducationExperience(node));
						break;
					case "profile language":
						resumedata.setLanguageSkill(extractLanguageSkill(node));
						break;
					case "profile projects":
						resumedata.setProjectExperience(extractProjectExperience(node));
						break;
					case "profile comments" :
						resumedata.setSelfEvaluation(extractSelfEvaluation(node));
						break;
					case "profile introduce":
						resumedata.setSelfEvaluation(extractSelfEvaluation(node));
						break;
					case "profile others":
						resumedata.addOtherInfo("其他信息", getNodeContent(node));	
						break;
					case "profile others word":
						resumedata.addOtherInfo("其他信息", getNodeContent(node));	
						break;
					}
				}
			}
			
			resumedata.setUrls(this.getUrls());
			resumedata.contactInfoData.setSource(resumedata.getSource());
			resumedata.contactInfoData.setSourceID(resumedata.getSourceID());
			resumedata.contactInfoData.setResumeID(resumedata.get_id());
			
			String phone = resumedata.contactInfoData.getPhone();
			String email = resumedata.contactInfoData.getEmail();
			resumedata.contactInfoData.setSource(resumedata.getSource());
			resumedata.contactInfoData.setSourceID(resumedata.getSourceID());
			if(phone == null && email == null){
				resumedata.contactInfoData = null;
			}else{
				if(resumedata.contactInfoData.getPhone().trim() != ""){
					resumedata.setIsCotactInformation("YES");
				}
				resumedata.contactInfoData.set_id(resumedata.get_id());
			}
			resumedata.setResumeText(resumeText);
			
			if(resumedata.getSourceID() == null || "".equals(resumedata.getSourceID())){
				throw new DocResumeParseException(Status.INVALID_RESUME);
			}
			
			resumedata.setCreateTime(new Date());
			return resumedata;
		
		}catch(Exception e){
			e.printStackTrace();
			throw new HtmlResumeParseException();
		}
	}

	@Override
	public boolean hasContactInfo() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void extractContactInfo(Node node) {
		// TODO Auto-generated method stub
		return ;
	}	

	public ArrayList<String> getUrls(){
		HashSet<String> urls = new HashSet<String>();
		ArrayList<String> url = new ArrayList<String>();
		if(this.resumeText.indexOf("www.")<0 && this.resumeText.indexOf("http://")<0){
			return url;
		}
		Pattern p = Pattern.compile("((http\\:\\/\\/|www[.]|http\\:\\/\\/www[.]).*?[.](com|cn|net|org|biz|info|cc|tv|me|(\\w+)/).*?)(，|,|。|;|[)]|[(]|\u00A0|[\u4e00-\u9fa5]|）|www|http|\\s)",Pattern.CASE_INSENSITIVE );   
		Matcher m = p.matcher(this.resumeText);
	    while(m.find()){
	    	urls.add(m.group(1));	    		    	
	    } 
	    url = new ArrayList<String>(urls);
		return url;
		
	}
	
	public void extractUrlID(){		
		Pattern pattern = Pattern.compile("res_id=(\\d+)",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(resumedata.url);
        if(matcher.find()){
        	resumedata.setUrlID(matcher.group(1));
        }
	}
	
	public void extractCurrentWork(Node node){
		NodeList childs = node.getChildren();
		for(int i=0;i<childs.size();i++){
			Node child = childs.elementAt(i);
			if(child instanceof BulletList){
				for(int j=0;j<child.getChildren().size();j++){
					Node currentNode = ((BulletList) child).getChildren().elementAt(j);
					if(currentNode instanceof Bullet){
						String text = currentNode.toPlainTextString();
						String [] arr = text.split("：");
						if(arr.length >= 2){
							String key = arr[0].trim();
							String value = arr[1].trim();
							
							switch(key){
							case "所在行业":
								resumedata.setLatestIndustry(value);
								break;
							case "公司名称":
								resumedata.setLatestCompanyName(value);
								break;
							case "所任职位":
								resumedata.setLatestPositionTitle(value);
								break;
							case "目前薪金":
								resumedata.setLatestSalary(value);
								break;
							}
						}
						
					}
				}
			}
		}
	}
	public Void extractBasicInfo(Node node) {
		// 抽取基本信息，包括联系信息
		NodeList childs = node.getChildren();
		for(int i=0;i<childs.size();i++){
			Node child = childs.elementAt(i);
			if(child instanceof Div && "clearfix".equals(((Div)child).getAttribute("class"))){
				int size = child.getChildren().size();
				for(int j = 0;j<size;j++){
					Node currentChild = child.getChildren().elementAt(j);
					if(currentChild instanceof BulletList){
						//开始获取没一个属性值
						NodeList infoNodes = currentChild.getChildren();
						for(int k=0;k<infoNodes.size();k++){
							Node liNode = infoNodes.elementAt(k);
							if(liNode instanceof Bullet){
								String nodeText = liNode.toPlainTextString();
								String [] arr = nodeText.split("：");
								if(arr.length == 2){
									String key = arr[0].trim();
									String value = arr[1].trim();
									switch(key){
									case "姓名":
										if(value.indexOf("******") < 0 && value.indexOf("不公开") < 0){
											resumedata.contactInfoData.setName(value);
										}
										if(value.indexOf("不公开") >= 0){
											resumedata.setNamePub(false);
										}
										break;
									case "性别":
										resumedata.setGender(value);
										break;
									case "电子邮件":
										if(value.indexOf("******") < 0 && value.indexOf("不公开") < 0){
											resumedata.contactInfoData.setEmail(value);
										}
										if(value.indexOf("不公开") >= 0){
											resumedata.setEmailPub(false);
										}
										break;
									case "年龄":
										Integer age = Integer.parseInt(value);
							        	resumedata.setAge(age);
										break;
									case "手机号码":
										if(value.indexOf("******") < 0 && value.indexOf("不公开") < 0){
											resumedata.contactInfoData.setPhone(value);
										}
										if(value.indexOf("不公开") >= 0){
											resumedata.setPhonePub(false);
										}
										break;
									case "所在地":
										resumedata.setAddress(value);
										break;
									case "婚姻状况":
										resumedata.setMaritalStatus(value);
										break;
									case "工作年限":
										value = value.substring(0,value.indexOf("年"));
										Integer worklen = Integer.parseInt(value);
										resumedata.setWorkExperienceLength(worklen);
										break;
									case "目前状态":
										currentStatus = value;
										break;
									case "最高学历":
										int nbspPos = value.indexOf("&nbsp");
										if(nbspPos >= 0){
											value = value.substring(0, nbspPos);
										}
										resumedata.setLatestDegree(value);
										break;
									case "教育程度":
										int nbspPos1 = value.indexOf("&nbsp");
										if(nbspPos1 >= 0){
											value = value.substring(0, nbspPos1);
										}
										resumedata.setLatestDegree(value);
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public String removeHtmlTag(String htmlStr) {
		// TODO Auto-generated method stub
		return super.removeHtmlTag(htmlStr);
	}

	@Override
	public String getSourceID(String html) {
		// TODO Auto-generated method stub
		String sourceID ="";
		Pattern pattern=Pattern.compile("简历编号：(\\d+)\\D",Pattern.CASE_INSENSITIVE);
        Matcher matcher=pattern.matcher(html);
        while(matcher.find()){
        	sourceID = matcher.group(1);
        }        
        return sourceID;
	}

	@Override
	public String getWebsite() {
		// TODO Auto-generated method stub
		return super.getWebsite();
	}

	@Override
	public String getUpdateTime() {
		// TODO Auto-generated method stub
		String updateTime ="";
		Pattern pattern=Pattern.compile("(最后登录|最后更新)：(.*?)</div>",Pattern.CASE_INSENSITIVE);
        Matcher matcher=pattern.matcher(htmlcontent);
        while(matcher.find()){
        	updateTime = matcher.group(2);
        	String []arr = updateTime.split(" "); 
        	if(arr.length==2){
        		updateTime = arr[0];
        	}
        }
        return updateTime;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}

	@Override
	public String getPhone() {
		// TODO Auto-generated method stub
		return super.getPhone();
	}

	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return super.getEmail();
	}

	@Override
	public String getAge() {
		// TODO Auto-generated method stub
		return super.getAge();
	}

	@Override
	public String Gender() {
		// TODO Auto-generated method stub
		return super.Gender();
	}

	@Override
	public String extractSelfEvaluation(Node node) {
		// 获取个人评价
		String selfEvalucation = "";
		NodeList childs = node.getChildren();		
		for(int i=0;i<childs.size();i++){
			Node childNode = childs.elementAt(i);
			if(childNode instanceof Div ){
				selfEvalucation = ((Div)childNode).getChildrenHTML();
				break;
			}
		}		
		return selfEvalucation;
	}

	@Override
	public JobTarget extractJobTarget(Node node) {
		// 解析出工作求职意向 和 目前年薪
		JobTarget jobTarget = new JobTarget();
		NodeList childs = node.getChildren();
		for(int i=0;i<childs.size();i++){
			Node child = childs.elementAt(i);
			if(child instanceof BulletList){
				if("clearfix".equals(((BulletList)child).getAttribute("class"))){
					int size = child.getChildren().size();
					for(int j = 0;j<size;j++){
						Node currentNode = child.getChildren().elementAt(j);
						if(currentNode instanceof Bullet){
							String nodeText = currentNode.toPlainTextString();
							String [] arr = nodeText.split("：");
							if(arr.length == 2){
								String key = arr[0].trim();
								String value = arr[1].trim();
								switch(key){
								case "期望行业":
									jobTarget.setJobIndustry(value);
									break;
								case "期望职位":
									jobTarget.setJobCareer(value);
									break;
								case "期望地点":
									jobTarget.setJobLocation(value);
									break;
								case "期望月薪":
									jobTarget.setSalary(value);
									break;
								case "勿推荐企业":
									jobTarget.setNotRecomandCompanyName(value);
									break;
									
								}
							}
						}
					}
				}
			}
		}
		jobTarget.setStatus(currentStatus);
		return jobTarget;
	}

	@Override
	public String getResumeKeywords(Node node) {
		// TODO Auto-generated method stub
		return super.getResumeKeywords(node);
	}

	public ArrayList<WorkExperienceData> extractWorkExperience(Node node) {
		//提取工作经历
		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
		NodeList childs = node.getChildren();
		int currentNum = 0;
		for(int i=0;i<childs.size();i++){
			Node child = childs.elementAt(i);
			if(child instanceof Div && "clearfix".equals(((Div)child).getAttribute("class"))){
				String companyName = "";
				String companyDesc = "";
				String companyCatagory = "";
				String companyScale = "";
				String companyIndustry = "";
//				WorkExperienceData workExperienceData = new WorkExperienceData();
//				ManageExperienceData manageExperienceData = new ManageExperienceData();
				for(int j=0;j<child.getChildren().size();j++){
					Node currentChild = child.getChildren().elementAt(j);
					if(currentChild instanceof Div && "rows-main".equals(((Div)currentChild).getAttribute("class"))){
						for(int k=0;k<currentChild.getChildren().size();k++){
							Node currentchild2 = currentChild.getChildren().elementAt(k);
							if(currentchild2 instanceof Div && "tit".equals(((Div)currentchild2).getAttribute("class"))){
//								workExperienceData.setCompanyName(currentchild2.toPlainTextString().trim().replaceAll("&nbsp;", ""));
								companyName = currentchild2.toPlainTextString().trim().replaceAll("&nbsp;", "");
							}else if(currentchild2 instanceof Div && "details".equals(((Div)currentchild2).getAttribute("class"))){
								
								for(int m=0;m<currentchild2.getChildren().size();m++){
									Node currentChild3 = currentchild2.getChildren().elementAt(m);
									if(currentChild3 instanceof DefinitionList){
										//提取公司信息
										NodeList childrens = currentChild3.getChildren();
										String title = "";
										String desc = "";
										for(int n=0;n<childrens.size();n++){
											Node node1 = childrens.elementAt(n);
											if(node1 instanceof DefinitionListBullet){
												String tagName = ((DefinitionListBullet)node1).getTagName();
												if("DT".equals(tagName)){
													title = node1.toPlainTextString().replaceAll("：", "");
												}else if("DD".equals(tagName)){
													desc = ((DefinitionListBullet)node1).getChildrenHTML();
												}
												
												switch(title){
												case "公司描述":
													companyDesc = desc;
													break;
												case "公司性质":
													companyCatagory = desc;
													break;
												case "公司规模":
													companyScale = desc;
													break;
												case "公司行业":
													companyIndustry = desc;
													break;
												case "薪酬状况":
													desc = desc.replaceAll("&nbsp;", " ");
													workExperienceDataList.get(currentNum-1).setSalary(desc);
													break;
												case "工作地点":
													desc = desc.replaceAll("&nbsp;", " ");
													workExperienceDataList.get(currentNum-1).setLocation(desc);
													break;
												case "所在部门":
													desc = desc.replaceAll("&nbsp;", " ");
													workExperienceDataList.get(currentNum-1).setDepartment(desc);
													break;
												case "汇报对象":
													desc = desc.replaceAll("&nbsp;", " ");
													workExperienceDataList.get(currentNum-1).getManageExperienceList().get(0).setReportTo(desc);
													break;
												case "下属人数":
													desc = desc.replaceAll("&nbsp;", " ");
													workExperienceDataList.get(currentNum-1).getManageExperienceList().get(0).setSubordinatesNum(desc);
													break;
												case "工作职责":		
													desc = desc.replaceAll("&nbsp;", " ");
													workExperienceDataList.get(currentNum-1).setJobDesc(desc);
													break;
												case "工作业绩":
													desc = desc.replaceAll("&nbsp;", " ");
													workExperienceDataList.get(currentNum-1).getManageExperienceList().get(0).setKeyPerformance(desc);
												}
											}
										}
									}else if(currentChild3 instanceof Div && "job-tit".equals(((Div)currentChild3).getAttribute("class"))){
										//提取职位信息	
										currentNum ++;
										Pattern pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)(.*)");
										Matcher matcher=pattern.matcher(currentChild3.toPlainTextString());
										
										if(matcher.find()){
											WorkExperienceData workExperienceData = new WorkExperienceData();
											ManageExperienceData manageExperienceData = new ManageExperienceData();
											
											workExperienceData.setStartTime(matcher.group(1).replaceAll("\\.", "-"));
											workExperienceData.setEndTime(matcher.group(2).replaceAll("\\.", "-"));
											workExperienceData.setPositionTitle(matcher.group(3));
											workExperienceData.setCompanyName(companyName);
											workExperienceData.setCompanyDesc(companyDesc);
											workExperienceData.setCompanyCatagory(companyCatagory);
											workExperienceData.setCompanyScale(companyScale);
											workExperienceData.setIndustryCatagory(companyIndustry);
											
											workExperienceData.addManageExperience(manageExperienceData);
											
											workExperienceDataList.add(workExperienceData);	
										}
									}else if(currentChild3 instanceof BulletList){
										for(int n=0;n<currentChild3.getChildren().size();n++){
											String txt = currentChild3.getChildren().elementAt(n).toPlainTextString();
											String [] arr = txt.split("：");
											if(arr.length == 2){
												String title = arr[0].trim();
												String desc = arr[1].trim();
												desc = desc.replaceAll("&nbsp;", " ");
												switch(title){
												case "公司描述":
													companyDesc = desc;
													break;
												case "公司性质":
													companyCatagory = desc;
													break;
												case "公司规模":
													companyScale = desc;
													break;
												case "公司行业":
													companyIndustry = desc;
													break;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return workExperienceDataList;
	}

	@Override
	public ArrayList<EducationExperienceData> extractEducationExperience(
			Node node) {
		// 解析教育经历
		ArrayList<EducationExperienceData> educationExperienceDataList = new ArrayList<EducationExperienceData>();
		NodeList childs = node.getChildren();
		for(int i=0;i<childs.size();i++){
			Node child = childs.elementAt(i);
			if(child instanceof Div && "clearfix".equals(((Div)child).getAttribute("class"))){
				EducationExperienceData educationExperienceData = new EducationExperienceData();
				for(int j=0;j<child.getChildren().size();j++){
					Node currentChild = child.getChildren().elementAt(j);
					if(currentChild instanceof Div && "rows-side".equals(((Div)currentChild).getAttribute("class"))){
						String eduTime = currentChild.toPlainTextString();
						String[] arr = eduTime.split("-");
						if(arr.length==2){
							educationExperienceData.setStartTime(arr[0].trim().replaceAll("\\.", "-"));
							educationExperienceData.setEndTime(arr[1].trim().replaceAll("\\.", "-"));
						}
					}else if(currentChild instanceof Div && "rows-main".equals(((Div)currentChild).getAttribute("class"))){
						for(int k=0;k<child.getChildren().size();k++){
							Node currentChild2 = currentChild.getChildren().elementAt(k);
							if(currentChild2 instanceof Div && "tit".equals(((Div)currentChild2).getAttribute("class"))){
								educationExperienceData.setSchool(currentChild2.toPlainTextString());
							}else if(currentChild2 instanceof Div && "details".equals(((Div)currentChild2).getAttribute("class"))){
								for(int m=0;m<currentChild2.getChildren().size();m++){
									Node currentChild3 = currentChild2.getChildren().elementAt(m);
									if(currentChild3 instanceof ParagraphTag){
										//提取专业学历信息
										String nodeText = currentChild3.toPlainTextString();
										String [] arr = nodeText.split("：");
										if(arr.length >= 2){
											String key = arr[0].trim();
											String value = arr[1].trim();
											switch(key){
											case "专业":
												educationExperienceData.setMajor(value);
												break;
											case "学历":
												Pattern pattern = Pattern.compile("(博士后|博士|MBA/EMBA|硕士|本科|大专|中专|中技|高中|初中)",Pattern.CASE_INSENSITIVE );   
												Matcher matcher = pattern.matcher(nodeText);
												if(matcher.find()){
													educationExperienceData.setDegree(matcher.group(1));
											    }
												pattern = Pattern.compile("是否统招.*?(是|否)",Pattern.CASE_INSENSITIVE );   
												matcher = pattern.matcher(nodeText);
											    if(matcher.find()){
											    	educationExperienceData.setSeriesIncurs(matcher.group(1));	    		    	
											    } 
											}
										}
									}
								}
							}
						}
					}
				}
				educationExperienceDataList.add(educationExperienceData);
			}
		}
			
		return educationExperienceDataList;
	}

	@Override
	public ArrayList<TrainingExperienceData> extractTrainingExperience(Node node) {
		// TODO Auto-generated method stub
		return super.extractTrainingExperience(node);
	}

	@Override
	public ArrayList<LanguageSkillData> extractLanguageSkill(Node node) {
		// TODO Auto-generated method stub
		ArrayList<LanguageSkillData> languageSkillDataList = new ArrayList<LanguageSkillData>();
		String languageDesc = "";
		languageDesc = node.toPlainTextString().trim();
		String [] arr = languageDesc.split("、");
		for(int i=0;i<arr.length;i++){
			LanguageSkillData languageSkillData = new LanguageSkillData();
			languageSkillData.setCatagory(arr[i]);
			languageSkillDataList.add(languageSkillData);
		}
		return languageSkillDataList;
	}

	@Override
	public ArrayList<CertificateData> extractCertficate(Node node) {
		// TODO Auto-generated method stub
		return super.extractCertficate(node);
	}

	@Override
	public ArrayList<ProjectExperienceData> extractProjectExperience(Node node) {
		// 解析项目经历
		ArrayList<ProjectExperienceData> projectExperienceDataList = new ArrayList<ProjectExperienceData>();
		NodeList childs = node.getChildren();
		for(int i=0;i<childs.size();i++){
			Node child = childs.elementAt(i);
			if(child instanceof Div && "clearfix".equals(((Div)child).getAttribute("class"))){
				ProjectExperienceData projectExperienceData = new ProjectExperienceData();
				for(int j=0;j<child.getChildren().size();j++){
					Node currentChild = child.getChildren().elementAt(j);
					if(currentChild instanceof Div && "rows-side".equals(((Div)currentChild).getAttribute("class"))){
						String proTime = currentChild.toPlainTextString();
						String[] arr = proTime.split("-");
						if(arr.length==2){
							projectExperienceData.setStartTime(arr[0].trim().replaceAll("\\.", "-"));
							projectExperienceData.setEndTime(arr[1].trim().replaceAll("\\.", "-"));
						}
					}else if(currentChild instanceof Div && "rows-main".equals(((Div)currentChild).getAttribute("class"))){
						for(int k=0;k<currentChild.getChildren().size();k++){
							Node currentChild2 = currentChild.getChildren().elementAt(k);
							if(currentChild2 instanceof Div && "tit".equals(((Div)currentChild2).getAttribute("class"))){
								projectExperienceData.setProjectTitle(currentChild2.toPlainTextString().trim());
							}else if(currentChild2 instanceof Div && "details".equals(((Div)currentChild2).getAttribute("class"))){
								for(int m=0;m<currentChild2.getChildren().size();m++){
									Node currentChild3 = currentChild2.getChildren().elementAt(m);
									if(currentChild3 instanceof DefinitionList){										
										//提取项目信息
										String key = "";
										String value = "";
										for(int l=0;l<currentChild3.getChildren().size();l++){
											Node currentNode5 = currentChild3.getChildren().elementAt(l);
											if(currentNode5 instanceof DefinitionListBullet){
												if(key.equals("")){
													key = currentNode5.toPlainTextString();
												}else{
													value = ((DefinitionListBullet)currentNode5).getChildrenHTML().trim();
												}
											}
										}
										switch(key){
										case "项目职务：":
											value = value.replaceAll("&nbsp;", "");
											projectExperienceData.setPositionTitle(value);
											break;
										case "所在公司：":
											value = value.replaceAll("&nbsp;", "");
											projectExperienceData.setCompany(value);
											break;
										case "项目描述：":
											projectExperienceData.setProjectDesc(value);
											break;
										case "项目职责：":
											projectExperienceData.setResponsibleFor(value);
											break;
										case "项目业绩：":
											projectExperienceData.setProjectPerformance(value);
											break;
										}
										}
									}
								}	
									}
								}
							}
				projectExperienceDataList.add(projectExperienceData);
			}
		}
		return projectExperienceDataList;
	}

	@Override
	public ArrayList<ProfessionalSkillData> extractProfessionalSkill(Node node) {
		// TODO Auto-generated method stub
		return super.extractProfessionalSkill(node);
	}
	
	public String getNodeContent(Node node){
		if(node == null || !(node instanceof Div)){
			return null;
		}
		Div divNode = (Div)node;
		for(int i=0;i<divNode.getChildCount();i++){
			Node tmpNode = divNode.getChildren().elementAt(i);
			if(tmpNode instanceof Div){
				divNode = (Div)tmpNode;
				break;
			}
		}
		String content =  divNode.getChildrenHTML();
		return content;
	}

	@Override
	public ArrayList<PracticalExperienceData> extractPracticalExperience(
			Node node) {
		// TODO Auto-generated method stub
		return super.extractPracticalExperience(node);
	}

	@Override
	public ArrayList<OtherInfoData> extractOtherInfo(Node node) {
		// TODO Auto-generated method stub
		return super.extractOtherInfo(node);
	}
	
	public LiePinResumeParser_V1(String html,String url,String objectid){
		htmlcontent = html;
		this.htmlurl = url;
		resumedata.setUrl(url);
		
//		Pattern pattern = Pattern.compile("<font color=\"red\">(.*?)</font>",Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(htmlcontent);
//        
//        if(matcher.find()){
//        	searchWords = matcher.group(1);
//        }
        htmlcontent = htmlcontent.replaceAll("<font color=\"red\">", "").replaceAll("</font>", "");
		if(htmlcontent.indexOf("&nbsp;") > 0){
			hasNbsp = true;
		}
		this.objectid = objectid;
	}
	
	public LiePinResumeParser_V1(String html,String url){
		htmlcontent = html;
		this.htmlurl = url;
		htmlcontent = htmlcontent.replaceAll("<font color=\"red\">", "").replaceAll("</font>", "");
		if(htmlcontent.indexOf("&nbsp;") > 0){
			hasNbsp = true;
		}		
	}
	
	
	public LiePinResumeParser_V1(){
		
	}
	
	public void setHtml(String html){
		htmlcontent = html;
		
		htmlcontent = htmlcontent.replaceAll("<font color=\"red\">", "").replaceAll("</font>", "");
		if(htmlcontent.indexOf("&nbsp;") > 0){
			hasNbsp = true;
		}
	}
	
	public void setUrl(String url){
		this.htmlurl = url;
		resumedata.setUrl(url);
	}
	
	public void setResumeID(String objectid){
		this.objectid = objectid;
	}
}
