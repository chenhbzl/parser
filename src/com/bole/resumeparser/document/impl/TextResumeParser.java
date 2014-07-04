package com.bole.resumeparser.document.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bole.config.ResourcesConfig;
import com.bole.resumeparser.document.impl.AbstractResumeParser.SectionInfo;
import com.bole.resumeparser.exception.ResumeParseException;
import com.bole.resumeparser.html.impl._51jobResumeParser;
import com.bole.resumeparser.models.CertificateData;
import com.bole.resumeparser.models.EducationExperienceData;
import com.bole.resumeparser.models.JobTarget;
import com.bole.resumeparser.models.LanguageSkillData;
import com.bole.resumeparser.models.OtherInfoData;
import com.bole.resumeparser.models.PracticalExperienceData;
import com.bole.resumeparser.models.ProfessionalSkillData;
import com.bole.resumeparser.models.ProjectExperienceData;
import com.bole.resumeparser.models.ResumeData;
import com.bole.resumeparser.models.TextResumeData;
import com.bole.resumeparser.models.TrainingExperienceData;
import com.bole.resumeparser.models.WorkExperienceData;

public class TextResumeParser extends AbstractResumeParser implements DocumentResumeParserInterface{
	/**
	 *1. 先对文本进行分段，在分段完成后对各个段进行解析，目前主要对基本信息，工作经历，项目经历，教育经验和专业技能进行解析，其他段由于差距很多，暂不支持解析
	 *2. 如果通过上述方法还是没有分析出教育经历和工作经历则通过全文匹配方式来获取工作经验，和教育经验（项目经验过于灵活，暂时不通过全文匹配方式来获取）
	 *3. 在通过全文匹配方式分析时候需要分析工作的时候将工作经验的开始部分一直到连续两行空行作为工作段落，取出的公司和职位之后的所有信息作为工作描述 
	 */

	public String filePath = "./resumes/docresume/test.txt";
	public String fileName = "";
	String nameStartRegex = "";

	TextResumeData resumedata = new TextResumeData();	

	HashMap<String,SectionInfo> sectionMap = new HashMap<String,SectionInfo>();  //用于保存有段名的段落情况
	ArrayList<SectionInfo> sectionInfoList = new ArrayList<SectionInfo>();  //该段落是通过连续两行空行来分割的
	HashMap<String,String> sectionSourceTextMap = new HashMap<String,String>();
	
	ArrayList<String> resumeContentList = new ArrayList<String>();
	
	boolean isJobTargetExisted = false;  //是否通过段落标题获取到求职意向
	boolean isWorkExperienceExisted = false;  //是否通过段落标题获取到工作经历
	boolean isEduExperienceExisted = false;  //是否通过段落标题获取到教育经历
	boolean isProExperienceExisted = false;  //是否通过段落标题获取到项目经历
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ResumeParseException 
	 */
	public static void main(String[] args) throws IOException, ResumeParseException {		
		// TODO Auto-generated method stub		
//		ResourcesConfig.loadConfig("./src/main/java/resources");
		String filePath = "/home/bobby/mnt/repo/resumes/095000.doc";
		TextResumeParser textResumeParser = new TextResumeParser(filePath);
		textResumeParser.convert(filePath);
		textResumeParser.parse();
	}	

	@Override
	public void extractContactInfo(int start, int end) {
		// TODO Auto-generated method stub
		return ;
	}

	@Override
	public String removeHtmlTag(String htmlStr) {
		// TODO Auto-generated method stub
		return super.removeHtmlTag(htmlStr);
	}

	@Override
	public TextResumeData parse() throws ResumeParseException{
		// TODO Auto-generated method stub
		preProcess();
		Iterator<Entry<String, SectionInfo>> iter = sectionMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			SectionInfo sectionInfo = (SectionInfo)(entry.getValue());
			String content = getContent(sectionInfo.start,sectionInfo.end);
			sectionSourceTextMap.put(key.toString(), content);
		}
		
		iter = sectionMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			SectionInfo sectionInfo = (SectionInfo)(entry.getValue());
			System.out.println(key.toString() + ": "+sectionInfo.start+","+sectionInfo.end);
			if(key.toString().equals("个人信息")){
				extractBasicInfo(sectionInfo.start,sectionInfo.end);
			}else if(key.toString().indexOf("教育") >= 0){
				isEduExperienceExisted = true;
				ArrayList<EducationExperienceData> educationExperienceDataList = extractEducationExperience(sectionInfo.start,sectionInfo.end);
				resumedata.setEducationExperience(educationExperienceDataList);
				resumedata.setEduSourceText(sectionSourceTextMap.get(key.toString()));
			}else if(key.toString().indexOf("工作") >= 0){
				isWorkExperienceExisted = true;
				ArrayList<WorkExperienceData>  workExperienceDataList = extractWorkExperience2(sectionInfo.start,sectionInfo.end,true);
				resumedata.setWorkExperience(workExperienceDataList);
				resumedata.setWorkSourceText(sectionSourceTextMap.get(key.toString()));
			}else if(key.toString().indexOf("实习") >= 0){
				
			}else if(key.toString().indexOf("项目") >= 0){
				ArrayList<ProjectExperienceData> projectExperienceDataList = extractProjectExperience(sectionInfo.start, sectionInfo.end);
				resumedata.setProjectExperience(projectExperienceDataList);
				resumedata.setProjectSourceText(sectionSourceTextMap.get(key.toString()));
			}else if(key.toString().indexOf("培训") >= 0){
				
			}else if(key.toString().indexOf("技能") >= 0 || key.toString().indexOf("能力") >= 0){
				String s = sectionSourceTextMap.get(key.toString());
				resumedata.setSkillSourceText(sectionSourceTextMap.get(key.toString()));
			}else if(key.toString().indexOf("评价") >= 0 || key.toString().indexOf("自我") >= 0){
				String selfEvaluation = extractSelfEvaluation(sectionInfo.start, sectionInfo.end);
				resumedata.setSelfEvaluation(selfEvaluation);
			}else if(key.toString().indexOf("求职") >= 0){
				isJobTargetExisted = true;
				JobTarget jobTarget = extractJobTarget(sectionInfo.start, sectionInfo.end);
				resumedata.setJobTarget(jobTarget);
				resumedata.setJobTragetSourceText(sectionSourceTextMap.get(key.toString()));
			}
		}
		
		if(!isEduExperienceExisted){
			if(sectionInfoList.size() > 1){
				//如果没有通过段标题获取到教育经历段 则直接全文搜索教育经历
				resumedata.setEducationExperience(searchEducationExperience());	
			}		
		}
		if(!isWorkExperienceExisted){
			if(sectionInfoList.size() > 1){
				//如果没有通过段标题获取到工作经历段 则直接全文搜索工作经历
				resumedata.setWorkExperience(searchWorkExperience());
			}
		}
		if(!isJobTargetExisted){
			if(sectionInfoList.size() > 1){
				resumedata.setJobTarget(searchJobTarget());
			}
		}
		return resumedata;
	}
	
	public String getContent(int start, int end){
		String content = "";
		String line = "";
		for(int i=start;i<end;i++){
			line = resumeContentList.get(i);
			if(content.equals("")){
				content = line;
			}else{
				content = content + "\r\n" + line;
			}
		}
		return content;
	}
	
	public String getContent2(int start, int end){
		String content = "";
		String line = "";
		for(int i=start;i<end;i++){
			line = resumeContentList.get(i);
			if(content.equals("")){
				content = line;
			}else{
				content = content + "###" + line;
			}
		}
		return content;
	}
	
	public ArrayList<EducationExperienceData> searchEducationExperience(){
		//从每个段中去搜索教育经历
		ArrayList<EducationExperienceData> educationExperienceDataList = new ArrayList<EducationExperienceData>();
		for(int i=0;i<sectionInfoList.size();i++){
			SectionInfo sectionInfo = sectionInfoList.get(i);
			educationExperienceDataList = extractEducationExperience(sectionInfo.start,sectionInfo.end);
			if(educationExperienceDataList.size()>0){
				break;
			}
		}
		return educationExperienceDataList;
	}
	
	public ArrayList<WorkExperienceData> searchWorkExperience(){
		//从每个段中去搜索工作经历
		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
		for(int i=0;i<sectionInfoList.size();i++){
			SectionInfo sectionInfo = sectionInfoList.get(i);
			workExperienceDataList = extractWorkExperience2(sectionInfo.start,sectionInfo.end,false);
			if(workExperienceDataList.size()>0){
				break;
			}
		}
		return workExperienceDataList;
	}
	
	public JobTarget searchJobTarget(){
		//从每个段中去搜索求职意向
		JobTarget jobTarget = null;
		for(int i=0;i<sectionInfoList.size();i++){
			SectionInfo sectionInfo = sectionInfoList.get(i);
			jobTarget = extractJobTarget(sectionInfo.start,sectionInfo.end);
		}
		return jobTarget;
	}

	public void extractBasicInfo(int start, int end) {
		// TODO Auto-generated method stub
		String basicInfo = "";
		for(int i=start;i<end;i++){
			String line = resumeContentList.get(i);
			if(line.trim() != ""){
				if(basicInfo == ""){
					basicInfo = line;
					continue;
				}
				basicInfo = basicInfo + "###" + line;
			}
		}
		
		if(resumedata.getName()!="" && resumedata.getName()!="简历"){
			String name = extractName(basicInfo);
			resumedata.setName(name);
			
			String email = extractEmail(basicInfo);
			resumedata.setEmail(email);
			
			String identityID = extractIdentityID(basicInfo);
			resumedata.setIdentityID(identityID);
			
			String phone = extractPhone(basicInfo);
			resumedata.setPhone(phone);
			
			String qq = extractQQ(basicInfo);
			resumedata.setQq(qq);
			
			int age = extractAge(basicInfo);
			resumedata.setAge(age);
			
			String gender = extractGender(basicInfo);
			resumedata.setGender(gender);
			
			String birthDay = extractBirthday(basicInfo);
			resumedata.setBirthday(birthDay);
			
			String school = extractSchool(basicInfo);
			resumedata.setLatestSchool(school);

			String degree = extractDegree(basicInfo);
			resumedata.setLatestDegree(degree);
			
			String major = extractMajor(basicInfo);
			resumedata.setLatestMajor(major);
			
			Pattern pattern=Pattern.compile("(未婚|已婚)",Pattern.CASE_INSENSITIVE);							
			Matcher matcher=pattern.matcher(basicInfo);
	        if(matcher.find()){
	        	String maritalStatus = matcher.group(1);
	        	resumedata.setMaritalStatus(maritalStatus);
	        }
	        
	        String houseHolds = extractHouseHolds(basicInfo);
	        resumedata.setHouseHolds(houseHolds);
		}
	}
	
	public String extractName(String content){
		//如果名字之间出现空格，则将名字长度限制在三个字内，也有可能是两个字
		//先取三个字
		String name = "";
		
		if(resumedata.getName() == null ){
			Pattern pattern=Pattern.compile("姓\\s*名.*?(("+ResourcesConfig.nameStartRegex+")(\\s*[\u4e00-\u9fa5]){1,2})\\s*(应\\s*聘|求\\s*职|性\\s*别|籍\\s*贯|#|$|\\s)");
			Matcher matcher=pattern.matcher(content);
            while(matcher.find()){
            	String match = matcher.group(1);
            	match = match.replaceAll(" ", "");
            	if(match.length()>1 && match.length()<=4){
            		if(!match.equals("简历")){
            			name = match;
            			return name;
            		}            		
            	}
            }
        }
		
		//判断名字是否连着，如果连着就直接取出
		name = "";
		String nameRegex = "(^|#|\\s|\ufeff)(("+ResourcesConfig.nameStartRegex+")([\\s*\u4e00-\u9fa5]){1,2})(#|\\s|[^\u4e00-\u9fa5]|$)";
		Pattern pattern=Pattern.compile(nameRegex);
        Matcher matcher=pattern.matcher(content);        
        while(matcher.find()){
        	//如果满足第一种方式则直接取出
        	String match = matcher.group(2);
        	match = match.replaceAll(" ", "");
        	if(match.length()>1 && match.length()<=4){
        		if(!match.equals("简历")){
        			name = match;
        			return name;
        		}
        	}
        }
        
        pattern=Pattern.compile("(^|###|\\s|\ufeff)(("+ResourcesConfig.nameStartRegex+")(\\s*[\u4e00-\u9fa5]){1,2})\\s*([^\u4e00-\u9fa5]|\\s|应\\s*聘|求\\s*职|性\\s*别|籍\\s*贯|#|$)");
        matcher=pattern.matcher(content);
        while(matcher.find()){
        	//如果满足第二种方式则直接取出
        	String match = matcher.group(2);
        	match = match.replaceAll(" ", "");
        	if(match.length()>1 && match.length()<=4){
        		if(!match.equals("简历")){
        			name = match;
        			return name;
        		}
        	}
        }
        
        if(resumedata.getName() == null ){
        	pattern=Pattern.compile("姓.*?名.*?(("+ResourcesConfig.nameStartRegex+")(\\s*[\u4e00-\u9fa5]){1,2})(#|$|[^\u4e00-\u9fa5])");
            matcher=pattern.matcher(content);
            while(matcher.find()){
            	String match = matcher.group(1);
            	match = match.replaceAll(" ", "");
            	if(match.length()>1 && match.length()<=4){
            		if(!match.equals("简历")){
            			name = match;
            			return name;
            		}            		
            	}
            }
        }		

		return name;
	}
	
	public static boolean isChinese(char c) {		 
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c); 
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS 
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS 
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A 
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION 
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION 
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) { 
            return true; 
        } 
        return false; 
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
	
	public String extractPhone(String content){
		//提取手机号码， 手机号码之间可能有空格
		String phone = "";
		Pattern pattern=Pattern.compile("(((\\d{3,4}-)\\d{7,8})|(1[3589]\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d))(\\D|$)");
		Matcher matcher=pattern.matcher(content);
        if(matcher.find()){
        	phone = matcher.group(1);
        	phone = phone.replaceAll(" ", "");
        	if(phone.indexOf("-")>=0){
        		resumedata.setPhone(phone);
        	}else{
        		if(phone.length()==11){
        			resumedata.setPhone(phone);
        		}
        	}
        }
        return phone;
	}
	
	public String extractQQ(String content){
		//提取手机号码， 手机号码之间可能有空格
		String qq = "";
		Pattern pattern=Pattern.compile("qq(:|:)?(\\d+)",Pattern.CASE_INSENSITIVE);
		Matcher matcher=pattern.matcher(content);
        if(matcher.find()){
        	qq = matcher.group(2);
        }
        return qq;
	}
	
	
	public String extractBirthday(String content){
		//提取生日
		String birthDay = "";
		//Pattern pattern=Pattern.compile("([1][09][6789][0-9](.*?\\d+.*?\\d+\\s*(日)?|.*?\\d+\\s*(月)?|\\s*(年)?))(###|\\s|$)");
//		Pattern pattern=Pattern.compile("([1][09][6789][0-9]\\s*(年|/|.|-)\\s*\\d+?(\\s*\\d+\\s*(月|/|.|-)(\\s*\\d+\\s*(日)))(###|\\$)");
		Pattern pattern=Pattern.compile("(([1][9][6789][0-9]\\s*(年|/|.|-))(\\s*\\d+\\s*(月|/|.|-))?(\\s*\\d+(\\s*日)?)?)(#|$|\\s)");
		
	//	Pattern pattern=Pattern.compile("([1][09][6789][0-9](.*?\\d+\\s*(月|/|.|-)?.*?\\d+\\s*(日)?|.*?\\d+\\s*(月|/|.|-)?\\s*\\d+|.*?\\d+\\s*(月)?|.*?\\d+|.*?\\d+(月)?|年)?)(###|\\s|$)");
		Matcher matcher=pattern.matcher(content);
        if(matcher.find()){
        	birthDay = matcher.group(1);
        }
        return birthDay;
	}

	@Override
	public String getEmail() {
		// TODO Auto-generated method stub		
		return "";
	}

	public String extractEmail(String content) {
		// TODO Auto-generated method stub
		String email = "";
		Pattern pattern=Pattern.compile("(email|电子邮件|邮箱).*?(:)?(\\S+@\\S)(\\s|$)",Pattern.CASE_INSENSITIVE);
		
		Matcher matcher=pattern.matcher(content);
        if(matcher.find()){
        	email = matcher.group(1);
        	return email;
        }
		
		//Pattern pattern=Pattern.compile("([a-z0-9]*[.-_]?[a-z0-9]+)*@([a-z0-9]*[-_.]?[a-z0-9]+)+[.][a-z]{2,3}([.][a-z]{2})?",Pattern.CASE_INSENSITIVE);
		pattern=Pattern.compile("([-_.a-z0-9]+@[-_.a-z0-9]+)",Pattern.CASE_INSENSITIVE);
		
		matcher=pattern.matcher(content);
        if(matcher.find()){
        	email = matcher.group(1);
        }
        return email;
	}

	@Override
	public String getAge() {
		// TODO Auto-generated method stub
		return super.getAge();
	}
	
	public int extractAge(String content){
		int age = 0;
		Pattern pattern=Pattern.compile("(\\d\\d)(岁)");
		Matcher matcher=pattern.matcher(content);
        if(matcher.find()){
        	age = Integer.parseInt(matcher.group(1));
        	return age;
        }
        
        if(age==0){
        	pattern=Pattern.compile("(年\\s*龄|年\\s*纪).*?(\\d{2})");
    		matcher=pattern.matcher(content);
            if(matcher.find()){
            	age = Integer.parseInt(matcher.group(2));
            }
        }
        return age;
	}

	public String extractIdentityID(String content){
		//获取身份证号码
		String identityID = "";
		Pattern pattern=Pattern.compile("(\\d{15}$|\\d{18}|\\d{17}(\\d|X|x))");
		Matcher matcher=pattern.matcher(content);
        if(matcher.find()){
        	identityID = matcher.group(1);
        }
        return identityID;
	}
	@Override
	public String Gender() {
		// TODO Auto-generated method stub
		return super.Gender();
	}
	
	public String extractGender(String content) {
		// TODO Auto-generated method stub
		String gender = "";
		Pattern pattern=Pattern.compile("性.*?别.*?(男|女)");
		Matcher matcher=pattern.matcher(content);
        if(matcher.find()){
        	gender = matcher.group(1);
        	return gender;
        }
        
        if(gender.equals("")){
        	pattern=Pattern.compile("(男|女)");
    		matcher=pattern.matcher(content);
            if(matcher.find()){
            	gender = matcher.group(1);
            }
        }
        return gender;
        
	}
	
	public String extractSchool(String content){
		//获取毕业学院。学校信息可能出现在基本信息也可能出现在教育经历中
		String school = "";
		Pattern pattern=Pattern.compile("("+ResourcesConfig.universityRegex+")(\\s|$|###)");
		Matcher matcher=pattern.matcher(content);
        if(matcher.find()){
        	school = matcher.group(1);
        }
        if(school.equals("")){
        	pattern=Pattern.compile("([\u4e00-\u9fa5]+(学院|学校|大学|中学|小学))");
    		matcher=pattern.matcher(content);
            if(matcher.find()){
            	school = matcher.group(1);
            }
        }
        
        return school;
	}
	
	public String extractDegree(String content){
		//获取学历
		String degree = "";
		Pattern pattern=Pattern.compile("("+ResourcesConfig.degreeRegex+")");
		Matcher matcher=pattern.matcher(content);
        if(matcher.find()){
        	degree = matcher.group(1);
        }
		return degree;
	}
	
	public String extractMajor(String content){
		String major = "";
		
		Pattern pattern=Pattern.compile("(^|#|\\s)专\\s*业(:|:|\\s+|###)([\u4e00-\u9fa5|[a-z]]+)($|#|\\s[^\u4e00-\u9fa5])");
		Matcher matcher=pattern.matcher(content);
        if(matcher.find()){
        	major = matcher.group(3);
        }
        if(major.equals("")){
        	pattern=Pattern.compile("(^|#|\\s)(([\u4e00-\u9fa5]+))专\\s*业");
    		matcher=pattern.matcher(content);
            if(matcher.find()){
            	major = matcher.group(2);
            }
        }
        
		return major;
	}

	public String extractHouseHolds(String content){
		String houseHolds = "";
		Pattern pattern=Pattern.compile("(^|#|\\s)户\\s*籍(:|\\s+|###)([\u4e00-\u9fa5|[a-z]]+)($|#|\\s[^\u4e00-\u9fa5])");
		Matcher matcher=pattern.matcher(content);
        if(matcher.find()){
        	houseHolds = matcher.group(3);
        }
        
		return houseHolds;
	}
	@Override
	public String extractSelfEvaluation(int start, int end) {
		// TODO Auto-generated method stub
		String selfEvaluation = "";
		String line = "";
		for(int i=start;i<end;i++){
			line = resumeContentList.get(i);
			if(selfEvaluation.equals("")){
				selfEvaluation = line;
			}else{
				selfEvaluation = selfEvaluation + "\r\n" + line;
			}
		}
		return selfEvaluation;
	}

	@Override
	public JobTarget extractJobTarget(int start, int end) {
		// TODO Auto-generated method stub
		JobTarget jobTarget = new JobTarget();
		//职位
		String line = "";
		for(int i=start;i<end;i++){
			line = resumeContentList.get(i);
			//行业
			Pattern pattern=Pattern.compile("(行\\s*业)(:)?.*?(\\S.*?)(\\s|$|#)");
			Matcher matcher=pattern.matcher(line);
			if(matcher.find()){
				jobTarget.setJobIndustry(matcher.group(3));
			}
			
			pattern=Pattern.compile("(职\\s*位|职\\s*业|意\\s*向|岗\\s*位)(:)?.*?(\\S.*?)(\\s|$|#)");
			matcher=pattern.matcher(line);
			if(matcher.find()){
				jobTarget.setJobCareer(matcher.group(3));
			}
			
			//薪资
			pattern=Pattern.compile("(月\\s*薪|薪\\s*(资|水)|工\\s*资)(:)?.*?(\\S.*?)(\\s|$|#)");
			matcher=pattern.matcher(line);
			if(matcher.find()){
				jobTarget.setSalary(matcher.group(4));
			}
			//地点
			pattern=Pattern.compile("(地\\s*区|地\\s*点|工\\s*作\\s*地)(:)?.*?(\\S.*?)(\\s|$|#)");
			matcher=pattern.matcher(line);
			if(matcher.find()){
				jobTarget.setJobLocation(matcher.group(3));
			}
			//性质
			pattern=Pattern.compile("(性\\s*质)(:)?.*?(\\S.*?)(\\s|$|#)");
			matcher=pattern.matcher(line);
			if(matcher.find()){
				jobTarget.setJobCatagory(matcher.group(3));
			}
			//到岗时间
			pattern=Pattern.compile("(到\\s*岗\\s*时\\s*间|目\\s*前\\s*状\\s*况)(:)?.*?(\\S.*?)(\\s|$|#)");
			matcher=pattern.matcher(line);
			if(matcher.find()){
				jobTarget.setEnrollTime(matcher.group(3));
			}
		}
		
		return jobTarget;
	}

	@Override
	public String getResumeKeywords(int start, int end) {
		// TODO Auto-generated method stub
		return super.getResumeKeywords(start, end);
	}

	@Override
	public ArrayList<WorkExperienceData> extractWorkExperience(int start,
			int end) {
		// TODO Auto-generated method stub
		//默认项目经验一定会有时间或者公司，公司包含关键字“股份，有限，责任，公司”，首先通过时间来解析出有多少份工作经验，如果失败则通过这些公司关键字来解析出工作数量
		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
		
		ArrayList<String> schoolLineList = new ArrayList<String>();  //用户保存每份教育经历的学校所在行，用户提取专业
		
		//先遍历每一行，通过时间获取出工作经验个数
		int count = 0;
		for(int i=start;i<end;i++){
			String line = resumeContentList.get(i);
			if(line.equals("")){
				continue;
			}
			Pattern pattern=Pattern.compile("(((19[89]|20[01])[0-9]\\s*(年|/|[.]|-)?)(\\s*\\d+\\s*(月|/|[.]|-)?)?(\\s*\\d+(\\s*日)?)?)\\s*(.){1,2}\\s*((((19[89]|20[01])[0-9]\\s*(年|/|[.]|-)?)(\\s*\\d+\\s*(月|/|[.]|-)?)?(\\s*\\d+(\\s*日)?)?)|至\\s*今|现\\s*在)");
			Matcher matcher=pattern.matcher(line);
	        if(matcher.find()){	 
	        	schoolLineList.add(line);
	        	count++;
	        	WorkExperienceData workExperienceData = new WorkExperienceData();
	        	String startTime = matcher.group(1);
	        	String endTime = matcher.group(10);
	        	if(startTime.endsWith("-")){
	        		startTime = startTime.replaceAll("-", "");
	        	}
	        	workExperienceData.setStartTime(startTime);
	        	workExperienceData.setEndTime(endTime);
	        	workExperienceDataList.add(workExperienceData);
	        	continue;
	        }
		}
		
		if(count==0){
			//如果工作经验中没有时间，则通过公司关键字来确定公司个数，并确定工作经验个数
			for(int i=start;i<end;i++){
				String line = resumeContentList.get(i);
				if(line.equals("")){
					continue;
				}
				Pattern pattern=Pattern.compile("(:|^|###)\\s*([\u4e00-\u9fa5]+(公司|集团))(#|$|\\s)");
				Matcher matcher=pattern.matcher(line);
		        if(matcher.find()){ 
		        	schoolLineList.add(line);
		        	count++;
		        	WorkExperienceData workExperienceData = new WorkExperienceData();
		        	String companyName = matcher.group(2);
		        	workExperienceData.setCompanyName(companyName);
		        	workExperienceDataList.add(workExperienceData);
		        	continue;
		        }
			}
		}
		if(count>0){
			//工作经验个数不为0,开始提取其它字段
			int currentNum = 0; //表明已经获处理到第几个工作经验
			int startLine = start;  //指明下一个工作的开始行
			for(int i =0;i<count;i++){
				String jobDesc = "";
				String startTime = ((WorkExperienceData)workExperienceDataList.get(i)).getStartTime();
				String endTime = ((WorkExperienceData)workExperienceDataList.get(i)).getEndTime();
				String companyName = ((WorkExperienceData)workExperienceDataList.get(i)).getCompanyName();
				String positionTitle = ((WorkExperienceData)workExperienceDataList.get(i)).getPositionTitle();
				
				if(!startTime.equals("")){
					//工作有起止时间，表明公司个数是通过时间来匹配的
					for(int j=startLine;j<end;j++){
						String line = resumeContentList.get(j);
						companyName = ((WorkExperienceData)workExperienceDataList.get(i)).getCompanyName();
						positionTitle = ((WorkExperienceData)workExperienceDataList.get(i)).getPositionTitle();
						if(!companyName.equals("") && !positionTitle.equals("")){
							//如果公司和职位都已经获取到，且当前行不包括时间则直接把当前行并入到当前工作的工作描述中
							Pattern pattern=Pattern.compile("(((19[89]|20[01])[0-9]\\s*(年|/|[.]|-)?)(\\s*\\d+\\s*(月|/|[.]|-)?)?(\\s*\\d+(\\s*日)?)?)\\s*(.){1,2}\\s*((((19[89]|20[01])[0-9]\\s*(年|/|[.]|-)?)(\\s*\\d+\\s*(月|/|[.]|-)?)?(\\s*\\d+(\\s*日)?)?)|至\\s*今|现\\s*在)");
							Matcher matcher=pattern.matcher(line);
					        if(matcher.find()){
					        	currentNum ++;
					        	if(currentNum > 1){
					        		//处理到第二个工作经验，直接将工作描述写入到前一个工作描述中
					        		startLine = j;
									((WorkExperienceData)workExperienceDataList.get(i)).setJobDesc(jobDesc);
									break;
					        	}else{
					        		continue;
					        	}
					        }							
							if(j==end-1){
								//如果处理到最后一行，直接将工作描述写入
								jobDesc = jobDesc + "\r\n" + line;
								((WorkExperienceData)workExperienceDataList.get(i)).setJobDesc(jobDesc);
							}else{
								//如果还没有到最后一行，并且没有匹配到时间，直接将该行加入到工作描述中去
								jobDesc = jobDesc + "\r\n" +line;
							}
						}else{
							//如果没有获取公司或者职位还未获取到，则提取公司和职位
							if(companyName.equals("")){
								//提取公司
								companyName = extractCompany(line);		
								if(!companyName.equals("")){
									//如果提取到公司，则直接获取该行的另外一个字符串，并且该字符串不为公司名字，则直接为职位名称
									Pattern pattern=Pattern.compile("(([\u4e00-\u9fa5]|[a-z0-9])+[\u4e00-\u9fa5]+)($|#|\\s|[^\u4e00-\u9fa5])",Pattern.CASE_INSENSITIVE);
									Matcher matcher=pattern.matcher(line);
									while(matcher.find()){
										String match = matcher.group(1);
										if(match.equals(companyName)){
											positionTitle = match;
											break;
										}
									}
									if(positionTitle.equals("")){
										//如果没有获取到职位则直接从下一行中获取
										if(j < end-1){
											String nextLine = resumeContentList.get(j+1);
											if(nextLine.equals("") && j < end-2){
												nextLine = resumeContentList.get(j+2);
											}
											positionTitle = extractPosition(nextLine);
											if(!positionTitle.equals("")){
												((WorkExperienceData)workExperienceDataList.get(i)).setPositionTitle(positionTitle);
											}
										}										
									}else{
										((WorkExperienceData)workExperienceDataList.get(i)).setPositionTitle(positionTitle);
									}
								}
							}else{
								//表示已经提取到公司，但是没有提取到职位
								if(positionTitle.equals("职位")){
									//提取到的职位名为职位，说明职位名称在下一行
									positionTitle = extractPosition(line);
									((WorkExperienceData)workExperienceDataList.get(i)).setPositionTitle(positionTitle);									
								}else if(positionTitle.equals("")){
									//表示未提取到
									positionTitle = extractPosition(line);
									if(!positionTitle.equals("")){
										((WorkExperienceData)workExperienceDataList.get(i)).setPositionTitle(positionTitle);
									}else{
										if(j==end-1){
											//如果处理到最后一行，直接将工作描述写入
											jobDesc = jobDesc + "\r\n" + line;
											((WorkExperienceData)workExperienceDataList.get(i)).setJobDesc(jobDesc);
										}else{
											//如果还没有到最后一行，并且没有匹配到时间，直接将该行加入到工作描述中去
											jobDesc = jobDesc + "\r\n" +line;
										}
									}
								}else{
									if(j==end-1){
										//如果处理到最后一行，直接将工作描述写入
										jobDesc = jobDesc + "\r\n" + line;
										((WorkExperienceData)workExperienceDataList.get(i)).setJobDesc(jobDesc);
									}else{
										//如果还没有到最后一行，并且没有匹配到时间，直接将该行加入到工作描述中去
										jobDesc = jobDesc + "\r\n" +line;
									}
								}
							}
							if(!positionTitle.equals("") ){
								if(!companyName.equals("")){
									if(!positionTitle.equals(companyName)){
										positionTitle = extractPosition(line);
									}									
								}
								
							}
							
							if(!companyName.equals("")){
								if(i>0){
									((WorkExperienceData)workExperienceDataList.get(i)).setJobDesc(jobDesc);
								}
								((WorkExperienceData)workExperienceDataList.get(i)).setCompanyName(companyName);
							}
							if(!positionTitle.equals("")){
								if(i>0){
									((WorkExperienceData)workExperienceDataList.get(i)).setJobDesc(jobDesc);
								}
								((WorkExperienceData)workExperienceDataList.get(i)).setPositionTitle(positionTitle);
							}
							if(companyName.equals("") && positionTitle.equals("")){
								if(line.indexOf(startTime)>=0 && line.indexOf(endTime)>=0){
									continue;
								}
								jobDesc = jobDesc + "\r\n" +line;
								if(i==count-1){
									((WorkExperienceData)workExperienceDataList.get(i)).setJobDesc(jobDesc);
								}
							}
						}					
					}
				}else{
					//工作经验个数是通过公司的关键字来确定的
					for(int j=startLine;j<end;j++){
						String line = resumeContentList.get(j);
						companyName = extractCompany(line);
						positionTitle = ((WorkExperienceData)workExperienceDataList.get(i)).getPositionTitle();
						if(companyName.equals("")){
							//如果公司和职位都已经获取到，且当前行不包括时间则直接把当前行并入到当前工作的工作描述中
				        	if(positionTitle.equals("职位")){
								//提取到的职位名为职位，说明职位名称在下一行
								String currentTitle = extractPosition(line);
								if(currentTitle.equals(positionTitle)){
									//如果该行为职位 则直接取出下一行整行的内容作为职位
									if(j<end-1){
										((WorkExperienceData)workExperienceDataList.get(i)).setPositionTitle(resumeContentList.get(j+1));
										j++;
										continue;
									}										
								}								
							}else{
								String currentTitle = extractPosition(line);
								if(!currentTitle.equals("") && currentTitle.equals(positionTitle)){
									continue;
								}else{
									//该行为职位行，直接跳过
									if(j==end-1){
										//如果处理到最后一行，直接将工作描述写入
										jobDesc = jobDesc + "\r\n" + line;
										((WorkExperienceData)workExperienceDataList.get(i)).setJobDesc(jobDesc);
									}else{
										//如果还没有到最后一行，并且没有匹配到时间，直接将该行加入到工作描述中去
										jobDesc = jobDesc + "\r\n" +line;
									}
								}
							}
														
						}else{
							//如果在公司所在行，则从中提取职位
							if(positionTitle.equals("")){
								//提取职位
								Pattern pattern=Pattern.compile("(([\u4e00-\u9fa5]|[a-z0-9])+[\u4e00-\u9fa5]+)($|#|\\s)",Pattern.CASE_INSENSITIVE);
								Matcher matcher=pattern.matcher(line);
								while(matcher.find()){
									String match = matcher.group(1);
									if(!match.equals(companyName)){
										positionTitle = match;
										startLine = j+1;
										break;
									}
								}
							}
							//如果工作行中仍然未提取到公司，则进从下一行中提取
							if(positionTitle.equals("")){
								//如果没有获取到职位则直接从下一行中获取
								if(j < end-1){
									String nextLine = resumeContentList.get(j+1);
									positionTitle = extractPosition(nextLine);
									((WorkExperienceData)workExperienceDataList.get(i)).setPositionTitle(positionTitle);
								}										
							}else{
								((WorkExperienceData)workExperienceDataList.get(i)).setPositionTitle(positionTitle);
							}
							
							currentNum ++;
				        	if(currentNum > 1){
				        		//处理到第二个工作经验，直接将工作描述写入到前一个工作描述中
				        		j++;
				        		startLine = j;
								((WorkExperienceData)workExperienceDataList.get(i)).setJobDesc(jobDesc);
								break;
				        	}else{
				        		continue;
				        	}	
						}
					}
				}
			}
		}
		return workExperienceDataList;
	}

	
	public ArrayList<WorkExperienceData> extractWorkExperience2(int start,
			int end,boolean isWorkSection) {
		//isWorkSection 用于当前分析的段是否确认为工作经历段落，如果为工作经历段落，则即使在只有一个字段如工作时间 公司存在的情况下也解析出工作，如果该不确定是否为工作段落的时候，则必须在确保有工作时间和工作公司的情况下才能确保当前工作解析成功
		// TODO Auto-generated method stub
		//默认项目经验一定会有时间或者公司，公司包含关键字“股份，有限，责任，公司”，首先通过时间来解析出有多少份工作经验，如果失败则通过这些公司关键字来解析出工作数量
		ArrayList<WorkExperienceData> workExperienceDataList = new ArrayList<WorkExperienceData>();
		
		ArrayList<String> workContentList =  new ArrayList<String>();
		
		//先遍历每一行，通过时间获取出工作经验个数
		int count = 0;
		String content = "";
		for(int i=start;i<end;i++){
			String line = resumeContentList.get(i);			

			Pattern pattern=Pattern.compile("(((19[89]|20[01])[0-9]\\s*(年|/|[.]|-)?)(\\s*\\d+\\s*(月|/|[.]|-)?)?(\\s*\\d+(\\s*日)?)?)\\s*(.){0,4}\\s*((((19[89]|20[01])[0-9]\\s*(年|/|[.]|-)?)(\\s*\\d+\\s*(月|/|[.]|-)?)?(\\s*\\d+(\\s*日)?)?)|至\\s*今|现\\s*在)");
			Matcher matcher=pattern.matcher(line);
	        if(matcher.find()){	 
	        	if(count > 0){
	        		workContentList.add(content);
	        		content = line;
	        	}else{
	        		content = line;
	        	}
	        	count++;
	        	WorkExperienceData workExperienceData = new WorkExperienceData();
	        	String startTime = matcher.group(1);
	        	String endTime = matcher.group(10);
	        	if(startTime.endsWith("-")){
	        		startTime = startTime.replaceAll("-", "");
	        	}
	        	workExperienceData.setStartTime(startTime);
	        	workExperienceData.setEndTime(endTime);
	        	workExperienceDataList.add(workExperienceData);
	        	
	        	if(i==end-1){
	        		workContentList.add(content);
	        	}
	        	continue;
	        }else{
	        	content = content + "###" + line;	
	        	if(i==end-1){
	        		workContentList.add(content);
	        	}
	        }
		}
		
		if(count>0){
			for(int j=0;j<workContentList.size();j++){
				String line = workContentList.get(j);
				String positionTitle = extractPosition(line);
				String companyName = extractCompany(line);
				if(!isWorkSection){
					companyName = extractCompany(line,true);
				}
				String startTime = ((WorkExperienceData)workExperienceDataList.get(j)).getStartTime();
				String endTime = ((WorkExperienceData)workExperienceDataList.get(j)).getEndTime();
				if(companyName.equals("") && positionTitle.equals("")){
					//如果公司和职位都没有匹配到则直接将结束时间之后的所有内容作为工作描述
				}else{
					if(!companyName.equals("") && !positionTitle.equals("")){
						//如果公司名和职位名都匹配到，这直接将职位名和公司名之后的所有内容全部当作工作描述
						((WorkExperienceData)workExperienceDataList.get(j)).setCompanyName(companyName);
						((WorkExperienceData)workExperienceDataList.get(j)).setPositionTitle(positionTitle); 
					}
					if(!companyName.equals("") && positionTitle.equals("")){
						//如果公司名匹配到但是职位没有匹配到 则通过夹逼方法得到职位名，这直接将职位名或者公司名之后的所有内容全部当作工作描述
						((WorkExperienceData)workExperienceDataList.get(j)).setCompanyName(companyName);
						Pattern pattern=Pattern.compile("("+endTime+")(([\u4e00-\u9fa5]|[a-z0-9])+[\u4e00-\u9fa5]+)"+companyName);
						Matcher matcher=pattern.matcher(line);
				        if(matcher.find()){ 
				        	//如果在工作时间和公司名之间获取到句子 则表明该短句为职位
				        	positionTitle = matcher.group(2).replaceAll("###", "\r\n");
				        	((WorkExperienceData)workExperienceDataList.get(j)).setPositionTitle(positionTitle);

				        }else{
				        	//如果在工作时间和公司名之间没有获取到句子 则从公司名之后或者下一行中包行了短语 则为职位名
				        	pattern=Pattern.compile(companyName+"(([\u4e00-\u9fa5]|[a-z0-9])+[\u4e00-\u9fa5]+)(#|$)");
							matcher=pattern.matcher(line);
					        if(matcher.find()){
					        	//如果获取到职位则将职位之后的所有内容作为工作内容
					        	positionTitle = matcher.group(1).replaceAll("###", "\r\n");
					        	((WorkExperienceData)workExperienceDataList.get(j)).setPositionTitle(positionTitle);
					        }

				        }
					}
					if(companyName.equals("") && !positionTitle.equals("")){
						//如果公司名没有匹配到但是职位匹配到 则通过夹逼方法得到公司名，这直接将职位名或公司名之后的所有内容全部当作工作描述
						((WorkExperienceData)workExperienceDataList.get(j)).setPositionTitle(positionTitle);
						String regex = "";
						String tmpLine = "";
						if(line.indexOf(positionTitle)>line.indexOf(endTime)){
							tmpLine = line.substring(0,line.indexOf(positionTitle));
						}else{
							tmpLine = line.substring(0,line.indexOf(startTime));
						}
						regex = "(^|\\s)(([\u4e00-\u9fa5]|[a-z0-9])+)($|\\s|#)";
						Pattern pattern=Pattern.compile(regex);
						Matcher matcher=pattern.matcher(tmpLine);
				        if(matcher.find()){
				        	companyName = matcher.group(2).replaceAll("###", "\r\n");
				        	if(!companyName.equals(endTime) && !companyName.equals(positionTitle) && !companyName.equals(startTime)){
				        		((WorkExperienceData)workExperienceDataList.get(j)).setCompanyName(companyName);
				        	}else{
				        		companyName = "";
				        	}
				        	if(companyName.equals("")){
				        		while(matcher.find()){
						        	//如果在工作时间和公司名之间获取到句子 则表明该短句为职位
						        	companyName = matcher.group(2).replaceAll("###", "\r\n");
						        	if(!companyName.equals(endTime) && !companyName.equals(positionTitle) && !companyName.equals(startTime)){
						        		((WorkExperienceData)workExperienceDataList.get(j)).setCompanyName(companyName);
						        		break;
						        	}					        		
					        	}
				        	}
				        		        	
				        }else{
				        	//如果在工作时间和公司名之间没有获取到句子 则从公司名之后或者下一行中包行了短语 则为职位名
				        	regex = "";
							if(line.indexOf(endTime)<line.indexOf(positionTitle)){
								regex = positionTitle+".*?(([\u4e00-\u9fa5]|[a-z0-9])+[\u4e00-\u9fa5]+)(#|$)";
							}else{
								regex = endTime+".*?(([\u4e00-\u9fa5]|[a-z0-9])+[\u4e00-\u9fa5]+)(#|$)";
							}
				        	pattern=Pattern.compile(positionTitle+".*?(([\u4e00-\u9fa5]|[a-z0-9])+[\u4e00-\u9fa5]+)(#|$)");
							matcher=pattern.matcher(line);
					        if(matcher.find()){
					        	//如果获取到职位则将职位之后的所有内容作为工作内容
					        	companyName = matcher.group(1).replaceAll("###", "\r\n");
					        	((WorkExperienceData)workExperienceDataList.get(j)).setCompanyName(companyName);
					        }
				        }
					}
				}
				if(companyName.equals("")){
					companyName = "********";
				}
				if(positionTitle.equals("")){
					positionTitle = "********";
				}
				int max = 0;
				if(line.indexOf(companyName)>line.indexOf(positionTitle)){
					max = line.indexOf(companyName);
					String jobDesc = "";
					if(max>=0){
						jobDesc = line.substring(max+companyName.length()).replaceAll("###", "\r\n");
					}else{
						max = line.indexOf(endTime);
						jobDesc = line.substring(max+endTime.length()).replaceAll("###", "\r\n");
					}
					((WorkExperienceData)workExperienceDataList.get(j)).setJobDesc(jobDesc);
				}else{
					max = line.indexOf(positionTitle);
					String jobDesc = "";
					if(max>=0){
						jobDesc = line.substring(max+positionTitle.length()).replaceAll("###", "\r\n");
					}else{
						max = line.indexOf(endTime);
						jobDesc = line.substring(max+endTime.length()).replaceAll("###", "\r\n");
					}
					((WorkExperienceData)workExperienceDataList.get(j)).setJobDesc(jobDesc);
				}
			}
			
			if(!isWorkSection){
				for(int i=0;i<workExperienceDataList.size();i++){
					String companyName = ((WorkExperienceData)workExperienceDataList.get(i)).getCompanyName();
//					String positionTitle = ((WorkExperienceData)workExperienceDataList.get(i)).getPositionTitle();
					if(companyName.equals("")){
						workExperienceDataList.remove(i);
						i--;
					}
				}
			}
			return workExperienceDataList;
		}
		if(count==0){
			content = "";
			workContentList.clear();
			//如果工作经验中没有时间，则通过公司关键字来确定公司个数，并确定工作经验个数
			for(int i=start;i<end;i++){
				String line = resumeContentList.get(i);
				if(i == end-1){
	        		content = content + "###" + line;
	        		workContentList.add(content);
	        		continue;
	        	}
				if(line.equals("")){
					continue;
				}
				Pattern pattern=Pattern.compile("(:|:|^|#)\\s*([\u4e00-\u9fa5]+(公司|集团))(#|$|\\s)");
				Matcher matcher=pattern.matcher(line);
		        if(matcher.find()){ 
		        	if(count > 0){
		        		workContentList.add(content);
		        		content = line;
		        	}else{
		        		content = line;
		        	}
		        	count++;
		        	WorkExperienceData workExperienceData = new WorkExperienceData();
		        	String companyName = matcher.group(2);
		        	workExperienceData.setCompanyName(companyName);
		        	workExperienceDataList.add(workExperienceData);
		        	continue;
		        }else{
		        	content = content + "###" + line;		        	
		        }
			}
		}
		
		if(count>0){
			for(int j=0;j<count;j++){
				String line = workContentList.get(j);
				String positionTitle = extractPosition(line);
				String companyName = ((WorkExperienceData)workExperienceDataList.get(j)).getCompanyName();
				if(positionTitle.equals("")){
					Pattern pattern=Pattern.compile("(^).*?(([\u4e00-\u9fa5]|[a-z0-9])+[\u4e00-\u9fa5]+).*?"+companyName);
					Matcher matcher=pattern.matcher(line);
			        if(matcher.find()){ 
			        	positionTitle = matcher.group(2).replaceAll("###", "\r\n");
			        	((WorkExperienceData)workExperienceDataList.get(j)).setPositionTitle(positionTitle);
			        }else{
			        	pattern=Pattern.compile("("+companyName+").*?(([\u4e00-\u9fa5]|[a-z0-9])+[\u4e00-\u9fa5]+)($|#)");
						matcher=pattern.matcher(line);
				        if(matcher.find()){ 
				        	//如果在工作时间和公司名之间获取到句子 则表明该短句为职位
				        	positionTitle = matcher.group(2).replaceAll("###", "\r\n");
				        	((WorkExperienceData)workExperienceDataList.get(j)).setPositionTitle(positionTitle);
				        }
			        }
				}else{
					((WorkExperienceData)workExperienceDataList.get(j)).setPositionTitle(positionTitle);
				}
				if(positionTitle.equals("")){
					positionTitle = "********";
				}
				int max = 0;
				if(line.indexOf(companyName)>line.indexOf(positionTitle)){
					max = line.indexOf(companyName);
					String jobDesc = "";
					if(max>=0){
						jobDesc = line.substring(max+companyName.length()).replaceAll("###", "\r\n");
					}
					((WorkExperienceData)workExperienceDataList.get(j)).setJobDesc(jobDesc);
				}else{
					max = line.indexOf(positionTitle);
					String jobDesc = "";
					if(max>=0){
						jobDesc = line.substring(max+positionTitle.length()).replaceAll("###", "\r\n");
					}
					((WorkExperienceData)workExperienceDataList.get(j)).setJobDesc(jobDesc);
				}
			}
		}
		
		if(!isWorkSection){
			for(int i=0;i<workExperienceDataList.size();i++){
				String endTime = ((WorkExperienceData)workExperienceDataList.get(i)).getEndTime();
				String companyName = ((WorkExperienceData)workExperienceDataList.get(i)).getCompanyName();
//				String positionTitle = ((WorkExperienceData)workExperienceDataList.get(i)).getPositionTitle();
				if(endTime.equals("") && companyName.equals("")){
					workExperienceDataList.remove(i);
				}
			}
		}
	    return workExperienceDataList;
	}
	
	//提取公司
	public String extractCompany(String line){
		String company="";
		
		Pattern pattern=Pattern.compile("(公\\s*司)(:|:)(([\u4e00-\u9fa5]|[a-z0-9])+)($|#|\\s)",Pattern.CASE_INSENSITIVE);
		Matcher matcher=pattern.matcher(line);		
		if(matcher.find()){
			company = matcher.group(3);
			return company;
		}
		
		pattern=Pattern.compile("(^|#|\\s|:|:)((([\u4e00-\u9fa5]|[a-z0-9])+((有限|科技|股份|责任)?[\u4e00-\u9fa5]*(公司|集团)|(有限|科技|股份|责任)[\u4e00-\u9fa5]*(公司|集团)?)))(（|\\(|（.*?人）|$|#|，|\\s)",Pattern.CASE_INSENSITIVE);		
		matcher=pattern.matcher(line);
		if(matcher.find()){
			company=matcher.group(2);
			if(company.startsWith("在")){
				company = company.replaceAll("在", "");
			}
			return company;		
		}
		return company;
	}
	
	//全文中提取公司
	public String extractCompany(String line,boolean searchCompany){
		String company="";
		
		Pattern pattern=Pattern.compile("(公\\s*司)(:|:)(([\u4e00-\u9fa5]|[a-z0-9])+)($|#|\\s)",Pattern.CASE_INSENSITIVE);
		Matcher matcher=pattern.matcher(line);		
		if(matcher.find()){
			company = matcher.group(3);
			return company;
		}
		
		pattern=Pattern.compile("(^|#|\\s|:|:)(([\u4e00-\u9fa5|[a-z]]*(公司|集团)))(（.*?人）|$|#|，|\\s)",Pattern.CASE_INSENSITIVE);		
		matcher=pattern.matcher(line);
		if(matcher.find()){
			company=matcher.group(2);
			if(company.startsWith("在")){
				company = company.replaceAll("在", "");
			}
			return company;		
		}
		return company;
	}
	
	//提取职位
	public String extractPosition(String line){
		String position="";
		//先寻找是否有关键字，如果有关键字”职位“ 则直接提取后面的词并返回
		Pattern pattern=Pattern.compile("(职\\s*位|角\\s*色)(:|:)?(([\u4e00-\u9fa5]|[a-z0-9])+)($|#|\\s)",Pattern.CASE_INSENSITIVE);
		Matcher matcher=pattern.matcher(line);		
		if(matcher.find()){
			position = matcher.group(3);
			return position;
		}
//		
		//如果没有关键词”职位“，则直接通过找职位常用后缀来匹配
		pattern=Pattern.compile("(:|:|^|#|\\s|)\\s*((\\S)*("+ResourcesConfig.rolesKeywordsRegex+"))($|#|\\s)",Pattern.CASE_INSENSITIVE);
		matcher=pattern.matcher(line);
		if(matcher.find()){
			position = matcher.group(2);
			return position;
		}
//		
//		//如果该行只有一个文字串，则也可能属于职位
//		pattern=Pattern.compile("(^|\\s)\\s*(([\u4e00-\u9fa5]|[a-z])+)\\s*($|###)",Pattern.CASE_INSENSITIVE);
//		matcher=pattern.matcher(line);
//		if(matcher.find()){
//			position = matcher.group(2);
//			return position;
//		}
		return position;
	}
	@Override
	public ArrayList<EducationExperienceData> extractEducationExperience(
			int start, int end) {
		System.out.println("开始处理教育经历");
		// TODO Auto-generated method stub
		//先遍历整个教育经历，统计出教育经历数量并获得每段教育经历的字符串，后针对每段教育经历进行分析，获取到教育经历中的开始结束时间，学校，专业和学历
		ArrayList<EducationExperienceData> educationExperienceDataList = new ArrayList<EducationExperienceData>();
		
		String eduContent = "";
		
		ArrayList<String> schoolLineList = new ArrayList<String>();  //用户保存每份教育经历的学校所在行，用于提取专业
		ArrayList<String> eduContentList = new ArrayList<String>(); 
		
		//统计个数
		int count = 0;
		String content = "";
		for(int i=start;i<end;i++){
			String line = resumeContentList.get(i);			
        	schoolLineList.add(line);
			
			Pattern pattern=Pattern.compile("(^|\\s|#|:|于|在)(([\u4e00-\u9fa5])+(研究院|学院|学校|大学|中学|小学))($|#|\\s|[^\u4e00-\u9fa5])");
			Matcher matcher=pattern.matcher(line);
	        if(matcher.find()){
	        	if(count > 0){
	        		eduContentList.add(content);
	        		content = line;
	        	}else{
	        		content = content + "###" + line;
	        	}
	        	count++;
	        	EducationExperienceData educationExperienceData = new EducationExperienceData();
	        	String school = matcher.group(2);
	        	educationExperienceData.setSchool(school);
	        	educationExperienceDataList.add(educationExperienceData);
	        	
	        	if(i==end-1){
	        		eduContentList.add(content);
	        	}
	        	continue;
	        }else{
	        	content = content + "###" +line;
	        	if(i==end-1){
	        		eduContentList.add(content);
	        	}
	        }
		}
		
		if(count>0){
			for(int j=0;j<eduContentList.size();j++){
				String line = eduContentList.get(j);
				String degree = extractDegree(line);
				String major = extractMajor(line);
				String startTime = "";
				String endTime = "";
				String school = ((EducationExperienceData)educationExperienceDataList.get(j)).getSchool();
				
				Pattern pattern=Pattern.compile("(((19[89]|20[01])[0-9]\\s*(年|/|[.]|-)?)(\\s*\\d+\\s*(月|/|[.]|-)?)?(\\s*\\d+(\\s*日)?)?)\\s*(.){1,2}\\s*((((19[89]|20[01])[0-9]\\s*(年|/|[.]|-)?)(\\s*\\d+\\s*(月|/|[.]|-)?)?(\\s*\\d+(\\s*日)?)?)|至\\s*今|现\\s*在)");
				Matcher matcher=pattern.matcher(line);
		        while(matcher.find()){	        	
		        	startTime = matcher.group(1);        	
		        	endTime = matcher.group(10);
	        		((EducationExperienceData)educationExperienceDataList.get(j)).setStartTime(startTime);
	        		((EducationExperienceData)educationExperienceDataList.get(j)).setEndTime(endTime);
		        	break;
		        }			        
				((EducationExperienceData)educationExperienceDataList.get(j)).setDegree(degree);
				
				int maxsite = 0;
				String maxSiteString = "";
				line = line.replaceAll("^(#)+", "");
				if(!startTime.equals("")){
					if(maxsite<line.indexOf(startTime)){
						maxsite = line.indexOf(startTime);
						maxSiteString = startTime;
					}
				}
				if(!degree.equals("")){
					if(maxsite<line.indexOf(degree)){
						maxsite = line.indexOf(degree);
						maxSiteString = degree;
					}
				}
				
				if(major.equals("")){
					//如果没有获取到专业，则通过夹逼方法来获取到专业
					
					String tmpLine = "";
					if(maxsite == 0){
						tmpLine = line.substring(0);
					}else{
						tmpLine = line.substring(0,maxsite);
					}
					
					String regex = "";
					regex = "(^|\\s|#|\\|)(([\u4e00-\u9fa5]|[a-z])+)($|\\s|#|\\|)";
					pattern=Pattern.compile(regex);
					matcher=pattern.matcher(tmpLine);
			        if(matcher.find()){
			        	major = matcher.group(2).replaceAll("###", "\r\n");
			        	if(major.indexOf(startTime)<0 && !major.equals(degree) && !major.equals(school)){
			        		if(major.length()>1){
	        					((EducationExperienceData)educationExperienceDataList.get(j)).setMajor(major);
	        					break;
	        				}else{
	        					major = "";
	        				}
			        	}else{
			        		major = "";
			        	}
			        	if(major.equals("")){
			        		while(matcher.find()){					        	
			        			major = matcher.group(2).replaceAll("###", "\r\n");
			        			if(major.indexOf(startTime)<0 && !major.equals(degree) && !major.equals(school)){
			        				if(major.length()>1){
			        					((EducationExperienceData)educationExperienceDataList.get(j)).setMajor(major);
			        					break;
			        				}					        		
					        	}else{
					        		major = "";
					        	}					        		
				        	}
			        	}
			        		        	
			        }else{
			        	//如果在工作时间和公司名之间没有获取到句子 则从公司名之后或者下一行中包行了短语 则为职位名
			        	regex = maxSiteString+".*?(([\u4e00-\u9fa5]|[a-z0-9])+[\u4e00-\u9fa5]+)(#|$)";					
		
			        	pattern=Pattern.compile(regex);
						matcher=pattern.matcher(line);
				        if(matcher.find()){
				        	//如果获取到职位则将职位之后的所有内容作为工作内容
				        	major = matcher.group(1).replaceAll("###", "\r\n");
				        	((EducationExperienceData)educationExperienceDataList.get(j)).setMajor(major);
				        }
		        }
				}else{
					((EducationExperienceData)educationExperienceDataList.get(j)).setMajor(major);
				}
	        	
		        
			}
		}
	    
	    return educationExperienceDataList;
	}

	@Override
	public ArrayList<TrainingExperienceData> extractTrainingExperience(
			int start, int end) {
		// TODO Auto-generated method stub
		return super.extractTrainingExperience(start, end);
	}

	@Override
	public ArrayList<LanguageSkillData> extractLanguageSkill(int start, int end) {
		// TODO Auto-generated method stub
		return super.extractLanguageSkill(start, end);
	}

	@Override
	public ArrayList<CertificateData> extractCertficate(int start, int end) {
		// TODO Auto-generated method stub
		return super.extractCertficate(start, end);
	}

	@Override
	public ArrayList<ProjectExperienceData> extractProjectExperience(int start,
			int end) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		//目前暂时通过获取时间周围的第一个字符串作为项目名称
		System.out.println("开始处理项目经历");
		ArrayList<ProjectExperienceData> projectExperienceDataList = new ArrayList<ProjectExperienceData>();
		
		ArrayList<String> projectLineList = new ArrayList<String>();  //用户保存每份教育经历的学校所在行，用户提取专业
		ArrayList<String> projectContentList =  new ArrayList<String>();
		
		//先遍历每一行，通过时间获取出项目经验个数
		int count = 0;
		String content = "";
		for(int i=start;i<end;i++){
			String line = resumeContentList.get(i);
			if(line.equals("")){
				continue;
			}
			Pattern pattern=Pattern.compile("(((19[89]|20[01])[0-9]\\s*(年|/|[.]|-)?)(\\s*\\d+\\s*(月|/|[.]|-)?)?(\\s*\\d+(\\s*日)?)?)\\s*(.){1,5}\\s*((((19[89]|20[01])[0-9]\\s*(年|/|[.]|-)?)(\\s*\\d+\\s*(月|/|[.]|-)?)?(\\s*\\d+(\\s*日)?)?)|至\\s*今|现\\s*在)");
			Matcher matcher=pattern.matcher(line);
	        if(matcher.find()){
	        	if(count > 0){
	        		projectContentList.add(content);
	        		content = line;
	        	}else{
	        		content = line;
	        	}
	        	
	        	projectLineList.add(line);
	        	ProjectExperienceData projectExperienceData = new ProjectExperienceData();
	        	String startTime = matcher.group(1);
	        	String endTime = matcher.group(10);
	        	projectExperienceData.setStartTime(startTime);
	        	projectExperienceData.setEndTime(endTime);
	        	projectExperienceDataList.add(projectExperienceData);
	        	count++;
	        	
	        	if(i==end-1){
	        		projectContentList.add(content);
	        	}
	        	continue;
	        }else{
	        	content = content + "###" + line;	
	        	if(i==end-1){
	        		projectContentList.add(content);
	        	}
	        }
		}
		
		int startLine = start;  //指明下一个工作的开始行
		int currentNum = 0;
		for(int i =0;i<count;i++){
			String projectDesc = "";
//			String startTime = ((ProjectExperienceData)projectExperienceDataList.get(i)).getStartTime();
//			String endTime = ((ProjectExperienceData)projectExperienceDataList.get(i)).getEndTime();
			String projectTitle = ((ProjectExperienceData)projectExperienceDataList.get(i)).getProjectTitle();
			for(int j=startLine;j<end;j++){
				String line = resumeContentList.get(j);				

				//如果公司和职位都已经获取到，且当前行不包括时间则直接把当前行并入到当前工作的工作描述中
				Pattern pattern=Pattern.compile("(((19[789]|20[012])[0-9]\\s*(年|/|[.]|-)?)(\\s*\\d+\\s*(月|/|[.]|-)?)?(\\s*\\d+(\\s*日)?)?)\\s*(.){1,5}\\s*((((19|20)[0189][0-9]\\s*(年|/|[.]|-)?)(\\s*\\d+\\s*(月|/|[.]|-)?)?(\\s*\\d+(\\s*日)?)?)|至\\s*今|现\\s*在)");
				Matcher matcher=pattern.matcher(line);
		        if(matcher.find()){	
		        	currentNum++;
					//在该行中获取项目名
		        	projectTitle = ((ProjectExperienceData)projectExperienceDataList.get(currentNum-1)).getProjectTitle();
		        	if(projectTitle.equals("")){
		        		projectTitle = getFirstPhrase(line);
						if(projectTitle.equals("")){
							//如果当前行中未找到字符串 则直接获取下一行的第一个字符串作为项目名称
							projectTitle = getFirstPhrase(resumeContentList.get(j+1));
							((ProjectExperienceData)projectExperienceDataList.get(currentNum-1)).setProjectTitle(projectTitle);
							if(j+2 <= end-1){
								j +=1;
								startLine = j+1;
								((ProjectExperienceData)projectExperienceDataList.get(i)).setProjectDesc(projectDesc);
								if(currentNum==1){
									continue;
								}else{
									break;
								}
							}							
						}else{
							((ProjectExperienceData)projectExperienceDataList.get(currentNum-1)).setProjectTitle(projectTitle);
						}			        	
			        	//如果当前行含有工作区间段，则直接开始第二个工作的处理
						startLine = j+1;
						((ProjectExperienceData)projectExperienceDataList.get(i)).setProjectDesc(projectDesc);
						if(currentNum==1){
							continue;
						}else{
							break;
						}
		        	}else{
		        		projectDesc = projectDesc + "\r\n" +line;
						if(j==end-1){
							projectDesc = projectDesc + "\r\n" + line;
							((ProjectExperienceData)projectExperienceDataList.get(i)).setProjectDesc(projectDesc);
						}
		        	}
					
		        }else{
		        	projectDesc = projectDesc + "\r\n" +line;
					if(j==end-1){
						projectDesc = projectDesc + "\r\n" + line;
						((ProjectExperienceData)projectExperienceDataList.get(i)).setProjectDesc(projectDesc);
					}
		        }				
			}
		}

	    return projectExperienceDataList;
	}

	public String getFirstPhrase(String content){
		String phrase = "";
		Pattern pattern=Pattern.compile("(([\u4e00-\u9fa5]).*?)(\\s|$)",Pattern.CASE_INSENSITIVE);
		Matcher matcher=pattern.matcher(content);
		if(matcher.find()){
			phrase = matcher.group(1);
		}
		return phrase;
	}
	@Override
	public ArrayList<ProfessionalSkillData> extractProfessionalSkill(int start,
			int end) {
		// TODO Auto-generated method stub
		return super.extractProfessionalSkill(start, end);
	}
 
	@Override
	public ArrayList<PracticalExperienceData> extractPracticalExperience(
			int start, int end) {
		// TODO Auto-generated method stub
		return super.extractPracticalExperience(start, end);
	}

	@Override
	public ArrayList<OtherInfoData> extractOtherInfo(int start, int end) {
		// TODO Auto-generated method stub
		return super.extractOtherInfo(start, end);
	}
	
//	public void preProcess(){    
//		convert(this.filePath);
//		String [] arr = filePath.split("\\.");
//		filePath = filePath.replaceAll("(\\.doc|\\.docx)$", ".txt");
//		BufferedReader reader = null;
//        try {
////            reader = new BufferedReader(new FileReader(file));
//            String line = "";
//            reader=new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8")); 
//            String currentSectionName = "";
//            
//            // 一次读入一行，直到读入null为文件结束
//            int i = 0;
//            
//			currentSectionName = "个人信息";
//    		SectionInfo sectionInfo = new SectionInfo(i,i);
//    		sectionMap.put(currentSectionName, sectionInfo);
//    		
//            while ((line = reader.readLine()) != null) {
//            	line = line.replaceAll("\u00A0"," " ).trim();
//            	line = line.replaceAll("\t"," " ).trim();
//            	line = line.replaceAll("\u3000", " ").trim();            	
//            	
//            	resumeContentList.add(line);
//            	//剔除掉该行的所有干扰字符，如":",以及行收的非中文字符，以确定该行是否为段名
//            	String trimLine = line.replaceAll(":", "").replaceAll(" ", "").replaceFirst("[^\u4e00-\u9fa5]+", "").trim();
//            	if(ResourcesConfig.segmentTitleSet.contains(trimLine)){
//            		//确定该行是否在段名库中，如果在则判断该段是否在前面遇到过
//            		if(!sectionMap.containsKey(trimLine)){
//            			currentSectionName = trimLine;
//                		SectionInfo curSectionInfo = new SectionInfo(i+1,i+1);
//                		sectionMap.put(trimLine, curSectionInfo);
//            		}else{
//            			//如果遇到过，则该段名可能出现在基本信息中，就需要判断之间的段名是否是在基本信息中，如果是在基本信息中则直接以当前段为开始，忽略之前的段开始位置
//            			// 加上判断之前段是否在基本信息中的逻辑
//            			if(trimLine.equals("工作经验")){
//            				currentSectionName = "工作经验";
//            				sectionMap.get(currentSectionName).start =i+1;
//            				sectionMap.get(currentSectionName).end =i+1;
//            			}else{
//            				sectionMap.get(currentSectionName).end +=1;
//            			}            			
//            		}
//            	}else{
//            		sectionMap.get(currentSectionName).end +=1;
//            	}
//            	i++;
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
//        
//        getSectionInfoList();
//	}
	
	
	public void preProcess(){    
		try {
			File directory = new File("..");//设定为当前文件夹
			try{
			    System.out.println(directory.getCanonicalPath());//获取标准的路径
			    System.out.println(directory.getAbsolutePath());//获取绝对路径
			}catch(Exception e){}
//			ResourcesConfig.loadConfig("/home/bobby/mnt/repo/RecruitingEngine/src/main/java/resources");
			ResourcesConfig.loadConfig("/media/data/project/parserservice/resources/resources");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (resumeContentList == null) {
			return;
		} else {
			String line = "";
			String currentSection = "个人信息";
			SectionInfo sectionInfo = new SectionInfo(0, 0);
			sectionMap.put(currentSection, sectionInfo);

			String currentSectionName = "个人信息";

			for (int i = 0; i < resumeContentList.size(); i++) {
				line = resumeContentList.get(i);
				line = line.replaceAll("\u00A0", " ").trim();
				line = line.replaceAll("\t", " ").trim();
				line = line.replaceAll("\u3000", " ").trim();

				// 剔除掉该行的所有干扰字符，如":",以及行收的非中文字符，以确定该行是否为段名
				String trimLine = line.replaceFirst("[^\u4e00-\u9fa5]*", "");
				Pattern pattern = Pattern.compile("(.*[\u4e00-\u9fa5])($|\\s|[^\u4e00-\u9fa5]*)");
				Matcher matcher = pattern.matcher(trimLine);
				if(matcher.find()){
					trimLine = matcher.group(1);
				}
				
				if (ResourcesConfig.segmentTitleSet.contains(trimLine)) {
					// 确定该行是否在段名库中，如果在则判断该段是否在前面遇到过
					if (!sectionMap.containsKey(trimLine)) {
						currentSectionName = trimLine;
						SectionInfo curSectionInfo = new SectionInfo(i + 1,
								i + 1);
						sectionMap.put(trimLine, curSectionInfo);
					} else {
						// 如果遇到过，则该段名可能出现在基本信息中，就需要判断之间的段名是否是在基本信息中，如果是在基本信息中则直接以当前段为开始，忽略之前的段开始位置
						// 加上判断之前段是否在基本信息中的逻辑
						if (trimLine.equals("工作经验")) {
							currentSectionName = "工作经验";
							sectionMap.get(currentSectionName).start = i + 1;
							sectionMap.get(currentSectionName).end = i + 1;
						} else {
							sectionMap.get(currentSectionName).end += 1;
						}
					}
				} else {
					sectionMap.get(currentSectionName).end += 1;
				}
			}
		}
	}
	
	public void getSectionInfoList(){
		int start = 0;
		int end = 0;
		String line = "";
		int size = resumeContentList.size();
		for(int i=0;i<size;i++){
			line = resumeContentList.get(i).trim();
			
			if(i==0){
				start = 0;
				continue;
			}else if(i == size-1){
				end = i;
				SectionInfo sectionInfo = new SectionInfo(start,end);
				sectionInfoList.add(sectionInfo);
				continue;
			}else{
				if(!line.equals("")){
					//如果当前不为空行，且前面两行都未空行，则该行是段的开始行
					if(i>=2){
						String preLine = resumeContentList.get(i-1).trim();
						String prePreLine = resumeContentList.get(i-2).trim();
						if(preLine.equals("") && prePreLine.equals("")){
							end = i-1;
							SectionInfo sectionInfo = new SectionInfo(start,end);
							sectionInfoList.add(sectionInfo);
							start = i;
						}
					}
				}
			}			
		}
	}
	public void convert(String path){
		try {
			File dir = new File(path).getParentFile();
			Process process = Runtime.getRuntime().exec(
					new String[] { "/usr/bin/soffice", "--headless", "--convert-to",
							"txt:Text", "-outdir", dir.getAbsolutePath(),
							path });

			int value = process.waitFor();
			if (value == 0) {
				String[] parts = path.split("\\.");
				String ext = parts[parts.length - 1];
				String txtPath = path.replace("." + ext, ".txt");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TextResumeParser(String filepath){
		this.filePath = filepath;
	}
	
	public TextResumeParser(ArrayList<String> resumeDataList){
		this.resumeContentList = resumeDataList;
	}

}
