package com.bole.resumeconverter.document;

import java.io.File;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bole.resumeparser.document.impl.DocumentResumeParserAbstract;
import com.bole.resumeparser.exception.document.ConVertException;
import com.bole.resumeparser.message.ResumeMessage;
import com.bole.resumeparser.models.ResumeData;
import com.bole.resumeparser.util.MhtParser;

/**
 * 主要负责将各种输入的文档的识别和预处理（如doc，docx，pdf，html，txt等）
 * @author liyao
 *
 */
@Component
public class MhtResumeConverter {
	private static Logger logger = LoggerFactory
			.getLogger(MhtResumeConverter.class);

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
	
	public static String convert(String filePath) throws Exception {
		String txtFilePath = "";
			
		if(filePath == null || "".equals(filePath)){
			return txtFilePath;
		}
		String[] parts = filePath.split("\\.");
		String ext = parts[parts.length - 1];
		String txtPath = filePath.replace("." + ext, ".txt");
		
		int re = MhtParser.mht2txt(filePath, txtPath);
		
		if(re == 1){
			return txtPath;
		}else{
			throw new ConVertException();
		}
	}

}
