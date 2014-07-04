package com.bole.resumeparser.models;


public class CertificateData {
	//证书名称
	String certificateTitle="";
	
	//获得实践
	String acquireTime="";
	
	//说明
	String comment="";

	public String getCertificateTitle() {
		return certificateTitle;
	}

	public void setCertificateTitle(String certificateTitle) {
		this.certificateTitle = certificateTitle;
	}

	public String getAcquireTime() {
		return acquireTime;
	}

	public void setAcquireTime(String acquireTime) {
		this.acquireTime = acquireTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	

	@Override
	public String toString(){
		return "Certificate [certificateTitle="
				+ certificateTitle + ", acquireTime=" + acquireTime + ", comment=" + comment  
		        + "]";
	}
	
	public String getString(){
		return certificateTitle + " " + acquireTime + " " + comment;
	}
	
}
