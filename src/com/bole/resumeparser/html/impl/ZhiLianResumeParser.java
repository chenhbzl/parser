package com.bole.resumeparser.html.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.HeadingTag;

import com.bole.config.Status;

import org.htmlparser.tags.TableTag;

import com.bole.resumeparser.exception.document.HtmlResumeParseException;
import com.bole.resumeparser.exception.html.DocResumeParseException;
import com.bole.resumeparser.models.*;

public class ZhiLianResumeParser extends AbstractResumeParser implements HtmlResumeParserInterface{
	/**
	 * 已经完成对基本信息的解析(性别，年龄),求职意向，项目经历，教育经历，自我评价的解析
	 * 需要完善职业技能，培训经历，证书，在校实践经验，其他信息的解析
	 */
	/**
	 * @author liyao
	 * @version 1.5
	 */
	
	public String htmlcontent = ""; //简历html内容
	public String htmlurl = "";   //简历url
	boolean hasNbsp = false;      //用于区别简历是后台爬虫爬取（爬虫爬取的不包含&nbsp;）如果没有&nbsp，则&nbsp;被替换为了相应的unicode值
	String objectid = null;     //简历的objectid(和collectedResume表中objectid一致)
	ResumeData resumedata = new ResumeData(); 
	public String resumeText  = "";   //去除html的简历内容（用于提取所有的url使用）

	/**
	 * 简历解析
	 */
	@Override
	public ResumeData parse() throws HtmlResumeParseException{
		//主要负责解析简历将解析之后的数据保存到ResumeData中
		if(!this.isValidResume()){
			throw new HtmlResumeParseException(Status.DELETED_RESUME);			
		}
		
		resumedata.url =  htmlurl;
		resumedata.set_id(objectid);
		resumedata.setSource("zhilian");
		resumedata.contactInfoData.setSource("zhilian");
		resumedata.setUpdateTime(getUpdateTime());
		
		Parser parser = Parser.createParser(htmlcontent, "utf-8");
		
		NodeFilter summaryFilter = new HasAttributeFilter("class","summary");
		NodeFilter workExperienceFilter = new HasAttributeFilter("class","resume-preview-all workExperience");
		NodeFilter workExperienceFilter2 = new HasAttributeFilter("class","resume-preview-all workexperience");
		NodeFilter otherInfoFilter = new HasAttributeFilter("class","resume-preview-all");
		NodeFilter contactInfoFilter = new HasAttributeFilter("class","summary-bottom");
		NodeFilter nameInfoFilter = new HasAttributeFilter("id","userName");
		
		
		NodeFilter[] filters = new NodeFilter[6];
		filters[0] = summaryFilter;
		filters[1] = workExperienceFilter;
		filters[2] = otherInfoFilter;
		filters[3] = contactInfoFilter;
		filters[4] = nameInfoFilter;
		filters[5] = workExperienceFilter2;
		
		OrFilter orfilter = new OrFilter();
		orfilter.setPredicates(filters);
		
		NodeList nodes;
		try {
			String sourceID = getSourceID(htmlcontent);
			resumedata.setSourceID(sourceID);
			extractUrlID();
			
			nodes = parser.extractAllNodesThatMatch(orfilter);
			for(int i = 0;i < nodes.size();i++){
				Node node = nodes.elementAt(i);
				//resumeText 用于提取简历中所有url,对其进行预处理
				this.resumeText += node.toHtml().replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", " ").replaceAll("</[a-zA-Z]+[1-9]?>", " ").replaceAll("\u4e00-\u9fa5", " "); 
				if(node instanceof Div){
					if("summary".equals(((Div) node).getAttribute("class"))){
						//基本信息
						String basicInfo = "";
						
						basicInfo = node.toPlainTextString().trim();
						
						resumedata.setImage_url(this.getImageUrl(node.toHtml()));
						
						String [] arr = basicInfo.split("\\|"); 
						if(arr.length >= 2){
							Pattern pattern=Pattern.compile("(男|女)",Pattern.CASE_INSENSITIVE);							
							Matcher matcher=pattern.matcher(arr[0]);
					        if(matcher.find()){
					        	resumedata.setGender(matcher.group(1)); 
					        }
					        
					        pattern=Pattern.compile("(\\d+)岁\\((.*?)\\)",Pattern.CASE_INSENSITIVE);							
							matcher=pattern.matcher(arr[0]);
					        if(matcher.find()){
					        	Integer age = Integer.parseInt(matcher.group(1));
					        	resumedata.setAge(age);
					        	resumedata.setBirthday(matcher.group(2));
					        }
					        
					        pattern=Pattern.compile("(未婚|已婚|离异)",Pattern.CASE_INSENSITIVE);							
							matcher=pattern.matcher(arr[0]);
					        if(matcher.find()){
					        	String maritalStatus = matcher.group(1);
					        	resumedata.setMaritalStatus(maritalStatus);
					        }
					        
					        pattern=Pattern.compile("(\\d+)年工作经验",Pattern.CASE_INSENSITIVE);							
							matcher=pattern.matcher(arr[0]);
					        if(matcher.find()){
					        	resumedata.setWorkExperienceLength(Integer.parseInt(matcher.group(1)));
					        }
					        
					        pattern=Pattern.compile("现居住地：(.*)",Pattern.CASE_INSENSITIVE);
					        matcher=pattern.matcher(arr[0]);
					        if(matcher.find()){
					        	resumedata.setAddress(matcher.group(1).trim());
					        }
					        
							if(basicInfo.indexOf("户口：") >= 0){
								pattern=Pattern.compile("户口：(.*?)(\\s|$)",Pattern.CASE_INSENSITIVE);
						        matcher=pattern.matcher(arr[1]);
						        if(matcher.find()){
						        	resumedata.setHouseHolds(matcher.group(1).trim());
						        }
							}
							
							pattern=Pattern.compile("(中共党员|团员|群众|民主党派|无党派人士)",Pattern.CASE_INSENSITIVE);
					        matcher=pattern.matcher(arr[arr.length-1]);
					        if(matcher.find()){
					        	resumedata.setPoliticalLandscape(matcher.group(1).trim());
					        }
					        
						}else if(arr.length == 1){
							Pattern pattern=Pattern.compile("(男|女)",Pattern.CASE_INSENSITIVE);							
							Matcher matcher=pattern.matcher(arr[0]);
					        if(matcher.find()){
					        	resumedata.setGender(matcher.group(1)); 
					        }
					        
					        pattern=Pattern.compile("(\\d+)岁\\((.*?)\\)",Pattern.CASE_INSENSITIVE);							
							matcher=pattern.matcher(arr[0]);
					        if(matcher.find()){
					        	Integer age = Integer.parseInt(matcher.group(1));
					        	resumedata.setAge(age);
					        	resumedata.setBirthday(matcher.group(2));
					        }
					        
					        pattern=Pattern.compile("现居住地：(.*)",Pattern.CASE_INSENSITIVE);
					        matcher=pattern.matcher(arr[0]);
					        if(matcher.find()){
					        	resumedata.setAddress(matcher.group(1).trim());
					        }
						}										        
						
					}
					if("summary-bottom".equals(((Div) node).getAttribute("class"))){
						extractContactInfo(node);
					}
					if("userName".equals(((Div) node).getAttribute("id"))){
						String name = this.getName(node);
						resumedata.contactInfoData.setName(name);
					}
					if("resume-preview-all".equals(((Div) node).getAttribute("class"))){
						//除工作经历以外的其他信息
						if(node.getChildren().size() >= 2){
							String title = "";
							for (int num=0;num<node.getChildren().size();num++){
								if (node.getChildren().elementAt(num) instanceof HeadingTag && "H3".equals(((HeadingTag)node.getChildren().elementAt(num)).getTagName())){
									title = node.getChildren().elementAt(num).toPlainTextString().trim();
								}
							}
							
							if("教育经历".equals(title)){
								ArrayList<EducationExperienceData> educationexperiencelist = extractEducationExperience(node);
								resumedata.setEducationExperience(educationexperiencelist);
							}else if("求职意向".equals(title) || "Career Objective".equals(title)){
								if(title.equals("Career Objective")){
									throw  new HtmlResumeParseException(Status.UNSUPPORTED_RESUME);
								}
								JobTarget jobtarget = extractJobTarget(node);
								resumedata.setJobTarget(jobtarget);
							}else if("项目经历".equals(title) || "Professional Skills".equals(title)){
								ArrayList<ProjectExperienceData> projectExperienceDataList = extractProjectExperience(node);
								resumedata.setProjectExperience(projectExperienceDataList);
							}else if("语言能力".equals(title)){
								ArrayList<LanguageSkillData> languageSkillDataList = extractLanguageSkill(node);
								resumedata.setLanguageSkill(languageSkillDataList);
							}else if("自我评价".equals(title) || "Self-Assessment".equals(title) || "职业目标".equals(title)){

								String selfEvaluation = extractSelfEvaluation(node);
								resumedata.setSelfEvaluation(selfEvaluation);
							}else if("简历内容".equals(title)){

								String resumeContent = extractNodeContent(node);
								resumedata.setResumeContent(resumeContent);
							}else if("培训经历".equals(title) || "Training".equals(title)){

								ArrayList<TrainingExperienceData> trainingExperienceDataList = extractTrainingExperience(node);
								resumedata.setTrainingExperience(trainingExperienceDataList);
							}else if("证书".equals(title)){

								ArrayList<CertificateData> certificateDataList = extractCertficate(node);
								resumedata.setCertficate(certificateDataList);
							}else if("专业技能".equals(title) || "Professional Skills".equals(title)){
								ArrayList<ProfessionalSkillData> professionalSkillDataList = extractProfessionalSkill(node);
								resumedata.setProfessionalSkill(professionalSkillDataList);
							}else if("在校学习情况".equals(title)){
								/**
								 * 
								 */
//								StudyInfoData studyInfoData = new StudyInfoData();
//								studyInfoData = extractStudyInfo(node);
//								resumedata.setStudyInfo(studyInfoData);
							}else if("在校实践经验".equals(title)){
								ArrayList<PracticalExperienceData> practicalExperienceList = extractPracticalExperience(node);
								resumedata.setPracticalExperience(practicalExperienceList);
							}else if("职业目标".equals(title)){
								String careerTarget = extractNodeContent(node);
								resumedata.setSelfEvaluation(careerTarget);
							}else{
								String content = extractNodeContent(node);
								resumedata.addOtherInfo(title, content);
							}
						}
						
					}
					
					if("resume-preview-all workExperience".equals(((Div) node).getAttribute("class")) || "resume-preview-all workexperience".equals(((Div) node).getAttribute("class"))){
						//工作信息
						ArrayList<WorkExperienceData> workExperienceDataList = extractWorkExperience(node);
						resumedata.setWorkExperience(workExperienceDataList);
					}
				}
			}
			
			resumedata.contactInfoData.setSourceID(resumedata.getSourceID());
			resumedata.contactInfoData.setSource(resumedata.getSource());
			resumedata.contactInfoData.setResumeID(resumedata.get_id());
			
			if(resumedata.getSourceID() == null || "".equals(resumedata.getSourceID())){
				throw new HtmlResumeParseException();
			}
			
			String phone = resumedata.contactInfoData.getPhone();
			String email = resumedata.contactInfoData.getEmail();
			if((phone == null || phone == "") && (email == null || email == "")){
				resumedata.contactInfoData = null;
			}else{
				if(resumedata.contactInfoData.getPhone().trim() != ""){
					resumedata.setIsCotactInformation("YES");
				}
				resumedata.contactInfoData.set_id(resumedata.get_id());
			}
			
			resumedata.setCreateTime(new Date());
			return resumedata;
			
		} catch (ParserException e) {
			throw new HtmlResumeParseException(e);
		}
	}
	
	
	
	/**
	 * 获取简历ID
	 * @param html 包含id的html文本
	 * @return 简历ID
	 */
	@Override
	public String getSourceID(String html) {
		// TODO Auto-generated method stub
		String sourceID = "";
		Pattern pattern=Pattern.compile("resume-left-tips-id\">ID:(.*?)(</span></div>)",Pattern.CASE_INSENSITIVE);
        Matcher matcher=pattern.matcher(html);
        if(matcher.find()){
        	sourceID = matcher.group(1);        	
        }
        if(logger.isDebugEnabled()){
        	logger.debug("sourceID is "+ sourceID);
        }
		return sourceID;
	}
	
	public void extractUrlID(){		
		Pattern pattern = Pattern.compile("viewone/2/(.*?(_\\d)+)",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(resumedata.url);
        if(matcher.find()){
        	resumedata.setUrlID(matcher.group(1));
        }
        
        pattern = Pattern.compile("viewone/1/(\\d+)",Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(resumedata.url);
        if(matcher.find()){
        	resumedata.setViewID(matcher.group(1));
        }
	}

	/**
	 * 获取简历中头像url
	 * @param content 简历的html内容
	 * @return 简历中头像url
	 */
	public String  getImageUrl(String content){
		String imageUrl = "";
		Pattern pattern = null;
		pattern = Pattern.compile("(http://my.zhaopin.com/pic/.*?.jpg)",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        if(matcher.find()){
        	imageUrl = matcher.group(1);
        }
        return imageUrl;
	}
	
	@Override
	public String getWebsite() {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * 获取简历更新时间
	 * @return 简历更新时间
	 */
	@Override
	public String getUpdateTime() {
		// TODO Auto-generated method stub
		String updateTime = "";
		Pattern pattern = null;
		if(hasNbsp){
			pattern=Pattern.compile("resumeUpdateTime.innerHTML.*\"(.*?)\"",Pattern.CASE_INSENSITIVE);
		}
		else{
			pattern=Pattern.compile("resumeUpdateTime.*?(\\d+年.*?日)",Pattern.CASE_INSENSITIVE);
		}
        Matcher matcher=pattern.matcher(this.htmlcontent);
        if(matcher.find()){
        	updateTime = matcher.group(1);        	
        }
        if(logger.isDebugEnabled()){
			logger.debug("updateTime is " + updateTime);
		}
        updateTime = updateTime.replaceAll("年", "-");
        updateTime = updateTime.replaceAll("月", "-");
        updateTime = updateTime.replaceAll("日", "");
        resumedata.setUpdate_time(this.transUpdateTime(updateTime));
		return updateTime;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 获取用户名
	 * @param node 包含求职者姓名的html 节点
	 * @return 求职者姓名
	 */
	public String getName(Node node){
		String name = "";
		name = node.toPlainTextString();
		return name;
	}


	/**
	 * 获取求职者联系信息,将联系信息保存到resumeData成员中
	 * @param 包含求职者联系信息的html节点
	 */
	@Override
	public void extractContactInfo(Node node) {
		// TODO Auto-generated method stub
		String contactInfoContent = node.toHtml();
		
		Pattern pattern = null;
		pattern = Pattern.compile("手机：.*?(\\d+)",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(contactInfoContent);
        if(matcher.find()){
        	String phone = matcher.group(1);
        	resumedata.contactInfoData.setPhone(phone);
        }
        
        pattern = Pattern.compile("身份证：((\\d+)(x|X)?)",Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(contactInfoContent);
        if(matcher.find()){
        	String identityID = matcher.group(1);
        	resumedata.contactInfoData.setIdentityID(identityID);
        }
        
        pattern = Pattern.compile("[eE]-mail：.*?\">(.*?)</a>",Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(contactInfoContent);
        if(matcher.find()){
        	String email = matcher.group(1);
        	resumedata.contactInfoData.setEmail(email);
        }
        
        pattern = Pattern.compile("个人主页：.*?\">(.*?)</a>",Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(contactInfoContent);
        if(matcher.find()){
        	resumedata.setHomePage(matcher.group(1));
        }
		return;
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
	 * 获取求职者自我评价
	 * @param 包含自我评价的html节点
	 * @return 自我评价内容
	 */
	@Override
	public String extractSelfEvaluation(Node node) {
		// TODO Auto-generated method stub
		if(node == null){
			return null;
		}
		String selfEvaluation = "";
		
		//获取div节点
		NodeList nodes = node.getChildren();
		NodeFilter filter = new NodeClassFilter(Div.class);
		nodes = nodes.extractAllNodesThatMatch(filter);
		for(int i = 0;i < nodes.size();i++){
			selfEvaluation += ((Div)(nodes.elementAt(i))).getChildrenHTML();
		}		
		return selfEvaluation;
	}
	
	public String extractNodeContent(Node node) {
		// TODO Auto-generated method stub
		if(node == null){
			return null;
		}
		String content = "";
		
		//获取div节点
		NodeList nodes = node.getChildren();
		NodeFilter filter = new NodeClassFilter(Div.class);
		nodes = nodes.extractAllNodesThatMatch(filter);
		for(int i = 0;i < nodes.size();i++){
			content += ((Div)(nodes.elementAt(i))).getChildrenHTML();
		}		
		return content;
	}


	/**
	 * 获取求职者求职意向
	 * @param 包含求职意向的html节点
	 * @return 求职意向 @see JobTarget 
	 */
	@Override
	public JobTarget extractJobTarget(Node node) {
		// TODO Auto-generated method stub
		if(node == null){
			return null;
		}
		JobTarget jobTarget = new JobTarget();
		
		//获取node子节点中的div节点
		NodeList nodes = node.getChildren();
		NodeFilter filter = new NodeClassFilter(Div.class);
		nodes = nodes.extractAllNodesThatMatch(filter);
		
		//进一步抽取到table tag
		if(nodes.size() >= 1){
			Node divNode = nodes.elementAt(0);
			nodes = divNode.getChildren();
			NodeFilter tableFilter = new NodeClassFilter(TableTag.class);
			nodes = nodes.extractAllNodesThatMatch(tableFilter);
			
			//获取table中具体内容
			if(nodes.size() >= 1){
				TableTag tableTag = (TableTag) nodes.elementAt(0);
				
				for(int i = 0;i < tableTag.getRowCount();i++){
					switch (tableTag.getRow(i).getColumns()[0].toPlainTextString()){
						case "期望工作地区：" :
							jobTarget.setJobLocation(tableTag.getRow(i).getColumns()[1].toPlainTextString());
							break;
						case "期望月薪：":
							jobTarget.setSalary(tableTag.getRow(i).getColumns()[1].toPlainTextString());
							break;
						case "目前状况：":
							jobTarget.setStatus(tableTag.getRow(i).getColumns()[1].toPlainTextString());
							break;
						case "期望工作性质：":
							jobTarget.setJobCatagory(tableTag.getRow(i).getColumns()[1].toPlainTextString());
							break;
						case "期望从事职业：":
							jobTarget.setJobCareer(tableTag.getRow(i).getColumns()[1].toPlainTextString());
							break;
						case "期望从事行业：":
							jobTarget.setJobIndustry(tableTag.getRow(i).getColumns()[1].toPlainTextString());
							break;
						case "Desired Location：":
							jobTarget.setJobLocation(tableTag.getRow(i).getColumns()[1].toPlainTextString());
							break;
						case "Desired Salary：":
							jobTarget.setSalary(tableTag.getRow(i).getColumns()[1].toPlainTextString());
							break;
						case "Type of Employment：":
							jobTarget.setJobCatagory(tableTag.getRow(i).getColumns()[1].toPlainTextString());
							break;
						case "Desired Position：":
							jobTarget.setJobCareer(tableTag.getRow(i).getColumns()[1].toPlainTextString());
							break;
						case "Desired Industry：":
							jobTarget.setJobIndustry(tableTag.getRow(i).getColumns()[1].toPlainTextString());
							break;
							
					}
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
	@Override
	public ArrayList<WorkExperienceData> extractWorkExperience(Node node) {
		// TODO Auto-generated method stub
		if(node==null){
			return null;
		}
		
		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
		NodeList nodes = node.getChildren();

		int index = 0;
		for(int i = 0;i < nodes.size();i++){
			Node childnode = nodes.elementAt(i);
			if(childnode instanceof HeadingTag && "H2".equals(((HeadingTag)childnode).getTagName())){
				String basicInfo = childnode.toPlainTextString().trim();
				
				WorkExperienceData workExperienceData = new WorkExperienceData();
				String [] arr = null;
				Pattern pattern = null;
				if(this.hasNbsp){
					arr = basicInfo.split("&nbsp;&nbsp;");
				}else{
					arr = basicInfo.split("\\u00A0\\u00A0");
				}
				pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)");
				Matcher matcher=pattern.matcher(basicInfo);
				if(matcher.find()){
					pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)");
					matcher=pattern.matcher(basicInfo);
					if(matcher.find()){
						workExperienceData.setStartTime(matcher.group(1).trim().replaceAll("\\.", "-"));
			        	workExperienceData.setEndTime(matcher.group(2).trim().replaceAll("\\.", "-"));
					}
					if(arr.length == 3){
						workExperienceData.setCompanyName(arr[1]);
						workExperienceData.setDruation(arr[2].replace("（", "").replace("）", ""));
					}
					if(arr.length == 2){
						workExperienceData.setCompanyName(arr[1]);
					}
					workExperienceDataList.add(workExperienceData);					
					index++;
				}
				
			}else if(childnode instanceof HeadingTag && "H5".equals(((HeadingTag)childnode).getTagName())){
				String positionTitle =  childnode.toPlainTextString().trim();
				String [] arr = positionTitle.split("\\|");
				if(arr.length == 1){
					if(arr[0].indexOf("元/月") < 0){
						((WorkExperienceData)workExperienceDataList.get(index-1)).setPositionTitle(arr[0]);
					}					
				}else if(arr.length == 2){
					if(positionTitle.indexOf("元/月") >= 0){
						((WorkExperienceData)workExperienceDataList.get(index-1)).setPositionTitle(arr[0]);
						((WorkExperienceData)workExperienceDataList.get(index-1)).setSalary(arr[1]);
					}else{
						((WorkExperienceData)workExperienceDataList.get(index-1)).setDepartment(arr[0]);
						((WorkExperienceData)workExperienceDataList.get(index-1)).setPositionTitle(arr[1]);
					}
				}else if(arr.length == 3){
					((WorkExperienceData)workExperienceDataList.get(index-1)).setDepartment(arr[0]);
					((WorkExperienceData)workExperienceDataList.get(index-1)).setPositionTitle(arr[1]);
					((WorkExperienceData)workExperienceDataList.get(index-1)).setSalary(arr[2]);
				}
				
			}else if (!hasNbsp){
				if (childnode instanceof Div && childnode.getChildren().size() == 1){
					if(childnode.getChildren().elementAt(0) instanceof TableTag){
						NodeList childnodes = childnode.getChildren();
						for(int num=0;num<childnodes.size();num++){
							Node currentNode = childnodes.elementAt(num);
							if(currentNode instanceof TableTag){
								TableTag tanode = (TableTag)currentNode;
								for(int j = 0;j < tanode.getRowCount();j++){
									switch(tanode.getRow(j).getColumns()[0].toPlainTextString().trim()){
										case "工作描述：":
											String jobdesc = tanode.getRow(j).getColumns()[1].getChildrenHTML().trim();
											if(jobdesc.startsWith("<br>")){
												jobdesc = jobdesc.substring(4);
											}if(jobdesc.startsWith("<br />")){
												jobdesc = jobdesc.substring(6);
											}
											((WorkExperienceData)workExperienceDataList.get(index-1)).setJobDesc(jobdesc);
											break;
										case "管理经验：":
											ManageExperienceData manageExperience = new ManageExperienceData(); 
											String manageDesc = tanode.getRow(j).getColumns()[1].getChildrenHTML().trim();
											String[] arr = manageDesc.split("<br />");
											String infonode = "";
											if(arr.length>0){
												infonode = arr[0];
												arr = infonode.split("\\|");
												for(int k=0;k<arr.length;k++){
													if(arr[k].indexOf("：")>=0){
														switch(arr[k].split("：")[0].trim()){
														case "汇报对象":
															manageExperience.setReportTo(arr[k].split("：")[1].trim());
															break;
														case "下属人数":
															manageExperience.setSubordinatesNum(arr[k].split("：")[1].trim());
															break;
														case "直接下属":
															manageExperience.setSuborinates(arr[k].split("：")[1].trim());
															break;
														}
													}
												}
											}
											if(manageDesc.indexOf("业绩描述：")>=0){
												manageExperience.setKeyPerformance(manageDesc.substring(manageDesc.indexOf("业绩描述：")+5).trim());
												((WorkExperienceData)workExperienceDataList.get(index-1)).manageExperienceDataList.add(manageExperience);
											}
										
									}
									
								}
							}
						}
					}else{
						String companyDesc = childnode.toPlainTextString().trim();
						String [] arr = companyDesc.split("\\|");
						for(int num =0 ;num< arr.length;num++){
							if(arr[num].indexOf(("企业性质："))>=0){
								if(arr[num].split("：").length == 2){
									((WorkExperienceData)workExperienceDataList.get(index-1)).setCompanyCatagory(arr[num].split("：")[1]);
								}
							}else if(arr[num].indexOf(("规模："))>=0){
								if(arr[num].split("：").length == 2){
									((WorkExperienceData)workExperienceDataList.get(index-1)).setCompanyScale(arr[num].split("：")[1]);
								}
							}else{							
								((WorkExperienceData)workExperienceDataList.get(index-1)).setIndustryCatagory(arr[num]);
							}
						}
					}
				}
				else{
					if(childnode instanceof Div && childnode.getChildren().size() > 1){
						NodeList childnodes = childnode.getChildren();
						for(int num=0;num<childnodes.size();num++){
							Node currentNode = childnodes.elementAt(num);
							if(currentNode instanceof TableTag){
								TableTag tanode = (TableTag)currentNode;
								for(int j = 0;j < tanode.getRowCount();j++){
									switch(tanode.getRow(j).getColumns()[0].toPlainTextString().trim()){
										case "工作描述：":
											String jobdesc = tanode.getRow(j).getColumns()[1].getChildrenHTML().trim();
											if(jobdesc.startsWith("<br>")){
												jobdesc = jobdesc.substring(4);
											}if(jobdesc.startsWith("<br />")){
												jobdesc = jobdesc.substring(6);
											}
											((WorkExperienceData)workExperienceDataList.get(index-1)).setJobDesc(jobdesc);
											break;
										case "管理经验：":
											ManageExperienceData manageExperience = new ManageExperienceData(); 
											String manageDesc = tanode.getRow(j).getColumns()[1].getChildrenHTML().trim();
											String[] arr = manageDesc.split("<br />");
											String infonode = "";
											if(arr.length>0){
												infonode = arr[0];
												arr = infonode.split("\\|");
												for(int k=0;k<arr.length;k++){
													if(arr[k].indexOf("：")>=0){
														switch(arr[k].split("：")[0].trim()){
														case "汇报对象":
															manageExperience.setReportTo(arr[k].split("：")[1].trim());
															break;
														case "下属人数":
															manageExperience.setSubordinatesNum(arr[k].split("：")[1].trim());
															break;
														case "直接下属":
															manageExperience.setSuborinates(arr[k].split("：")[1].trim());
															break;
														}
													}
												}
											}
											if(manageDesc.indexOf("业绩描述：")>=0){
												manageExperience.setKeyPerformance(manageDesc.substring(manageDesc.indexOf("业绩描述：")+5).trim());
												((WorkExperienceData)workExperienceDataList.get(index-1)).manageExperienceDataList.add(manageExperience);
											}
									}
									
								}
							}
						}
					}
				}
			}else{
				if(childnode instanceof Div){
					if(!childnode.toPlainTextString().equals("工作经历")){
						if(childnode.getChildren().size()>=1){
							String hasTable = "";
							for(int num = 0;num<childnode.getChildren().size();num++){
								Node tmpNode = childnode.getChildren().elementAt(num);
								if(tmpNode instanceof TableTag){
									hasTable = "yes";
									break;
								}
							}
							if(hasTable.equals("yes")){
								NodeList childnodes = childnode.getChildren();
								for(int num=0;num<childnodes.size();num++){
									Node currentNode = childnodes.elementAt(num);
									if(currentNode instanceof TableTag){
										TableTag tanode = (TableTag)currentNode;
										for(int j = 0;j < tanode.getRowCount();j++){
											switch(tanode.getRow(j).getColumns()[0].toPlainTextString().trim()){
												case "工作描述：":
													String jobdesc = tanode.getRow(j).getColumns()[1].getChildrenHTML().trim();
													if(jobdesc.startsWith("<br>")){
														jobdesc = jobdesc.substring(4);
													}if(jobdesc.startsWith("<br />")){
														jobdesc = jobdesc.substring(6);
													}
													((WorkExperienceData)workExperienceDataList.get(index-1)).setJobDesc(jobdesc);
													break;
												case "管理经验：":
													ManageExperienceData manageExperience = new ManageExperienceData(); 
													String manageDesc = tanode.getRow(j).getColumns()[1].getChildrenHTML().trim();
													String[] arr = manageDesc.split("<br />");
													String infonode = "";
													if(arr.length>0){
														infonode = arr[0];
														arr = infonode.split("\\|");
														for(int k=0;k<arr.length;k++){
															if(arr[k].indexOf("：")>=0){
																switch(arr[k].split("：")[0].trim()){
																case "汇报对象":
																	manageExperience.setReportTo(arr[k].split("：")[1].trim());
																	break;
																case "下属人数":
																	manageExperience.setSubordinatesNum(arr[k].split("：")[1].trim());
																	break;
																case "直接下属":
																	manageExperience.setSuborinates(arr[k].split("：")[1].trim());
																	break;
																}
															}
														}
													}
													if(manageDesc.indexOf("业绩描述：")>=0){
														manageExperience.setKeyPerformance(manageDesc.substring(manageDesc.indexOf("业绩描述：")+5).trim());
														((WorkExperienceData)workExperienceDataList.get(index-1)).manageExperienceDataList.add(manageExperience);
													}
													
											}
											
										}
									}
								}
							}else{
								String companyDesc = childnode.toPlainTextString().trim();
								String [] arr = companyDesc.split("\\|");
								for(int num =0 ;num< arr.length;num++){
									if(arr[num].indexOf(("企业性质："))>=0){
										if(arr[num].split("：").length == 2){
											((WorkExperienceData)workExperienceDataList.get(index-1)).setCompanyCatagory(arr[num].split("：")[1]);
										}
									}else if(arr[num].indexOf(("规模："))>=0){
										if(arr[num].split("：").length == 2){
											((WorkExperienceData)workExperienceDataList.get(index-1)).setCompanyScale(arr[num].split("：")[1]);
										}
									}else{							
										((WorkExperienceData)workExperienceDataList.get(index-1)).setIndustryCatagory(arr[num]);
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


	/**
	 * 获取求职者教育经历
	 * @param node 包含教育经历的html节点
	 * @return 教育经历 @see EducationExperienceData 
	 */
	@Override
	public ArrayList<EducationExperienceData> extractEducationExperience(Node node) {
		// TODO Auto-generated method stub
		ArrayList<EducationExperienceData> educationExperienceDataList = new ArrayList<EducationExperienceData>();
		
		if(node==null){
			return null;
		}else{
			String content =  node.toPlainTextString();
			
			Pattern pattern = null;
			if(content.indexOf("&nbsp;") > 0){
				pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)&nbsp;&nbsp;(.*?)&nbsp;&nbsp;(.*?)&nbsp;&nbsp;(本科|硕士|博士|MBA|EMBA|大专|中专|高中|初中|中技|本科|硕士|博士|MBA|EMBA|大专|中专|高中|初中|中技|其他)");
			}else{
				pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)\\u00A0\\u00A0(.*?)\\u00A0\\u00A0(.*?)\\u00A0\\u00A0(本科|硕士|博士|MBA|EMBA|大专|中专|高中|初中|中技|本科|硕士|博士|MBA|EMBA|大专|中专|高中|初中|中技|其他)");
			}
			
			
	        Matcher matcher=pattern.matcher(content);
	        while(matcher.find()){
	        	EducationExperienceData educationExperienceData = new EducationExperienceData();
	        	
	        	educationExperienceData.setStartTime(matcher.group(1).trim());
	        	educationExperienceData.setEndTime(matcher.group(2).trim());
	        	educationExperienceData.setSchool(matcher.group(3).trim());
	        	
	        	educationExperienceData.setMajor(matcher.group(4).trim());
	        	educationExperienceData.setDegree(matcher.group(5).trim());
	        	
	        	educationExperienceDataList.add(educationExperienceData);
	        }
		}
		
		return educationExperienceDataList;
	}


	/**
	 * 获取求职者培训经历
	 * @param node 包含培训经历的html节点
	 * @return 培训经历 @see TrainingExperienceData 
	 */
	@Override
	public ArrayList<TrainingExperienceData> extractTrainingExperience(Node node) {
		if(node == null){
			return null;
		}
		
		ArrayList<TrainingExperienceData> trainingExperienceDataList = new ArrayList<TrainingExperienceData>();
		NodeList nodes = node.getChildren();  //获取所有子节点
		
		int index = 0;
		for(int i = 0;i < nodes.size();i++){
			Node childnode = nodes.elementAt(i);
			if(childnode instanceof HeadingTag && "H2".equals(((HeadingTag)childnode).getTagName())){
				String title = childnode.toPlainTextString();
				TrainingExperienceData trainingExperienceData = new TrainingExperienceData();
				
				//获取项目基本信息：开始时间，结束时间，项目名称
				Pattern pattern = null;
				if(title.indexOf("&nbsp;") > 0){
					pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)&nbsp;&nbsp;(.*)",Pattern.CASE_INSENSITIVE);
				}else{
					pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)\\u00A0\\u00A0(.*)",Pattern.CASE_INSENSITIVE);
				}

		        Matcher matcher=pattern.matcher(title);
		        if(matcher.find()){		        	
		        	trainingExperienceData.setStartTime(matcher.group(1).trim());
		        	trainingExperienceData.setEndTime(matcher.group(2).trim());
		        	trainingExperienceData.setCourse(matcher.group(3).trim());		        			        	
		        }
		        
		        trainingExperienceDataList.add(trainingExperienceData);
		        index++;
			}
			if(childnode instanceof Div){
				NodeList childnodes = childnode.getChildren();
				NodeFilter tableFilter = new NodeClassFilter(TableTag.class);
				childnodes = childnodes.extractAllNodesThatMatch(tableFilter);
				
				if(childnodes.size() >= 1){
					TableTag tableTag = (TableTag) childnodes.elementAt(0);
					for(int j = 0;j < tableTag.getRowCount();j++){
						switch(tableTag.getRow(j).getColumns()[0].toPlainTextString().trim()){
							case "培训机构：":
								trainingExperienceDataList.get(index-1).setInstituation(tableTag.getRow(j).getColumns()[1].toPlainTextString());
								break;
							case "培训地点：":
								trainingExperienceDataList.get(index-1).setLocation(tableTag.getRow(j).getColumns()[1].toPlainTextString());
								break;
							case "培训描述：":
								trainingExperienceDataList.get(index-1).setTrainDesc(tableTag.getRow(j).getColumns()[1].getChildrenHTML());
								break;
							case "所获证书：":
								trainingExperienceDataList.get(index-1).setCertificate(tableTag.getRow(j).getColumns()[1].getChildrenHTML());
								break;
						}
						
					}
				}
				
			}
		}
		
		return trainingExperienceDataList;
	}

	/**
	 * 获取求职者语言能力
	 * @param node 包含语言能力的html节点
	 * @return 语言能力 @see LanguageSkillData 
	 */
	@Override
	public ArrayList<LanguageSkillData> extractLanguageSkill(Node node) {
		// TODO Auto-generated method stub
		ArrayList<LanguageSkillData> languageSkillDataList = new ArrayList<LanguageSkillData>();
		
		//获取div节点
		NodeList nodes = node.getChildren();
		NodeFilter filter = new NodeClassFilter(Div.class);
		nodes = nodes.extractAllNodesThatMatch(filter);
		
		String professionalSkillContent =  "";
		for(int i = 0;i < nodes.size();i++){
			professionalSkillContent += nodes.elementAt(i).toPlainTextString().trim();
		}
		
		Pattern pattern=Pattern.compile("(.*?)：读写能力(一般|良好|熟练|精通).*?\\|\\s听说能力(一般|良好|熟练|精通)",Pattern.CASE_INSENSITIVE);
        Matcher matcher=pattern.matcher(professionalSkillContent);
        while(matcher.find()){
        	LanguageSkillData languageSkillData = new LanguageSkillData();
        	
        	languageSkillData.setCatagory(matcher.group(1).trim());
        	languageSkillData.setReadAndWriteAbility(matcher.group(2).trim());
        	languageSkillData.setListenAndSpeakAbility(matcher.group(3).trim());      	
        	
        	languageSkillDataList.add(languageSkillData);
        }
		return languageSkillDataList;
	}


	/**
	 * 获取求职者证书信息
	 * @param node 包含证书信息的html节点
	 * @return 证书信息 @see CertificateData 
	 */
	@Override
	public ArrayList<CertificateData> extractCertficate(Node node) {
		// TODO Auto-generated method stub
		if(node == null){
			return null;
		}
		ArrayList<CertificateData> certificateDataList = new  ArrayList<CertificateData>();

		NodeList nodes = node.getChildren();  //获取所有子节点
		
		int index = 0;
		for(int i = 0;i < nodes.size();i++){
			Node childnode = nodes.elementAt(i);
			if(childnode instanceof HeadingTag && "H2".equals(((HeadingTag)childnode).getTagName())){
				String title = childnode.toPlainTextString();
				CertificateData certificateData = new CertificateData();
				
				//获取项目基本信息：开始时间，结束时间，项目名称
				Pattern pattern = null;
				if(title.indexOf("&nbsp;") > 0){
					pattern=Pattern.compile("(\\d+[.]\\d+).*?&nbsp;&nbsp;(.*)",Pattern.CASE_INSENSITIVE);
				}else{
					pattern=Pattern.compile("(\\d+[.]\\d+).*?\\u00A0\\u00A0(.*)",Pattern.CASE_INSENSITIVE);
				}			
				
		        Matcher matcher=pattern.matcher(title);
		        if(matcher.find()){		        	
		        	certificateData.setAcquireTime(matcher.group(1).trim());
		        	certificateData.setCertificateTitle(matcher.group(2).trim());     			        	
		        }
		        
		        certificateDataList.add(certificateData);
		        index++;
			}
			if(childnode instanceof Div){
				String desc = childnode.toPlainTextString();
				desc = desc.substring(desc.indexOf("证书说明：")+5).trim();
				if(index-1 > 0){
					certificateDataList.get(index-1).setComment(desc);
				}
			}
		}
		return certificateDataList;
	}


	/**
	 * 获取求职者项目经历
	 * @param node 包含项目经历的html节点
	 * @return 项目经历 @see ProjectExperienceData 
	 */
	@Override
	public ArrayList<ProjectExperienceData> extractProjectExperience(Node node) {
		// TODO Auto-generated method stub  项目经验
		if(node == null){
			return null;
		}
		
		ArrayList<ProjectExperienceData> projectExperienceDataList = new ArrayList<ProjectExperienceData>();
		NodeList nodes = node.getChildren();  //获取所有子节点
		
		int index = 0;
		for(int i = 0;i < nodes.size();i++){
			Node childnode = nodes.elementAt(i);
			if(childnode instanceof HeadingTag && "H2".equals(((HeadingTag)childnode).getTagName())){
				String title = childnode.toPlainTextString();
				ProjectExperienceData projectExperienceData = new ProjectExperienceData();
				
				//获取项目基本信息：开始时间，结束时间，项目名称
				Pattern pattern = null;
				if(title.indexOf("&nbsp;") > 0){
					pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)(&nbsp;&nbsp;)?(.*)",Pattern.CASE_INSENSITIVE);
				}else{
					pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)(\\u00A0\\u00A0)?(.*)",Pattern.CASE_INSENSITIVE);
				}			
				
		        Matcher matcher=pattern.matcher(title);
		        if(matcher.find()){		        	
		        	projectExperienceData.setStartTime(matcher.group(1).trim());
		        	projectExperienceData.setEndTime(matcher.group(2).trim());
		        	projectExperienceData.setProjectTitle(matcher.group(4).trim());		        			        	
		        }
		        
		        projectExperienceDataList.add(projectExperienceData);
		        index++;
			}
			if(childnode instanceof Div){
				NodeList childnodes = childnode.getChildren();
				NodeFilter tableFilter = new NodeClassFilter(TableTag.class);
				childnodes = childnodes.extractAllNodesThatMatch(tableFilter);
				
				if(childnodes.size() >= 1){
					TableTag tableTag = (TableTag) childnodes.elementAt(0);
					for(int j = 0;j < tableTag.getRowCount();j++){
						switch(tableTag.getRow(j).getColumns()[0].toPlainTextString()){
							case "软件环境：":
								projectExperienceDataList.get(index-1).setSoftwareEnvir(tableTag.getRow(j).getColumns()[1].toPlainTextString().replaceAll("&#160;", " "));
								break;
							case "硬件环境：":
								projectExperienceDataList.get(index-1).setHardEnvir(tableTag.getRow(j).getColumns()[1].toPlainTextString().replaceAll("&#160;", " "));
								break;
							case "开发工具：":
								projectExperienceDataList.get(index-1).setDevelopTool(tableTag.getRow(j).getColumns()[1].toPlainTextString().replaceAll("&#160;", " "));
								break;
							case "责任描述：":
								String responsibleFor = tableTag.getRow(j).getColumns()[1].getChildrenHTML().trim();
								if(responsibleFor.startsWith("<br>")){
									responsibleFor = responsibleFor.substring(4);
								}if(responsibleFor.startsWith("<br />")){
									responsibleFor = responsibleFor.substring(6);
								}
								projectExperienceDataList.get(index-1).setResponsibleFor(responsibleFor);
								break;
							case "项目描述：":
								String projectDesc = tableTag.getRow(j).getColumns()[1].getChildrenHTML().trim();
								if(projectDesc.startsWith("<br>")){
									projectDesc = projectDesc.substring(4);
								}if(projectDesc.startsWith("<br />")){
									projectDesc = projectDesc.substring(6);
								}
								projectExperienceDataList.get(index-1).setProjectDesc(projectDesc);								
								break;
						}
						
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
	@Override
	public ArrayList<ProfessionalSkillData> extractProfessionalSkill(Node node) {
		// TODO Auto-generated method stub
		
		ArrayList<ProfessionalSkillData> professionalSkillDataList = new ArrayList<ProfessionalSkillData>();
		
		//获取div节点
		NodeList nodes = node.getChildren();
		NodeFilter filter = new NodeClassFilter(Div.class);
		nodes = nodes.extractAllNodesThatMatch(filter);
		
		String professionalSkillContent =  "";
		for(int i = 0;i < nodes.size();i++){
			professionalSkillContent += nodes.elementAt(i).toPlainTextString().trim();
		}
		
		Pattern pattern=Pattern.compile("(.*?)：(一般|良好|熟练|精通).*?\\|\\s(.*?)个月",Pattern.CASE_INSENSITIVE);
        Matcher matcher=pattern.matcher(professionalSkillContent);
        while(matcher.find()){
        	ProfessionalSkillData professionalSkillData = new ProfessionalSkillData();
        	
        	professionalSkillData.setSkillDesc(matcher.group(1).trim());
        	professionalSkillData.setProficiency(matcher.group(2).trim());
        	professionalSkillData.setMonths(matcher.group(3).trim());
        	
        	professionalSkillDataList.add(professionalSkillData);
        }
		return professionalSkillDataList;
	}


	public StudyInfoData extractStudyInfo(Node node){
		if(null == node){
			return null;
		}
		StudyInfoData studyInfoData = new StudyInfoData();
		
		NodeList nodes = node.getChildren();  //获取所有子节点		
		int index = 0;
		for(int i = 0;i < nodes.size();i++){
			Node childnode = nodes.elementAt(i);
			if(childnode instanceof HeadingTag && "H2".equals(((HeadingTag)childnode).getTagName())){
				String title = childnode.toPlainTextString();				
				Pattern pattern=Pattern.compile("(\\d+[.]\\d+).*?曾获(.*?级)(.*)",Pattern.CASE_INSENSITIVE);	
		        Matcher matcher=pattern.matcher(title);
		        if(matcher.find()){		        	
		        	RewardData rewardData = new RewardData();
		        	rewardData.setTime(matcher.group(1).trim());
		        	rewardData.setRewardsLevel(matcher.group(2).trim());
		        	rewardData.setRewards(matcher.group(3).trim());
		        	studyInfoData.addRewarData(rewardData);
		        	index++;
		        }else{
		        	if(title.indexOf("曾获")>=0){
		        		String scholarShip = title.substring(title.indexOf("曾获")+2).trim();
		        		studyInfoData.addScholarShip(scholarShip);
		        	}
		        }
			}
			if(childnode instanceof Div){
				String desc = childnode.toPlainTextString();
				if(desc.indexOf("活动描述：")>=0){
					desc = desc.substring(desc.indexOf("活动描述：")+5).trim();
					studyInfoData.setActivityDesc(desc);
				}else if(desc.indexOf("奖项描述：")>=0){
					desc = desc.substring(desc.indexOf("奖项描述：")+5).trim();
					studyInfoData.getRewardDataList().get(index-1).setDesc(desc);
				}					
			}
		}
		
		return studyInfoData;
	}
	@Override
	public ArrayList<PracticalExperienceData> extractPracticalExperience(
			Node node) {
		// TODO Auto-generated method stub
		if(node == null){
			return null;
		}
		
		ArrayList<PracticalExperienceData> practicalExperienceDataList = new ArrayList<PracticalExperienceData>();
		NodeList nodes = node.getChildren();  //获取所有子节点
		
		int index = 0;
		for(int i = 0;i < nodes.size();i++){
			Node childnode = nodes.elementAt(i);
			if(childnode instanceof HeadingTag && "H2".equals(((HeadingTag)childnode).getTagName())){
				String title = childnode.toPlainTextString();
				PracticalExperienceData practicalExperienceData = new PracticalExperienceData();
				
				//获取项目基本信息：开始时间，结束时间，项目名称
				Pattern pattern = null;
				if(title.indexOf("&nbsp;") > 0){
					pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)&nbsp;&nbsp;(.*)",Pattern.CASE_INSENSITIVE);
				}else{
					pattern=Pattern.compile("(\\d+[.]\\d+).*?(至今|\\d+[.]\\d+)\\u00A0\\u00A0(.*)",Pattern.CASE_INSENSITIVE);
				}			
				
		        Matcher matcher=pattern.matcher(title);
		        if(matcher.find()){		        	
		        	practicalExperienceData.setStartTime(matcher.group(1).trim());
		        	practicalExperienceData.setEndTime(matcher.group(2).trim());
		        	practicalExperienceData.setPracticeTitle(matcher.group(3).trim());		        			        	
		        }
		        
		        practicalExperienceDataList.add(practicalExperienceData);
		        index++;
			}
			if(childnode instanceof Div){
				String desc = childnode.toPlainTextString();
				desc = desc.substring(desc.indexOf("实践描述：")+5).trim();
				practicalExperienceDataList.get(index-1).setPracticeDesc(desc);
			}
		}		
		return practicalExperienceDataList;
	}


	@Override
	public ArrayList<OtherInfoData> extractOtherInfo(Node node) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getResumeKeywords(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	public ZhiLianResumeParser(String html,String url,String objectid){
		htmlcontent = html;
		this.htmlurl = url;
		resumedata.setUrl(url);
		htmlcontent = htmlcontent.replaceAll("<span class=\"keyword-highlight\">", "<span>");
		if(htmlcontent.indexOf("&nbsp;") > 0){
			hasNbsp = true;
		}
		this.objectid = objectid;
	}
	
	public ZhiLianResumeParser(){
		
	}
	
	public void setHtml(String html){
		this.htmlcontent = html;
		htmlcontent = htmlcontent.replaceAll("<span class=\"keyword-highlight\">", "<span>");
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
	
	public ZhiLianResumeParser(String html,String url){
		htmlcontent = html;
		this.htmlurl = url;
		if(htmlcontent.indexOf("&nbsp;") > 0){
			hasNbsp = true;
		}		
	}
	
	public boolean isValidResume(){
		if(this.htmlcontent == null){
			return false;
		}
		if(this.htmlcontent.indexOf("该简历已被求职者删除") >= 0 || this.htmlcontent.equals("") ){
			return false;
		}
		return true;
	}

	/**
	 * 判断是否包含联系信息
	 */
	@Override
	public boolean hasContactInfo(){
		if(this.htmlcontent.indexOf("如需联系方式请下载该简历") >= 0){
			return false;
		}
		return true;
	}
	
	
	/**
	 * 判断当前简历是否需要解析
	 */
}
