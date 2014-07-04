package com.bole.resumeparser.document.impl;

import java.util.ArrayList;
import java.util.HashMap;

import com.bole.resumeparser.message.ResumeMessage;
import com.bole.resumeparser.models.ResumeData;
/**
 * 该类定义了解析文本文档的接口和模型，目前文档仅限于txt文件，非txt文档需要先将文档转换为txt文档
 * 第一步：将文档按行读入ArrayList中，同时将文档按段映射放入map中，map中存放了每个段的标题和每个段在ArrayList中起始位置
 * 第二步：遍历map对每个段单独进行解析。
 * @author liyao
 *
 */
public abstract class DocumentResumeParserAbstract {
	//同时将简历内容读取到list中
	public String filePath = "";
	ResumeData resumedata = new ResumeData();
	
	//同时将简历内容读取到list中 
	HashMap<String,SectionInfo> sectionMap = new HashMap<String,SectionInfo>();
	ArrayList<String> resumeContent = new ArrayList<String>();
	
	
	public abstract void process(ResumeMessage message) throws Exception;  //
	public abstract void preProcess();  //将文档按行读入ArrayList中，同时将文档按段映射放入map中
	public abstract ResumeData parse();  //遍历map对每个段单独进行解析。
}



class SectionInfo{
	/**
	 * 段信息 指定该段的开始位置和结束位置
	 */
	int start;
	int end;
	
	
	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public int getEnd() {
		return end;
	}


	public void setEnd(int end) {
		this.end = end;
	}


	public SectionInfo(int start,int end){
		this.start = start;
		this.end = end;
	}
}
