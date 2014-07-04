package com.bole.resumeparser.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.List;

public class TextResumeData {

	public String url;	
	private String _id;
	private String sourceID; // 简历id
	private String urlID;  //如果url中有简历id 则提取出其中的id
	private String viewID; //如果是查看联系信息的url则提取出其中的id
	private String resumeLanguage;  //简历语言
	
	private String searchKeyWord;
	
	private String homePage ; //个人主页
	private String qq;  //qq号码
	private String weibo; //个人微博
	
	private String resumeKeyWord;  //简历关键字

	private String source; // 简历所属网站

	private String updateTime; // 简历更新时间
	private Date createTime; //新建时间
	private Date modifyTime;  //最近修改时间
	
	private ArrayList<String> urls = new ArrayList<String>(); 
//	private String isCotactInformation = "NO";  //YES 有联系信息, NO 没有联系信息
	private String image_url = ""; //头像url
	// 个人基本信息
	private String name;// 获取用户名
	private String phone; // 用户电话
	private String identityID; //身份证号码
	private String email;
	
	private String address;  //现在居住地址
	private String houseHolds;  //户口所在地
	private String residence;   //常居地
	private String politicalLandscape;  //政治面貌
	private String maritalStatus;    //婚姻状况
	private int workExperienceLength;   //工作年限
	
	private String latestDegree;  //最近学历
	private String latestSchool;  //最近学校
	private String latestMajor;  //最近专业
	
	private String latestCompanyName; //最近的公司
	private String latestPositionTitle; //最近的职位
	private String latestSalary; //最近的薪资
	private String latestIndustry; //最近公司的行业
	
	private EducationExperienceData highestEducation;
	private WorkExperienceData currentWork;
	private String zipCode;    //邮编
	private String currentSalary;  //目前年薪
//	@Indexed
//	private String email; // email
	private int age; // 年龄
	private String birthday; //出生年月
	private String gender; // 性别
	private String height;  //身高
	
	//原文本（文本简历内容）
	private String basicInfoText;  //基本信息原文
	private String eduSourceText; //教育经历原文
	private String workSourceText;  //工作经历原文
	private String projectSourceText;  //项目经历原文
	private String skillSourceText;  //专业技能原文
	private String trainSourceText;  //培训经历原文
	private String jobTragetSourceText;  //求职意向原文
	
	ArrayList<String> keywords_list = new ArrayList<String>();
	
	private HashMap<String,String> sectionMap = new HashMap<String,String>();
	// 自我评价
	private String selfEvaluation;
	
	//简历内容
	private String resumeContent;

	// 求职意向
	private JobTarget jobTarget;

	// 工作经历
	private ArrayList<WorkExperienceData> workExperience;

	// 教育经历
	private ArrayList<EducationExperienceData> educationExperience;

	// 培训经历
	private ArrayList<TrainingExperienceData> trainingExperience;

	// 语言能力
	private ArrayList<LanguageSkillData> languageSkill;

	// 证书
	private ArrayList<CertificateData> certficate;

	// 项目经历
	private ArrayList<ProjectExperienceData> projectExperience;

	// 专业技能
	private ArrayList<ProfessionalSkillData> professionalSkill;

	// 在校实践经验
	private ArrayList<PracticalExperienceData> practicalExperience;

	// 记录用户自定义的标题段
	public HashMap<String,String> otherInfoMap = new HashMap<String,String>(); 
	
	//简历被解析的状态
	private String status;
	
	//简历文本内容
	private String resumeText;
	
	//在校学习情况
	private StudyInfoData studyInfo;
	
	//简历是否更新
	String updated = "NO";
	
	//个人联系信息
	public TextResumeContactInfoData contactInfoData = new TextResumeContactInfoData();
	
	public ArrayList<String> getKeywords_list() {
		return keywords_list;
	}
	public void setKeywords_list(ArrayList<String> keywords_list) {
		this.keywords_list = keywords_list;
	}
	public HashMap<String, String> getOtherInfoMap() {
		return otherInfoMap;
	}
	public void setOtherInfoMap(HashMap<String, String> otherInfoMap) {
		this.otherInfoMap = otherInfoMap;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getIdentityID() {
		return identityID;
	}
	public void setIdentityID(String identityID) {
		this.identityID = identityID;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public StudyInfoData getStudyInfo() {
		return studyInfo;
	}
	public void setStudyInfo(StudyInfoData studyInfo) {
		this.studyInfo = studyInfo;
	}
	public void addOtherInfo(String title,String content){
		otherInfoMap.put(title, content);
	}
	public String getResumeText() {
		return resumeText;
	}

	public void setResumeText(String resumeText) {
		this.resumeText = resumeText;
	}

	public String getStatus() {
		return status;
	}
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public void addPracticeList(ArrayList<PracticalExperienceData> practicalList){
		if(practicalExperience==null){
			practicalExperience = practicalList;
		}else{
			practicalExperience.addAll(practicalList);
		}
	}
	public String getLatestIndustry() {
		return latestIndustry;
	}
	public void setLatestIndustry(String latestIndustry) {
		this.latestIndustry = latestIndustry;
	}
	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public String getLatestCompanyName() {
		return latestCompanyName;
	}

	public void setLatestCompanyName(String latestCompanyName) {
		this.latestCompanyName = latestCompanyName;
	}

	public String getLatestPositionTitle() {
		return latestPositionTitle;
	}

	public void setLatestPositionTitle(String latestPositionTitle) {
		this.latestPositionTitle = latestPositionTitle;
	}

	public String getLatestSalary() {
		return latestSalary;
	}

	public void setLatestSalary(String latestSalary) {
		this.latestSalary = latestSalary;
	}

	public ArrayList<String> getUrls() {
		return urls;
	}

	public void setUrls(ArrayList<String> urls) {
		this.urls = urls;
	}

	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getImage_url() {
		return image_url;
	}
	

	public String getHomePage() {
		return homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public WorkExperienceData getCurrentWork() {
		return currentWork;
	}

	public void setCurrentWork(WorkExperienceData currentWork) {
		this.currentWork = currentWork;
	}

	public String getCurrentSalary() {
		return currentSalary;
	}

	public void setCurrentSalary(String currentSalary) {
		this.currentSalary = currentSalary;
	}

	public String getSearchKeyWord() {
		return searchKeyWord;
	}

	public String getLatestSchool() {
		return latestSchool;
	}

	public void setLatestSchool(String latestSchool) {
		this.latestSchool = latestSchool;
	}

	public String getLatestMajor() {
		return latestMajor;
	}

	public void setLatestMajor(String latestMajor) {
		this.latestMajor = latestMajor;
	}

	public void setSearchKeyWord(String searchKeyWord) {
		this.searchKeyWord = searchKeyWord;
	}


//	public String getIsCotactInformation() {
//		return isCotactInformation;
//	}
//
//	public void setIsCotactInformation(String isCotactInformation) {
//		this.isCotactInformation = isCotactInformation;
//	}

	public String getSourceID() {
		return sourceID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public String getResumeKeyWord() {
		return resumeKeyWord;
	}

	public void setResumeKeyWord(String resumeKeyWord) {
		this.resumeKeyWord = resumeKeyWord;
	}
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}	

	public String getLatestDegree() {
		return latestDegree;
	}

	public void setLatestDegree(String latestDegree) {
		this.latestDegree = latestDegree;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public String getPhone() {
//		return phone;
//	}
//
//	public void setPhone(String phone) {
//		this.phone = phone;
//	}

//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}
	
	
//
//	public String getIdentityID() {
//		return identityID;
//	}
//
//	public void setIdentityID(String identityID) {
//		this.identityID = identityID;
//	}

	public String getAddress() {
		return address;
	}

	public String getResumeContent() {
		return resumeContent;
	}

	public void setResumeContent(String resumeContent) {
		this.resumeContent = resumeContent;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getHouseHolds() {
		return houseHolds;
	}

	public void setHouseHolds(String houseHolds) {
		this.houseHolds = houseHolds;
	}

	public String getResidence() {
		return residence;
	}

	public void setResidence(String residence) {
		this.residence = residence;
	}

	public String getPoliticalLandscape() {
		return politicalLandscape;
	}

	public void setPoliticalLandscape(String politicalLandscape) {
		this.politicalLandscape = politicalLandscape;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public int getWorkExperienceLength() {
		return workExperienceLength;
	}

	public void setWorkExperienceLength(int workExperienceLength) {
		this.workExperienceLength = workExperienceLength;
	}

	public EducationExperienceData getHighestEducation() {
		return highestEducation;
	}

	public void setHighestEducation(EducationExperienceData highestEducation) {
		this.highestEducation = highestEducation;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getSelfEvaluation() {
		return selfEvaluation;
	}

	public void setSelfEvaluation(String selfEvaluation) {
		this.selfEvaluation = selfEvaluation;
	}

	public JobTarget getJobTarget() {
		return jobTarget;
	}

	public void setJobTarget(JobTarget jobTarget) {
		this.jobTarget = jobTarget;
	}

	public ArrayList<WorkExperienceData> getWorkExperience() {
		return workExperience;
	}

	public void setWorkExperience(ArrayList<WorkExperienceData> workExperience) {
		this.workExperience = workExperience;
	}

	

	public ArrayList<EducationExperienceData> getEducationExperience() {
		return educationExperience;
	}

	public void setEducationExperience(
			ArrayList<EducationExperienceData> educationExperience) {
		this.educationExperience = educationExperience;
	}

	public ArrayList<TrainingExperienceData> getTrainingExperience() {
		return trainingExperience;
	}

	public void setTrainingExperience(
			ArrayList<TrainingExperienceData> trainingExperience) {
		this.trainingExperience = trainingExperience;
	}

	public ArrayList<LanguageSkillData> getLanguageSkill() {
		return languageSkill;
	}

	public void setLanguageSkill(ArrayList<LanguageSkillData> languageSkill) {
		this.languageSkill = languageSkill;
	}

	public ArrayList<CertificateData> getCertficate() {
		return certficate;
	}

	public void setCertficate(ArrayList<CertificateData> certficate) {
		this.certficate = certficate;
	}

	public ArrayList<ProjectExperienceData> getProjectExperience() {
		return projectExperience;
	}

	public void setProjectExperience(
			ArrayList<ProjectExperienceData> projectExperience) {
		this.projectExperience = projectExperience;
	}

	public ArrayList<ProfessionalSkillData> getProfessionalSkill() {
		return professionalSkill;
	}

	public void setProfessionalSkill(
			ArrayList<ProfessionalSkillData> professionalSkill) {
		this.professionalSkill = professionalSkill;
	}

	

	public ArrayList<PracticalExperienceData> getPracticalExperience() {
		return practicalExperience;
	}

	public void setPracticalExperience(
			ArrayList<PracticalExperienceData> practicalExperience) {
		this.practicalExperience = practicalExperience;
	}

	public HashMap<String, String> getSectionMap() {
		return sectionMap;
	}

	public void setSectionMap(HashMap<String, String> sectionMap) {
		this.sectionMap = sectionMap;
	}

	public String getEduSourceText() {
		return eduSourceText;
	}

	public void setEduSourceText(String eduSourceText) {
		this.eduSourceText = eduSourceText;
	}

	public String getWorkSourceText() {
		return workSourceText;
	}

	public void setWorkSourceText(String workSourceText) {
		this.workSourceText = workSourceText;
	}

	public String getProjectSourceText() {
		return projectSourceText;
	}

	public void setProjectSourceText(String projectSourceText) {
		this.projectSourceText = projectSourceText;
	}

	public String getSkillSourceText() {
		return skillSourceText;
	}

	public void setSkillSourceText(String skillSourceText) {
		this.skillSourceText = skillSourceText;
	}

	public String getTrainSourceText() {
		return trainSourceText;
	}

	public void setTrainSourceText(String trainSourceText) {
		this.trainSourceText = trainSourceText;
	}

	public String getJobTragetSourceText() {
		return jobTragetSourceText;
	}

	public void setJobTragetSourceText(String jobTragetSourceText) {
		this.jobTragetSourceText = jobTragetSourceText;
	}
	public String getBasicInfoText() {
		return basicInfoText;
	}
	public void setBasicInfoText(String basicInfoText) {
		this.basicInfoText = basicInfoText;
	}
	
	public String getUrlID() {
		return urlID;
	}
	public void setUrlID(String urlID) {
		this.urlID = urlID;
	}
	public String getViewID() {
		return viewID;
	}
	public void setViewID(String viewID) {
		this.viewID = viewID;
	}
	public String getResumeLanguage() {
		return resumeLanguage;
	}
	public void setResumeLanguage(String resumeLanguage) {
		this.resumeLanguage = resumeLanguage;
	}
	
	public TextResumeContactInfoData getContactInfoData() {
		return contactInfoData;
	}
	public void setContactInfoData(TextResumeContactInfoData contactInfoData) {
		this.contactInfoData = contactInfoData;
	}
//	public String getString(){
//		String text = "";
////		for(int i=0;i<urls.size();i++){
////			text += " "+ urls.get(i);
////		}
//		text += selfEvaluation;
//		if(jobTarget!=null){
//			text += jobTarget.getString();
//		}
//		
//		if(workExperience!=null){
//			for(int i=0;i<workExperience.size();i++){
//				text += " "+ workExperience.get(i).getString();
//			}
//		}
//		
//		if(educationExperience!=null){
//			for(int i=0;i<educationExperience.size();i++){
//				text += " "+ educationExperience.get(i).getString();
//			}
//		}
//		
//		if(trainingExperience!=null){
//			for(int i=0;i<trainingExperience.size();i++){
//				text += " "+ trainingExperience.get(i).getString();
//			}
//		}
//		
//		if(languageSkill!=null){
//			for(int i=0;i<languageSkill.size();i++){
//				text += " "+ languageSkill.get(i).getString();
//			}
//		}
//		
//		if(certficate!=null){
//			for(int i=0;i<certficate.size();i++){
//				text += " "+ certficate.get(i).getString();
//			}
//		}
//		
//		if(projectExperience!=null){
//			for(int i=0;i<projectExperience.size();i++){
//				text += " "+ projectExperience.get(i).getString();
//			}
//		}
//		
//		if(professionalSkill!=null){
//			for(int i=0;i<professionalSkill.size();i++){
//				text += " "+ professionalSkill.get(i).getString();
//			}
//		}
//		
//		if(practicalExperience!=null){
//			for(int i=0;i<practicalExperience.size();i++){
//				text += " "+ practicalExperience.get(i).getString();
//			}
//		}
//
//		text += " "+ basicInfoText + eduSourceText + workSourceText + projectSourceText +
//				skillSourceText + trainSourceText + jobTragetSourceText;
//		return text;
//	}
	
}
