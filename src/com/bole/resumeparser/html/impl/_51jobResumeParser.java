package com.bole.resumeparser.html.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.Span;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;

import com.bole.resumeparser.exception.document.HtmlResumeParseException;
import com.bole.resumeparser.exception.html.DocResumeParseException;
import com.bole.resumeparser.models.*;

import org.htmlparser.tags.TableTag;

public class _51jobResumeParser extends AbstractResumeParser implements HtmlResumeParserInterface{
	/**
	 * 已经完成对基本信息的解析(性别，年龄),求职意向，项目经历，教育经历，自我评价的解析
	 * 需要完善职业技能，培训经历，证书，在校实践经验，其他信息的解析
	 */
	public String htmlcontent = "";
	public String htmlurl = "";
	public ResumeData resumedata = new ResumeData();
	public String objectid = null;
	boolean hasNbsp = false;
	public String resumeText = "";
	public String resumeContent = "";
	

	/**
	 * 简历解析
	 */
	@Override
	public ResumeData parse() throws HtmlResumeParseException {
		// TODO Auto-generated method stub
		//主要负责解析简历将解析之后的数据保存到ResumeData中
		resumedata.url =  htmlurl;
		resumedata.set_id(objectid);
		resumedata.setSource("51job");
		
		Parser parser = Parser.createParser(htmlcontent, "utf-8");
		
		NodeFilter divResumeFilter = new HasAttributeFilter("id","divResume");
		NodeFilter divInfoFilter = new HasAttributeFilter("id","divInfo");
		NodeFilter divResumeFilter2 = new HasAttributeFilter("id","divresume");
		NodeFilter divInfoFilter2 = new HasAttributeFilter("id","divinfo");
		
		NodeFilter[] filters = new NodeFilter[4];
		filters[0] = divResumeFilter;
		filters[1] = divInfoFilter;
		filters[2] = divResumeFilter2;
		filters[3] = divInfoFilter2;
		
		OrFilter orfilter = new OrFilter();
		orfilter.setPredicates(filters);
		
		NodeList nodes = null;
		
		try {
			nodes = parser.extractAllNodesThatMatch(orfilter);
			
			int num = nodes.size();
			if(num >= 2){
				for(int nodenum = 0;nodenum<nodes.size() ; nodenum++){
					Node node = nodes.elementAt(nodenum);	
					this.resumeText += " "+node.toPlainTextString();
					if(node instanceof Div){						
						if(((Div)node).getAttribute("id").toLowerCase().equals("divresume")){
							TableTag tableTag = null;
							
							for(int i=0;i<node.getChildren().size();i++){
								Node tmpNode = node.getChildren().elementAt(i);
								if(tmpNode instanceof TableTag){
									tableTag = (TableTag)tmpNode;
								}
							}
							if(tableTag != null){
								Node tables = tableTag.getRow(0).getColumns()[0];
								NodeList childs = tables.getChildren();
								Node resumeKeywordNode = childs.elementAt(0);
								Node basicInfoNode = childs.elementAt(1);
								Node recentInfoNode = childs.elementAt(2);
								Node resumeInfoNode = childs.elementAt(3);
								
								if(resumeInfoNode==null && recentInfoNode!=null){
									//如果简历信息节点为空并且最近信息节点不为空则说明没有最近信息节点直接将最近信息节点赋值给简历信息节点
									resumeInfoNode = recentInfoNode;
									recentInfoNode = null;
								}
								NodeFilter tableFilter = null;
								if("divResume".equals(((Div)node).getAttribute("id"))){
									tableFilter = new HasAttributeFilter("id","divInfo");
								}else{
									tableFilter = new HasAttributeFilter("id","divinfo");
								}
								
								NodeList childnodesList = resumeInfoNode.getChildren();
								childnodesList = childnodesList.extractAllNodesThatMatch(tableFilter);
								
								if(resumeInfoNode instanceof TableTag){
									resumeInfoNode = ((TableTag)resumeInfoNode).getRow(2).getColumns()[0];
								}
								
								resumedata.setUpdateTime(this.getUpdateTime());
								
								//提取基本信息
								processBasicInfoNode(basicInfoNode);
								//提取最近信息
								if(recentInfoNode!=null){
									processRecentInfoNode(recentInfoNode);
								}
								
								resumedata.setResumeKeyWord(getResumeKeywords(resumeKeywordNode));
							}							
						}
					}
					if(node instanceof TableTag){
						if(((TableTag)node).getAttribute("id").toLowerCase().equals("divresume")){
							if(((TableTag)node).getRowCount() == 1){
								if(((TableTag)node).getRows()[0].getColumnCount() == 2){
									if(((TableTag)node).getRows()[0].getColumns()[0].getChildren().size() >= 4){
										int a = ((TableTag)node).getRows()[0].getColumns()[0].getChildren().size();
										Node lastTableNode = new TableTag();
										for(int i= 0;i< a;i++){
											Node tmpNode = ((TableTag)node).getRows()[0].getColumns()[0].getChildren().elementAt(i);
											if(tmpNode instanceof Div){
												if("divhead".equals(((Div)tmpNode).getAttribute("id").toLowerCase())){
													Node divHeadNode = ((TableTag)node).getRows()[0].getColumns()[0].getChildren().elementAt(i);
													resumedata.setUpdateTime(this.getUpdateTime(divHeadNode));
												}
												if("divchart".equals(((Div)tmpNode).getAttribute("id"))){
													Node divChartNode = ((TableTag)node).getRows()[0].getColumns()[0].getChildren().elementAt(i);
													resumedata.setResumeKeyWord(getResumeKeywords(divChartNode));
												}
											}
											if(tmpNode instanceof TableTag){
												lastTableNode = tmpNode;
											}
										}
										this.extarctContactInfo(lastTableNode);
									}
								}
							}
						}
					}
					if(node instanceof TableColumn){
						if(((TableColumn)node).getAttribute("id").toLowerCase().equals("divinfo")){
							NodeList resumeInfoNodes = node.getChildren();
							for(int i = 0;i < resumeInfoNodes.size();i++){
								Node currentNode = resumeInfoNodes.elementAt(i);
								if(currentNode instanceof TableTag){
							        TableTag table = (TableTag)currentNode;
							        String nodeText = table.toPlainTextString();
							        if(!nodeText.startsWith("目前年薪")){
							        	if(((TableTag) currentNode).getRowCount()==0){
							        		continue;
							        	}
							        	String title = ((TableTag) currentNode).getRow(0).getColumns()[0].toPlainTextString().trim();
							        	if("自我评价".equals(title)){
							        		String selfEvaluation = extractSelfEvaluation(currentNode);
							        		resumedata.setSelfEvaluation(selfEvaluation);
							        	}else if("求职意向".equals(title)){
							        		JobTarget jobtarget = extractJobTarget(currentNode);
							        		resumedata.setJobTarget(jobtarget);
							        	}else if("工作经验".equals(title) || "项目经验".equals(title) || "教育经历".equals(title) || "IT 技能".equals(title)){
							        		int rowCounts = table.getRowCount();
							        		for(int j = 0; j < rowCounts ; j++){
							        			TableColumn tdNode = table.getRow(j).getColumns()[0];			        			
							        			if("cvtitle".equals(tdNode.getAttribute("class"))){
							        				String sctionTitle = tdNode.toPlainTextString().trim();
							        				if (sctionTitle.equals("工作经验")){
							        					Node jobDetailNode = table.getRow(j+3).getColumns()[0].getChild(0);
							        					ArrayList<WorkExperienceData> workExperienceDataList = extractWorkExperience(jobDetailNode);
							        					resumedata.setWorkExperience(workExperienceDataList);										
							        				}
							        				else if (sctionTitle.equals("项目经验")){
							        					Node projectDetailNode = table.getRow(j+3).getColumns()[0].getChild(0);
							        					ArrayList<ProjectExperienceData> projectExperienceDataList = extractProjectExperience(projectDetailNode);
							        					resumedata.setProjectExperience(projectExperienceDataList);
														
							        				}else if (sctionTitle.equals("教育经历")){
							        					Node ecucationDetailNode = table.getRow(j+3).getColumns()[0].getChild(0);
							        					ArrayList<EducationExperienceData> educationexperiencelist = extractEducationExperience(ecucationDetailNode);
							        					resumedata.setEducationExperience(educationexperiencelist);
														
							        				}else if (sctionTitle.equals("IT    技能")){
							        					Node skillDetailNode = table.getRow(j+3).getColumns()[0].getChild(0);
							        					ArrayList<ProfessionalSkillData> professionalSkillDataList = extractProfessionalSkill(skillDetailNode);
							        					
														resumedata.setProfessionalSkill(professionalSkillDataList);										
							        				}else if (sctionTitle.equals("证书")){
							        					Node certficateDetailNode = table.getRow(j+3).getColumns()[0].getChild(0);
							        					ArrayList<CertificateData> certificateDataList = extractCertficate(certficateDetailNode);
							        
														resumedata.setCertficate(certificateDataList);										
							        				}else if (sctionTitle.equals("语言能力")){
							        					Node languageDetailNode = table.getRow(j+3).getColumns()[0].getChild(0);
							        					ArrayList<LanguageSkillData> languageSkillDataList = extractLanguageSkill(languageDetailNode);
							        
														resumedata.setLanguageSkill(languageSkillDataList);								
							        				}else if (sctionTitle.equals("培训经历")){
							        					Node trainDetailNode = table.getRow(j+4).getColumns()[0].getChild(0);
							        					ArrayList<TrainingExperienceData> trainingExperienceDataList = extractTrainingExperience(trainDetailNode);
							        
														resumedata.setTrainingExperience(trainingExperienceDataList);								
							        				}else if (sctionTitle.equals("所获奖项")){
							        					Node reWardNode = table.getRow(j+3).getColumns()[0].getChild(0);;
							        					StudyInfoData studyInfoData = new StudyInfoData();
							        					ArrayList<RewardData> rewardDataList = extractReward(reWardNode);
							        					studyInfoData.setRewardDataList(rewardDataList);
							        				}else if (sctionTitle.equals("社会经验")){
							        					Node priacticeNode = table.getRow(j+3).getColumns()[0].getChild(0);
							        					ArrayList<PracticalExperienceData> 	practicalExperienceData = extractPracticalExperience(priacticeNode);
							        					
							        					resumedata.addPracticeList(practicalExperienceData);
							        				}else if (sctionTitle.equals("校内职务")){
							        					Node priacticeNode = table.getRow(j+3).getColumns()[0].getChild(0);
							        					ArrayList<PracticalExperienceData> 	practicalExperienceData = extractPracticalExperience(priacticeNode);
							        					
							        					resumedata.addPracticeList(practicalExperienceData);								
							        				}else if (sctionTitle.equals("其他信息")){
							        					Node trainDetailNode = table.getRow(j+3).getColumns()[0].getChild(0);
							        					HashMap<String,String> otherInfoMap = extractOtherInfoMap(trainDetailNode);
							        					
														resumedata.setOtherInfoMap(otherInfoMap);								
							        				}
							        			}else{
							        				
							        			}
							        			
							        		}
							        	}
							        }else{
							        	Pattern pattern=Pattern.compile("目前年薪：(.*?人民币)",Pattern.CASE_INSENSITIVE);
							            Matcher matcher=pattern.matcher(nodeText);
							            String currentSalary = "";
							            if(matcher.find()){
							            	currentSalary = matcher.group(1);        	
							            }
							        	
				        				resumedata.setLatestSalary(currentSalary);
							        }
									
								}
							}
						}
					}
				
					
				}
				resumedata.setImage_url(this.getImageUrl());
				
			}
			
			resumedata.setUrls(this.getUrls());
			if(resumedata.getSourceID()==null){
				resumedata.setSourceID(this.getSourceID(htmlcontent));
			}
			resumedata.contactInfoData.setSource(resumedata.getSource());
			resumedata.contactInfoData.setSourceID(resumedata.getSourceID());
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
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
		String resumeId = "";
		Pattern pattern=Pattern.compile("ID:(\\d+)",Pattern.CASE_INSENSITIVE);
        Matcher matcher=pattern.matcher(html);
        if(matcher.find()){
        	resumeId = matcher.group(1);        	
        }
		return resumeId;
	}

	@Override
	public String getUpdateTime() {
		// TODO Auto-generated method stub
		String updateTime = "";
		
		Pattern pattern=Pattern.compile("更新时间：(\\d+[-]\\d+[-]\\d+)");
        Matcher matcher=pattern.matcher(this.htmlcontent);
        if(matcher.find()){
        	updateTime = matcher.group(1);        	
        }
        resumedata.setUpdate_time(this.transUpdateTime(updateTime));
		return updateTime;
	}
	
	/**
	 * 获取简历更新时间
	 * @param 包含简历更新时间的html节点
	 * @return 简历更新时间
	 */
	public String getUpdateTime(Node node) {
		// TODO Auto-generated method stub
		String updateTime = "";
		String content = node.toPlainTextString();
		Pattern pattern=Pattern.compile("更新时间：(\\d+[-]\\d+[-]\\d+)");
        Matcher matcher=pattern.matcher(content);
        if(matcher.find()){
        	updateTime = matcher.group(1);        	
        }
        
		return updateTime;
	}

	/**
	 * 获取简历信息中的网址链接，该链接默认为用户的产品
	 */
	public ArrayList<String> getUrls(){
		HashSet<String> urls = new HashSet<String>();
		ArrayList<String> url = new ArrayList<String>();
		resumeText = removeHtmlTag(resumeText);
		if(this.resumeText.indexOf("www")<0 && this.resumeText.indexOf("http")<0){
			return url;
		}
		String filterurl1 = "http://img01.51jobcdn.com/imehire/ehire2007/default/image/candidate/tabc.gif";
		String filterurl2 = "http://img01.51jobcdn.com/imehire/ehire2007/default/image/candidate/tabd.gif";
		String filterurl3 = "http://amos.im.alisoft.com/msg.aw?v=2&s=2&charset=utf-8&site=cntaobao&uid=\"+userId";
		Pattern p = Pattern.compile("((http\\:\\/\\/|www[.]|http\\:\\/\\/www[.]).*?[.](com|cn|net|org|biz|info|cc|tv|me|(\\w+)/).*?)(，|,|。|;|[)]|[(]|\u00A0|[\u4e00-\u9fa5]|）|www|http|\\s)",Pattern.CASE_INSENSITIVE );   
		Matcher m = p.matcher(this.resumeText);
	    while(m.find()){
	    	String tmpUrl = m.group(1);
	    	if(!tmpUrl.equals(filterurl1) && !tmpUrl.equals(filterurl2) && !tmpUrl.equals(filterurl3)){
	    		urls.add(tmpUrl);
	    	}    		    	
	    }	     
	    url = new ArrayList<String>(urls);
		return url;
	}

	/**
	 * 获取简历中头像url
	 * @param content 简历的html内容
	 * @return 简历中头像url
	 */
	public String getImageUrl(){
		String imageUrl = "";
		if(this.htmlcontent.indexOf("http://img01.51jobcdn.com/imehire/ehire2007/default/image/im2009/resume_match_manpic.gif")<=0){
			imageUrl = "http://ehire.51job.com/candidate/readattach.aspx?userid="+resumedata.getSourceID() + "&attachid=&picsize=Big";
		}	
		
        return imageUrl;
	}
	
	public void extractUrlID(){		
		Pattern pattern = Pattern.compile("hidUserID=(\\d+)",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(resumedata.url);
        if(matcher.find()){
        	resumedata.setUrlID(matcher.group(1));
        }
        
        pattern = Pattern.compile("hidSeqID=(\\d+)",Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(resumedata.url);
        if(matcher.find()){
        	resumedata.setViewID(matcher.group(1));
        }
	}

	@Override
	public String getResumeKeywords(Node node) {
		// TODO Auto-generated method stub
		if(node == null){
			return "";
		}
		String keywords = "";
		if(node instanceof Div){
			if(((Div)node).getChildCount() >= 2){
				for(int i = 0; i<((Div)node).getChildCount();i++){
					Node tmpnode  = ((Div)node).getChildren().elementAt(i);
					if(tmpnode instanceof TableTag){
						
					}
				}
			}
		}else{
			String content = node.toPlainTextString();
			Pattern pattern=Pattern.compile("简历关键字：(.*)");
	        Matcher matcher=pattern.matcher(content);
	        if(matcher.find()){
	        	keywords = matcher.group(1);        	
	        }    
		}		    
		return keywords;
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
		if(node instanceof TableTag){
			if(((TableTag)node).getRowCount() == 5){
				if(((TableTag)node).getRows()[3].getColumns()[0].getChildren().elementAt(0) instanceof Span){
					selfEvaluation = ((Span)(((TableTag)node).getRows()[3].getColumns()[0].getChildren().elementAt(0))).getChildrenHTML();
				}
			}
		}
		
		return selfEvaluation;
	}


	public void processRecentInfoNode(Node node){
		if(node == null){
			return;
		}
		String recentInfoContent = node.toPlainTextString();
		
		Pattern pattern = Pattern.compile("公.*?司：(.*?)行.*?业：(.*?)职.*?位：(.*?)最高学历");
		Matcher matcher = pattern.matcher(recentInfoContent);
        if(matcher.find()){
        	resumedata.setLatestCompanyName(matcher.group(1).trim());
        	resumedata.setLatestIndustry(matcher.group(2).trim());
        	resumedata.setLatestPositionTitle(matcher.group(3).trim());
        }
		pattern = Pattern.compile("学.*?历：(.*?)专.业：(.*?)学.*?校：(.*)");
		matcher = pattern.matcher(recentInfoContent);
        if(matcher.find()){
        	resumedata.setLatestDegree(matcher.group(1).trim());
        	resumedata.setLatestMajor(matcher.group(2).trim());
        	resumedata.setLatestSchool(matcher.group(3).trim());
        }
		
	}
	public void processContactInfoNode(Node node){
		if(node == null){
			return ;
		}
		if(node instanceof TableTag){
			if(((TableTag)node).getRowCount() == 2){
				if(((TableTag)node).getRows()[1].getColumns()[0].getChildCount() == 3){
					Node parentNode = ((TableTag)node).getRows()[1].getColumns()[0].getChildren().elementAt(2);
					if(parentNode instanceof TableTag){
						Node contactNode = ((TableTag)parentNode).getRows()[0].getColumns()[0].getChildren().elementAt(1);
						extarctContactInfo(contactNode);
						Node recentInfoNode = ((TableTag)parentNode).getRows()[0].getColumns()[0].getChildren().elementAt(2);
						processRecentInfoNode(recentInfoNode);
					}
				}
			}
		}
	}
	
	public void extarctContactInfo(Node node){
		if(node == null){
			return;
		}
		TableTag basicInfoNode = null;
		TableTag currentInfoNode = null;
		if(node instanceof TableTag){
			if(((TableTag)node).getRowCount() == 2){
				NodeList childList = ((TableTag)node).getRows()[1].getColumns()[0].getChildren();
				TableTag infoNode = null;
				for(int i=0;i<childList.size();i++){
					Node currentNode = childList.elementAt(i);
					if(currentNode instanceof TableTag){
						infoNode = (TableTag)currentNode;
						break;
					}
				}
				
				int tableNum = 0;
				childList = infoNode.getRow(0).getColumns()[0].getChildren();
				for(int i=0;i<childList.size();i++){
					Node currentNode = childList.elementAt(i);
					if(currentNode instanceof TableTag){
						tableNum++;
						if(tableNum == 2){
							infoNode = (TableTag)currentNode;							
						}else if(tableNum == 3){
							currentInfoNode = (TableTag)currentNode;
							break;
						}
					}
				}
				
				childList = infoNode.getRow(1).getColumns()[0].getChildren();
				for(int i=0;i<childList.size();i++){
					Node currentNode = childList.elementAt(i);
					if(currentNode instanceof TableTag){
						basicInfoNode = (TableTag)currentNode;
						break;
					}
				}
				String name = infoNode.getRow(0).toPlainTextString().trim();
				name = name.replaceAll("流程状态：", "").replaceAll("&nbsp.*", "");
				resumedata.contactInfoData.setName(name);
				
				for(int i = 0; i<basicInfoNode.getRowCount();i++){
					String title1="";
					String desc1="";
					String title2="";
					String desc2="";
					
					title1 = basicInfoNode.getRow(i).getColumns()[0].toPlainTextString().trim();
					desc1 = basicInfoNode.getRow(i).getColumns()[1].toPlainTextString().trim().replaceAll("&nbsp;", "");
					if(basicInfoNode.getRow(i).getColumnCount()==4){						
						title2 = basicInfoNode.getRow(i).getColumns()[2].toPlainTextString().trim();
						desc2 = basicInfoNode.getRow(i).getColumns()[3].toPlainTextString().trim().replaceAll("&nbsp;", "");
					}
					
					Pattern pattern = Pattern.compile("([一二三四五六七八九十]+)年以上工作经验");
					Matcher matcher = pattern.matcher(title1);
			        if(matcher.find()){
			        	String worklen = matcher.group(1);
			        	resumedata.setWorkExperienceLength(cn2int(worklen));
			        }
					pattern = Pattern.compile("(男|女).*?(\\d+)岁（(.*?)）");
			        matcher = pattern.matcher(title1);
			        if(matcher.find()){
			        	Integer age = Integer.parseInt(matcher.group(2));
			        	resumedata.setAge(age);
			        	resumedata.setGender(matcher.group(1));
			        	resumedata.setBirthday(matcher.group(3));
			        	
			        	pattern = Pattern.compile("ID:(\\d+)");
			    		
			            matcher = pattern.matcher(desc1);
			            if(matcher.find()){
			            	resumedata.setSourceID(matcher.group(1));
			            	extractUrlID();
			            }
			            
			            pattern=Pattern.compile("(未婚|已婚)",Pattern.CASE_INSENSITIVE);							
						matcher=pattern.matcher(title1);
				        if(matcher.find()){
				        	String maritalStatus = matcher.group(1);
				        	resumedata.setMaritalStatus(maritalStatus);
				        }
				        
				        pattern=Pattern.compile("(中共党员|团员|群众|民主党派|无党派人士)",Pattern.CASE_INSENSITIVE);
				        matcher=pattern.matcher(title1);
				        if(matcher.find()){
				        	resumedata.setPoliticalLandscape(matcher.group(1).trim());
				        }
				        
				        pattern=Pattern.compile("(\\d+)cm",Pattern.CASE_INSENSITIVE);
				        matcher=pattern.matcher(title1);
				        if(matcher.find()){
				        	resumedata.setHeight(matcher.group(1));
				        }
				        
			        }else{
			        	if(title1.equals("居住地：")){
			        		resumedata.setAddress(desc1);
			        	}else if(title1.equals("电　话：")){
			        		resumedata.contactInfoData.setPhone(desc1);
			        	}else if(title1.equals("E-mail：") || title1.equals("e-mail：")){
			        		resumedata.contactInfoData.setEmail(desc1);
			        	}else if(title1.equals("主　页：")){
			        		resumedata.setHomePage(desc1);
			        	}else if(title1.equals("地　址：")){
			        		resumedata.setResidence(desc1.replaceAll(" ", ""));
			        	}else if(title1.equals("户　口：")){
			        		resumedata.setHouseHolds(desc1);
			        	}
			        	
			        	if(title2.equals("居住地：")){
			        		resumedata.setAddress(desc2);
			        	}else if(title2.equals("电　话：")){
			        		resumedata.contactInfoData.setPhone(desc2);
			        	}else if(title2.equals("E-mail：") || title2.equals("e-mail：")){
			        		resumedata.contactInfoData.setEmail(desc2);
			        	}else if(title2.equals("主　页：")){
			        		resumedata.setHomePage(desc2);
			        	}else if(title2.equals("地　址：")){
			        		resumedata.setResidence(desc2);
			        	}else if(title2.equals("户　口：")){
			        		resumedata.setHouseHolds(desc2);
			        	}	
			        }
			        
				}
				processRecentInfoNode(currentInfoNode);
				
			}
		}
	}
	
	public void extarctContactInfo2(Node node){
		if(node == null){
			return;
		}
		if(node instanceof TableTag){
			if(((TableTag)node).getRowCount() == 2){
				Node addressInfoNode = ((TableTag)node).getRow(1).getColumns()[0].getChild(0);
				String name = ((TableTag)node).getRow(0).getColumns()[0].getChildren().elementAt(1).toPlainTextString();
//				resumedata.setName(name);
				resumedata.contactInfoData.setName(name);
				
				if(addressInfoNode instanceof TableTag){
					for(int i = 0; i<((TableTag)addressInfoNode).getRowCount();i++){
						String title = ((TableTag)addressInfoNode).getRow(i).getColumns()[0].toPlainTextString().trim();
						String desc = ((TableTag)addressInfoNode).getRow(i).getColumns()[1].toPlainTextString().trim().replaceAll("&nbsp;", "");
						
						Pattern pattern = Pattern.compile("([一二三四五六七八九十]+)年以上工作经验");
						Matcher matcher = pattern.matcher(title);
				        if(matcher.find()){
				        	String work = matcher.group(1);
				        	resumedata.setWorkExperienceLength(cn2int(work));
				        }
						pattern = Pattern.compile("(男|女).*?(\\d+)岁（(.*?)）");
				        matcher = pattern.matcher(title);
				        if(matcher.find()){
				        	Integer age = Integer.parseInt(matcher.group(2));
				        	resumedata.setAge(age);
				        	resumedata.setGender(matcher.group(1));
				        	resumedata.setBirthday(matcher.group(3));
				        	
				        	pattern = Pattern.compile("ID:(\\d+)");
				    		
				            matcher = pattern.matcher(desc);
				            if(matcher.find()){
				            	resumedata.setSourceID(matcher.group(1));
				            	extractUrlID();
				            }
				        }else{
				        	if(title.equals("居住地：")){
				        		resumedata.setAddress(desc);
				        	}else if(title.equals("电　话：")){
				        		resumedata.contactInfoData.setPhone(desc);
				        	}else if(title.equals("E-mail：") || title.equals("e-mail：")){
				        		resumedata.contactInfoData.setEmail(desc);
				        	}else if(title.equals("主　页：")){
				        		resumedata.setHomePage(desc);
				        	}
				        }
					}
				}
				
			}
		}
	}
	
	public void processBasicInfoNode(Node node){
		if(node == null){
			return;
		}
		if(node instanceof TableTag){
			if (node.toPlainTextString().indexOf("电　话：")>=0){
				extarctContactInfo2(node);
				return ;
			}			
		}
		String basicInfoContent = node.toHtml();
		
		Pattern pattern = Pattern.compile("ID:(\\d+)",Pattern.CASE_INSENSITIVE);
		
        Matcher matcher = pattern.matcher(basicInfoContent);
        if(matcher.find()){
        	resumedata.setSourceID(matcher.group(1));
        	extractUrlID();
        }
        
        pattern = Pattern.compile("(男|女).*?(\\d+)岁（(.*?)）");
        matcher = pattern.matcher(basicInfoContent);
        if(matcher.find()){
        	Integer age = Integer.parseInt(matcher.group(2));
        	resumedata.setAge(age);
        	resumedata.setGender(matcher.group(1));
        	resumedata.setBirthday(matcher.group(3));
        }
        
        pattern = Pattern.compile("([一二三四五六七八九十]+)年以上工作经验");
        String txt = node.toPlainTextString();
        matcher = pattern.matcher(txt);
        if(matcher.find()){
        	String work = matcher.group(1);
        	resumedata.setWorkExperienceLength(cn2int(work));
        }
        
        pattern = Pattern.compile("居住地：.*?\"20\">(.*?)</td>");
        matcher = pattern.matcher(basicInfoContent);
        if(matcher.find()){
        	resumedata.setAddress(matcher.group(1));
        }
        
        pattern = Pattern.compile("户.*?口：.*?\"20\">(.*?)</td>");
        matcher = pattern.matcher(basicInfoContent);
        if(matcher.find()){
        	resumedata.setHouseHolds(matcher.group(1));
        }
        
        pattern=Pattern.compile("(未婚|已婚)",Pattern.CASE_INSENSITIVE);							
		matcher=pattern.matcher(txt);
        if(matcher.find()){
        	String maritalStatus = matcher.group(1);
        	resumedata.setMaritalStatus(maritalStatus);
        }
        
        pattern=Pattern.compile("(中共党员|团员|群众|民主党派|无党派人士)",Pattern.CASE_INSENSITIVE);
        matcher=pattern.matcher(txt);
        if(matcher.find()){
        	resumedata.setPoliticalLandscape(matcher.group(1).trim());
        }
        
        pattern=Pattern.compile("(\\d+)cm",Pattern.CASE_INSENSITIVE);
        matcher=pattern.matcher(txt);
        if(matcher.find()){
        	resumedata.setHeight(matcher.group(1));
        }
	}
	
	@Override
	public void extractContactInfo(Node node) {
		// TODO Auto-generated method stub
		
		return ;
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
		String jobTargetHtml = node.toHtml().replaceAll("\r", "").replaceAll("\n", "");
		
		Pattern pattern=Pattern.compile("到岗时间：.*?\">(.*?)</span>");
        Matcher matcher=pattern.matcher(jobTargetHtml);
        if(matcher.find()){
        	jobTarget.setEnrollTime(matcher.group(1));        	
        }
        
        pattern=Pattern.compile("工作性质：.*?\">(.*?)</span>");
        matcher=pattern.matcher(jobTargetHtml);
        if(matcher.find()){
        	jobTarget.setJobCatagory(matcher.group(1));        	
        }
        
        pattern=Pattern.compile("希望行业：.*?\">(.*?)</span>");
        matcher=pattern.matcher(jobTargetHtml);
        if(matcher.find()){
        	jobTarget.setJobIndustry(matcher.group(1));        	
        }
        
        pattern=Pattern.compile("目标地点：.*?\">(.*?)</span>");
        matcher=pattern.matcher(jobTargetHtml);
        if(matcher.find()){
        	jobTarget.setJobLocation(matcher.group(1));        	
        }
        
        pattern=Pattern.compile("期望月薪：.*?\">(.*?)</span>");
        matcher=pattern.matcher(jobTargetHtml);
        if(matcher.find()){
        	jobTarget.setSalary(matcher.group(1));        	
        }
        
        pattern=Pattern.compile("目标职能：.*?\">(.*?)</span>");
        matcher=pattern.matcher(jobTargetHtml);
        if(matcher.find()){
        	jobTarget.setJobCareer(matcher.group(1));        	
        }
        
        pattern=Pattern.compile("求职状态：.*?\">(.*?)</span>");
        matcher=pattern.matcher(jobTargetHtml);
        if(matcher.find()){
        	jobTarget.setStatus(matcher.group(1));        	
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
		//暂时不分析公司性质：企业规模 ，企业性质 （不重要）
		if(node==null){
			return null;
		}
		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
		
		TableTag jobDetailTable = (TableTag)(node);
		int rowsCount = jobDetailTable.getRowCount();
		int currentnum = 0;
		for(int i = 0;i < rowsCount; i++){
			if(jobDetailTable.getRow(i).getColumnCount() ==1){
				if(jobDetailTable.getRow(i).getColumns()[0].getAttribute("valign") != null){
					currentnum ++;
	        		continue;
	        	}else{
	        		if(jobDetailTable.getRow(i).toHtml().indexOf("<img") < 0){
	        			String jobdesc = jobDetailTable.getRow(i).getColumns()[0].getChildrenHTML();
						if(jobdesc.startsWith("<br>")){
							jobdesc = jobdesc.substring(4);
						}if(jobdesc.startsWith("<br />")){
							jobdesc = jobdesc.substring(6);
						}
	        			((WorkExperienceData)workExperienceDataList.get(currentnum)).setJobDesc(jobdesc);	
	        		}else{
	        			String info = jobDetailTable.getRow(i).toPlainTextString();
						info = info.replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "");
						
						Pattern pattern = Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+)：(.*)\\[(.*)\\]");
				        Matcher matcher = pattern.matcher(info);
				        if(matcher.find()){
				        	WorkExperienceData workExperienceData = new WorkExperienceData();
				        	
				        	workExperienceData.setStartTime(matcher.group(1).replaceAll("/", "-"));	        	
				        	workExperienceData.setEndTime(matcher.group(2).replaceAll("/", "-"));
				        	workExperienceData.setCompanyName(matcher.group(3));
				        	workExperienceData.setDruation(matcher.group(4));
				        	
				        	pattern = Pattern.compile("(.*)\\((.*)\\)");
					        matcher = pattern.matcher(workExperienceData.getCompanyName());
					        if(matcher.find()){
					        	workExperienceData.setCompanyName(matcher.group(1));
					        	workExperienceData.setCompanyScale(matcher.group(2));
					        }
				        	
				        	workExperienceDataList.add(workExperienceData);
				        }
	        		}
	        		
	        	}
				
			}else if(jobDetailTable.getRow(i).getColumnCount() ==2){
        		String title = jobDetailTable.getRow(i).getColumns()[0].toPlainTextString().trim();
        		String desc = jobDetailTable.getRow(i).getColumns()[1].toPlainTextString().trim();
        		if(title.equals("所属行业：")){
        			workExperienceDataList.get(currentnum).setIndustryCatagory(desc);
        		}else if(title.equals("汇报对象：")){
        			if(workExperienceDataList.get(currentnum).manageExperienceDataList.size()==0){
        				ManageExperienceData manageExperience =  new ManageExperienceData();
        				manageExperience.setReportTo(desc);
        				workExperienceDataList.get(currentnum).manageExperienceDataList.add(manageExperience);
        			}else{
        				workExperienceDataList.get(currentnum).manageExperienceDataList.get(0).setReportTo(desc);       				
        			}
        		}else if(title.equals("下属人数：")){
        			if(workExperienceDataList.get(currentnum).manageExperienceDataList.size()==0){
        				ManageExperienceData manageExperience =  new ManageExperienceData();
        				manageExperience.setSubordinatesNum(desc);
        				workExperienceDataList.get(currentnum).manageExperienceDataList.add(manageExperience);
        			}else{
        				workExperienceDataList.get(currentnum).manageExperienceDataList.get(0).setSubordinatesNum(desc);    				
        			}
        		}else if(title.equals("证 明 人：")){
        			if(workExperienceDataList.get(currentnum).manageExperienceDataList.size()==0){
        				ManageExperienceData manageExperience =  new ManageExperienceData();
        				manageExperience.setReterence(desc);
        				workExperienceDataList.get(currentnum).manageExperienceDataList.add(manageExperience);
        			}else{        				
        				workExperienceDataList.get(currentnum).manageExperienceDataList.get(0).setReterence(desc);
        			}
        		}else if(title.equals("离职原因：")){
        			if(workExperienceDataList.get(currentnum).manageExperienceDataList.size()==0){
        				ManageExperienceData manageExperience =  new ManageExperienceData();
        				manageExperience.setLeavingReason(desc);
        				workExperienceDataList.get(currentnum).manageExperienceDataList.add(manageExperience);
        			}else{
        				workExperienceDataList.get(currentnum).manageExperienceDataList.get(0).setLeavingReason(desc);
        			}
        		}else if(title.equals("工作业绩：")){
        			if(workExperienceDataList.get(currentnum).manageExperienceDataList.size()==0){
        				ManageExperienceData manageExperience =  new ManageExperienceData();
        				manageExperience.setKeyPerformance(desc);
        				workExperienceDataList.get(currentnum).manageExperienceDataList.add(manageExperience);
        			}else{
        				workExperienceDataList.get(currentnum).manageExperienceDataList.get(0).setKeyPerformance(desc);
        			}
        		}
        		else{
        			workExperienceDataList.get(currentnum).setDepartment(title);
        			workExperienceDataList.get(currentnum).setPositionTitle(desc);
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
			TableTag educationDetailTable = (TableTag)(node);
			int rowsCount = educationDetailTable.getRowCount();
			for(int i = 0;i < rowsCount;i++){
				if(educationDetailTable.getRow(i).getColumnCount()>=2){
					EducationExperienceData educationExperienceData = new EducationExperienceData();
					String time = educationDetailTable.getRow(i).getColumns()[0].toPlainTextString().replaceAll(" ", "").replaceAll("\r","").replaceAll("\n","");
					if(time.indexOf("--")>=0){
						String start = time.split("--")[0];
						String end = time.split("--")[1];
						String school = educationDetailTable.getRow(i).getColumns()[1].toPlainTextString().replaceAll(" ", "");
						String major = educationDetailTable.getRow(i).getColumns()[2].toPlainTextString().replaceAll(" ", "");
						String degree = educationDetailTable.getRow(i).getColumns()[3].toPlainTextString().replaceAll(" ", "");
						
						educationExperienceData.setStartTime(start.replaceAll("/", "-"));
						educationExperienceData.setEndTime(end.replaceAll("/", "-"));
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
	@Override
	public ArrayList<TrainingExperienceData> extractTrainingExperience(Node node) {
		if(node == null){
			return null;
		}
		
		int index = 0;
		ArrayList<TrainingExperienceData> trainingExperienceDataList = new ArrayList<TrainingExperienceData>();
		if(node instanceof TableTag){
			TableTag tableTag = (TableTag)node;
			for(int j = 0;j < tableTag.getRowCount();j++){
				TableRow tableRow = tableTag.getRow(j);
				if(4 == tableRow.getColumnCount()){
					TrainingExperienceData trainingExperienceData = new TrainingExperienceData();
					String time = tableRow.getColumns()[0].toPlainTextString().trim().replaceAll("\\s+/", "-");
					String instituation = tableRow.getColumns()[1].toPlainTextString().trim();
					String course = tableRow.getColumns()[2].toPlainTextString().trim();
					String certificate = tableRow.getColumns()[3].toPlainTextString().trim();
					
					if(time.split("--").length == 2){
						String startTime = time.split("--")[0];
						String endTime = time.split("--")[1].replaceAll("：", "");
						trainingExperienceData.setStartTime(startTime);
						trainingExperienceData.setEndTime(endTime);
					}
					
					trainingExperienceData.setInstituation(instituation);
					trainingExperienceData.setCourse(course);
					trainingExperienceData.setCertificate(certificate);
					
					trainingExperienceDataList.add(trainingExperienceData);
					index++;
				}else if(1 == tableRow.getColumnCount()){
					TableColumn tableColumn = tableRow.getColumns()[0];
					String classText = tableColumn.getAttribute("class");
					if("text_left".equals(classText)){
						String trainDesc = tableColumn.getChildrenHTML();
						if(index >= 1){
							trainingExperienceDataList.get(index-1).setTrainDesc(trainDesc);
						}
					}
				}
			}
		}
		
		return trainingExperienceDataList;
	}

	public HashMap<String,String> extractOtherInfoMap(Node node){
		HashMap<String,String> otherInfoMap = new HashMap<String,String>();
		
		if(node instanceof TableTag){
			TableTag tableTag = (TableTag)node;
			for(int j = 0;j < tableTag.getRowCount();j++){
				TableRow tableRow = tableTag.getRow(j);
				if(2 == tableRow.getColumnCount()){
					String title = tableRow.getColumns()[0].toPlainTextString().trim().replaceAll("：", "");
					String desc = tableRow.getColumns()[1].toPlainTextString().trim();
					
					otherInfoMap.put(title, desc);
				}
			}
		}
		return otherInfoMap;
	}
	/**
	 * 获取求职者语言能力
	 * @param node 包含语言能力的html节点
	 * @return 语言能力 @see LanguageSkillData 
	 */
	@Override
	public ArrayList<LanguageSkillData> extractLanguageSkill(Node node) {
		// TODO Auto-generated method stub
		if(null == node){
			return null;
		}
		
		ArrayList<LanguageSkillData> languageSkillDataList = new ArrayList<LanguageSkillData>();
		
		if(node instanceof TableTag){
			TableTag tableTag = (TableTag)node;
			for(int j = 0;j < tableTag.getRowCount();j++){
				TableRow tableRow = tableTag.getRow(j);
				if(2 == tableRow.getColumnCount()){
					String title = tableRow.getColumns()[0].toPlainTextString().trim().replaceAll("：", "");
					String desc = tableRow.getColumns()[1].toPlainTextString().trim();
					
					if("".equals(desc)){
						return languageSkillDataList;
					}
					String height = tableRow.getAttribute("height");
					if("25".equals(height)){
						LanguageSkillData languageSkillData = new LanguageSkillData();
						Pattern pattern=Pattern.compile("(.*)（(.*?)）");
				        Matcher matcher=pattern.matcher(desc);
				        if(matcher.find()){		        	
				        	String language = matcher.group(1);
				        	String level = matcher.group(2);
				        	languageSkillData.setCatagory(language);
				        	languageSkillData.setLevel(level);				        	
				        }
						title = title.substring(0,title.indexOf("（"));
						
						languageSkillData.setCatagory(title);
						
						pattern=Pattern.compile("听说（(.*?)）.*?读写（(.*?)）");
				        matcher=pattern.matcher(desc);
				        if(matcher.find()){		        	
				        	String listenAndSpeakAbility = matcher.group(1);
				        	String readAndWriteAbility = matcher.group(2);
				        	languageSkillData.setListenAndSpeakAbility(listenAndSpeakAbility);
				        	languageSkillData.setReadAndWriteAbility(readAndWriteAbility);
				        }
				        languageSkillDataList.add(languageSkillData);						
					}else{
						LanguageSkillData languageSkillData = new LanguageSkillData();
						languageSkillData.setCatagory(title);
						languageSkillData.setLevel(desc);
						languageSkillDataList.add(languageSkillData);
					}
				}
			}
		}
		return languageSkillDataList;
	}


	public ArrayList<RewardData> extractReward(Node node){
		if(node==null || !(node instanceof TableTag)){
			return null;
		}
		
		ArrayList<RewardData> rewardDataList = new ArrayList<RewardData>();
		
		TableTag table = (TableTag)node;
		int rowCount = table.getRowCount();
		for(int i=0;i < rowCount; i++){
			TableRow tableRow = table.getRow(i);
			if(tableRow.getColumnCount() == 3){
				RewardData rewardData = new RewardData();
				
				String time = tableRow.getColumns()[0].toPlainTextString();
				time = time.replaceAll(" ", "").replaceAll("/", "-");
				String title = tableRow.getColumns()[1].toPlainTextString();
				String level = tableRow.getColumns()[2].toPlainTextString();
				
				rewardData.setTime(time);
				rewardData.setRewards(title);
				rewardData.setRewardsLevel(level);
				
				rewardDataList.add(rewardData);
			}
			
		}
		
		return rewardDataList;
	}
	/**
	 * 获取求职者证书信息
	 * @param node 包含证书信息的html节点
	 * @return 证书信息 @see CertificateData 
	 */
	@Override
	public ArrayList<CertificateData> extractCertficate(Node node) {
		// TODO Auto-generated method stub
		if(null == node){
			return null;
		}
		
		ArrayList<CertificateData> certificateDataList = new ArrayList<CertificateData>();
		
		if(node instanceof TableTag){
			TableTag tableTag = (TableTag)node;
			for(int j = 0;j < tableTag.getRowCount();j++){
				TableRow tableRow = tableTag.getRow(j);
				if(3 == tableRow.getColumnCount()){
					CertificateData certificateData = new CertificateData();
					String time = tableRow.getColumns()[0].toPlainTextString().trim().replaceAll("\\s+/", "-");
					String title = tableRow.getColumns()[1].toPlainTextString().trim();
					String comment = tableRow.getColumns()[2].toPlainTextString().trim();
					
					certificateData.setAcquireTime(time);
					certificateData.setCertificateTitle(title);
					certificateData.setComment(comment);
					
					certificateDataList.add(certificateData);
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
		String projectContent = "";
		projectContent =projectContent.replaceAll(" ", "");
		ArrayList<ProjectExperienceData> projectExperienceDataList = new ArrayList<ProjectExperienceData>();
		
		TableTag table = (TableTag)(node);
		int rowsCount = table.getRowCount();
		int currentnum = 0;
		for(int i = 0;i < rowsCount; i++){
			TableRow tdRow = table.getRow(i);
			if(tdRow.getColumnCount() == 1){
				if("text_left".equals(tdRow.getColumns()[0].getAttribute("class"))){
					ProjectExperienceData projectExperienceData = new ProjectExperienceData();
					String basinInfo = tdRow.getColumns()[0].toPlainTextString().replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "");
					
					Pattern pattern = Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+)：(.*)");
			        Matcher matcher = pattern.matcher(basinInfo);
			        while(matcher.find()){
			        	projectExperienceData.setStartTime(matcher.group(1).replaceAll("/", "-"));
			        	projectExperienceData.setEndTime(matcher.group(2).replaceAll("/", "-"));
			        	projectExperienceData.setProjectTitle(matcher.group(3));
			        }
			        projectExperienceDataList.add(projectExperienceData);
				}
				if("middle".equals(tdRow.getColumns()[0].getAttribute("valign"))){
					currentnum ++;
				}				
			}else if (tdRow.getColumnCount() == 2){
				
				String title = tdRow.getColumns()[0].getChildrenHTML().trim();
				String desc = tdRow.getColumns()[1].getChildrenHTML().trim();
				if(title.equals("开发工具：")){
					((ProjectExperienceData)projectExperienceDataList.get(currentnum)).setDevelopTool(desc);
				}else if(title.equals("软件环境：")){
					((ProjectExperienceData)projectExperienceDataList.get(currentnum)).setSoftwareEnvir(desc);
				}else if(title.equals("硬件环境：")){
					((ProjectExperienceData)projectExperienceDataList.get(currentnum)).setHardEnvir(desc);
				}else if(title.equals("责任描述：")){
					if(desc.startsWith("<br>")){
						desc = desc.substring(4);
					}if(desc.startsWith("<br />")){
						desc = desc.substring(6);
					}
					((ProjectExperienceData)projectExperienceDataList.get(currentnum)).setResponsibleFor(desc);
				}else if(title.equals("项目描述：")){
					if(desc.startsWith("<br>")){
						desc = desc.substring(4);
					}if(desc.startsWith("<br />")){
						desc = desc.substring(6);
					}
					((ProjectExperienceData)projectExperienceDataList.get(currentnum)).setProjectDesc(desc);
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
		if(node == null){
			return null;
		}
		
		ArrayList<ProfessionalSkillData> professionalSkillDataList = new ArrayList<ProfessionalSkillData>();
		
		if(node instanceof TableTag){
			if(((TableTag)node).getRowCount()==3){
				if(((TableTag)node).getRow(2).getColumnCount()==3){
					ProfessionalSkillData professionalSkillData = new ProfessionalSkillData();
					
					String skill = ((TableTag)node).getRow(2).getColumns()[0].toPlainTextString().trim();
					String proficiency = ((TableTag)node).getRow(2).getColumns()[1].toPlainTextString().trim();
					String months = ((TableTag)node).getRow(2).getColumns()[2].toPlainTextString().trim();
					
					professionalSkillData.setSkillDesc(skill);
					professionalSkillData.setProficiency(proficiency);
					professionalSkillData.setMonths(months);
					
					professionalSkillDataList.add(professionalSkillData);
				}else if(((TableTag)node).getRow(2).getColumnCount()==1){
					Node tableNode = (TableTag)(((TableTag)node).getRow(2).getColumns()[0].getChild(0));
					if(tableNode instanceof TableTag){
						tableNode = (TableTag)(((TableTag)tableNode).getRow(0).getColumns()[0].getChild(0));
						if(tableNode instanceof TableTag){
							TableTag skillDescNode = (TableTag)tableNode;
							int rowsCount = skillDescNode.getRowCount();
							for(int i = 0;i < rowsCount;i++){
								ProfessionalSkillData professionalSkillData = new ProfessionalSkillData();
								int coloums = skillDescNode.getRow(i).getColumnCount();
								if(coloums == 3){
									String skill = skillDescNode.getRow(i).getColumns()[0].toPlainTextString().trim();
									String proficiency = skillDescNode.getRow(i).getColumns()[1].toPlainTextString().trim();
									String months = skillDescNode.getRow(i).getColumns()[2].toPlainTextString().trim();
									
									professionalSkillData.setSkillDesc(skill);
									professionalSkillData.setProficiency(proficiency);
									professionalSkillData.setMonths(months);
									
									professionalSkillDataList.add(professionalSkillData);
								}
							}
						}
						
					}
				}
			}else if(((TableTag)node).getRowCount() > 3){
				for(int i = 2; i < ((TableTag)node).getRowCount();i++){
					ProfessionalSkillData professionalSkillData = new ProfessionalSkillData();
					
					String skill = ((TableTag)node).getRow(i).getColumns()[0].toPlainTextString().trim();
					String proficiency = ((TableTag)node).getRow(i).getColumns()[1].toPlainTextString().trim();
					String months = ((TableTag)node).getRow(i).getColumns()[2].toPlainTextString().trim();
					
					professionalSkillData.setSkillDesc(skill);
					professionalSkillData.setProficiency(proficiency);
					professionalSkillData.setMonths(months);
					
					professionalSkillDataList.add(professionalSkillData);
				}
			}
		}
//		//TableTag educationDetailTable = (TableTag)(node);
//		if((((TableTag)node).getRow(2).getColumnCount()==3)){
//			ProfessionalSkillData professionalSkillData = new ProfessionalSkillData();
//			
//			String skill = ((TableTag)node).getRow(2).getColumns()[0].toPlainTextString().trim();
//			String proficiency = ((TableTag)node).getRow(2).getColumns()[1].toPlainTextString().trim();
//			String months = ((TableTag)node).getRow(2).getColumns()[2].toPlainTextString().trim();
//			
//			professionalSkillData.setSkillDesc(skill);
//			professionalSkillData.setProficiency(proficiency);
//			professionalSkillData.setMonths(months);
//			
//			professionalSkillDataList.add(professionalSkillData);
//		}else{
//			TableTag educationDetailTable = (TableTag)(((TableTag)node).getRow(2).getColumns()[0].getChild(0));
//			educationDetailTable = (TableTag)(((TableTag)educationDetailTable).getRow(0).getColumns()[0].getChild(0));
//			int rowsCount = educationDetailTable.getRowCount();
//			for(int i = 0;i < rowsCount;i++){
//				ProfessionalSkillData professionalSkillData = new ProfessionalSkillData();
//				int coloums = educationDetailTable.getRow(i).getColumnCount();
//				if(coloums == 3){
//					String skill = educationDetailTable.getRow(i).getColumns()[0].toPlainTextString().trim();
//					String proficiency = educationDetailTable.getRow(i).getColumns()[1].toPlainTextString().trim();
//					String months = educationDetailTable.getRow(i).getColumns()[2].toPlainTextString().trim();
//					
//					professionalSkillData.setSkillDesc(skill);
//					professionalSkillData.setProficiency(proficiency);
//					professionalSkillData.setMonths(months);
//					
//					professionalSkillDataList.add(professionalSkillData);
//				}
//			}
//		}		
		return professionalSkillDataList;
	}


	@Override
	public ArrayList<PracticalExperienceData> extractPracticalExperience(
			Node node) {
		if(node == null || !(node instanceof TableTag)){
			return null;
		}
		
		ArrayList<PracticalExperienceData> practicalExperienceDataList = new ArrayList<PracticalExperienceData>();
		TableTag table = (TableTag)node;
		int index = 0;
		int rowCount = table.getRowCount();
		for(int i=0;i < rowCount; i++){
			TableRow tableRow = table.getRow(i);
			String text = tableRow.toPlainTextString().replaceAll(" ", "");
			String align = tableRow.getAttribute("align");
			if("center".equals(align)){
				continue;
			}
			
			Pattern pattern = null;
			pattern=Pattern.compile("(\\d{4}/\\d+).*?(至今|\\d{4}/\\d+)(.*)",Pattern.CASE_INSENSITIVE);
			
	        Matcher matcher=pattern.matcher(text);
	        if(matcher.find()){
	        	PracticalExperienceData practicalExperienceData = new PracticalExperienceData();
	        	
	        	practicalExperienceData.setStartTime(matcher.group(1).trim().replaceAll("/", "-"));
	        	practicalExperienceData.setEndTime(matcher.group(2).trim().replaceAll("/", "-"));
	        	practicalExperienceData.setPracticeTitle(matcher.group(3).trim());
	        	
	        	practicalExperienceDataList.add(practicalExperienceData);
	        	index++;
	        }else{
	        	if(index>=1){
	        		if(!"".equals(text)){
	        			if(tableRow.getColumnCount()==1){
		        			text = tableRow.getColumns()[0].getChildrenHTML();
		        			practicalExperienceDataList.get(index-1).setPracticeDesc(text);
		        		}	
	        		}
	        			
	        	}
	        }
			
		}
		return practicalExperienceDataList;
	}


	@Override
	public ArrayList<OtherInfoData> extractOtherInfo(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	public _51jobResumeParser(String html,String url){

		if(htmlcontent.indexOf("&nbsp;") > 0){
			hasNbsp = true;
		}
		htmlcontent = html;
		htmlcontent = htmlcontent.replaceAll("\r", "").replaceAll("\n", "").replaceAll("#→start→#", "").replaceAll("#←end←#", "");
		
		this.htmlurl = url;
	}
	public _51jobResumeParser(String html,String url,String objectid){
		
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
	
	public _51jobResumeParser(){
		
	}
	
	public void setHtml(String html){
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
	}
	
	public void setUrl(String url){
		this.htmlurl = url;
		resumedata.setUrl(url);
	}
	
	public void setResumeID(String objectid){
		this.objectid = objectid;
	}
	
	public boolean isValidResume(){
		if(this.htmlcontent.indexOf("该简历已被求职者删除") >= 0){
			return false;
		}
		return true;
	}
	

	/**
	 * 判断当前简历是否需要解析
	 */
//	public boolean needToParse(){
//		String sourceID = this.getSourceID(this.htmlcontent);
//		ResumeData existed = null;
//		
//		if(resumedata.getSourceID() != ""){
//			existed = DBHolder.databaseWriter.findResumeData(sourceID);
//		}else{
//			existed = null;
//		}
//		if(existed ==  null){
//			return true;
//		}else{
//			if(this.getUpdateTime().equals(existed.getUpdateTime())){
//				if(!existed.getIsCotactInformation().equals("YES") && this.hasContactInfo()){
//					return true;
//				}else{
//					return false;
//				}
//			}
//		}
//		
//		return true;
//	}
		
	@Override
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
}
