package com.bole.resumeparser.models;

import java.util.Date;

public class ContactInfoData {
	//邮件简历同步信息
	private String _id;  //主键
	
	private int mysqlid;
	//同步邮箱
	private String resumeID; //在resumedata中的主键
	
	private String name;// 获取用户名
	private String source; // 简历所属网站
	private String sourceID; // 简历id
	private String phone;  //电话
	private String email; // email
	private String qq;  //qq号码
	private String weibo; //个人微博	
	
	private String identityID; //身份证号码
	private Date add_time;
	
	private String status;  //联系信息状态
	
	
	
	public int getMysqlid() {
		return mysqlid;
	}
	public void setMysqlid(int mysqlid) {
		this.mysqlid = mysqlid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getAdd_time() {
		return add_time;
	}
	public void setAdd_time(Date add_time) {
		this.add_time = add_time;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSourceID() {
		return sourceID;
	}
	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}
	public String getIdentityID() {
		return identityID;
	}
	public void setIdentityID(String identityID) {
		this.identityID = identityID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getResumeID() {
		return resumeID;
	}
	public void setResumeID(String resumeID) {
		this.resumeID = resumeID;
	}
	
	
}

