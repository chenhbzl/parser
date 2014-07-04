package com.bole.resumeparser.document.impl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.util.NodeList;

import com.bole.resumeparser.document.impl.AbstractResumeParser.SectionInfo;
import com.bole.resumeparser.exception.ResumeParseException;
import com.bole.resumeparser.message.ResumeMessage;
import com.bole.resumeparser.models.*;

public class ZhiLianResumeParser extends AbstractResumeParser implements DocumentResumeParserInterface{

	/**

	 * 已经完成对基本信息的解析(性别，年龄),求职意向，项目经历，教育经历，自我评价的解析

	 * 需要完善职业技能，培训经历，证书，在校实践经验，其他信息的解析

	 */

	public String filePath = "";

	TextResumeData resumedata = new TextResumeData();

	String type = "";  //求职者自己下载的doc简历还是公司下载的简历
	boolean hasBuy = true; //是否买过该简历

	HashMap<String,SectionInfo> sectionMap = new HashMap<String,SectionInfo>();

	ArrayList<String> resumeContentList = new ArrayList<String>();

	

	/**

	 * @param args

	 * @throws IOException 

	 * @throws InterruptedException 

	 */

	public static void main(String[] args) throws IOException, InterruptedException {		// TODO Auto-generated method stub

//		String	SOFFICE_PATH	= "/usr/bin/soffice";

//		File dir = new File("/home/liyao/mnt/num1.doc").getParentFile();

//		Process process =

//		        Runtime.getRuntime().exec(

//		                new String[] { SOFFICE_PATH, "--headless", "--convert-to", "txt:Text", "-outdir",

//		                        dir.getAbsolutePath(), "/home/liyao/mnt/num1.doc" });

//		

//		int value = process.waitFor();

//		if (value == 0) {		

//		}

		ZhiLianResumeParser t = new ZhiLianResumeParser("/home/bobby/mnt/邮箱同步的简历/简历模板/resume3/");
		try {
			TextResumeData resumeData = t.parse();
		} catch (ResumeParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.init();	

	}



	@Override
	public TextResumeData parse() throws ResumeParseException{		
		this.preProcess();       
		resumedata.setSource("zhilian");
		
        Iterator iter = sectionMap.entrySet().iterator();
        while (iter.hasNext()) {
        	Map.Entry entry = (Map.Entry) iter.next();
        	Object key = entry.getKey();
        	SectionInfo val = (SectionInfo)entry.getValue();
        	System.out.println(key + ":" +val.getStart() +","+val.getEnd());
        	if(key.equals("basicInfo")){
        		this.processBasicInfo(val.getStart(), val.getEnd());
        		this.processContactInfo(val.getStart(), val.getEnd());
        	}else if(key.equals("workExperience")){
        		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
        		workExperienceDataList = this.extractWorkExperience(val.getStart(), val.getEnd());
        		resumedata.setWorkExperience(workExperienceDataList);
        	}else if(key.equals("projectExperience")){
        		ArrayList<ProjectExperienceData> projectExperienceDataList = new ArrayList<ProjectExperienceData>();
        		projectExperienceDataList = this.extractProjectExperience(val.getStart(), val.getEnd());
        		resumedata.setProjectExperience(projectExperienceDataList);
        	}else if(key.equals("languageSkill")){
        		ArrayList<LanguageSkillData> languageSkillDataList = new ArrayList<LanguageSkillData>();
        		languageSkillDataList = this.extractLanguageSkill(val.start, val.end);
        		resumedata.setLanguageSkill(languageSkillDataList);
        	}else if(key.equals("educationExperience")){
        		ArrayList<EducationExperienceData> educationExperienceDataList = new ArrayList<EducationExperienceData>();
        		educationExperienceDataList = this.extractEducationExperience(val.getStart(), val.getEnd());
        		resumedata.setEducationExperience(educationExperienceDataList);
        	}else if(key.equals("selfEvaluation")){
        		String selfEvaluation = this.extractSelfEvaluation(val.getStart(), val.getEnd());
        		resumedata.setSelfEvaluation(selfEvaluation);
        	}else if(key.equals("jobTarget")){
        		JobTarget jobTarget = new JobTarget();
        		jobTarget = this.extractJobTarget(val.getStart(),val.getEnd());
        		resumedata.setJobTarget(jobTarget);
        	}else if(key.equals("professionalSkill")){
        		ArrayList<ProfessionalSkillData> professionalSkillDataList = new ArrayList<ProfessionalSkillData>();
        		professionalSkillDataList = this.extractProfessionalSkill(val.getStart(), val.getEnd());
        		resumedata.setProfessionalSkill(professionalSkillDataList);
        	}
        }
        resumedata.setCreateTime(new Date());
        return resumedata;

	}	

	@Override
	public String getSourceID(String html) {
		// TODO Auto-generated method stub
		String resumeId = "";
		Pattern pattern=Pattern.compile("resume-left-tips-id\">ID:(.*?)(</span></div>)");
        Matcher matcher=pattern.matcher(html);
        if(matcher.find()){
        	resumeId = matcher.group(1);    
        }	
		return resumeId;
	}
	
	public String getImageUrl(){
		String imageUrl = "";
		return imageUrl;
	}

	@Override

	public String getWebsite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateTime() {
		// TODO Auto-generated method stub
		String updateTime = "";
		return updateTime;
	}	

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName(Node node){
		String name = "";
		name = node.toPlainTextString();
		return name;
	}
	
	@Override
	public void extractContactInfo(int start,int end) {
		// TODO Auto-generated method stub
		return;
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
		String selfEvaluation = "";
		
		for(int i = start;i <= end; i++){
			String line = resumeContentList.get(i);
			if(line.equals("")){
				continue;
			}else{
				if(!line.equals("自我评价")){
					selfEvaluation += "\r\n"+line;
				}
			}
		}
		selfEvaluation = selfEvaluation.replaceAll("\\s+", " ").trim();
		return selfEvaluation;
	}

	@Override
	public JobTarget extractJobTarget(int start,int end) {
		// TODO Auto-generated method stub
		JobTarget jobTarget = new JobTarget();
		for(int i = start;i <= end; i++){
			String line = resumeContentList.get(i);
			if(line.equals("")){
				continue;
			}else{
				if(line.indexOf("地区:") >= 0){
					if(line.endsWith("地区:")){
						jobTarget.setJobLocation(resumeContentList.get(i+1));
					}else{
						Pattern pattern = Pattern.compile("地区:(.*)");
						Matcher matcher = pattern.matcher(line);
						if(matcher.find()){
							jobTarget.setJobLocation(matcher.group(1).trim());
						}
					}
        		}else if(line.indexOf("月薪:") >= 0){
        			if(line.endsWith("月薪:")){
        				jobTarget.setSalary(resumeContentList.get(i+1));
					}else{
						Pattern pattern = Pattern.compile("月薪:(.*)");
						Matcher matcher = pattern.matcher(line);
						if(matcher.find()){
							jobTarget.setSalary(matcher.group(1).trim());
						}
					}
        		}else if(line.indexOf("状况:") >= 0){
        			if(line.endsWith("状况:")){
        				jobTarget.setStatus(resumeContentList.get(i+1));
					}else{
						Pattern pattern = Pattern.compile("状况:(.*)");
						Matcher matcher = pattern.matcher(line);
						if(matcher.find()){
							jobTarget.setStatus(matcher.group(1).trim());
						}
					}
        		}else if(line.indexOf("性质:") >= 0){
        			if(line.endsWith("性质:")){
        				jobTarget.setJobCatagory(resumeContentList.get(i+1));
					}else{
						Pattern pattern = Pattern.compile("性质:(.*)");
						Matcher matcher = pattern.matcher(line);
						if(matcher.find()){
							jobTarget.setJobCatagory(matcher.group(1).trim());
						}
					}
        		}else if(line.indexOf("职业:") >= 0){
        			if(line.endsWith("职业:")){
        				jobTarget.setJobCareer(resumeContentList.get(i+1));
					}else{
						Pattern pattern = Pattern.compile("职业:(.*)");
						Matcher matcher = pattern.matcher(line);
						if(matcher.find()){
							jobTarget.setJobCareer(matcher.group(1).trim());
						}
					}
        		}else if(line.indexOf("行业:") >= 0){
        			if(line.endsWith("行业:")){
        				jobTarget.setJobIndustry(resumeContentList.get(i+1));
					}else{
						Pattern pattern = Pattern.compile("行业:(.*)");
						Matcher matcher = pattern.matcher(line);
						if(matcher.find()){
							jobTarget.setJobIndustry(matcher.group(1).trim());
						}
					}
        		}
			}
		}
		return jobTarget;
	}

	@Override
	public ArrayList<WorkExperienceData> extractWorkExperience(int start,int end) {
		// TODO Auto-generated method stub
		//暂时不分析:企业规模（不重要）
		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
		
		String line = "";
		int currentNum = 0;
		String jobDesc = "";
		int currentRow = 0;
		for(int i = start;i <= end; i++){			
			line = resumeContentList.get(i).trim();
			
			if(i == end){
				if(currentNum>=1){
					((WorkExperienceData)(workExperienceDataList.get(currentNum-1))).setJobDesc(jobDesc+line);
					break;
				}
			}
			if(line.equals("")){
				continue;
			}else{
				String [] arr = null;
				Pattern pattern = null;
				pattern=Pattern.compile("(\\d+[./]\\d+).*?(至今|\\d+[./]\\d+)");
				Matcher matcher=pattern.matcher(line);
				if(matcher.find()){
					arr = line.split("  ");
					if(currentNum-1 >=0){
			        	((WorkExperienceData)(workExperienceDataList.get(currentNum-1))).setJobDesc(jobDesc);
			        	jobDesc = "";
			        	currentRow = 0;
			        }
					WorkExperienceData workExperienceData = new WorkExperienceData();
					workExperienceData.setStartTime(matcher.group(1).trim().replaceAll("\\.", "-"));
		        	workExperienceData.setEndTime(matcher.group(2).trim().replaceAll("\\.", "-"));
		        	
		        	
					if(arr.length == 3){
						workExperienceData.setCompanyName(arr[1]);
						workExperienceData.setDruation(arr[2].replace("（", "").replace("）", ""));
					}else if(arr.length == 2){
						workExperienceData.setCompanyName(arr[1]);
					}
					workExperienceDataList.add(workExperienceData);
					currentNum ++;
                	currentRow ++;
				}else if(currentRow == 1 ){
        			arr = line.split("\\|");
        			int num = arr.length;
        			if(resumeContentList.get(i-1).trim().endsWith(workExperienceDataList.get(currentNum-1).getEndTime())){
        				workExperienceDataList.get(currentNum-1).setCompanyName(arr[0]);
        				workExperienceDataList.get(currentNum-1).setPositionTitle(arr[num-1]);
        			}else{
        				if(num == 3){
            				workExperienceDataList.get(currentNum-1).setDepartment(arr[0]);
            				workExperienceDataList.get(currentNum-1).setPositionTitle(arr[1]);
            				workExperienceDataList.get(currentNum-1).setSalary(arr[2]);
            			}
            			if(num == 2){
            				if(arr[1].indexOf("元/月") >= 0){
            					((WorkExperienceData)(workExperienceDataList.get(currentNum-1))).setPositionTitle(arr[0]);
            					((WorkExperienceData)(workExperienceDataList.get(currentNum-1))).setSalary(arr[1]);
            				}else{
            					((WorkExperienceData)(workExperienceDataList.get(currentNum-1))).setDepartment(arr[0]);
                				((WorkExperienceData)(workExperienceDataList.get(currentNum-1))).setPositionTitle(arr[1]);
            				}
            			}
            			if(num == 1){
            				if(arr[0].indexOf("") < 0){
            					((WorkExperienceData)(workExperienceDataList.get(currentNum-1))).setPositionTitle(arr[0]);
            				}        				
            			}
        			}
        			         			
        			currentRow++;
        		}else if(currentRow == 2){
        			arr = line.split("\\|");
        			int num = arr.length;
        			for(int j=0;j < num;j++){
        				String txt = arr[j].trim();
        				if(j == 0){
        					String industryCatatory = arr[0].trim();
        					if(industryCatatory.indexOf(":")>=0){
        						industryCatatory = industryCatatory.substring(industryCatatory.indexOf(":")+1);
        					}
        					((WorkExperienceData)(workExperienceDataList.get(currentNum-1))).setIndustryCatagory(industryCatatory);
        				}else{
        					Pattern pat = Pattern.compile("(.*?)(:|:)(.*?)(\\s|$)");
        					Matcher mat = pat.matcher(txt);
        					if(mat.find()){
        						String title = mat.group(1).trim();
        						String desc = mat.group(3).trim();
        						if(title.indexOf("性质") >= 0){
        							workExperienceDataList.get(currentNum-1).setCompanyCatagory(desc);
        						}else if(title.indexOf("规模") >= 0){
        							workExperienceDataList.get(currentNum-1).setCompanyScale(desc);
        						}else if(title.indexOf("月薪") >= 0){
        							workExperienceDataList.get(currentNum-1).setSalary(desc);
        						}
        					}
        				}
        			}
        			currentRow ++;
        		}else if(currentRow == 3){
        			if(line.equals("工作描述:")){
        				jobDesc += line;        				
        			}else{
        				jobDesc += "\r\n" + line;
        			}
        		}
			}
		}
		return workExperienceDataList;
	}

	@Override
	public ArrayList<EducationExperienceData> extractEducationExperience(int start,int end) {
		// TODO Auto-generated method stub
		ArrayList<EducationExperienceData> educationExperienceDataList = new ArrayList<EducationExperienceData>();
		
		for(int i = start;i <= end; i++){
			String line = resumeContentList.get(i).trim();
			if(line.equals("")){
				continue;
			}else{
				Pattern pattern=Pattern.compile("(\\d+[./]\\d+).*?(至今|\\d+[./]\\d+)");
				Matcher mat = pattern.matcher(line);
				if(mat.find()){
					EducationExperienceData educationExperienceData = new EducationExperienceData();
					
					educationExperienceData.setStartTime(mat.group(1).trim().replaceAll("\\.", "-"));
        			educationExperienceData.setEndTime(mat.group(2).trim().replaceAll("\\.", "-"));
        			
					String [] arr = line.split("  ");
					int num = arr.length;
					if(num == 4){
						educationExperienceData.setSchool(arr[1]);
	        			educationExperienceData.setMajor(arr[2]);
	        			educationExperienceData.setDegree(arr[3]);
					}else if(line.endsWith(educationExperienceData.getEndTime())){
						String nextLine = resumeContentList.get(i+1).trim();
						arr = nextLine.split("\\|");
						if(arr.length == 3){
							educationExperienceData.setSchool(arr[0]);
		        			educationExperienceData.setMajor(arr[1]);
		        			educationExperienceData.setDegree(arr[2]);
						}
						i++;
					}
        			
        			educationExperienceDataList.add(educationExperienceData); 
				}
			}
		}
			
		return educationExperienceDataList;
	}

	@Override
	public ArrayList<TrainingExperienceData> extractTrainingExperience(int start,int end) {
		ArrayList<TrainingExperienceData> trainingExperienceDataList = new ArrayList<TrainingExperienceData>();
		return trainingExperienceDataList;
	}

	@Override
	public ArrayList<LanguageSkillData> extractLanguageSkill(int start,int end) {
		// TODO Auto-generated method stub
		ArrayList<LanguageSkillData> languageSkillDataList = new ArrayList<LanguageSkillData>();
		
		//获取div节点
		for(int i = start;i <= end; i++){
			String line = resumeContentList.get(i);
			if(line.equals("")){
				continue;
			}else{
				Pattern pattern=Pattern.compile("(.*?):读写能力(一般|良好|熟练|精通).*?\\|\\s听说能力(一般|良好|熟练|精通)",Pattern.CASE_INSENSITIVE);
		        Matcher matcher=pattern.matcher(line);
		        while(matcher.find()){
		        	LanguageSkillData languageSkillData = new LanguageSkillData();
		        	
		        	languageSkillData.setCatagory(matcher.group(1).trim());
		        	languageSkillData.setReadAndWriteAbility(matcher.group(2).trim());
		        	languageSkillData.setListenAndSpeakAbility(matcher.group(3).trim());      	
		        	
		        	languageSkillDataList.add(languageSkillData);
		        }
			}
		}
		return languageSkillDataList;
	}
	
	@Override
	public ArrayList<CertificateData> extractCertficate(int start,int end) {
		// TODO Auto-generated method stub
		ArrayList<CertificateData> certificateDataList = new  ArrayList<CertificateData>();
		return certificateDataList;
	}

	@Override
	public ArrayList<ProjectExperienceData> extractProjectExperience(int start,int end) {
		// TODO Auto-generated method stub  项目经验
		ArrayList<ProjectExperienceData> projectExperienceDataList = new ArrayList<ProjectExperienceData>();

		String line = "";
		int currentNum = 0;
		String projectDesc = "";
		String responsibleFor = "";
		String preTitle = "";
		
		for(int i = start;i <= end; i++){			
			line = resumeContentList.get(i).trim();
			
			if(i == end){
				((ProjectExperienceData)(projectExperienceDataList.get(currentNum-1))).setProjectDesc(projectDesc+"\r\n"+line);
				break;
			}
			
			Pattern pattern=Pattern.compile("(\\d+[./]\\d+).*?(至今|\\d+[./]\\d+)");
			Matcher matcher=pattern.matcher(line);
			if(matcher.find()){
				if(preTitle.equals("项目描述")){
					if(currentNum-1 >=0){
						if(projectExperienceDataList.get(currentNum-1).getProjectDesc().equals("")){
							projectExperienceDataList.get(currentNum-1).setProjectDesc(projectDesc.trim());
						}
						projectDesc = "";
						preTitle = "";
		        	}
				}else if(preTitle.equals("项目职责")){
					if(currentNum-1 >=0){
						if(projectExperienceDataList.get(currentNum-1).getResponsibleFor().equals("")){
							projectExperienceDataList.get(currentNum-1).setResponsibleFor(responsibleFor.trim());
						}
						responsibleFor = "";
						preTitle = "";
		        	}
				}
    				
    			
    			ProjectExperienceData projectExperienceData = new ProjectExperienceData();
    			
    			projectExperienceData.setStartTime(matcher.group(1).trim().replaceAll("\\.", "-"));
    			projectExperienceData.setEndTime(matcher.group(2).trim().replaceAll("\\.", "-"));
    			
    			if(line.endsWith(projectExperienceData.getEndTime())){
    				projectExperienceData.setProjectTitle(resumeContentList.get(i+1));
    				i++;
    			}else{
    				pattern=Pattern.compile("(\\d+[./]\\d+).*?(至今|\\d+[./]\\d+)(.*)");
    				matcher=pattern.matcher(line);
    				
    				if(matcher.find()){
    					projectExperienceData.setProjectTitle(matcher.group(3).trim());
    				}
    			}
    			
    			projectExperienceDataList.add(projectExperienceData);
        		currentNum ++;
        	}else{
        		if(line.indexOf("软件环境:") >= 0){
        			if(line.endsWith("软件环境:")){
        				projectExperienceDataList.get(currentNum-1).setSoftwareEnvir(resumeContentList.get(i+1));
        				i++;
        			}else{
        				pattern = Pattern.compile("软件环境:(.*)");
        				matcher = pattern.matcher(line);
        				if(matcher.find()){
        					projectExperienceDataList.get(currentNum-1).setSoftwareEnvir(matcher.group(1));
        				}
        			}
        		}else if(line.indexOf("硬件环境:") >= 0){
        			if(line.endsWith("硬件环境:")){
        				projectExperienceDataList.get(currentNum-1).setHardEnvir(resumeContentList.get(i+1));
        				i++;
        			}else{
        				pattern = Pattern.compile("硬件环境:(.*)");
        				matcher = pattern.matcher(line);
        				if(matcher.find()){
        					projectExperienceDataList.get(currentNum-1).setHardEnvir(matcher.group(1));
        				}
        			}
        		}else if(line.indexOf("开发工具:") >= 0){
        			if(line.endsWith("开发工具:")){
        				projectExperienceDataList.get(currentNum-1).setDevelopTool(resumeContentList.get(i+1));
        				i++;
        			}else{
        				pattern = Pattern.compile("开发工具:(.*)");
        				matcher = pattern.matcher(line);
        				if(matcher.find()){
        					projectExperienceDataList.get(currentNum-1).setDevelopTool(matcher.group(1));
        				}
        			}
        		}else if(line.indexOf("项目描述:") >= 0){
        			if(line.endsWith("项目描述:")){
        				projectDesc += "\r\n" + line;
        				projectExperienceDataList.get(currentNum-1).setResponsibleFor(responsibleFor);
        				if(i+2 > end){
        					projectExperienceDataList.get(currentNum-1).setProjectDesc(resumeContentList.get(i+1).trim());
        				}
        				preTitle = "项目描述";
        				i++;
        			}else{
        				pattern = Pattern.compile("项目描述:(.*)");
        				matcher = pattern.matcher(line);
        				if(matcher.find()){
        					projectExperienceDataList.get(currentNum-1).setProjectDesc(matcher.group(1));
        				}
        			}
        		}else if(line.indexOf("项目职责:") >= 0 || line.indexOf("责任描述:") >= 0){
        			if(line.endsWith("项目职责:") || line.endsWith("责任描述:")){
        				responsibleFor += "\r\n" + resumeContentList.get(i+1);
        				if(i+2 > end){
        					projectExperienceDataList.get(currentNum-1).setResponsibleFor(responsibleFor);
        				}
        				preTitle = "项目职责";
        				i++;
        			}else{
        				pattern = Pattern.compile("(项目职责|责任描述):(.*)");
        				matcher = pattern.matcher(line);
        				if(matcher.find()){
        					projectExperienceDataList.get(currentNum-1).setResponsibleFor(matcher.group(1));
        				}
        			}
        		}
        	}
		}	
		return projectExperienceDataList;
	}

	@Override
	public ArrayList<ProfessionalSkillData> extractProfessionalSkill(int start,int end) {
		// TODO Auto-generated method stub
		ArrayList<ProfessionalSkillData> professionalSkillDataList = new ArrayList<ProfessionalSkillData>();
		
		for(int i = start;i <= end; i++){
			String line = resumeContentList.get(i);
			if(line.equals("")){
				continue;
			}else{
				Pattern pattern=Pattern.compile("(.*?)(:|\\s)(一般|良好|熟练|精通).*?(\\d+)",Pattern.CASE_INSENSITIVE);
		        Matcher matcher=pattern.matcher(line);
		        while(matcher.find()){
		        	ProfessionalSkillData professionalSkillData = new ProfessionalSkillData();
		        	
		        	professionalSkillData.setSkillDesc(matcher.group(1).trim());
		        	professionalSkillData.setProficiency(matcher.group(3).trim());
		        	professionalSkillData.setMonths(matcher.group(4).trim());
		        	
		        	professionalSkillDataList.add(professionalSkillData);
		        }
			}
		}
		return professionalSkillDataList;
	}

	@Override
	public ArrayList<PracticalExperienceData> extractPracticalExperience(int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<OtherInfoData> extractOtherInfo(int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getResumeKeywords(int start,int end) {
		// TODO Auto-generated method stub
		return null;
	}

	public ZhiLianResumeParser(ResumeMessage obj){	

	}
	public ZhiLianResumeParser(String html,String url,String objectid){	

	}

	public ZhiLianResumeParser(String filePath){
		this.filePath = filePath;
	}
	
	public ZhiLianResumeParser(ArrayList<String> resumeDataList){
		this.resumeContentList = resumeDataList;
	}

	

	public void init(){
		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
		ArrayList<ProjectExperienceData> projectExperienceDataList = new ArrayList<ProjectExperienceData>();
		ArrayList<EducationExperienceData> educationExperienceDataList = new ArrayList<EducationExperienceData>();
		ArrayList<ProfessionalSkillData> professionalSkillDataList = new ArrayList<ProfessionalSkillData>();	

		resumedata.setWorkExperience(workExperienceDataList);
		resumedata.setProjectExperience(projectExperienceDataList);
		resumedata.setEducationExperience(educationExperienceDataList);
		resumedata.setProfessionalSkill(professionalSkillDataList);
	}



	public void processContactInfo(int start,int end){
		for(int i = start;i <= end; i++){
			String line = resumeContentList.get(i);
			if(line.equals("")){
				continue;
			}else{
				Matcher mat = Pattern.compile("ID:(.*)").matcher(line);
        		if (mat.find()){
        			String t = mat.group(1);
        			resumedata.setSourceID(t);
        			resumedata.setSource("zhilian");
        		}
        		
        		mat = Pattern.compile("(男|女)").matcher(line);
        		if(mat.find()){
        			if(this.hasBuy){
        				if(this.type.equals("recruiterDownload")){
        					if(resumedata.getName() == null){
        						resumedata.setName(resumeContentList.get(i-1));
        					}
        				}else{
        					if(resumedata.getName() == null){
        						resumedata.setName(resumeContentList.get(i-1));
        					}
        				}
        			}
        			resumedata.setGender(mat.group(1).trim()); 
        		}
        		
        		mat = Pattern.compile("(\\d+)岁").matcher(line);
        		if(mat.find()){
		        	resumedata.setAge(Integer.parseInt(mat.group(1).trim()));
        		}
        		
        		mat = Pattern.compile("(\\d+\\s{0,2}年s{0,2}\\d+s{0,2}月)").matcher(line);
        		if(mat.find()){
        			resumedata.setBirthday(mat.group(1).trim());
        		}
        		
        		mat = Pattern.compile("(\\d+)年工作经验").matcher(line);
        		if(mat.find()){
        			resumedata.setWorkExperienceLength(Integer.parseInt(mat.group(1)));
        		}
        		
        		mat = Pattern.compile("(本科|硕士|博士|MBA|EMBA|大专|中专|高中|初中|中技|本科|硕士|博士|MBA|EMBA|大专|中专|高中|初中|中技|其他)").matcher(line);
        		if(mat.find()){
        			resumedata.setLatestDegree(mat.group(1));
        		}
        		
        		mat = Pattern.compile("(已婚|未婚)").matcher(line);
        		if(mat.find()){
        			resumedata.setMaritalStatus(mat.group(1));
        		}
        		
        		mat = Pattern.compile("现居住地(:|:)(.*?)[|]").matcher(line);
        		if(mat.find()){
        			resumedata.setAddress(mat.group(2).trim());
        		}
        		
        		mat = Pattern.compile("现居住于(.*)").matcher(line);
        		if(mat.find()){
        			resumedata.setAddress(mat.group(1).trim());
        		}
        		
        		mat = Pattern.compile("地址(:|:)(.*?)(\\s|\\||$)").matcher(line);
        		if(mat.find()){
        			resumedata.setResidence(mat.group(2).trim());
        		}
        		
        		mat = Pattern.compile("E-mail(:|:)(.*?@.*?)(\\s|\\||$)").matcher(line);
        		if(mat.find()){
        			resumedata.setEmail(mat.group(2).trim());
        		}
        		
        		mat=Pattern.compile("户口(:|:)(.*?)(\\s|$)",Pattern.CASE_INSENSITIVE).matcher(line);
		        if(mat.find()){
		        	resumedata.setHouseHolds(mat.group(2).trim());
		        }
		        
		        mat=Pattern.compile("(党员|团员|群众|民主党派|无党派人士)",Pattern.CASE_INSENSITIVE).matcher(line);
		        if(mat.find()){
		        	resumedata.setPoliticalLandscape(mat.group(1).trim());
		        }
		        
		        mat = Pattern.compile("身份证(:|:)(.*)").matcher(line);
        		if(mat.find()){
        			resumedata.setIdentityID(mat.group(2));
        		}
        		
        		mat = Pattern.compile("邮编(:|:)(\\d+)").matcher(line);
        		if(mat.find()){
        			resumedata.setZipCode(mat.group(2));
        		}
        		
        		mat = Pattern.compile("手机").matcher(line);
        		if(mat.find()){
        			mat = Pattern.compile("(\\d+)").matcher(line);
        			if(mat.find()){
        				if(this.hasBuy){
            				if(this.type.equals("recruiterDownload")){
            					if(resumedata.getName() == null){
            						resumedata.setName(resumeContentList.get(i-1));
            					}
            				}
            			}
        				resumedata.setPhone(mat.group(1));
        			}
        		}
        		
			}
		}
	}
		
	public void processBasicInfo(int start,int end){
		for(int i = start;i <= end;i++){
			String line = resumeContentList.get(i);
			if(line.equals("")){
				continue;
			}else{
				if(line.equals("简历更新时间:")){
					resumedata.setUpdateTime(resumeContentList.get(i+1).replaceAll("\\.", "-"));
					i ++;
				}else if(line.equals("﻿简历名称:")){
					resumedata.setResumeKeyWord(resumeContentList.get(i+1));
				}
			}
		}
	}

//	public void preProcess(){
//		File file = new File(this.filePath);
//		BufferedReader reader = null;
//        try {
//            System.out.println("以行为单位读取文件内容，一次读一整行:");
//            reader = new BufferedReader(new FileReader(file));
//            String line = "";
//            int lineNum = 0;
//            String currentSection = "basicInfo";
//            SectionInfo sectionInfo = new SectionInfo(0,0);
//            sectionMap.put(currentSection, sectionInfo);
//            // 一次读入一行，直到读入null为文件结束
//
//            while ((line = reader.readLine()) != null) {
//            	line = line.replaceAll("\u00A0"," " ).trim();
//            	line.replaceAll(" ", "");
//            	resumeContentList.add(line);
//            	if(line.equals("个人信息") || Pattern.compile("ID:(.*)").matcher(line).find()){
//              		if(!sectionMap.containsKey("contactInfo")){
//            			currentSection = "contactInfo";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}            		
//
//            	}else if(line.equals("自我评价")){         		
//
//            		if(!sectionMap.containsKey("selfEvaluation")){
//            			currentSection = "selfEvaluation";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//
//            	}else if(line.equals("求职意向")){
//            		if(!sectionMap.containsKey("jobTarget")){
//            			currentSection = "jobTarget";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//
//            	}else if(line.equals("工作经历")){    		
//
//            		if(!sectionMap.containsKey("workExperience")){
//            			currentSection = "workExperience";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//
//            	}else if(line.equals("项目经历")){
//            		if(!sectionMap.containsKey("projectExperience")){
//            			currentSection = "projectExperience";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//            	}else if(line.equals("教育经历")){
//            		if(!sectionMap.containsKey("educationExperience")){
//            			currentSection = "educationExperience";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//            	}else if(line.equals("语言能力")){
//            		if(!sectionMap.containsKey("languageSkill")){
//            			currentSection = "languageSkill";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//
//            	}else if(line.equals("专业技能")){   
//            		if(!sectionMap.containsKey("professionalSkill")){
//            			currentSection = "professionalSkill";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//
//            	}else{
//            		sectionMap.get(currentSection).setEnd(lineNum);
//            	}
//            	lineNum ++;
//
//            }
//
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                }
//            }
//        }
//	}

	public void preProcess(){
		if(resumeContentList==null){
			return ;
		}else{
			for (int i = 0; i < resumeContentList.size(); i++) {
				String line = resumeContentList.get(i).trim();
				if("".equals(line)){
					resumeContentList.remove(i);
					i--;
				}else{
					if(line.indexOf("如需联系方式请下载该简历")>=0){
						this.hasBuy = false;
					}
				}
			}
			String firstLine = resumeContentList.get(0).trim();
			if(firstLine.equals("﻿简历名称:")){
				this.type = "recruiterDownload";
			}
			
			int lineNum = 0;
			String line = "";
			String currentSection = "basicInfo";
            SectionInfo sectionInfo = new SectionInfo(0,0);
            sectionMap.put(currentSection, sectionInfo);
			for(int i=0;i<resumeContentList.size();i++){
				line = resumeContentList.get(i);
				
				line = line.replaceAll("\u00A0"," " ).trim();
            	line.replaceAll(" ", "");
            	if(line.equals("自我评价")){         		
            		if(!sectionMap.containsKey("selfEvaluation")){
            			currentSection = "selfEvaluation";
                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
                		sectionMap.put(currentSection, contactInfo);
            		}

            	}else if(line.equals("求职意向")){
            		if(!sectionMap.containsKey("jobTarget")){
            			currentSection = "jobTarget";
                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
                		sectionMap.put(currentSection, contactInfo);
            		}

            	}else if(line.equals("工作经历") || line.equals("工作经验")){    		

            		if(!sectionMap.containsKey("workExperience")){
            			currentSection = "workExperience";
                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
                		sectionMap.put(currentSection, contactInfo);
            		}

            	}else if(line.equals("项目经验") || line.equals("项目经历")){
            		if(!sectionMap.containsKey("projectExperience")){
            			currentSection = "projectExperience";
                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
                		sectionMap.put(currentSection, contactInfo);
            		}
            	}else if(line.equals("教育经历")){
            		if(!sectionMap.containsKey("educationExperience")){
            			currentSection = "educationExperience";
                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
                		sectionMap.put(currentSection, contactInfo);
            		}
            	}else if (line.equals("培训经历")) {
					if (!sectionMap.containsKey("trainExperience")) {
						currentSection = "trainExperience";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}

				}else if(line.equals("语言能力")){
            		if(!sectionMap.containsKey("languageSkill")){
            			currentSection = "languageSkill";
                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
                		sectionMap.put(currentSection, contactInfo);
            		}

            	}else if(line.equals("在校学习情况")){   
            		if(!sectionMap.containsKey("schoolLearning")){
            			currentSection = "schoolLearning";
                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
                		sectionMap.put(currentSection, contactInfo);
            		}

            	}else if(line.equals("证书")){   
            		if(!sectionMap.containsKey("certificate")){
            			currentSection = "certificate";
                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
                		sectionMap.put(currentSection, contactInfo);
            		}

            	}else if(line.equals("专业技能")){   
            		if(!sectionMap.containsKey("professionalSkill")){
            			currentSection = "professionalSkill";
                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
                		sectionMap.put(currentSection, contactInfo);
            		}

            	}else{
            		sectionMap.get(currentSection).setEnd(lineNum);
            	}
            	lineNum ++;
            	}
			}
		}
}

