package com.bole.config;

public class Status {
	//简历解析过程中的错误
	public static String CONVERTED = "converted";
	public static String CONVERTED_ERROR = "convertedError";
	public static String PARSED_ERROR = "parseError";
	public static String PARSED = "parsed";
	
	
	
	//简历解析的状态
	public static String INVALID_RESUME = "invalid resume";
	public static String DELETED_RESUME = "deleted resume";
	public static String TIMEOUT_RESUME = "timeout resume";  //请求超时的简历
	public static String UNSUPPORTED_RESUME = "unsupported resume";  //不支持的简历
	
	
	//简历的来源
	public static String _51JOB = "51job";
	public static String ZHILIAN = "zhilian";
	public static String LIEPIN = "liepin";
	public static String HEADIN = "headin";
	
	
	//简历类型
	public static String DOC = "doc";
	public static String DOCX = "DOCX";
	public static String PDF = "pdf";
	public static String HTML = "html";
	public static String DOC_HTML = "doc_html";
	public static String MHTML = "mhtml";
	public static String TXT = "txt";
	public static String XML = "xml";
	public static String DOC_XML = "doc_xml";
}
