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
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
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
import com.bole.resumeparser.util.MhtParser;

public class LiePinResumeParser extends AbstractResumeParser implements HtmlResumeParserInterface{
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
				if(node instanceof Div){
					arrName = ((Div)node).getAttribute("class");
					idName = ((Div)node).getAttribute("id");
					
					switch(arrName){
					case "alert alert-warning":  //目前状态
						break;
					case "resume-basic":  //处理基本信息包括个人信息，目前职业状况，职业发展意向以及联系信息
						processBasicInfo(node);
						break;
					case "profile career clearfix":
						if(node.toPlainTextString().indexOf("12个月")>=0){
							resumedata.setCurrentSalary(node.toPlainTextString().trim().replaceAll("&nbsp;", " "));
						}else{
							resumedata.setJobTarget(extractJobTarget(node));
						}						
						break;
					case "resume-work":  //工作经验
						resumedata.setWorkExperience(extractWorkExperience(node));
						break;
					case "resume-education":  //教育经历
						resumedata.setEducationExperience(extractEducationExperience(node));
						break;
					case "resume-language":   //语言能力
						resumedata.setLanguageSkill(extractLanguageSkill(node));
						break;
					case "resume-project":  //项目经验
						resumedata.setProjectExperience(extractProjectExperience(node));
						break;
					case "resume-comments" :  //自我评价
						resumedata.setSelfEvaluation(extractSelfEvaluation(node));
						break;
//					case "profile others":
//						resumedata.addOtherInfo("其他信息", getNodeContent(node));	
//						break;
					case "resume-others":
						resumedata.addOtherInfo("其他信息", extractSelfEvaluation(node));	
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
			if((phone == null || phone.trim().equals("") || phone.trim().equals("已屏蔽")|| phone.trim().equals("不公开")) && (email == null || email.trim().equals("")) || phone.trim().equals("已屏蔽")|| phone.trim().equals("不公开")){
				resumedata.contactInfoData = null;
			}else{
				if(resumedata.contactInfoData.getPhone() != null){
					if(resumedata.contactInfoData.getPhone().trim() != ""){
						resumedata.setIsCotactInformation("YES");
					}
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

	public void processBasicInfo(Node node){
		NodeList nodeList = node.getChildren();
		for(int i=0;i<nodeList.size();i++){
			String text = MhtParser.html2text(nodeList.elementAt(i).toHtml());
			if(text.indexOf("姓名") >= 0){
				extractBasicInfo(text);
			}else if(text.indexOf("目前职业概况") >= 0){
				extractCurrentWork(text);
			}else if(text.indexOf("职业发展意向") >= 0){
				resumedata.setJobTarget(extractJobTarget(text));
			}
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
		Pattern pattern = Pattern.compile("res_id_encode=(.*?)(&|$)",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(resumedata.url);
        if(matcher.find()){
        	resumedata.setUrlID(matcher.group(1));
        }
	}
	
	public void extractCurrentWork(String text){
		String[] lines = text.split(System.getProperty("line.separator"));
		for(int i = 0;i < lines.length; i++){
			String line = lines[i];
			line = line.replaceAll("\t", " ");
			Pattern pattern=Pattern.compile("([\u4e00-\u9fa5]+)：.*?(\\S+)",Pattern.CASE_INSENSITIVE);
	        Matcher matcher=pattern.matcher(line);
	        while(matcher.find()){
	        	String title = matcher.group(1);
	        	String value = matcher.group(2);
	        	switch(title){
				case "所在行业":
					resumedata.setLatestIndustry(value);
					break;
				case "公司名称":
					resumedata.setLatestCompanyName(value);
					break;
				case "所任职位":
					resumedata.setLatestPositionTitle(value);
					break;
				case "目前薪资":
					resumedata.setLatestSalary(value);
					break;
				}
	        }
		}
		return;
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
	public void extractBasicInfo(String text){
		String[] lines = text.split(System.getProperty("line.separator"));
		for(int i = 0;i < lines.length; i++){
			String line = lines[i];
//			line = line.replaceAll("\t", " ");
			Pattern pattern=Pattern.compile("([\u4e00-\u9fa5]+)：(.*?)(\\t|$)",Pattern.CASE_INSENSITIVE);
	        Matcher matcher=pattern.matcher(line);
	        while(matcher.find()){
	        	String title = matcher.group(1).trim();
	        	String value = matcher.group(2).trim();
	        	switch(title){
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
					value = value.substring(0,value.indexOf("年")).trim();
					Integer worklen = Integer.parseInt(value);
					resumedata.setWorkExperienceLength(worklen);
					break;
				case "目前状态":
					currentStatus = value;
					break;
				case "学历":
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
	public void extractBasicInfo(Node node) {
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
		return;
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
		Pattern pattern=Pattern.compile("(最后登录|最后更新)：(.*?)</span>",Pattern.CASE_INSENSITIVE);
        Matcher matcher=pattern.matcher(htmlcontent);
        while(matcher.find()){
        	updateTime = matcher.group(2);
        	String []arr = updateTime.split(" "); 
        	if(arr.length==2){
        		updateTime = arr[0];
        		updateTime =updateTime.replace("/", "-");
        	}
        }
        resumedata.setUpdate_time(this.transUpdateTime(updateTime));
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
			if(childNode instanceof TableTag){
				selfEvalucation = ((TableTag)childNode).getRow(0).getColumns()[0].getChildrenHTML().trim();
				break;
			}
		}		
		return selfEvalucation;
	}
	public JobTarget extractJobTarget(String text){
		JobTarget jobTarget = new JobTarget();
		
		String[] lines = text.split(System.getProperty("line.separator"));
		for(int i = 0;i < lines.length; i++){
			String line = lines[i];
			line = line.replaceAll("\t", " ");
			Pattern pattern=Pattern.compile("([\u4e00-\u9fa5]+)：.*?(\\S.*)",Pattern.CASE_INSENSITIVE);
	        Matcher matcher=pattern.matcher(line);
	        while(matcher.find()){
	        	String title = matcher.group(1);
	        	String value = matcher.group(2);
	        	switch(title){
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
		return jobTarget;
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
	
	public ArrayList<WorkExperienceData> getWorkContent(WorkExperienceData workExperienceData,Node node){
		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
		NodeList nodeList = node.getChildren();
		int currentNum = 0;
		for(int i=0;i<nodeList.size();i++){
			Node child = nodeList.elementAt(i);
			if(child instanceof TableTag && "job-list".equals(((TableTag)child).getAttribute("class"))){
				WorkExperienceData workExperienceDataTmp = new WorkExperienceData();
				ManageExperienceData manageExperienceData = new ManageExperienceData();
				TableTag table = (TableTag)child;
				for(int j=0;j<table.getRowCount();j++){
					TableRow tableRow = table.getRow(j);
					String html = tableRow.toHtml();
					if(j == 0){
						html = html.replaceAll("\t", " ");
						Pattern pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)(.*)",Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
						Matcher matcher=pattern.matcher(html);
						if(matcher.find()){
							//提取职位名，时间
							String startTime = matcher.group(1);
							String endTime = matcher.group(2);
							workExperienceDataTmp.setStartTime(startTime);
							workExperienceDataTmp.setEndTime(endTime);
							pattern=Pattern.compile("<strong>(.*?)</strong>",Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
							matcher=pattern.matcher(html);
							if(matcher.find()){
								workExperienceDataTmp.setPositionTitle(matcher.group(1));
							}
							
							pattern=Pattern.compile("(\\d+/月)",Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
							matcher=pattern.matcher(html);
							if(matcher.find()){
								workExperienceDataTmp.setSalary(matcher.group(1));
							}
						}
					}else if(j==1){
						//提取管理信息，工作地
						html = tableRow.toPlainTextString().trim().replace("&nbsp;", "");
						String[] arrs = html.split("\\|");
						for(int k=0 ;k<arrs.length;k++){
							String[] arrs2 = arrs[k].trim().split(":");
							if(arrs2.length == 2){
								String title = arrs2[0];
								String value = arrs2[1];
								switch(title){
								case "工作地点":
									workExperienceDataTmp.setLocation(value);
									break;
								case "所在部门":
									workExperienceDataTmp.setDepartment(value);
									break;
								case "汇报对象":
									manageExperienceData.setReportTo(value);
									break;
								case "下属人数":
									manageExperienceData.setSubordinatesNum(value);
									break;
								}
							}
						}
						workExperienceDataTmp.addManageExperience(manageExperienceData);
					}else if(j==2){
						//提取工作职责
						String job_desc = tableRow.getColumns()[0].getChildrenHTML().trim();
						workExperienceDataTmp.setJobDesc(job_desc);
					}else if(j==3){
						//提取工作业绩
						String key_performance = tableRow.getColumns()[0].getChildrenHTML().trim();
						workExperienceDataTmp.getManageExperienceList().get(0).setKeyPerformance(key_performance);
					}
				}
//				workExperienceDataTmp.setStartTime(workExperienceData.getStartTime());
//				workExperienceDataTmp.setEndTime(workExperienceData.getEndTime());
				workExperienceDataTmp.setCompanyName(workExperienceData.getCompanyName());
				workExperienceDataTmp.setDruation(workExperienceData.getDruation());
				workExperienceDataTmp.setCompanyCatagory(workExperienceData.getCompanyCatagory());
				workExperienceDataTmp.setIndustryCatagory(workExperienceData.getIndustryCatagory());
				workExperienceDataTmp.setCompanyScale(workExperienceData.getCompanyScale());
				workExperienceDataList.add(workExperienceDataTmp);
				currentNum ++;
			}else if(child instanceof TableTag){
				String text = child.toPlainTextString().trim();
				text = text.replace("&nbsp;", "");
				String[] arrs = text.split("\\|");
				if(arrs.length == 3){
					workExperienceData.setCompanyScale(arrs[2]);
					workExperienceData.setIndustryCatagory(arrs[1]);
					workExperienceData.setCompanyCatagory(arrs[0]);
				}else if(arrs.length == 2){
					workExperienceData.setCompanyCatagory(arrs[0]);
					workExperienceData.setIndustryCatagory(arrs[1]);
				}else if(arrs.length == 1){
					workExperienceData.setCompanyCatagory(arrs[0]);
				}
			}
		}
		return workExperienceDataList;
	}
	
	public WorkExperienceData getWorkHeader(Node node){
		WorkExperienceData workExperienceData = new WorkExperienceData();
		String text = MhtParser.html2text(node.toHtml());
		String[] lines = text.split(System.getProperty("line.separator"));
		for(int i = 0;i < lines.length; i++){
			String line = lines[i];
			Pattern pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)(.*)");
			Matcher matcher=pattern.matcher(line);
			if(matcher.find()){
				String startTime = matcher.group(1).replaceAll("\\.", "-");
				String endTime = matcher.group(2).replaceAll("\\.", "-");
				
				workExperienceData.setStartTime(startTime);
				workExperienceData.setEndTime(endTime);
			}
		}
		Pattern pattern=Pattern.compile("<em.*?>(.*?)</em>",Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
		String nodeHtml = node.toHtml();
		Matcher matcher=pattern.matcher(nodeHtml);
		if(matcher.find()){
			String companyHtml = matcher.group(1);
			Pattern pattern2=Pattern.compile("<span>(.*?)</span>",Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
			Matcher matcher2=pattern2.matcher(companyHtml);
			if(matcher2.find()){
				String duration = matcher2.group(1);
				duration = duration.replace("\r", "").replace("\n", "").replace(" ", "").replace("\t", "").replace("(", "").replace(")", "");
				workExperienceData.setDruation(duration);
				String companyName = companyHtml.substring(0, companyHtml.indexOf("<span>"));
				workExperienceData.setCompanyName(companyName);
			}else{
				workExperienceData.setCompanyName(matcher.group(1).trim());
			}
		}
		
		return workExperienceData;
	}
	public ArrayList<WorkExperienceData> extractWorkExperience(Node node) {
		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
		NodeList childs = node.getChildren();
		WorkExperienceData workExperienceData = new WorkExperienceData();
		if(childs != null){
			for(int i=0;i<childs.size();i++){
				Node child = childs.elementAt(i);
				if(child instanceof Div && "resume-job-title".equals(((Div)child).getAttribute("class"))){
					workExperienceData = getWorkHeader(child);
				}else if(child instanceof Div && "resume-indent".equals(((Div)child).getAttribute("class"))){
					ArrayList<WorkExperienceData> workExperienceDataList1 = getWorkContent(workExperienceData, child);
					for(int j =0;j<workExperienceDataList1.size();j++){
						workExperienceDataList.add(workExperienceDataList1.get(j));
					}
				}
			}
		}
		
		return workExperienceDataList;
	}
	
	@Override
	public ArrayList<EducationExperienceData> extractEducationExperience(Node node) {
		// 解析教育经历
		ArrayList<EducationExperienceData> educationExperienceDataList = new ArrayList<EducationExperienceData>();
		
		NodeList childs = node.getChildren();
		for(int i=0;i<childs.size();i++){
			Node child = childs.elementAt(i);
			if (child instanceof TableTag){
				EducationExperienceData educationExperienceData = new EducationExperienceData();
				TableTag table = (TableTag)child;
				for(int j=0;j<table.getRowCount();j++){
					TableRow tableRow = table.getRow(j);
					String text = tableRow.toPlainTextString().trim();
					String html = tableRow.getChildrenHTML();
					if(text.startsWith("专业")){
						Pattern pattern=Pattern.compile("<td>(.*?)：(.*?)</td>",Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
						Matcher matcher=pattern.matcher(html);
						while(matcher.find()){
							String key = matcher.group(1);
							String value = matcher.group(2);
							switch(key){
							case "专业":
								educationExperienceData.setMajor(value);
								break;
							case "学历":
								educationExperienceData.setDegree(value);
								break;
							case "是否统招":
								educationExperienceData.setSeriesIncurs(value);
								break;
							}
						}
					}else{
						Pattern pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)(.*)",Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
						Matcher matcher=pattern.matcher(html);
						if(matcher.find()){
							//提取职位名，时间
							String startTime = matcher.group(1);
							String endTime = matcher.group(2);
							educationExperienceData.setStartTime(startTime);
							educationExperienceData.setEndTime(endTime);
							pattern=Pattern.compile("<strong>(.*?)</strong>",Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
							matcher=pattern.matcher(html);
							if(matcher.find()){
								educationExperienceData.setSchool(matcher.group(1));
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
		
		NodeList childs = node.getChildren();
		for(int i=0;i<childs.size();i++){
			Node child = childs.elementAt(i);
			if (child instanceof TableTag){
				String languageText = child.toPlainTextString().trim();
				String [] arr = languageText.split("、");
				for(int j=0;j<arr.length;j++){
					LanguageSkillData languageSkillData = new LanguageSkillData();
					languageSkillData.setCatagory(arr[j]);
					languageSkillDataList.add(languageSkillData);
				}
			}
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
		ArrayList<ProjectExperienceData> projectExperienceDataList = new ArrayList<ProjectExperienceData>();
		NodeList childs = node.getChildren();
		for(int i=0;i<childs.size();i++){
			Node child = childs.elementAt(i);
			if(child instanceof TableTag && "project-list".equals(((TableTag)child).getAttribute("class"))){
				TableTag table = (TableTag)child;
				ProjectExperienceData projectExperienceData = new ProjectExperienceData();
				for(int j=0;j<table.getRowCount();j++){
					TableRow tableRow = table.getRow(j);
					String text = tableRow.toPlainTextString().trim();
					if(text.startsWith("项目职务")){
						String positionTitle = tableRow.getColumns()[0].getChildrenHTML().trim();
						projectExperienceData.setPositionTitle(positionTitle);
					}else if(text.startsWith("所在公司")){
						String company = tableRow.getColumns()[0].getChildrenHTML().trim();
						projectExperienceData.setCompany(company);
					}else if(text.startsWith("项目简介")){
						String projectDesc = tableRow.getColumns()[0].getChildrenHTML().trim();
						projectExperienceData.setProjectDesc(projectDesc);
					}else if(text.startsWith("项目职责")){
						String respon = tableRow.getColumns()[0].getChildrenHTML().trim();
						projectExperienceData.setResponsibleFor(respon);
					}else{
						String html = tableRow.getChildrenHTML().trim();
						Pattern pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)(.*)",Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
						Matcher matcher=pattern.matcher(html);
						if(matcher.find()){
							//提取项目名，时间
							String startTime = matcher.group(1);
							String endTime = matcher.group(2);
							projectExperienceData.setStartTime(startTime);
							projectExperienceData.setEndTime(endTime);
							pattern=Pattern.compile("<strong>(.*?)</strong>",Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
							matcher=pattern.matcher(html);
							if(matcher.find()){
								projectExperienceData.setPositionTitle(matcher.group(1));
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
	
	public LiePinResumeParser(String html,String url,String objectid){
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
	
	public LiePinResumeParser(String html,String url){
		htmlcontent = html;
		this.htmlurl = url;
		htmlcontent = htmlcontent.replaceAll("<font color=\"red\">", "").replaceAll("</font>", "");
		if(htmlcontent.indexOf("&nbsp;") > 0){
			hasNbsp = true;
		}		
	}
	
	
	public LiePinResumeParser(){
		
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
