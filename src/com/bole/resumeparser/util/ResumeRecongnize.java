package com.bole.resumeparser.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import com.bole.config.Status;
import com.bole.resumeparser.exception.document.UnSupportedResumeTypeException;

/**
 * 用于识别简历来源（主要用于判断来自传统模板的文本简历，如51,智联，猎聘）
 * 51的doc简历为mhtml格式
 * 智联的doc简历为html格式
 * 列聘的doc简历为xml格式
 * @author liyao
 */

public class ResumeRecongnize {
	
	String type = "";
	String source = "";
	
	private static HashSet<String> CONVERT_TYPES = new HashSet<String>();
	
	static {
		CONVERT_TYPES.add("doc"); // Doc
		CONVERT_TYPES.add("docx"); // Docx
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filePath = "";
		ResumeRecongnize resumeRecongnize = new ResumeRecongnize();
		resumeRecongnize.recongnize("/home/bobby/mnt/邮箱同步的简历/简历模板/resume3/64B8A6B7C9C245A3806C80146279B4A3.doc");
		resumeRecongnize.docType(filePath);
	}
	
	public String recongnize(String filepath){
		String type = "";
		File file = new File(filepath);
		BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            // 一次读入一行，直到读入null为文件结束
            String line = "";
            while ((line = reader.readLine()) != null) {
            	int first = line.charAt(0);
            	int second = line.charAt(1);
            	System.out.println(first);
            	System.out.println(line);
            	if(line.equals("")){
            		continue;
            	}else{
            		if(line.equals("﻿简历名称：")){
            			type = "zhilian";
            			return type;
            		}else if(line.indexOf("简历更新时间") >= 0){
            			type = "51job";
            			return type;
            		}
            	}

            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return type;
	}
	
	public String recongnize(ArrayList<String> resumeDataList){
		String type = "";
		String line = "";
		for(int i=0;i<resumeDataList.size();i++){
			line = resumeDataList.get(i);
			if(line.equals("")){
        		continue;
        	}else{
        		if(line.equals("﻿简历名称：")){
        			type = Status.ZHILIAN;
        			return type;
        		}else if(line.indexOf("简历更新时间") >= 0){
        			type = Status._51JOB;
        			return type;
        		}
        	}
		}
		
        return type;
	}
	
	public static String docType(String filePath){
		String docType = "";
		if(filePath ==  null){
			return docType;
		}else if(filePath.endsWith(".docx") || filePath.endsWith(".doc")){
			File file = new File(filePath);
			BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            // 一次读入一行，直到读入null为文件结束
	            String line = "";
	            while ((line = reader.readLine()) != null) {
	            	line = line.toLowerCase();
	            	System.out.println(line);
	            	if(line.equals("")){
	            		continue;
	            	}else{
	            		if(line.startsWith("mime-version") || line.startsWith("date:")){
	            			docType = Status.MHTML;
	            			break;
	            		}else if(line.equals("<html xmlns:v=\"urn:schemas-microsoft-com:vml\"")){
	            			docType = Status.DOC_HTML;
	            			break;
	            		}else if(line.equals("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>")){
	            			docType = Status.DOC_XML;
	            			break;
	            		}else {
	            			docType = Status.DOC;
	            			break;
	            		}
	            	}
	            }
	            reader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
		}
		return docType;
	}
	
	public static HashMap<String,String> getTypeAndSource(String filePath) throws UnSupportedResumeTypeException{
		HashMap<String,String> sourceInfo = new HashMap<String,String>();
		
		String source = "unknown";
		String docType = "";
		
		String fileExtension = "";
		String [] arr = filePath.split("\\.");
		if(arr.length >= 1){
			fileExtension = filePath.split("\\.")[arr.length-1];
		}
		
		if(CONVERT_TYPES.contains(fileExtension)){
			if(filePath.endsWith(".docx")){
				docType = Status.DOCX;
				source = "userOwn";
			}else if(filePath.endsWith(".doc")){
				//首先通过文件名来判断简历来源
				if(filePath.indexOf("51job") >= 0){
					docType = Status.MHTML;
					source = "51job";
				}else if(filePath.indexOf("智联招聘") >= 0){
					docType = Status.DOC_HTML;
					source = "zhilian";
				}else if(filePath.indexOf("猎聘网") >= 0){
					docType = Status.XML;
					source = "liepin";
				}
			}
			
			if(source.equals("unknown")){
				//如果无法通过文件名获取到文件来源，则通过文件格式和内容来获取简历来源
				if(filePath ==  null || "".equals(filePath)){
					return sourceInfo;
				}else {
					File file = new File(filePath);
					BufferedReader reader = null;
			        try {
			            reader = new BufferedReader(new FileReader(file));
			            // 一次读入一行，直到读入null为文件结束
			            String line = "";
			            while ((line = reader.readLine().trim()) != null) {
			            	line = line.toLowerCase();
			            	System.out.println(line);
			            	if(line.equals("")){
			            		continue;
			            	}else{
			            		if(line.charAt(0) == 65533){
			            			docType = Status.DOC;
			            			break;
			            		}else{
			            			if(line.startsWith("mime-version") || line.startsWith("date:")){
				            			docType = Status.MHTML;
				            			source = Status._51JOB;
				            			break;
				            		}else if(line.indexOf("html") >= 0){
				            			if(line.equals("<html xmlns:v=\"urn:schemas-microsoft-com:vml\"")){
				            				source = Status.ZHILIAN;
				            				docType = Status.DOC_HTML;
				            			}else if(line.equals("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">")){
				            				source = Status.LIEPIN;
				            				docType = Status.DOC_HTML;
				            			}
				            			break;
				            		}else if(line.equals("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>")){
				            			docType = Status.DOC_XML;
				            			source = Status.LIEPIN;
				            			break;
				            		}else{
				            			if(line.indexOf("lietou") >= 0){
				            				source = Status.LIEPIN;
				            				break;
				            			}else if(line.indexOf("zhaopin") >= 0){
				            				source = Status.ZHILIAN;
				            				break;
				            			}
				            		}
			            		}
			            	}
			            }
			            reader.close();
			        } catch (IOException e) {
			            e.printStackTrace();
			        } finally {
			            if (reader != null) {
			                try {
			                    reader.close();
			                } catch (IOException e1) {
			                }
			            }
			        }
				}
				sourceInfo.put("type", docType);
				sourceInfo.put("source", source);
				return sourceInfo;
			}else{
				sourceInfo.put("type", docType);
				sourceInfo.put("source", source);
				return sourceInfo;
			}
		}else{
			throw new UnSupportedResumeTypeException();
		}
		
		
	}
}
