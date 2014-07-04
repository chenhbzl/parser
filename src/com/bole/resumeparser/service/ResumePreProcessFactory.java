package com.bole.resumeparser.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bole.config.Status;
import com.bole.resumeconverter.document.DocResumeConverter;
import com.bole.resumeconverter.document.MhtResumeConverter;
import com.bole.resumeparser.document.impl.DocumentResumeParserAbstract;
import com.bole.resumeparser.exception.ResumeParseException;
import com.bole.resumeparser.exception.document.ConVertException;
import com.bole.resumeparser.exception.document.UnSupportedResumeTypeException;
import com.bole.resumeparser.message.ResumeMessage;
import com.bole.resumeparser.message.ResumePreProcessInfo;
import com.bole.resumeparser.message.ResumePreProcessInfo.ResumeSourceInfo;
import com.bole.resumeparser.models.ResumeData;
import com.bole.resumeparser.models.ResumeInfo;
import com.bole.resumeparser.util.ResumeRecongnize;

/**
 * 主要负责将各种文档的识别和预处理（如doc，docx，pdf，html，txt等）
 * 识别主要是根据内容来判断简历是否来自已知的网站，然后有针对的进行解析
 * 预处理主要是读取文本，因为文本包括txt，doc，docx，pdf，html，字符串等，预处理主要负责读取各种类型的文档的内容并保存在list中
 * @author liyao
 *
 */
@Component
public class ResumePreProcessFactory {
	private static Logger logger = LoggerFactory
			.getLogger(ResumePreProcessFactory.class);

	private String sofficePath;
	private static HashSet<String> CONVERT_TYPES = new HashSet<String>();
	
	static {
		CONVERT_TYPES.add("doc"); // Doc
		CONVERT_TYPES.add("docx"); // Docx
	}

	public String getSofficePath() {
		return sofficePath;
	}

	@Value("${soffice.binPath}")
	public void setSofficePath(String sofficePath) {
		this.sofficePath = sofficePath;
	}

	public void convert(ResumeMessage message) throws Exception {
		try{
			File dir = new File(message.getPath()).getParentFile();
			Process process = Runtime.getRuntime().exec(
					new String[] { sofficePath, "--headless", "--convert-to",
							"txt:Text", "-outdir", dir.getAbsolutePath(),
							message.getPath() });

			int value = process.waitFor();
			if (value == 0) {
				String[] parts = message.getPath().split("\\.");
				String ext = parts[parts.length - 1];
				String txtPath = message.getPath().replace("." + ext, ".txt");
				logger.info(" [x] Done converting document from: "
						+ message.getPath() + " to " + txtPath);

				message.setConvertedFilePath(txtPath);
			}
		}catch(Exception e){
			throw e;
		}
	}
	
//	public ResumePreProcessInfo preProcess(String filePath) throws Exception{
//		if(filePath==null || "".equals(filePath)){
//			return null;
//		}
//		ResumePreProcessInfo resumePreProcessInfo = new ResumePreProcessInfo();
//		
//		HashMap<String,String> resumeInfo = ResumeRecongnize.getTypeAndSource(filePath);
//		String source = resumeInfo.get("source");
//		String type = resumeInfo.get("type");
//		String txtFilePath = "";
//		
//		ResumeSourceInfo resumeSourceInfo = resumePreProcessInfo.getResumeSourceInfo();
//		resumeSourceInfo.setResumeSource(source);
//		resumeSourceInfo.setResumeType(type);
//		resumePreProcessInfo.setResumeSourceInfo(resumeSourceInfo);
//		
//		if(Status._51JOB.equals(source)){
//			txtFilePath = MhtResumeConverter.convert(filePath);
//		}else if(Status.ZHILIAN.equals(source)){
//			txtFilePath = DocResumeConverter.convert(filePath);
//		}else if(Status.LIEPIN.equals(source)){
//			txtFilePath = DocResumeConverter.convert(filePath);
//		}else{
//			txtFilePath = DocResumeConverter.convert(filePath);
//		}
//		
//		resumePreProcessInfo.getResumeMetaData().setTxtFilePath(txtFilePath);
//		ArrayList<String> resumeDataList =  new ArrayList<String>();
//		resumeDataList = readTxtFile(txtFilePath);
//		resumePreProcessInfo.getResumeMetaData().setSourceDataList(resumeDataList);
//		
//		return resumePreProcessInfo;
//	}
	
	public ResumeInfo preProcess(String filePath) throws Exception{
		if(filePath==null || "".equals(filePath)){
			return null;
		}
		ResumeInfo resumeInfo = new ResumeInfo();
		
		HashMap<String,String> resumeSourceInfo = ResumeRecongnize.getTypeAndSource(filePath);
		String source = resumeSourceInfo.get("source");
		String type = resumeSourceInfo.get("type");
		String txtFilePath = "";
		
		resumeInfo.setResumeSource(source);
		resumeInfo.setResumeType(type);
		
		if(Status._51JOB.equals(source)){
			txtFilePath = MhtResumeConverter.convert(filePath);
		}else if(Status.ZHILIAN.equals(source)){
			txtFilePath = DocResumeConverter.convert(filePath);
		}else if(Status.LIEPIN.equals(source)){
			txtFilePath = DocResumeConverter.convert(filePath);
		}else{
			txtFilePath = DocResumeConverter.convert(filePath);
		}
		
		resumeInfo.setTxtFilePath(txtFilePath);
		ArrayList<String> resumeDataList =  new ArrayList<String>();
		resumeDataList = readTxtFile(txtFilePath);
		resumeInfo.setSourceDataList(resumeDataList);
		
		return resumeInfo;
	}
	
	private ArrayList<String> readDocFile(String filePath) throws Exception{
		ArrayList<String> fileDataList = new ArrayList<String>();
		
		String txtFilePath = convert(filePath);
		if("".equals(txtFilePath) || txtFilePath == null){
			return null;
		}else{
			fileDataList = readTxtFile(txtFilePath);
		}
		return fileDataList;
	}
	
	private ArrayList<String> readTxtFile(String filePath) throws IOException{
		ArrayList<String> fileDataList = new ArrayList<String>();
		
		String line = "";
		BufferedReader reader = null;
        reader=new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8")); 
        
        // 一次读入一行，直到读入null为文件结束
        while ((line = reader.readLine()) != null) {
        	line = line.replaceAll("\u00A0", " ").replaceAll("\u3000", " ").trim();
        	line = line.replaceAll("：", ":");
        	fileDataList.add(line);
        }
        reader.close();
		return fileDataList;
	}
	
	private String convert(String filePath) throws Exception {
		String txtFilePath = "";
		if(filePath == null || "".equals(filePath)){
			return null;
		}
		File dir = new File(filePath).getParentFile();
		Process process = Runtime.getRuntime().exec(
				new String[] { sofficePath, "--headless", "--convert-to",
						"txt:Text", "-outdir", dir.getAbsolutePath(),
						filePath });

		int value = process.waitFor();
		if (value == 0) {
			String[] parts = filePath.split("\\.");
			String ext = parts[parts.length - 1];
			String txtPath = filePath.replace("." + ext, ".txt");
			txtFilePath = txtPath;
			return txtFilePath;
		}else{
			throw new ConVertException();
		}
	}

}
