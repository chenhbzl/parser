package com.bole.resumeparser.document.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.bole.config.ResourcesConfig;
import com.bole.resumeparser.document.impl.AbstractResumeParser.SectionInfo;
import com.bole.resumeparser.html.impl._51jobResumeParser;
import com.bole.resumeparser.models.EducationExperienceData;
import com.bole.resumeparser.models.JobTarget;
import com.bole.resumeparser.models.ProjectExperienceData;
import com.bole.resumeparser.models.ResumeData;
import com.bole.resumeparser.models.ResumeInfo;
import com.bole.resumeparser.models.TextResumeData;
import com.bole.resumeparser.models.WorkExperienceData;
import com.bole.resumeparser.service.ResumePreProcessFactory;

public class Parser {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ResourcesConfig.loadConfig("./src/main/java/resources");
		Parser p = new Parser();
		URL projectPath = Parser.class.getClassLoader().getResource("");
		String filePath = projectPath.getPath().replaceAll("bin", "resumes/docresume");
//		String filePath = "/home/bobby/mnt/邮箱同步的简历/简历模板/resume3/";
		File file = new File(filePath);		
		String[] filelist = file.list();
        for (int i = 0; i < filelist.length; i++) {
        	//String filename = filelist[i];
        	
            String resumeid = filelist[i].split("\\.")[0];
            try{
				//ZhiLianResumeParser parser=new ZhiLianResumeParser(htmlContent,"");
            	ResumePreProcessFactory resumePreProcessFactory = new ResumePreProcessFactory();
            	String filename = filelist[i];
            	System.out.println("开始解析文件： "+filename);
//        		TextResumeParser parser = new TextResumeParser(filePath+filename);
        		String outputFilename = filelist[i].split("\\.")[0]+".out.txt";	 
        		ResumeInfo resumeInfo = resumePreProcessFactory.preProcess(filePath+filename);
        		String source = resumeInfo.getResumeSource();
            	ArrayList<String> resumeContentList = resumeInfo.getSourceDataList();
            	TextResumeParser parser = new TextResumeParser(resumeContentList);
//	 			p.printResume(filePath+outputFilename,parser);
            	TextResumeData tr = new TextResumeData();
            	tr = parser.parse();
        		p.printResume(filePath+outputFilename,tr);
        		
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
	}
	
	public void printResume(String outfilePath,TextResumeData textResumeData) throws IOException{
		FileWriter fw = new FileWriter(outfilePath);     
		BufferedWriter out=new BufferedWriter(fw);
		out.write("-----------------------------");
		out.newLine();
		out.write("个人信息");
		out.newLine();
		
		String name = textResumeData.getName();
		if((name!=null) && (!name.equals(""))){
			out.write("姓名："+name);
			out.newLine();
		}
		String email = textResumeData.getEmail();
		if((email!=null) &&(!email.equals(""))){
			out.write("邮箱："+email);
			out.newLine();
		}
		String identityID = textResumeData.getIdentityID();
		if((identityID!=null) && (!identityID.equals(""))){
			out.write("身份证号码："+identityID);
			out.newLine();
		}
		String phone = textResumeData.getPhone();
		if(!phone.equals("")){
			out.write("电话："+phone);
			out.newLine();
		}
		int age = textResumeData.getAge();
		if(age!=0){
			out.write("年龄："+age);
			out.newLine();
		}
		String gender = textResumeData.getGender();
		if(!gender.equals("")){
			out.write("性别："+gender);
			out.newLine();
		}
		String birthDay = textResumeData.getBirthday();
		if(!birthDay.equals("")){
			out.write("出生年月："+birthDay);
			out.newLine();
		}
//		String school = parser.extractSchool(basicInfo);
//		if(!school.equals("")){
//			out.write("学校："+school);
//			out.newLine();
//		}
//		String degree = parser.extractDegree(basicInfo);
//		if(!degree.equals("")){
//			out.write("学历："+degree);
//			out.newLine();
//		}
//		
//		String major = parser.extractMajor(basicInfo);
//		if(!major.equals("")){
//			out.write("专业："+major);
//			out.newLine();
//		}
		
		if(textResumeData.getEducationExperience() !=null){
			out.write("-----------------------------");	
			out.newLine();
			out.write("教育经历");	
			out.newLine();
			if(textResumeData.getEducationExperience().size()==0){
				if(textResumeData.getEduSourceText()!=null){
					out.write("教育经历原文：");
					out.newLine();
					out.write(textResumeData.getEduSourceText());
					out.newLine();
				}			
			}else{
				for(int i=0;i<textResumeData.getEducationExperience().size();i++){
					out.write("教育经历 "+i+":");
					out.newLine();
					out.write("开始时间: "+((EducationExperienceData)textResumeData.getEducationExperience().get(i)).getStartTime());
					out.newLine();
					out.write("结束时间: "+((EducationExperienceData)textResumeData.getEducationExperience().get(i)).getEndTime());
					out.newLine();
					out.write("学校: "+((EducationExperienceData)textResumeData.getEducationExperience().get(i)).getSchool());
					out.newLine();
					out.write("专业: "+((EducationExperienceData)textResumeData.getEducationExperience().get(i)).getMajor());
					out.newLine();
					out.write("学历: "+((EducationExperienceData)textResumeData.getEducationExperience().get(i)).getDegree());
					out.newLine();
					out.write("");
					out.newLine();
				}
			}
		}
		
		if(textResumeData.getWorkExperience() != null){
			out.write("-----------------------------");	
			out.newLine();
			out.write("工作经历");	
			out.newLine();
			if(textResumeData.getWorkExperience().size()==0){
				if(textResumeData.getWorkSourceText()!=null){
					out.write("工作经历原文：");
					out.newLine();
					out.write(textResumeData.getWorkSourceText());
					out.newLine();
				}			
			}else{
				for(int i=0;i<textResumeData.getWorkExperience().size();i++){
					out.write("工作经历 "+i+":");
					out.newLine();
					out.write("开始时间: "+((WorkExperienceData)textResumeData.getWorkExperience().get(i)).getStartTime());
					out.newLine();
					out.write("结束时间: "+((WorkExperienceData)textResumeData.getWorkExperience().get(i)).getEndTime());
					out.newLine();
					out.write("公司: "+((WorkExperienceData)textResumeData.getWorkExperience().get(i)).getCompanyName());
					out.newLine();
					out.write("职位: "+((WorkExperienceData)textResumeData.getWorkExperience().get(i)).getPositionTitle());
					out.newLine();
					out.write("工作描述: "+((WorkExperienceData)textResumeData.getWorkExperience().get(i)).getJobDesc());
					out.newLine();
					out.write("");
					out.newLine();
				}
			}
		}
		
		if(textResumeData.getProjectExperience() != null){
			out.write("-----------------------------");
			out.newLine();
			out.write("项目经历");
			out.newLine();
			if(textResumeData.getProjectExperience().size()==0){
				if(textResumeData.getProjectSourceText()!=null){
					out.write("项目经历原文：");
					out.newLine();
					out.write(textResumeData.getProjectSourceText());
					out.newLine();
				}
			}else{
				for(int i=0;i<textResumeData.getProjectExperience().size();i++){
					out.write("项目经历 "+i+":");
					out.newLine();
					out.write("开始时间: "+((ProjectExperienceData)textResumeData.getProjectExperience().get(i)).getStartTime());
					out.newLine();
					out.write("结束时间: "+((ProjectExperienceData)textResumeData.getProjectExperience().get(i)).getEndTime());
					out.newLine();
					out.write("项目名称: "+((ProjectExperienceData)textResumeData.getProjectExperience().get(i)).getProjectTitle());
					out.newLine();
					out.write("项目描述: "+((ProjectExperienceData)textResumeData.getProjectExperience().get(i)).getProjectDesc());
					out.newLine();
					out.write("");
					out.newLine();
				}
			}			
		}		
		
		if(textResumeData.getSkillSourceText() != null){
			out.write("-----------------------------");
			out.newLine();
			out.write("技能描述原文:");
			out.newLine();
			out.write(textResumeData.getSkillSourceText());
			out.newLine();
		}
		
		if(textResumeData.getSelfEvaluation()!=null){
			out.write("-----------------------------");
			out.newLine();
			out.write("自我评价原文:");
			out.newLine();
			out.write(textResumeData.getSelfEvaluation());
			out.newLine();
		}
		out.close();
	}
	public void printResume(String outfilePath,TextResumeParser parser) throws IOException{
		FileWriter fw = new FileWriter(outfilePath);     
		BufferedWriter out=new BufferedWriter(fw);
		parser.preProcess();
		
		Iterator<Entry<String, SectionInfo>> iter = parser.sectionMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			SectionInfo sectionInfo = (SectionInfo)(entry.getValue());
			String content = parser.getContent(sectionInfo.start,sectionInfo.end);
			parser.sectionSourceTextMap.put(key.toString(), content);
		}
		
		iter = parser.sectionMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			SectionInfo sectionInfo = (SectionInfo)(entry.getValue());
			if(key.toString().equals("个人信息")){
				out.write("-----------------------------");
				out.newLine();
				out.write("个人信息");
				out.newLine();
				String basicInfo = parser.getContent2(sectionInfo.start,sectionInfo.end);
				String name = parser.extractName(basicInfo);
				if(!name.equals("")){
					out.write("姓名："+name);
					out.newLine();
				}
				String email = parser.extractEmail(basicInfo);
				if(!email.equals("")){
					out.write("邮箱："+email);
					out.newLine();
				}
				String identityID = parser.extractIdentityID(basicInfo);
				if(!identityID.equals("")){
					out.write("身份证号码："+identityID);
					out.newLine();
				}
				String phone = parser.extractPhone(basicInfo);
				if(!phone.equals("")){
					out.write("电话："+phone);
					out.newLine();
				}
				int age = parser.extractAge(basicInfo);
				if(age!=0){
					out.write("年龄："+age);
					out.newLine();
				}
				String gender = parser.extractGender(basicInfo);
				if(!gender.equals("")){
					out.write("性别："+gender);
					out.newLine();
				}
				String birthDay = parser.extractBirthday(basicInfo);
				if(!birthDay.equals("")){
					out.write("出生年月："+birthDay);
					out.newLine();
				}
				String school = parser.extractSchool(basicInfo);
				if(!school.equals("")){
					out.write("学校："+school);
					out.newLine();
				}
				String degree = parser.extractDegree(basicInfo);
				if(!degree.equals("")){
					out.write("学历："+degree);
					out.newLine();
				}
				
				String major = parser.extractMajor(basicInfo);
				if(!major.equals("")){
					out.write("专业："+major);
					out.newLine();
				}
				
			}else if(key.toString().indexOf("教育") >= 0){
				ArrayList<EducationExperienceData> educationExperienceDataList = parser.extractEducationExperience(sectionInfo.start,sectionInfo.end);
				out.write("-----------------------------");
				out.newLine();
				if(educationExperienceDataList.size()==0){
					out.write("教育经历原文");
					out.newLine();
					out.write(parser.sectionSourceTextMap.get(key.toString()));
					out.newLine();
				}else{
					for(int i=0;i<educationExperienceDataList.size();i++){
						out.write("教育经历 "+i+":");
						out.newLine();
						out.write("开始时间: "+((EducationExperienceData)educationExperienceDataList.get(i)).getStartTime());
						out.newLine();
						out.write("结束时间: "+((EducationExperienceData)educationExperienceDataList.get(i)).getEndTime());
						out.newLine();
						out.write("学校: "+((EducationExperienceData)educationExperienceDataList.get(i)).getSchool());
						out.newLine();
						out.write("专业: "+((EducationExperienceData)educationExperienceDataList.get(i)).getMajor());
						out.newLine();
						out.write("学历: "+((EducationExperienceData)educationExperienceDataList.get(i)).getDegree());
						out.newLine();
						out.write("");
						out.newLine();
					}
				}
			}else if(key.toString().indexOf("工作") >= 0){
				ArrayList<WorkExperienceData>  workExperienceDataList = parser.extractWorkExperience(sectionInfo.start,sectionInfo.end);
				out.write("-----------------------------");
				if(workExperienceDataList.size()==0){
					out.newLine();
					out.write("工作经历原文");
					out.newLine();
					out.write(parser.sectionSourceTextMap.get(key.toString()));
					out.newLine();
				}else{
					for(int i=0;i<workExperienceDataList.size();i++){
						out.newLine();
						out.write("工作经历 "+i+":");
						out.newLine();
						out.write("开始时间: "+((WorkExperienceData)workExperienceDataList.get(i)).getStartTime());
						out.newLine();
						out.write("结束时间: "+((WorkExperienceData)workExperienceDataList.get(i)).getEndTime());
						out.newLine();
						out.write("公司: "+((WorkExperienceData)workExperienceDataList.get(i)).getCompanyName());
						out.newLine();
						out.write("职位: "+((WorkExperienceData)workExperienceDataList.get(i)).getPositionTitle());
						out.newLine();
						out.write("工作描述: "+((WorkExperienceData)workExperienceDataList.get(i)).getJobDesc());
						out.newLine();
						out.write("");
						out.newLine();
					}
				}
				
			}else if(key.toString().indexOf("实习") >= 0){
				
			}else if(key.toString().indexOf("项目") >= 0){
				ArrayList<ProjectExperienceData> projectExperienceDataList = parser.extractProjectExperience(sectionInfo.start, sectionInfo.end);
				out.write("-----------------------------");
				out.newLine();
				if(projectExperienceDataList.size()==0){
					out.write("项目经历原文");
					out.newLine();
					out.write(parser.sectionSourceTextMap.get(key.toString()));
					out.newLine();
				}else{
					for(int i=0;i<projectExperienceDataList.size();i++){
						out.write("项目经历 "+i+":");
						out.newLine();
						out.write("开始时间: "+((ProjectExperienceData)projectExperienceDataList.get(i)).getStartTime());
						out.newLine();
						out.write("结束时间: "+((ProjectExperienceData)projectExperienceDataList.get(i)).getEndTime());
						out.newLine();
						out.write("项目名称: "+((ProjectExperienceData)projectExperienceDataList.get(i)).getProjectTitle());
						out.newLine();
						out.write("项目描述: "+((ProjectExperienceData)projectExperienceDataList.get(i)).getProjectDesc());
						out.newLine();
						out.write("");
						out.newLine();
					}
				}
			}else if(key.toString().indexOf("培训") >= 0){
				
			}else if(key.toString().indexOf("技能") >= 0 || key.toString().indexOf("能力") >= 0){
				String skillDesc = parser.sectionSourceTextMap.get(key.toString());
				out.write("-----------------------------");
				out.newLine();
				out.write("技能描述原文:");
				out.newLine();
				out.write(skillDesc);
				out.newLine();
			}else if(key.toString().indexOf("评价") >= 0 || key.toString().indexOf("自我") >= 0){
				String selfEvaluation = parser.extractSelfEvaluation(sectionInfo.start, sectionInfo.end);
				out.write("-----------------------------");
				out.newLine();
				out.write("自我评价:");
				out.newLine();
				out.write(selfEvaluation);
				out.newLine();
			}else if(key.toString().indexOf("求职") >= 0){
				out.write("-----------------------------");
				out.newLine();
				JobTarget jobTarget = parser.extractJobTarget(sectionInfo.start, sectionInfo.end);
			}
//			out.write(key.toString()+":  "+sectionInfo.start+" ,"+sectionInfo.end);
		} 
		out.close();
	}

}
