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
import com.bole.resumeparser.models.ResumeInfo;
import com.bole.resumeparser.models.TextResumeData;
import com.bole.resumeparser.models.TrainingExperienceData;
import com.bole.resumeparser.models.WorkExperienceData;
import com.bole.resumeparser.service.ResumePreProcessFactory;

public class LiepinResumeParser extends AbstractResumeParser implements DocumentResumeParserInterface{

	//同时将简历内容读取到list中
	public String filePath = "";
	TextResumeData resumedata = new TextResumeData();
	public String currentStatus = "";  //目前工作状态
	
	//同时将简历内容读取到list中 
	HashMap<String,SectionInfo> sectionMap = new HashMap<String,SectionInfo>();
	ArrayList<String> resumeContentList = new ArrayList<String>();
	
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
		String filePath = "/home/bobby/mnt/邮箱同步的简历/简历模板/resume3/liepin.doc";
		ResumePreProcessFactory resumePreProcessFactory = new ResumePreProcessFactory();
		ResumeInfo resumeInfo = null;;
		try {
			resumePreProcessFactory.setSofficePath("/usr/bin/soffice");
			resumeInfo = resumePreProcessFactory.preProcess(filePath);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LiepinResumeParser parser = null;
		TextResumeData re = new TextResumeData();
        if(resumeInfo == null){
        	return ;
        }else{
        	String source = resumeInfo.getResumeSource();
        	ArrayList<String> resumeContentList = resumeInfo.getSourceDataList();
        	parser = new LiepinResumeParser(resumeContentList);
        	
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
		return;
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
        		this.processContactInfo(val.getStart(), val.getEnd());
        	}else if(key.equals("contactInfo")){
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
        		languageSkillDataList = extractLanguageSkill(val.getStart(), val.getEnd());
        		resumedata.setLanguageSkill(languageSkillDataList);
        	}else if(key.equals("currentSalary")){
        		this.processCurrentSalary(val.getStart(), val.getEnd());
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
        	}else if(key.equals("otherInfo")){
        		String otherInfo = "";
        		otherInfo = this.extractSelfEvaluation(val.getStart(), val.getEnd());
        		resumedata.otherInfoMap.put("otherInfo", otherInfo);
        	}
        }
        resumedata.setCreateTime(new Date());
//        if(resumedata.getPhone() != null && resumedata.getEmail() != null){
//        	resumedata.setIsCotactInformation("YES");
//        }
		return resumedata;
	}

	@Override
	public String getSourceID(String html) {
		// TODO Auto-generated method stub
		return super.getSourceID(html);
	}

	@Override
	public String getWebsite() {
		// TODO Auto-generated method stub
		return super.getWebsite();
	}

	@Override
	public String getUpdateTime() {
		// TODO Auto-generated method stub
		return super.getUpdateTime();
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
	public String extractSelfEvaluation(int start,int end) {
		// TODO Auto-generated method stub
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
						selfEvaluation +="\r\n" + line;
					}
				}
				
			}
		}
		return selfEvaluation;
	}
	
	public void processCurrentSalary(int start,int end){
		String line = "";
		for(int i=start; i <= end;i++){
			line = resumeContentList.get(i).replaceAll(":", "").trim();
			if(i==start){
				resumedata.setLatestSalary(line);
			}else{
				if(line.equals("工作年限")){
					String nextLine = resumeContentList.get(i+1);
					Matcher mat = Pattern.compile("(\\d+).*?年").matcher(nextLine);
            		if (mat.find()){
            			resumedata.setWorkExperienceLength(Integer.parseInt(mat.group(1)));
            		}
				}
			}
		}
		return;
	}
	
	@Override
	public JobTarget extractJobTarget(int start,int end) {
		// TODO Auto-generated method stub
		String line = "";
		JobTarget jobTarget = new JobTarget();
		for(int i=start; i <= end;i++){
			line = resumeContentList.get(i).replaceAll(" ", "").trim();
			if(line.equals("")){
				continue;
			}else{
				if(line.indexOf("地点:") >= 0){
					if(line.endsWith("地点:")){
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
        		}else if(line.indexOf("职位:") >= 0 || line.indexOf("职业:") >= 0){
        			if(line.endsWith("职位:") || line.endsWith("职业:")){
        				jobTarget.setJobCareer(resumeContentList.get(i+1));
					}else{
						Pattern pattern = Pattern.compile("(职位|职业):(.*)");
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
		jobTarget.setStatus(currentStatus);
		return jobTarget;
	}

	@Override
	public String getResumeKeywords(int start,int end) {
		// TODO Auto-generated method stub
		return super.getResumeKeywords(start,end);
	}

	@Override
	public ArrayList<WorkExperienceData> extractWorkExperience(int start,int end) {
		// TODO Auto-generated method stub
		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
		
		int tmpNum = 0;
		
		String line = "";
		int currentNum = 0;
		String jobDesc = "";
		String preTitle = "";
		
		String startTime = "";
		String endTime = "";
		String positionTitle = "";
		
		String companyName = "";
		String companyDesc = "";
		String companyCatagory = "";
		String companyScale = "";
		String companyIndustry = "";
		
		String location = "";
		String department = "";
		String reportTo = "";
		String suborinates = "";
		String keyPerformance = "";
		String salary = "";
		
		for(int i = start; i <= end ;i++){
			line = resumeContentList.get(i);
			line = line.replaceAll(":", "").replace("-", "").trim();
			
			if(i == end){
				if(preTitle.equals("工作职责")){
					jobDesc += "\r\n" + line;
					(workExperienceDataList.get(currentNum-1)).setJobDesc(jobDesc.trim());
					jobDesc = "";
				}else if(preTitle.equals("工作业绩")){
					keyPerformance += "\r\n" + line;
					if(workExperienceDataList.get(currentNum-1).manageExperienceDataList.size()==0){
        				ManageExperienceData manageExperience = new ManageExperienceData();
        				manageExperience.setKeyPerformance(keyPerformance.trim());
        				workExperienceDataList.get(currentNum-1).manageExperienceDataList.add(manageExperience);
        			}else{
        				workExperienceDataList.get(currentNum-1).manageExperienceDataList.get(0).setKeyPerformance(keyPerformance.trim());
        			}
					keyPerformance = "";
				}
			}
			
			Pattern pattern = Pattern.compile("(\\d{4}[.]\\d+).*?(至今|\\d{4}[.]\\d+)");
			Matcher matcher = pattern.matcher(line);
			
			if(matcher.find()){
				
				String nextLine = resumeContentList.get(i+1).replaceAll(":", "").replace("-", "").trim();
				if(!nextLine.startsWith("所在地区") && !nextLine.startsWith("薪酬状况")){
					
					startTime = matcher.group(1);
					endTime = matcher.group(2);
		        	companyName = resumeContentList.get(i+1);
		        	
		        	
		        	if(!line.startsWith(startTime)){
		        		companyName = line.substring(0,line.indexOf(startTime));
		        	}else if(!line.endsWith(endTime)){
		        		companyName = line.substring(line.indexOf(endTime));
		        	}
		        		
		        	if(currentNum > 0){
		        		if(preTitle.equals("工作职责")){
							(workExperienceDataList.get(currentNum-1)).setJobDesc(jobDesc.trim());
							jobDesc = "";
						}else if(preTitle.equals("工作业绩")){
							if(workExperienceDataList.get(currentNum-1).manageExperienceDataList.size()==0){
		        				ManageExperienceData manageExperience = new ManageExperienceData();
		        				manageExperience.setKeyPerformance(keyPerformance.trim());
		        				workExperienceDataList.get(currentNum-1).manageExperienceDataList.add(manageExperience);
		        			}else{
		        				workExperienceDataList.get(currentNum-1).manageExperienceDataList.get(0).setKeyPerformance(keyPerformance.trim());
		        			}
							
							keyPerformance = "";
						}
		        	}
				}else{
					positionTitle = resumeContentList.get(i-1);
					if(jobDesc.indexOf(positionTitle) >= 0){
						jobDesc = jobDesc.substring(0,jobDesc.indexOf(positionTitle)-1).trim();
					}else if(keyPerformance.indexOf(positionTitle) >= 0){
						keyPerformance = keyPerformance.substring(0,keyPerformance.indexOf(positionTitle)-1).trim();
					}
					
					if(currentNum > 0){
		        		if(preTitle.equals("工作职责")){
							(workExperienceDataList.get(currentNum-1)).setJobDesc(jobDesc);
							jobDesc = "";
						}else if(preTitle.equals("工作业绩")){
							if(workExperienceDataList.get(currentNum-1).manageExperienceDataList.size()==0){
		        				ManageExperienceData manageExperience = new ManageExperienceData();
		        				manageExperience.setKeyPerformance(keyPerformance.trim());
		        				workExperienceDataList.get(currentNum-1).manageExperienceDataList.add(manageExperience);
		        			}else{
		        				workExperienceDataList.get(currentNum-1).manageExperienceDataList.get(0).setKeyPerformance(keyPerformance.trim());
		        			}
							keyPerformance = "";
						}
		        	}
					
					currentNum ++;
					
					WorkExperienceData workExperienceData = new WorkExperienceData();
					workExperienceData.setStartTime(startTime);
					workExperienceData.setEndTime(endTime);
					workExperienceData.setCompanyName(companyName);
					workExperienceData.setCompanyDesc(companyDesc);
					workExperienceData.setIndustryCatagory(companyIndustry);
					workExperienceData.setCompanyScale(companyScale);
					workExperienceData.setPositionTitle(positionTitle);
					
					workExperienceDataList.add(workExperienceData);
				}
				
				preTitle = "";
				
			}else{
				if(line.startsWith("公司描述")){
					if(line.equals("公司描述")){
						companyDesc = resumeContentList.get(i+1);
					}else{
						companyDesc = line.substring(line.indexOf("公司描述")+4);
					}
				}else if(line.startsWith("公司性质")){
					if(line.equals("公司性质")){
						companyCatagory = resumeContentList.get(i+1);
					}else{
						companyCatagory = line.substring(line.indexOf("公司性质")+4);
					}
				}else if(line.startsWith("公司规模")){
					if(line.equals("公司规模")){
						companyScale = resumeContentList.get(i+1);
					}else{
						companyScale = line.substring(line.indexOf("公司规模")+4);
					}
				}else if(line.startsWith("公司行业")){
					if(line.equals("公司行业")){
						companyIndustry = resumeContentList.get(i+1);
					}else{
						companyIndustry = line.substring(line.indexOf("公司行业")+4);
					}
				}else if(line.startsWith("所在地区")){
					if(line.equals("所在地区")){
						location = resumeContentList.get(i+1);
					}else{
						location = line.substring(line.indexOf("所在地区")+4);
					}
					workExperienceDataList.get(currentNum-1).setLocation(location);
				}else if(line.startsWith("所在部门")){
					if(line.equals("所在部门")){
						department = resumeContentList.get(i+1);
					}else{
						department = line.substring(line.indexOf("所在部门")+4);
					}
					workExperienceDataList.get(currentNum-1).setDepartment(department);
				}else if(line.equals("汇报对象")){
					reportTo = resumeContentList.get(i+1);
					if(workExperienceDataList.get(currentNum-1).manageExperienceDataList.size()==0){
        				ManageExperienceData manageExperience = new ManageExperienceData();
        				manageExperience.setReportTo(reportTo);
        				workExperienceDataList.get(currentNum-1).manageExperienceDataList.add(manageExperience);
        			}else{
        				workExperienceDataList.get(currentNum-1).manageExperienceDataList.get(0).setReportTo(reportTo);
        			}
				}else if(line.equals("下属人数")){
					suborinates = resumeContentList.get(i+1);
					if(workExperienceDataList.get(currentNum-1).manageExperienceDataList.size()==0){
        				ManageExperienceData manageExperience = new ManageExperienceData();
        				manageExperience.setSuborinates(suborinates);
        				workExperienceDataList.get(currentNum-1).manageExperienceDataList.add(manageExperience);
        			}else{
        				workExperienceDataList.get(currentNum-1).manageExperienceDataList.get(0).setSuborinates(suborinates);
        			}
				}else if(line.startsWith("工作业绩")){
					if(preTitle.equals("工作职责")){
						workExperienceDataList.get(currentNum-1).setJobDesc(jobDesc.trim());
						jobDesc = "";
					}
					preTitle = "工作业绩";
					if(!line.equals("工作业绩"))
					{
						keyPerformance = line.substring(line.indexOf("工作业绩")+4);
					}
				}else if(line.equals("薪酬状况")){
					salary = resumeContentList.get(i+1);
				}else if(line.startsWith("工作职责")){
					preTitle = "工作职责";
					if(!line.endsWith("工作职责")){
						jobDesc = line.substring(line.indexOf("工作职责")+4);
					}
				}else{
					if(preTitle.equals("工作职责")){
						if(jobDesc.equals("")){
							jobDesc = line;
						}else{
							jobDesc += "\r\n" + line;
						}
					}else if(preTitle.equals("工作业绩"))
					{
						if(keyPerformance.equals("")){
							keyPerformance = line;
						}else{
							keyPerformance += "\r\n" + line;
						}
					}
				}
			}
			
		}
		return workExperienceDataList;
	}

	@Override
	public ArrayList<EducationExperienceData> extractEducationExperience(
			int start,int end) {
		// TODO Auto-generated method stub
		ArrayList<EducationExperienceData> educationExperienceDataList = new ArrayList<EducationExperienceData>();
		String line = "";
		int currentNum = 0;
		
		for(int i = start;i <= end; i++){			
			line = resumeContentList.get(i);
			line = line.replaceAll(" ", "").trim();
			
			if(line.equals("")){
				continue;
			}else{
				if(Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+)").matcher(line).find()){
					Matcher mat = Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+)").matcher(line);
			        if(mat.find()){
			        	currentNum++;
			        	
			        	EducationExperienceData educationExperienceData = new EducationExperienceData();
			        	
			        	educationExperienceData.setStartTime(mat.group(1));
			        	educationExperienceData.setEndTime(mat.group(2));
			        	educationExperienceData.setSchool(resumeContentList.get(i+1));
			        	i++;
			        	educationExperienceDataList.add(educationExperienceData);
			        }
				}else{
					if(line.indexOf("专业:") >= 0){
						if(line.endsWith("专业:")){
							educationExperienceDataList.get(currentNum-1).setMajor(resumeContentList.get(i+1));
						}else{
							String major = line.substring(line.indexOf("专业:")+3);
							educationExperienceDataList.get(currentNum-1).setMajor(major);
						}
					}else if(line.indexOf("学历:") >= 0){
						if(line.endsWith("学历:")){
							educationExperienceDataList.get(currentNum-1).setDegree(resumeContentList.get(i+1));
						}else{
							String degree = line.substring(line.indexOf("学历:")+3);
							educationExperienceDataList.get(currentNum-1).setDegree(degree);
						}
					}else if(line.indexOf("是否统招:") >= 0){
						if(line.endsWith("是否统招:")){
							educationExperienceDataList.get(currentNum-1).setSeriesIncurs(resumeContentList.get(i+1));
						}else{
							String seriesIncurs = line.substring(line.indexOf("是否统招:")+5);
							educationExperienceDataList.get(currentNum-1).setSeriesIncurs(seriesIncurs);
						}
					}
				}
			}
		}
		return educationExperienceDataList;
	}

	@Override
	public ArrayList<TrainingExperienceData> extractTrainingExperience(
			int start,int end) {
		// TODO Auto-generated method stub
		return super.extractTrainingExperience(start,end);
	}

	@Override
	public ArrayList<LanguageSkillData> extractLanguageSkill(int start,int end) {
		// TODO Auto-generated method stub
		ArrayList<LanguageSkillData> languageSkillDataList = new ArrayList<LanguageSkillData>();
		String line = "";
		for(int i = start; i <= end ;i++){
			line = resumeContentList.get(i).replaceAll(" ", "");
			String [] arr = line.split("、");
			for(int j=0;j<arr.length;j++){
				LanguageSkillData languageSkillData = new LanguageSkillData();
				languageSkillData.setCatagory(arr[j]);
				languageSkillDataList.add(languageSkillData);
			}
		}
		
		return languageSkillDataList;
	}

	@Override
	public ArrayList<CertificateData> extractCertficate(int start,int end) {
		// TODO Auto-generated method stub
		return super.extractCertficate(start,end);
	}

	@Override
	public ArrayList<ProjectExperienceData> extractProjectExperience(int start,int end) {
		// TODO Auto-generated method stub
		ArrayList<ProjectExperienceData> projectExperienceDataList = new ArrayList<ProjectExperienceData>();
		
		String preTitle = "";
		String projectDesc = "";
		String responsibleFor = "";
		String projectPerformance = "";
		
		int currentNum = 0;
		String line = "";
		for(int i = start; i <= end ;i++){
			line = resumeContentList.get(i).replaceAll("-", "").trim();
			if(i==end){
				if(currentNum > 0){
					if(preTitle.equals("项目职责")){
						responsibleFor += "\r\n" + line;
						projectExperienceDataList.get(currentNum-1).setResponsibleFor(responsibleFor);
						responsibleFor = "";
					}else if(preTitle.equals("项目描述")){
						projectDesc += "\r\n" + line;
						projectExperienceDataList.get(currentNum-1).setProjectDesc(projectDesc);
						projectDesc = "";
					}else if(preTitle.equals("项目业绩")){
						projectPerformance += "\r\n" + line;
						projectExperienceDataList.get(currentNum-1).setProjectPerformance(projectPerformance);
						projectPerformance = "";
					}
				}
			}
			if(line.equals("")){
				continue;
			}else{
				if(Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+)").matcher(line).find()){
					Matcher mat = Pattern.compile("(\\d+.\\d+).*?(至今|\\d+.\\d+)").matcher(line);
			        if(mat.find()){
			        	if(currentNum > 0){
							if(preTitle.equals("项目职责")){
								projectExperienceDataList.get(currentNum-1).setResponsibleFor(responsibleFor);
								responsibleFor = "";
							}else if(preTitle.equals("项目描述")){
								projectExperienceDataList.get(currentNum-1).setProjectDesc(projectDesc);
								projectDesc = "";
							}else if(preTitle.equals("项目业绩")){
								projectExperienceDataList.get(currentNum-1).setProjectPerformance(projectPerformance);
								projectPerformance = "";
							}
						}
			        	preTitle = "";
			        	currentNum ++;
			        	ProjectExperienceData projectExperienceData = new ProjectExperienceData();
			        	
			        	String startTime = mat.group(1);
			        	String endTime = mat.group(2);
			        	String projectTitle = "";
			        	projectExperienceData.setStartTime(startTime.replaceAll("/", "-"));	        	
			        	projectExperienceData.setEndTime(endTime.replaceAll("/", "-"));
			        	
			        	if(!line.startsWith(startTime) || !line.endsWith(endTime)){
			        		if(!line.startsWith(startTime)){
			        			projectTitle = line.substring(0, line.indexOf(startTime)-1);
			        		}else if(!line.endsWith(endTime)){
			        			projectTitle = line.substring(line.indexOf(endTime)+endTime.length());
			        		}
			        	}else{
			        		projectTitle = resumeContentList.get(i+1);
			        	}
			        	projectExperienceData.setProjectTitle(projectTitle);
			        	
			        	projectExperienceDataList.add(projectExperienceData);
			        }
				}else{
					if(line.indexOf("项目职务:") >= 0){
						if(line.endsWith("项目职务:")){
							projectExperienceDataList.get(currentNum-1).setPositionTitle(resumeContentList.get(i+1));
						}else{
							String positionTitle = line.substring(line.indexOf("项目职务:")+5);
							projectExperienceDataList.get(currentNum-1).setPositionTitle(positionTitle);
						}
					}else if(line.indexOf("所在公司:") >= 0){
						if(line.endsWith("所在公司:")){
							projectExperienceDataList.get(currentNum-1).setPositionTitle(resumeContentList.get(i+1));
						}else{
							String companyName = line.substring(line.indexOf("所在公司:")+5);
							projectExperienceDataList.get(currentNum-1).setCompany(companyName);
						}
					}else if(line.indexOf("项目简介:") >= 0){
						if(preTitle.equals("项目职责")){
							projectExperienceDataList.get(currentNum-1).setResponsibleFor(responsibleFor);
							responsibleFor = "";
						}else if(preTitle.equals("项目业绩")){
							projectExperienceDataList.get(currentNum-1).setProjectPerformance(projectPerformance);
							projectPerformance = "";
						}
						preTitle = "项目描述";
						if(!line.endsWith("项目简介:"))
						{
							projectDesc = line.substring(line.indexOf("项目简介:")+5);
//							projectExperienceDataList.get(currentNum-1).setProjectDesc(projectDesc);
						}
					}else if(line.indexOf("项目职责:") >= 0){
						if(preTitle.equals("项目描述")){
							projectExperienceDataList.get(currentNum-1).setProjectDesc(projectDesc);
							projectDesc = "";
						}else if(preTitle.equals("项目业绩")){
							projectExperienceDataList.get(currentNum-1).setProjectPerformance(projectPerformance);
							projectPerformance = "";
						}
						preTitle = "项目职责";
						if(!line.endsWith("项目职责:"))
						{
							responsibleFor = line.substring(line.indexOf("项目职责:")+5);
//							projectExperienceDataList.get(currentNum-1).setResponsibleFor(responsibleFor);
						}
					}else if(line.indexOf("项目描述:") >= 0){
						if(preTitle.equals("项目职责")){
							projectExperienceDataList.get(currentNum-1).setResponsibleFor(responsibleFor);
							responsibleFor = "";
						}else if(preTitle.equals("项目业绩")){
							projectExperienceDataList.get(currentNum-1).setProjectPerformance(projectPerformance);
							projectPerformance = "";
						}
						preTitle = "项目描述";
						if(!line.endsWith("项目描述:"))
						{
							projectDesc = line.substring(line.indexOf("项目描述:")+5);
//							projectExperienceDataList.get(currentNum-1).setProjectDesc(projectDesc);
						}
					}else if(line.indexOf("项目业绩:") >= 0){
						if(preTitle.equals("项目职责")){
							projectExperienceDataList.get(currentNum-1).setResponsibleFor(responsibleFor);
							responsibleFor = "";
						}else if(preTitle.equals("项目描述")){
							projectExperienceDataList.get(currentNum-1).setProjectDesc(projectDesc);
							projectDesc = "";
						}
						preTitle = "项目业绩";
						if(line.endsWith("项目业绩:"))
						{
							projectDesc = line.substring(line.indexOf("项目描述:")+5);
//							projectExperienceDataList.get(currentNum-1).setProjectDesc(projectDesc);
						}
					}else{
						if(preTitle.equals("项目职责")){
							if(responsibleFor.equals("")){
								responsibleFor = line;
							}else{
								responsibleFor += "\r\n" + line;
							}
						}else if(preTitle.equals("项目描述")){
							if(projectDesc.equals("")){
								projectDesc = line;
							}else{
								projectDesc += "\r\n" + line;
							}
						}else if(preTitle.equals("项目业绩")){
							if(projectPerformance.equals("")){
								projectPerformance = line;
							}else{
								projectPerformance += "\r\n" + line;
							}
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
		return super.extractProfessionalSkill(start,end);
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
	
	public LiepinResumeParser(String filePath){
		this.filePath = filePath;
	}
	
	public LiepinResumeParser(ArrayList<String> resumeDataList){
		this.resumeContentList = resumeDataList;
	}
	
	public EducationExperienceData processHighestEducation(int start, int end){
		EducationExperienceData educationExperienceData = new EducationExperienceData();
		for(int i = start; i <= end ;i++){
			String line = resumeContentList.get(i);
			if(line.equals("")){
				continue;
			}else{
				if(line.equals("学　历:")){
					educationExperienceData.setDegree(resumeContentList.get(i+1));
				}else if(line.equals("专　业:")){
					educationExperienceData.setMajor(resumeContentList.get(i+1));
				}else if(line.equals("学　校:")){
					educationExperienceData.setSchool(resumeContentList.get(i+1));
				}
			}
		}
		return educationExperienceData;
	}
	
	public void processCurrentWork(int start, int end){
		for(int i = start; i <= end; i++){
			String line = resumeContentList.get(i).replaceAll(" ", "");
			if(line.equals("")){
				continue;
			}else{
				if(line.equals("公司名称")){
					resumedata.setLatestCompanyName(resumeContentList.get(i+1));
				}else if(line.equals("所任职位")){
					resumedata.setLatestPositionTitle(resumeContentList.get(i+1));
				}else if(line.equals("所在行业")){
					resumedata.setLatestIndustry(resumeContentList.get(i+1));
				}
			}
		}
	}
	
	public void processContactInfo(int start,int end){
		for(int i = start;i <= end; i++){
			String line = resumeContentList.get(i).replaceAll(":", " ").replaceAll(" ", "").trim();
			if(line.equals("")){
				continue;
			}else{
				if(line.equals("姓名")){
					resumedata.setName(resumeContentList.get(i+1));
					i++;
				}else if(line.equals("性别")){
					resumedata.setGender(resumeContentList.get(i+1));
					i++;
				}else if(line.equals("手机号码") || line.equals("联系电话")){
					resumedata.setPhone(resumeContentList.get(i+1));
				}else if(line.equals("年龄")){
					String age = resumeContentList.get(i+1).trim();
					if(age.indexOf("岁") > 0){
						age = age.substring(0,age.indexOf("岁"));
					}
					resumedata.setAge(Integer.parseInt(age));
				}else if(line.equals("电子邮件")){
					resumedata.setEmail(resumeContentList.get(i+1).trim());
				}else if(line.equals("所在地")){
					resumedata.setAddress(resumeContentList.get(i+1));
				}else if(line.equals("婚姻状况")){
					resumedata.setMaritalStatus(resumeContentList.get(i+1));
				}else if(line.equals("工作年限")){
					Matcher mat = Pattern.compile("(\\d+).*?年").matcher(line);
            		if (mat.find()){
            			resumedata.setWorkExperienceLength(Integer.parseInt(mat.group(1)));
            		}
				}else if(line.equals("教育程度")){
					Matcher mat = Pattern.compile("(本科|硕士|博士|MBA|EMBA|大专|中专|高中|初中|中技|本科|硕士|博士|MBA|EMBA|大专|中专|高中|初中|中技|其他)").matcher(resumeContentList.get(i+1));
            		if (mat.find()){
            			resumedata.setLatestDegree(mat.group(1));
            		}
				}else if(line.equals("目前状态")){
					currentStatus = resumeContentList.get(i+1);
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

	public void preProcess() {
		if (resumeContentList == null) {
			return;
		} else {
			for (int i = 0; i < resumeContentList.size(); i++) {
				String line = resumeContentList.get(i).trim();
				if("".equals(line)){
					resumeContentList.remove(i);
					i--;
				}
			}
			
			int lineNum = 0;
			String line = "";
			String currentSection = "basicInfo";
			SectionInfo sectionInfo = new SectionInfo(0, 0);
			sectionMap.put(currentSection, sectionInfo);

			for (int i = 0; i < resumeContentList.size(); i++) {
				line = resumeContentList.get(i);

				String tmpline = line.replaceAll("(:|：)", "").trim();
//				resumeContentList.add(line);
				if (tmpline.equals("个人信息")
						|| Pattern.compile("ID:(.*)").matcher(line).find()) {
					if (!sectionMap.containsKey("contactInfo")) {
						currentSection = "contactInfo";
						SectionInfo contactInfo = new SectionInfo(lineNum+1,
								lineNum+1);
						sectionMap.put(currentSection, contactInfo);
					}
				} else if (tmpline.equals("自我评价")) {
					if (!sectionMap.containsKey("selfEvaluation")) {
						currentSection = "selfEvaluation";
						SectionInfo contactInfo = new SectionInfo(lineNum,
								lineNum);
						sectionMap.put(currentSection, contactInfo);
					}

				} else if (tmpline.equals("职业意向") || tmpline.equals("求职意向")) {
					if (!sectionMap.containsKey("jobTarget")) {
						currentSection = "jobTarget";
						SectionInfo contactInfo = new SectionInfo(lineNum+1,
								lineNum+1);
						sectionMap.put(currentSection, contactInfo);
					}

				} else if (tmpline.equals("工作经历")) {

					if (!sectionMap.containsKey("workExperience")) {
						currentSection = "workExperience";
						SectionInfo contactInfo = new SectionInfo(lineNum+1,
								lineNum+1);
						sectionMap.put(currentSection, contactInfo);
					}

				} else if (tmpline.equals("目前职位概况")) {

					if (!sectionMap.containsKey("currentWork")) {
						currentSection = "currentWork";
						SectionInfo contactInfo = new SectionInfo(lineNum+1,
								lineNum+1);
						sectionMap.put(currentSection, contactInfo);
					}

				}else if (tmpline.equals("项目经历")) {
					if (!sectionMap.containsKey("projectExperience")) {
						currentSection = "projectExperience";
						SectionInfo contactInfo = new SectionInfo(lineNum+1,
								lineNum+1);
						sectionMap.put(currentSection, contactInfo);
					}
				} else if (tmpline.equals("教育经历")) {
					if (!sectionMap.containsKey("educationExperience")) {
						currentSection = "educationExperience";
						SectionInfo contactInfo = new SectionInfo(lineNum+1,
								lineNum+1);
						sectionMap.put(currentSection, contactInfo);
					}
				} else if (tmpline.equals("语言能力")) {
					if (!sectionMap.containsKey("languageSkill")) {
						currentSection = "languageSkill";
						SectionInfo contactInfo = new SectionInfo(lineNum+1,
								lineNum+1);
						sectionMap.put(currentSection, contactInfo);
					}

				} else if (tmpline.equals("专业技能")) {
					if (!sectionMap.containsKey("professionalSkill")) {
						currentSection = "professionalSkill";
						SectionInfo contactInfo = new SectionInfo(lineNum+1,
								lineNum+1);
						sectionMap.put(currentSection, contactInfo);
					}

				}else if (tmpline.equals("目前年薪")) {
					if (!sectionMap.containsKey("currentSalary")) {
						currentSection = "currentSalary";
						SectionInfo contactInfo = new SectionInfo(lineNum+1,
								lineNum+1);
						sectionMap.put(currentSection, contactInfo);
					}

				}else if (tmpline.equals("附加信息")) {
					if (!sectionMap.containsKey("otherInfo")) {
						currentSection = "otherInfo";
						SectionInfo contactInfo = new SectionInfo(lineNum+1,
								lineNum+1);
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
