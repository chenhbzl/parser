package com.bole.resumeparser.models;

public class UserResume {

	private String _id;
	
	private String username;
	private String resume_id;
	private int has_contact = 0;
	private String add_time ;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getResume_id() {
		return resume_id;
	}
	public void setResume_id(String resume_id) {
		this.resume_id = resume_id;
	}
	public int getHas_contact() {
		return has_contact;
	}
	public void setHas_contact(int has_contact) {
		this.has_contact = has_contact;
	}
	public String getAdd_time() {
		return add_time;
	}
	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}	
	
}
