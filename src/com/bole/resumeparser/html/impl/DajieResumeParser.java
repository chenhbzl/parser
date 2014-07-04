//package com.bole.resumeparser.html.impl;
//
//import java.util.ArrayList;
//
//import org.bson.types.ObjectId;
//import org.htmlparser.Node;
//import org.htmlparser.NodeFilter;
//import org.htmlparser.Parser;
//import org.htmlparser.filters.HasAttributeFilter;
//import org.htmlparser.filters.OrFilter;
//import org.htmlparser.tags.Div;
//import org.htmlparser.util.NodeList;
//
//import com.bole.resumeparser.exception.html.NotRegularResumeException;
//import com.bole.resumeparser.exception.html.ResumeProcessException;
//import com.bole.resumeparser.models.CertificateData;
//import com.bole.resumeparser.models.EducationExperienceData;
//import com.bole.resumeparser.models.JobTarget;
//import com.bole.resumeparser.models.LanguageSkillData;
//import com.bole.resumeparser.models.OtherInfoData;
//import com.bole.resumeparser.models.PracticalExperienceData;
//import com.bole.resumeparser.models.ProfessionalSkillData;
//import com.bole.resumeparser.models.ProjectExperienceData;
//import com.bole.resumeparser.models.ResumeData;
//import com.bole.resumeparser.models.TrainingExperienceData;
//import com.bole.resumeparser.models.WorkExperienceData;
//
//public class DajieResumeParser extends AbstractResumeParser implements ResumeParserInterface{
//	/**
//	 * 解析大街网的简历
//	 */
//	/**
//	 * @author liyao
//	 * @version 1.5
//	 */
//	public String htmlcontent = ""; //简历html内容
//	public String htmlurl = "";   //简历url
//	boolean hasNbsp = false;      //用于区别简历是后台爬虫爬取（爬虫爬取的不包含nbsp;）
//	ObjectId objectid = null;     //简历的objectid(和collectedResume表中objectid一致)
//	ResumeData resumedata = new ResumeData();   
//	public String resumeText  = "";   //去除html的简历内容（用于提取所有的url使用）
//	public String currentStatus = "";  //目前工作状态
//	
//	@Override
//	public ResumeData parse() throws ResumeProcessException,
//			NotRegularResumeException {
//		//主要负责解析简历将解析之后的数据保存到ResumeData中
//		if(logger.isDebugEnabled()){
//			logger.debug("zhilianresumeparser is startting to parse resume Objecid: " + objectid + " , url :" + htmlurl);
//		}
//		
//		resumedata.url =  htmlurl;
//		resumedata.set_id(objectid);
//		resumedata.setSource("liepin_plugin");
//		resumedata.setUpdateTime(getUpdateTime());
//		resumedata.setSourceID(getSourceID(this.htmlcontent));
//		
//		Parser parser = Parser.createParser(htmlcontent, "utf-8");		
//		
//		NodeFilter infoFilter = new HasAttributeFilter("class","pf-info");
//		NodeFilter dataFilter = new HasAttributeFilter("id","dj-widget-profile-data");
//		
//		NodeFilter[] filters = new NodeFilter[2];
//		filters[0] = infoFilter;
//		filters[1] = dataFilter;
//
//		OrFilter orfilter = new OrFilter();
//		orfilter.setPredicates(filters);
//		
//		NodeList nodes = null;
//		try {
//			nodes = parser.extractAllNodesThatMatch(orfilter);
//			String className = ""; //div node的class属性名
//			String idName = ""; //id属性名
//			for(int i = 0;i < nodes.size();i++){
//				Node node = nodes.elementAt(i);
//				resumeText = resumeText + " " + node.toPlainTextString();
//				if(node instanceof Div){
//					className = ((Div)node).getAttribute("class");
//					idName = ((Div)node).getAttribute("id");
//					
//					if(idName.equals("workexps")){
//						NodeList childs = node.getChildren();
//						if(childs!=null){
//							for(int j=0;j<childs.size();j++){
//								Node child = childs.elementAt(j);
//								if(child instanceof Div && "profile jobs".equals(((Div)child).getAttribute("class"))){
//									resumedata.setWorkExperience(extractWorkExperience(child));
//									break;
//								}
//							}
//							continue;
//						}
//					}
//					continue;
//					
//					switch(className){
//					case "profile basic":
//						extractBasicInfo(node);
//						break;
//					case "profile career clearfix":
//						System.out.println("profile career clearfix");
//						if(node.toPlainTextString().indexOf("12个月")>=0){
//							resumedata.setCurrentSalary(node.toPlainTextString().trim().replaceAll("&nbsp;", " "));
//						}else{
//							resumedata.setJobTarget(extractJobTarget(node));
//						}						
//						break;
//					case "profile jobs":
//						System.out.println("profile jobs");
//						resumedata.setWorkExperience(extractWorkExperience(node));
//						break;
//					case "profile current-job clearfix":
//						System.out.println("profile current-job clearfix");
//						resumedata.setCurrentWork(extractCurrentWork(node));
//						break;
//					case "profile education":
//						System.out.println("profile education");
//						resumedata.setEducationExperience(extractEducationExperience(node));
//						break;
//					case "profile language":
//						System.out.println("profile language");
//						resumedata.setLanguageSkill(extractLanguageSkill(node));
//						break;
//					case "profile projects":
//						System.out.println("profile projects");
//						resumedata.setProjectExperience(extractProjectExperience(node));
//						break;
//					case "profile introduce word" :
//						System.out.println("profile introduce word");
//						resumedata.setSelfEvaluation(extractSelfEvaluation(node));
//						break;
//					case "profile introduce":
//						System.out.println("profile introduce word");
//						resumedata.setSelfEvaluation(extractSelfEvaluation(node));
//					}
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		resumedata.setUrls(this.getUrls());
//		if(resumedata.getPhone()==null){
//			resumedata.setIsCotactInformation("NO");
//		}
//		resumedata.setResumeText(resumeText);
//		return resumedata;
//	}
//	
//	@Override
//	public boolean hasContactInfo() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public Void extractContactInfo(Node node) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String removeHtmlTag(String htmlStr) {
//		// TODO Auto-generated method stub
//		return super.removeHtmlTag(htmlStr);
//	}
//
//
//	@Override
//	public String getSourceID(String html) {
//		// TODO Auto-generated method stub
//		return super.getSourceID(html);
//	}
//
//	@Override
//	public String getWebsite() {
//		// TODO Auto-generated method stub
//		return super.getWebsite();
//	}
//
//	@Override
//	public String getUpdateTime() {
//		// TODO Auto-generated method stub
//		return super.getUpdateTime();
//	}
//
//	@Override
//	public String getName() {
//		// TODO Auto-generated method stub
//		return super.getName();
//	}
//
//	@Override
//	public String getPhone() {
//		// TODO Auto-generated method stub
//		return super.getPhone();
//	}
//
//	@Override
//	public String getEmail() {
//		// TODO Auto-generated method stub
//		return super.getEmail();
//	}
//
//	@Override
//	public String getAge() {
//		// TODO Auto-generated method stub
//		return super.getAge();
//	}
//
//	@Override
//	public String Gender() {
//		// TODO Auto-generated method stub
//		return super.Gender();
//	}
//
//	@Override
//	public String extractSelfEvaluation(Node node) {
//		// TODO Auto-generated method stub
//		return super.extractSelfEvaluation(node);
//	}
//
//	@Override
//	public JobTarget extractJobTarget(Node node) {
//		// TODO Auto-generated method stub
//		return super.extractJobTarget(node);
//	}
//
//	@Override
//	public String getResumeKeywords(Node node) {
//		// TODO Auto-generated method stub
//		return super.getResumeKeywords(node);
//	}
//
//	@Override
//	public ArrayList<WorkExperienceData> extractWorkExperience(Node node) {
//		// TODO Auto-generated method stub
//		return super.extractWorkExperience(node);
//	}
//
//	@Override
//	public ArrayList<EducationExperienceData> extractEducationExperience(
//			Node node) {
//		// TODO Auto-generated method stub
//		return super.extractEducationExperience(node);
//	}
//
//	@Override
//	public ArrayList<TrainingExperienceData> extractTrainingExperience(Node node) {
//		// TODO Auto-generated method stub
//		return super.extractTrainingExperience(node);
//	}
//
//	@Override
//	public ArrayList<LanguageSkillData> extractLanguageSkill(Node node) {
//		// TODO Auto-generated method stub
//		return super.extractLanguageSkill(node);
//	}
//
//	@Override
//	public ArrayList<CertificateData> extractCertficate(Node node) {
//		// TODO Auto-generated method stub
//		return super.extractCertficate(node);
//	}
//
//	@Override
//	public ArrayList<ProjectExperienceData> extractProjectExperience(Node node) {
//		// TODO Auto-generated method stub
//		return super.extractProjectExperience(node);
//	}
//
//	@Override
//	public ArrayList<ProfessionalSkillData> extractProfessionalSkill(Node node) {
//		// TODO Auto-generated method stub
//		return super.extractProfessionalSkill(node);
//	}
//
//	@Override
//	public ArrayList<PracticalExperienceData> extractPracticalExperience(
//			Node node) {
//		// TODO Auto-generated method stub
//		return super.extractPracticalExperience(node);
//	}
//
//	@Override
//	public ArrayList<OtherInfoData> extractOtherInfo(Node node) {
//		// TODO Auto-generated method stub
//		return super.extractOtherInfo(node);
//	}
//	
//
//}
