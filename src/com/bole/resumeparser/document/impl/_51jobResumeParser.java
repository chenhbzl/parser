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

import com.bole.config.Status;
import com.bole.resumeparser.document.impl.AbstractResumeParser.SectionInfo;
import com.bole.resumeparser.exception.ResumeParseException;
import com.bole.resumeparser.exception.html.DBConnectException;
import com.bole.resumeparser.message.ResumePreProcessInfo;
import com.bole.resumeparser.models.CertificateData;
import com.bole.resumeparser.models.EducationExperienceData;
import com.bole.resumeparser.models.JobTarget;
import com.bole.resumeparser.models.LanguageSkillData;
import com.bole.resumeparser.models.OtherInfoData;
import com.bole.resumeparser.models.PracticalExperienceData;
import com.bole.resumeparser.models.ProfessionalSkillData;
import com.bole.resumeparser.models.ProjectExperienceData;
import com.bole.resumeparser.models.ResumeData;
import com.bole.resumeparser.models.ResumeInfo;
import com.bole.resumeparser.models.TextResumeData;
import com.bole.resumeparser.models.TrainingExperienceData;
import com.bole.resumeparser.models.WorkExperienceData;
import com.bole.resumeparser.service.ResumePreProcessFactory;

public class _51jobResumeParser extends AbstractResumeParser implements DocumentResumeParserInterface{

	//同时将简历内容读取到list中
	public String filePath = "";
	TextResumeData resumedata = new TextResumeData();
//	ResumeData resumedata = new ResumeData();
	
	String type = "";  //求职者自己下载的doc简历还是公司下载的简历
	boolean hasBuy = true; //是否买过该简历
	
	//同时将简历内容读取到list中 
	HashMap<String,SectionInfo> sectionMap = new HashMap<String,SectionInfo>();
	ArrayList<String> resumeContentList = new ArrayList<String>();
	ArrayList<String> processedContentList = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException, InterruptedException {		// TODO Auto-generated method stub
//		String	SOFFICE_PATH	= "/usr/bin/soffice";
//		File dir = new File("/home/liyao/mnt/51job2.docx").getParentFile();
//		Process process =
//		        Runtime.getRuntime().exec(
//		                new String[] { SOFFICE_PATH, "--headless", "--convert-to", "txt:Text", "-outdir",
//		                        dir.getAbsolutePath(), "/home/liyao/mnt/51job2.docx" });
//		System.out.println(dir.getAbsolutePath());
//		int value = process.waitFor();
//		if (value == 0) {		
//		}
//		_51jobResumeParser t = new _51jobResumeParser("/home/liyao/mnt/19d2.txt");		
//		ResumeData resumeData = t.parse();
//		t.init();
		String filePath = "/home/bobby/mnt/邮箱同步的简历/简历模板/resume3/解析/聘宝简历_尹继旺.docx";
		ResumePreProcessFactory resumePreProcessFactory = new ResumePreProcessFactory();
		ResumeInfo resumeInfo = null;
		try {
			resumeInfo = resumePreProcessFactory.preProcess(filePath);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DocumentResumeParserInterface parser = null;
		TextResumeData re = new TextResumeData();
        if(resumeInfo == null){
        	return ;
        }else{
        	String source = resumeInfo.getResumeSource();
        	source = "51job";
        	ArrayList<String> resumeContentList = resumeInfo.getSourceDataList();
        	if(Status.ZHILIAN.equals(source)){
        		parser = new ZhiLianResumeParser(resumeContentList);
        	}else if(Status._51JOB.equals(source)){
        		parser = new _51jobResumeParser(resumeContentList);
        	}else {
        		parser = new TextResumeParser(resumeContentList);
        	}
        	
        	try {
				re=parser.parse();
			} catch (ResumeParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	//解析成功 写入数据表，并更新collectedResume的status字段
        	int a = 3;
        }
	}
	
	@Override
	public void extractContactInfo(int start,int end) {
		// TODO Auto-generated method stub
	}

	@Override
	public String removeHtmlTag(String htmlStr) {
		// TODO Auto-generated method stub
		return super.removeHtmlTag(htmlStr);
	}

	@Override
	public TextResumeData parse() throws ResumeParseException{
		// TODO Auto-generated method stub
		this.init();
		this.preProcess();
        
        Iterator iter = sectionMap.entrySet().iterator();
        while (iter.hasNext()) {
        	Map.Entry entry = (Map.Entry) iter.next();
        	Object key = entry.getKey();
        	SectionInfo val = (SectionInfo)entry.getValue();
        	System.out.println(key + ":" +val.getStart() +","+val.getEnd());
        	if(key.equals("basicInfo")){
        		this.processBasicInfo(val.getStart(), val.getEnd());
        	}else if(key.equals("contactInfo")){
        		if(this.type.equals("recruiterDownload")){
        			this.processContactInfo(val.getStart(), val.getEnd(), "");
        		}else{
        			this.processContactInfo(val.getStart(), val.getEnd());
        		}
        	}else if(key.equals("recentWork")){
        		this.processCurrentWork(val.getStart(), val.getEnd());
        	}else if(key.equals("higestDegree")){
        		this.processHighestEducation(val.getStart(), val.getEnd());
        	}else if(key.equals("workExperience")){
        		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
        		workExperienceDataList = this.extractWorkExperience(val.getStart(), val.getEnd());
        		resumedata.setWorkExperience(workExperienceDataList);
        	}else if(key.equals("projectExperience")){
        		ArrayList<ProjectExperienceData> projectExperienceDataList = new ArrayList<ProjectExperienceData>();
        		projectExperienceDataList = this.extractProjectExperience(val.getStart(), val.getEnd());
        		resumedata.setProjectExperience(projectExperienceDataList);
        	}else if(key.equals("languageSkill")){
        		ArrayList<LanguageSkillData> languageSkillDataList = this.extractLanguageSkill(val.getStart(), val.getEnd());
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
        		if(this.type.equals("recruiterDownload")){
        			jobTarget = this.extractJobTarget(val.getStart(),val.getEnd(),"");
        		}else{
        			jobTarget = this.extractJobTarget(val.getStart(),val.getEnd());
        		}
        		resumedata.setJobTarget(jobTarget);
        	}else if(key.equals("professionalSkill")){
        		ArrayList<ProfessionalSkillData> professionalSkillDataList = new ArrayList<ProfessionalSkillData>();
        		professionalSkillDataList = this.extractProfessionalSkill(val.getStart(), val.getEnd());
        		resumedata.setProfessionalSkill(professionalSkillDataList);
        	}else if(key.equals("trainExperience")){
        		ArrayList<TrainingExperienceData> trainingExperienceDataList = this.extractTrainingExperience(val.getStart(), val.getEnd());
        		resumedata.setTrainingExperience(trainingExperienceDataList);
        	}
        }
        resumedata.setCreateTime(new Date());
//        if(resumedata.getPhone() != null && resumedata.getEmail() != null){
//        	resumedata.setIsCotactInformation("YES");
//        }
		return resumedata;
	}
	
//	public ResumeData parse1(int a) throws ResumeParseException{
//		// TODO Auto-generated method stub
//		this.init();
//		this.preProcess();
//        
//        Iterator iter = sectionMap.entrySet().iterator();
//        while (iter.hasNext()) {
//        	Map.Entry entry = (Map.Entry) iter.next();
//        	Object key = entry.getKey();
//        	SectionInfo val = (SectionInfo)entry.getValue();
//        	System.out.println(key + ":" +val.getStart() +","+val.getEnd());
//        	if(key.equals("basicInfo")){
//        		this.processBasicInfo(val.getStart(), val.getEnd());
//        	}else if(key.equals("contactInfo")){
//        		if(this.type.equals("recruiterDownload")){
//        			this.processContactInfo(val.getStart(), val.getEnd(), "");
//        		}else{
//        			this.processContactInfo(val.getStart(), val.getEnd());
//        		}
//        	}else if(key.equals("recentWork")){
//        		this.processCurrentWork(val.getStart(), val.getEnd());
//        	}else if(key.equals("higestDegree")){
//        		this.processHighestEducation(val.getStart(), val.getEnd());
//        	}else if(key.equals("workExperience")){
//        		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
//        		workExperienceDataList = this.extractWorkExperience(val.getStart(), val.getEnd());
//        		resumedata.setWorkExperience(workExperienceDataList);
//        	}else if(key.equals("projectExperience")){
//        		ArrayList<ProjectExperienceData> projectExperienceDataList = new ArrayList<ProjectExperienceData>();
//        		projectExperienceDataList = this.extractProjectExperience(val.getStart(), val.getEnd());
//        		resumedata.setProjectExperience(projectExperienceDataList);
//        	}else if(key.equals("languageSkill")){
//        		ArrayList<LanguageSkillData> languageSkillDataList = this.extractLanguageSkill(val.getStart(), val.getEnd());
//        		resumedata.setLanguageSkill(languageSkillDataList);
//        	}else if(key.equals("educationExperience")){
//        		ArrayList<EducationExperienceData> educationExperienceDataList = new ArrayList<EducationExperienceData>();
//        		educationExperienceDataList = this.extractEducationExperience(val.getStart(), val.getEnd());
//        		resumedata.setEducationExperience(educationExperienceDataList);
//        	}else if(key.equals("selfEvaluation")){
//        		String selfEvaluation = this.extractSelfEvaluation(val.getStart(), val.getEnd());
//        		resumedata.setSelfEvaluation(selfEvaluation);
//        	}else if(key.equals("jobTarget")){
//        		JobTarget jobTarget = new JobTarget();
//        		if(this.type.equals("recruiterDownload")){
//        			jobTarget = this.extractJobTarget(val.getStart(),val.getEnd(),"");
//        		}else{
//        			jobTarget = this.extractJobTarget(val.getStart(),val.getEnd());
//        		}
//        		resumedata.setJobTarget(jobTarget);
//        	}else if(key.equals("professionalSkill")){
//        		ArrayList<ProfessionalSkillData> professionalSkillDataList = new ArrayList<ProfessionalSkillData>();
//        		professionalSkillDataList = this.extractProfessionalSkill(val.getStart(), val.getEnd());
//        		resumedata.setProfessionalSkill(professionalSkillDataList);
//        	}else if(key.equals("trainExperience")){
//        		ArrayList<TrainingExperienceData> trainingExperienceDataList = this.extractTrainingExperience(val.getStart(), val.getEnd());
//        		resumedata.setTrainingExperience(trainingExperienceDataList);
//        	}
//        }
//        resumedata.setCreateTime(new Date());
////        if(resumedata.getPhone() != null && resumedata.getEmail() != null){
////        	resumedata.setIsCotactInformation("YES");
////        }
//		return resumedata;
//	}

	@Override
	public String getSourceID(String html) {
		return super.getSourceID(html);
	}

	@Override
	public String getWebsite() {
		return super.getWebsite();
	}

	@Override
	public String getUpdateTime() {
		return super.getUpdateTime();
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public String getPhone() {
		return super.getPhone();
	}

	@Override
	public String getEmail() {
		return super.getEmail();
	}

	@Override
	public String getAge() {
		return super.getAge();
	}

	@Override
	public String Gender() {
		return super.Gender();
	}

	@Override
	public String extractSelfEvaluation(int start,int end) {
		String selfEvaluation = "";
		String line = "";
		for(int i = start;i <= end;i++){
			line = resumeContentList.get(i);
			if(line.equals("")){
				continue;
			}else{
				if(line.equals("自我评价")){
					continue;
				}else{
					if(selfEvaluation.equals("")){
						selfEvaluation = line;
					}else{
						selfEvaluation += "\r\n" + line;
					}
				}
				
			}
		}
		return selfEvaluation;
	}

	@Override
	public JobTarget extractJobTarget(int start,int end) {
		String title = "";
		String desc = "";
		JobTarget jobTarget = new JobTarget();
		for(int i=start; i <= end;i++){
			title = resumeContentList.get(i).replaceAll(" ", "").trim();
			desc = resumeContentList.get(i+1).replaceAll(" ", "").trim();
			if(title.equals("")){
				continue;
			}else{
				if(title.indexOf("时间") >= 0){
					jobTarget.setEnrollTime(desc);
				}else if(title.indexOf("性质") >= 0){
					jobTarget.setJobCatagory(desc);
				}else if(title.indexOf("行业") >= 0){
					jobTarget.setJobIndustry(desc);
				}else if(title.indexOf("地点") >= 0){
					jobTarget.setJobLocation(desc);
				}else if(title.indexOf("月薪") >= 0 || title.indexOf("薪水") >= 0){
					jobTarget.setSalary(desc);
				}else if(title.indexOf("职能") >= 0 || title.indexOf("职业") >= 0){
					jobTarget.setJobCareer(desc);
				}
			}
		}
		return jobTarget;
	}

	
	public JobTarget extractJobTarget(int start,int end,String type) {
		String title = "";
		String desc = "";
		JobTarget jobTarget = new JobTarget();
		for(int i=start; i <= end;i++){
			title = resumeContentList.get(i).replaceAll(" ", "").trim();
			desc = resumeContentList.get(i+1).replaceAll(" ", "").trim();
			if(title.equals("")){
				continue;
			}else{
				if(title.indexOf(":") >= 0){
					String [] arr = title.split(":");
					if(arr.length == 2){
						title = arr[0];
						desc = arr[1];
						
						if(title.indexOf("时间") >= 0){
							jobTarget.setEnrollTime(desc);
						}else if(title.indexOf("性质") >= 0){
							jobTarget.setJobCatagory(desc);
						}else if(title.indexOf("行业") >= 0){
							jobTarget.setJobIndustry(desc);
						}else if(title.indexOf("地点") >= 0){
							jobTarget.setJobLocation(desc);
						}else if(title.indexOf("月薪") >= 0 || title.indexOf("薪水") >= 0){
							jobTarget.setSalary(desc);
						}else if(title.indexOf("职能") >= 0 || title.indexOf("职业") >= 0){
							jobTarget.setJobCareer(desc);
						}
					}
				}
			}
		}
		return jobTarget;
	}
	
	@Override
	public String getResumeKeywords(int start,int end) {
		return super.getResumeKeywords(start,end);
	}

	@Override
	public ArrayList<WorkExperienceData> extractWorkExperience(int start,int end) {
		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
		
		String line = "";
		int currentNum = 0;
		
		String jobDesc = "";
		for(int i = start; i <= end ;i++){
			line = resumeContentList.get(i);
			line = line.replaceAll(" ", "").trim();
			if(i == end){
				jobDesc += line;
				((WorkExperienceData)(workExperienceDataList.get(currentNum-1))).setJobDesc(jobDesc);
			}
			if(line.equals("") || line.equals("工作经验")){
				continue;
			}else{
				Pattern pattern = Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+):(.*)");
		        Matcher matcher = pattern.matcher(line);
		        if(matcher.find()){
		        	if(currentNum >= 1){
		        		((WorkExperienceData)(workExperienceDataList.get(currentNum-1))).setJobDesc(jobDesc);
		        		jobDesc = "";
		        	}
		        	
		        	currentNum++;
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
		        }else{
		        	if("所属行业:".equals(line)){
		        		workExperienceDataList.get(currentNum-1).setIndustryCatagory(resumeContentList.get(i+1));
		        		String positionTitleTxt = resumeContentList.get(i+2);
		        		if(positionTitleTxt.split(" ").length == 2){
		        			workExperienceDataList.get(currentNum-1).setDepartment(positionTitleTxt.split(" ")[0]);
		        			workExperienceDataList.get(currentNum-1).setPositionTitle(positionTitleTxt.split(" ")[1]);
		        			i +=2;
		        		}else if(positionTitleTxt.split(" ").length == 1){
		        			String depart = resumeContentList.get(i+2);
		        			String positionTitle = resumeContentList.get(i+3);
		        			workExperienceDataList.get(currentNum-1).setDepartment(depart);
		        			workExperienceDataList.get(currentNum-1).setPositionTitle(positionTitle);
		        			i +=3;
		        		}
		        		
		        	}else{
		        		jobDesc += "\r\n" + line;
		        	}
		        }
			}
		}
		return workExperienceDataList;
	}

	@Override
	public ArrayList<EducationExperienceData> extractEducationExperience(
			int start,int end) {
		ArrayList<EducationExperienceData> educationExperienceDataList = new ArrayList<EducationExperienceData>();
		String line = "";
		
		for(int i = start;i <= end; i++){			
			line = resumeContentList.get(i);
			line = line.replaceAll(" ", "").trim();
			
			if(line.equals("")){
				continue;
			}else{
				if(Pattern.compile("(\\d+/\\d+).*?(至今|\\d+/\\d+)").matcher(line).find()){
					Matcher mat = Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+)").matcher(line);
			        if(mat.find()){
			        	
			        	EducationExperienceData educationExperienceData = new EducationExperienceData();
			        	
			        	educationExperienceData.setStartTime(mat.group(1).replaceAll("/", "-"));
			        	educationExperienceData.setEndTime(mat.group(2).replaceAll("/", "-"));
			        	educationExperienceData.setSchool(resumeContentList.get(i+1));
			        	educationExperienceData.setMajor(resumeContentList.get(i+2));
			        	educationExperienceData.setDegree(resumeContentList.get(i+3));
			        	
			        	i += 3;
			        	
			        	educationExperienceDataList.add(educationExperienceData);
			        }
				}
			}
		}
		return educationExperienceDataList;
	}

	@Override
	public ArrayList<TrainingExperienceData> extractTrainingExperience(
			int start,int end) {
		ArrayList<TrainingExperienceData> trainingExperienceDataList = new ArrayList<TrainingExperienceData>();
		String line = "";
		for(int i = start; i <= end ;i++){
			line = resumeContentList.get(i).trim().replaceAll(" ", "");
			Pattern pattern = Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+)");
	        Matcher matcher = pattern.matcher(line);
	        if(matcher.find()){
	        	TrainingExperienceData trainingExperienceData = new TrainingExperienceData();
				String instituation = resumeContentList.get(i+1);
				String course = resumeContentList.get(i+2);
				String certificate = resumeContentList.get(i+3);
				
				i += 3;
				
				trainingExperienceData.setStartTime(matcher.group(1).replaceAll("/", "-"));
				trainingExperienceData.setEndTime(matcher.group(2).replaceAll("/", "-"));
				trainingExperienceData.setInstituation(instituation);
				trainingExperienceData.setCourse(course);
				trainingExperienceData.setCertificate(certificate);
				
				trainingExperienceDataList.add(trainingExperienceData);
	        }
		}
		
		return trainingExperienceDataList;
	}

	@Override
	public ArrayList<LanguageSkillData> extractLanguageSkill(int start,int end) {
		ArrayList<LanguageSkillData> languageSkillDataList = new ArrayList<LanguageSkillData>();
		String line = "";
		String desc = "";
		for(int i = start; i <= end ;i++){
			line = resumeContentList.get(i).trim().replaceAll(" ", "");
			if(i<end){
				desc = resumeContentList.get(i+1).trim().replaceAll(" ", "");
			}
			
			if(line.endsWith(":")){
				LanguageSkillData languageSkillData = new LanguageSkillData();
				
				Pattern pattern=Pattern.compile("(.*)（(.*?)）");
		        Matcher matcher=pattern.matcher(desc);
		        if(matcher.find()){		        	
		        	String language = matcher.group(1);
		        	String level = matcher.group(2);
		        	languageSkillData.setCatagory(language);
		        	languageSkillData.setLevel(level);
		        	
		        	line = line.substring(0,line.indexOf("（"));
					
					languageSkillData.setCatagory(line);
					
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
					languageSkillData.setCatagory(line);
					languageSkillData.setLevel(desc);
					languageSkillDataList.add(languageSkillData);
		        }
		        i++;
			}
		}
		
		return languageSkillDataList;
	}

	@Override
	public ArrayList<CertificateData> extractCertficate(int start,int end) {
		return super.extractCertficate(start,end);
	}

	@Override
	public ArrayList<ProjectExperienceData> extractProjectExperience(int start,int end) {
		// TODO Auto-generated method stub
		ArrayList<ProjectExperienceData> projectExperienceDataList = new ArrayList<ProjectExperienceData>();
		
		int currentNum = 0;
		String line = "";
		String projectDesc = "";
		String responsibleFor = "";
		String preTitle = "";
		
		for(int i = start; i <= end ;i++){
			line = resumeContentList.get(i).trim().replaceAll(" ", "");
			if(i == end){
				if(preTitle.equals("责任描述")){
					projectExperienceDataList.get(currentNum-1).setResponsibleFor(responsibleFor);
				}else if(preTitle.equals("项目描述")){
					projectExperienceDataList.get(currentNum-1).setProjectDesc(projectDesc);
				}
			}
			if(line.equals("")){
				continue;
			}else{
				if(Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+):(.*)").matcher(line).find()){
					Matcher mat = Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+):(.*)").matcher(line);
			        if(mat.find()){
			        	if(currentNum >= 1){
			        		projectExperienceDataList.get(currentNum-1).setResponsibleFor(responsibleFor);
			        		projectExperienceDataList.get(currentNum-1).setProjectDesc(projectDesc);
			        	}
			        	
			        	projectDesc = "";
			    		responsibleFor = "";
			    		preTitle = "";
			    		
			        	currentNum ++;
			        	ProjectExperienceData projectExperienceData = new ProjectExperienceData();
			        	
			        	projectExperienceData.setStartTime(mat.group(1).replaceAll("/", "-"));	        	
			        	projectExperienceData.setEndTime(mat.group(2).replaceAll("/", "-"));
			        	projectExperienceData.setProjectTitle(mat.group(3));
			        	
			        	projectExperienceDataList.add(projectExperienceData);
			        }
				}else{
					if(line.equals("开发工具:")){
						projectExperienceDataList.get(currentNum-1).setDevelopTool(resumeContentList.get(i+1));
					}else if(line.equals("软件环境:")){
						projectExperienceDataList.get(currentNum-1).setSoftwareEnvir(resumeContentList.get(i+1));
					}else if(line.equals("硬件环境:")){
						projectExperienceDataList.get(currentNum-1).setHardEnvir(resumeContentList.get(i+1));
					}else if(line.equals("责任描述:")){
						if(preTitle.equals("项目描述")){
							projectExperienceDataList.get(currentNum-1).setProjectDesc(projectDesc);
						}
						preTitle = "责任描述";
						responsibleFor += resumeContentList.get(i+1);
						i++;
						if(i+1 > end){
							projectExperienceDataList.get(currentNum-1).setResponsibleFor(responsibleFor);
						}
					}else if(line.equals("项目描述:")){
						if(preTitle.equals("责任描述")){
							projectExperienceDataList.get(currentNum-1).setResponsibleFor(responsibleFor);
						}
						preTitle = "项目描述";
						projectDesc += resumeContentList.get(i+1);
						i++;
					}else{
						if(preTitle.equals("责任描述")){
							responsibleFor += line;
						}else if(preTitle.equals("项目描述")){
							projectDesc += line;
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
		
		String content = "";
		String line = "";
		for(int i = start; i <= end ;i++){
			line = resumeContentList.get(i);
			if(line.indexOf("使用时间") < 0 && content.equals("")){
				continue;
			}else{
				if(content.equals("")){
					content += resumeContentList.get(i+1);
					i++;
				}else{
					content += line;
				}
				
			}
		}
		Pattern pattern = Pattern.compile("(.*?)(精通|熟练|了解|一般)(\\d+)(月)?");
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()){
			ProfessionalSkillData professionalSkillData = new ProfessionalSkillData();
			
			String desc = matcher.group(1);
			String proficiency = matcher.group(2);
			String months = matcher.group(3);
			
			professionalSkillData.setSkillDesc(desc);
			professionalSkillData.setProficiency(proficiency);
			professionalSkillData.setMonths(months);
			
			professionalSkillDataList.add(professionalSkillData);
		}
		
		return professionalSkillDataList;
	}

	@Override
	public ArrayList<PracticalExperienceData> extractPracticalExperience(
			int start,int end) {
		// TODO Auto-generated method stub
		return super.extractPracticalExperience(start,end);
	}

	@Override
	public ArrayList<OtherInfoData> extractOtherInfo(int start,int end) {
		// TODO Auto-generated method stub
		return super.extractOtherInfo(start,end);
	}
	
	public _51jobResumeParser(String filePath){
		this.filePath = filePath;
	}
	
	public _51jobResumeParser(ArrayList<String> resumeDataList){
		this.resumeContentList = resumeDataList;
	}
	
	public void processHighestEducation(int start, int end){
		for(int i = start; i <= end ;i++){
			String line = resumeContentList.get(i).trim().replaceAll(" ", "");
			String desc = resumeContentList.get(i+1);
			if(line.equals("")){
				continue;
			}else{
				if(line.equals("学历:")){
					resumedata.setLatestDegree(desc);
				}else if(line.equals("专业:")){
					resumedata.setLatestMajor(desc);
				}else if(line.equals("学校:")){
					resumedata.setLatestSchool(desc);
				}else if(line.equals("目前年薪:")){
					resumedata.setLatestSalary(desc);
				}
			}
		}
	}
	
	public void processCurrentWork(int start, int end){
		for(int i = start; i <= end; i++){
			String line = resumeContentList.get(i).trim().replaceAll(" ", "");
			String desc = resumeContentList.get(i+1);
			if(line.equals("")){
				continue;
			}else{
				if(line.equals("公司:")){
					resumedata.setLatestCompanyName(desc);
				}else if(line.equals("行业:")){
					resumedata.setLatestIndustry(desc);
				}else if(line.equals("职位:")){
					resumedata.setLatestPositionTitle(desc);
				}
			}
		}
	}
	
	
	public void processContactInfo(int start,int end,String type){
		//解析招聘方下载的doc简历
		for(int i = start;i <= end; i++){
			String title = resumeContentList.get(i).trim().replaceAll(" ", "");
			String desc = resumeContentList.get(i+1).trim();
			if(title.equals("")){
				continue;
			}else{
				if(desc.indexOf("男")>=0 || desc.indexOf("女")>=0){
					resumedata.contactInfoData.setName(title);
					continue;
				}
				
				if(title.indexOf("|") >= 0){
					Pattern pattern = Pattern.compile("(男|女).*?(\\d+)岁（(.*?)）");
			        Matcher matcher = pattern.matcher(title);
			        if(matcher.find()){
			        	Integer age = Integer.parseInt(matcher.group(2));
			        	resumedata.setAge(age);
			        	resumedata.setGender(matcher.group(1));
			        	resumedata.setBirthday(matcher.group(3));
			        }
			        
			        pattern = Pattern.compile("([一二三四五六七八九十]+)年以上工作经验");
			        matcher = pattern.matcher(title);
			        if(matcher.find()){
			        	String work = matcher.group(1);
			        	resumedata.setWorkExperienceLength(cn2int(work));
			        }
			        
			        pattern=Pattern.compile("(未婚|已婚)",Pattern.CASE_INSENSITIVE);							
					matcher=pattern.matcher(title);
			        if(matcher.find()){
			        	String maritalStatus = matcher.group(1);
			        	resumedata.setMaritalStatus(maritalStatus);
			        }
			        
			        pattern=Pattern.compile("(中共党员|团员|群众|民主党派|无党派人士)",Pattern.CASE_INSENSITIVE);
			        matcher=pattern.matcher(title);
			        if(matcher.find()){
			        	resumedata.setPoliticalLandscape(matcher.group(1).trim());
			        }
			        
			        pattern=Pattern.compile("(\\d+)cm",Pattern.CASE_INSENSITIVE);
			        matcher=pattern.matcher(title);
			        if(matcher.find()){
			        	resumedata.setHeight(matcher.group(1));
			        }
			        
			        continue;
				}else {
					if(title.indexOf(":")>=0){
						Pattern pattern=Pattern.compile("简历关键字:(.*)");
				        Matcher matcher=pattern.matcher(title);
				        if(matcher.find()){
				        	resumedata.setResumeKeyWord(matcher.group(1));
				        }else{
				            
				            pattern = Pattern.compile("简历更新时间:(.*)");
				            matcher = pattern.matcher(title);
				            if(matcher.find()){
				            	resumedata.setUpdateTime(matcher.group(1));
				            }
				            
				        	switch(title){
							case "姓名:":
								resumedata.contactInfoData.setName(desc);
								break;
							case "性别:":
								resumedata.setGender(desc);
								break;
							case "出生日期:":
								resumedata.setBirthday(desc);
								break;
							case "居住地:":
								resumedata.setAddress(desc);
								break;
							case "工作年限:":
								pattern = Pattern.compile("([一二三四五六七八九十]+)年");
								matcher = pattern.matcher(desc);
						        if(matcher.find()){
						        	String worklen = matcher.group(1);
						        	resumedata.setWorkExperienceLength(cn2int(worklen));
						        }
								break;
							case "E-mail:":
								resumedata.contactInfoData.setEmail(desc);
								break;
							case "电话:":
								resumedata.contactInfoData.setPhone(desc);
								break;
							case "户口:":
								resumedata.setHouseHolds(desc);
								break;
							case "目前年薪:":
								resumedata.setLatestSalary(desc);
								break;
							case "身高:":
								resumedata.setHeight(desc);
								break;
							case "婚姻状况:":
								resumedata.setMaritalStatus(desc);
								break;
							case "政治面貌:":
								resumedata.setPoliticalLandscape(desc);
								break;
							case "地址:":
								resumedata.setAddress(desc);
								break;
							case "邮编:":
								resumedata.setZipCode(desc);
								break;
							case "个人主页:":
								resumedata.setHomePage(desc);
								break;
							case "关键词:":
								resumedata.setResumeKeyWord(desc);
								break;
							case "QQ:":
								resumedata.contactInfoData.setQq(desc);
								break;
							}
				        }
					
			        }else{
			        	Pattern pattern = Pattern.compile("ID:(\\d+)");
			            Matcher matcher = pattern.matcher(title);
			            if(matcher.find()){
			            	resumedata.setSourceID(matcher.group(1));
			            }
			        }
				}
			}
		}
	}
	public void processContactInfo(int start,int end){
		for(int i = start;i <= end; i++){
			String title = resumeContentList.get(i).trim().replaceAll(" ", "");
			String desc = resumeContentList.get(i+1).trim();
			if(title.equals("")){
				continue;
			}else{
				switch(title){
				case "姓名:":
					resumedata.contactInfoData.setName(desc);
					break;
				case "性别:":
					resumedata.setGender(desc);
					break;
				case "出生日期:":
					resumedata.setBirthday(desc);
					break;
				case "居住地:":
					resumedata.setAddress(desc);
					break;
				case "户口:":
					resumedata.setHouseHolds(desc);
					break;
				case "工作年限:":
					Pattern pattern = Pattern.compile("([一二三四五六七八九十]+)年");
					Matcher matcher = pattern.matcher(desc);
			        if(matcher.find()){
			        	String worklen = matcher.group(1);
			        	resumedata.setWorkExperienceLength(cn2int(worklen));
			        }
					break;
				case "电子邮件:":
					resumedata.contactInfoData.setEmail(desc);
					break;
				case "手机:":
					resumedata.contactInfoData.setPhone(desc);
					break;
				case "目前年薪:":
					resumedata.setLatestSalary(desc);
					break;
				case "身高:":
					resumedata.setHeight(desc);
					break;
				case "婚姻状况:":
					resumedata.setMaritalStatus(desc);
					break;
				case "政治面貌:":
					resumedata.setPoliticalLandscape(desc);
					break;
				case "地址:":
					resumedata.setResidence(desc);
					break;
				case "邮编:":
					resumedata.setZipCode(desc);
					break;
				case "个人主页:":
					resumedata.setHomePage(desc);
					break;
				case "关键词:":
					resumedata.setResumeKeyWord(desc);
					break;
				case "QQ:":
					resumedata.contactInfoData.setQq(desc);
					break;
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
				if(Pattern.compile("﻿简历更新时间:(.*)").matcher(line).find()){
            		Matcher mat = Pattern.compile("﻿简历更新时间:(.*)").matcher(line);
            		if(mat.find()){
            			resumedata.setUpdateTime(mat.group(1));
            		}
            		
            	}else if(line.indexOf("简历关键字:") >= 0){
            		String [] arr = line.split(":");
            		if(arr.length == 2){
            			String keyword = arr[1];
            			resumedata.setResumeKeyWord(keyword);
            		}
            		
            	}else{
            		resumedata.contactInfoData.setName(line);
//            		resumedata.setName(line);
            	}
			}
		}
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
	
	/**
	 * 将简历预处理为不同的段保存到map中, 同时将简历内容读取到list中
	 */
//	public void preProcess(){
//		File file = new File(this.filePath);
//        BufferedReader reader = null;
//        try {
//            System.out.println("以行为单位读取文件内容，一次读一整行:");
//            reader = new BufferedReader(new FileReader(file));
//
//            String line = "";
//            int lineNum = 0;
//            String currentSection = "basicInfo";
//            SectionInfo sectionInfo = new SectionInfo(0,0);
//            sectionMap.put(currentSection, sectionInfo);
//            // 一次读入一行，直到读入null为文件结束
//            while ((line = reader.readLine()) != null) {
//            	line = line.replaceAll("\u00A0"," " ).trim();
//            	line.replaceAll(" ", "");
//            	resumeContentList.add(line);
//            	
//            	if(Pattern.compile("(男|女).*?(\\d+岁.*?日\\）)").matcher(line).find()){
//            		if(!sectionMap.containsKey("contactInfo")){
//            			currentSection = "contactInfo";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}            		
//            	}else if(Pattern.compile("最近工作.*?\\[.*?\\]").matcher(line).find()){
//            		
//            		if(!sectionMap.containsKey("recentWork")){
//            			currentSection = "recentWork";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//            	}else if(line.equals("最高学历")){
//            		
//            		if(!sectionMap.containsKey("higestDegree")){
//            			currentSection = "higestDegree";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//            	}else if(line.equals("自我评价")){
//            		
//            		if(!sectionMap.containsKey("selfEvaluation")){
//            			currentSection = "selfEvaluation";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//            	}else if(line.equals("求职意向")){
//            		
//            		if(!sectionMap.containsKey("jobTarget")){
//            			currentSection = "jobTarget";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//            	}else if(line.equals("工作经验")){
//            		
//            		if(!sectionMap.containsKey("workExperience")){
//            			currentSection = "workExperience";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//            	}else if(line.equals("项目经验")){
//            		
//            		if(!sectionMap.containsKey("projectExperience")){
//            			currentSection = "projectExperience";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//            	}else if(line.equals("教育经历")){
//            		
//            		if(!sectionMap.containsKey("educationExperience")){
//            			currentSection = "educationExperience";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//            	}else if(line.equals("语言能力")){
//            		
//            		if(!sectionMap.containsKey("languageSkill")){
//            			currentSection = "languageSkill";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//            	}else if(line.equals("专业技能")){            		
//            		if(!sectionMap.containsKey("professionalSkill")){
//            			currentSection = "professionalSkill";
//                		SectionInfo contactInfo = new SectionInfo(lineNum,lineNum);
//                		sectionMap.put(currentSection, contactInfo);
//            		}
//            	}else{
//            		sectionMap.get(currentSection).setEnd(lineNum);
//            	}
//            	lineNum ++;
//            }
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
//	
//

	public void preProcess() {
		for (int i = 0; i < resumeContentList.size(); i++) {
			String line = resumeContentList.get(i).trim();
			if("".equals(line)){
				resumeContentList.remove(i);
				i--;
			}else{
				if(line.indexOf("此简历为未下载简历")>=0){
					this.hasBuy = false;
				}
				if(type.equals("")){
					Pattern pattern = Pattern.compile("(男|女).*?(\\d+)岁（(.*?)）");
			        Matcher matcher = pattern.matcher(line);
			        if(matcher.find()){
			        	type = "recruiterDownload";
			        }
				}
			}
		}
		
		if (resumeContentList == null) {
			return;
		} else {
			int lineNum = 0;
			String line = "";
			String currentSection = "basicInfo";
			SectionInfo sectionInfo = new SectionInfo(0, 0);
			sectionMap.put(currentSection, sectionInfo);

			for (int i = 0; i < resumeContentList.size(); i++) {
				line = resumeContentList.get(i).trim();

				line = line.replaceAll("\u00A0", " ").replaceAll("\u3000", " ").trim();
				if (line.equals("个人信息") || line.startsWith("简历关键字") || Pattern.compile("ID:(.*)").matcher(line).find() || line.startsWith("简历更新时间")) {
					if (!sectionMap.containsKey("contactInfo")) {
						currentSection = "contactInfo";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}

				} else if (line.equals("自我评价")) {
					if (!sectionMap.containsKey("selfEvaluation")) {
						currentSection = "selfEvaluation";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}
				}else if (line.equals("求职意向")) {
					if (!sectionMap.containsKey("jobTarget")) {
						currentSection = "jobTarget";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}

				} else if (line.equals("工作经验")) {

					if (!sectionMap.containsKey("workExperience")) {
						currentSection = "workExperience";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}

				} else if (line.startsWith("最近工作 [")) {

					if (!sectionMap.containsKey("recentWork")) {
						currentSection = "recentWork";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}

				}else if (line.equals("最高学历")) {

					if (!sectionMap.containsKey("higestDegree")) {
						currentSection = "higestDegree";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}

				}else if (line.equals("项目经验")) {
					if (!sectionMap.containsKey("projectExperience")) {
						currentSection = "projectExperience";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}
				} else if (line.equals("教育经历")) {
					if (!sectionMap.containsKey("educationExperience")) {
						currentSection = "educationExperience";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}
				} else if (line.equals("语言能力")) {
					if (!sectionMap.containsKey("languageSkill")) {
						currentSection = "languageSkill";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}

				} else if (line.equals("专业技能")) {
					if (!sectionMap.containsKey("professionalSkill")) {
						currentSection = "professionalSkill";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}

				} else if (line.equals("培训经历")) {
					if (!sectionMap.containsKey("trainExperience")) {
						currentSection = "trainExperience";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}

				}else if (line.equals("语言能力")) {
					if (!sectionMap.containsKey("languageSkill")) {
						currentSection = "languageSkill";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}

				}else if (line.equals("IT技能")) {
					if (!sectionMap.containsKey("professionalSkill")) {
						currentSection = "professionalSkill";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}

				}else if (line.equals("其他信息")) {
					if (!sectionMap.containsKey("otherInfo")) {
						currentSection = "otherInfo";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}

				}else {
					sectionMap.get(currentSection).setEnd(lineNum);
				}
				lineNum++;
			}
		}
	}
}
