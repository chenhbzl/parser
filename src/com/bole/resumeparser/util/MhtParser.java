package com.bole.resumeparser.util;

import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;  
import java.io.BufferedReader;  
import java.io.DataOutputStream;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStreamWriter;  
import java.io.Reader;  
import java.io.Writer;  
import java.util.Enumeration;  
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;  
import javax.mail.Header;
import javax.mail.MessagingException;    
import javax.mail.Multipart;    
import javax.mail.Session;    
import javax.mail.internet.MimeBodyPart;    
import javax.mail.internet.MimeMessage;    
import javax.mail.internet.MimeMultipart;    
import javax.mail.internet.MimePartDataSource;  

import com.bole.resumeparser.document.impl.DocHtml51jobResumeParser;
import com.bole.resumeparser.models.ContactInfoData;
import com.bole.resumeparser.models.TextResumeData;

public class MhtParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MhtParser.mht2txt("/home/bobby/mnt/邮箱同步的简历/简历模板/resume3/李林芯.doc", "/home/bobby/mnt/邮箱同步的简历/简历模板/resume/何川.txt");
	}
	/** 
	 * 将 mht文件转换成 html文件 
	 * @param s_SrcMht 
	 * @param s_DescHtml 
	 */
	
	public static String[] mht2txt(String s_SrcMht){
		String [] resumeContent= null;
		try {    
	        InputStream fis = new FileInputStream(s_SrcMht);  
	        Session mailSession = Session.getDefaultInstance(System.getProperties(), null);  
	        MimeMessage msg = new MimeMessage(mailSession, fis);  
	        Object content = msg.getContent(); 
//	        System.out.println(content.toString());
	        if (content instanceof Multipart){
	            MimeMultipart mp = (MimeMultipart)content;
	            MimeBodyPart bp1 = (MimeBodyPart)mp.getBodyPart(0);
	            String strText = "";
	            
	            String inputstr = "";
	            
	            String strEncoding = bp1.getEncoding();
	            
	            if("base64".equals(strEncoding)){
	            	BufferedReader in = new BufferedReader(new InputStreamReader(bp1.getInputStream()));
	 	            StringBuffer buffer = new StringBuffer();
	 	            String line = "";
	 	            while ((line = in.readLine()) != null){
	 	              buffer.append(line);
	 	            }
	 	            inputstr = buffer.toString();
	            }else if("quoted-printable".equals(strEncoding)){
	            	strEncoding = getEncoding(bp1);  
		              
		            //获取mht文件的内容 
	            	if(strEncoding==null){
	            		inputstr = getHtmlText(bp1, "utf-8");
	            	}else{
	            		inputstr = getHtmlText(bp1, strEncoding);
	            	}
		            
	            }
	            
	            inputstr = inputstr.replaceAll("&nbsp;", " ");
	            DocHtml51jobResumeParser parser =  new DocHtml51jobResumeParser(inputstr,"51job");
	 			TextResumeData redata = parser.parse();
	 			
	            strText = html2text(inputstr);
	            strText = strText.replaceAll("#\\d+;", "").replaceAll("\t", "\r");
	            resumeContent = strText.split(System.getProperty("line.separator"));
	        }
		}catch (Exception e) {    
	    	e.printStackTrace();   
	    	return null;
        }
		return resumeContent;
	}
	public static int mht2txt(String s_SrcMht, String s_DescHtml) {  
	    try {    
	        InputStream fis = new FileInputStream(s_SrcMht);  
	        Session mailSession = Session.getDefaultInstance(System.getProperties(), null);  
	        MimeMessage msg = new MimeMessage(mailSession, fis);  
	        Object content = msg.getContent(); 
//	        System.out.println(content.toString());
	        if (content instanceof Multipart){
	            MimeMultipart mp = (MimeMultipart)content;
	            MimeBodyPart bp1 = (MimeBodyPart)mp.getBodyPart(0);
	            String strText = "";
	            
	            String inputstr = "";
	            
	            String strEncoding = bp1.getEncoding();
	            
	            if("base64".equals(strEncoding)){
	            	BufferedReader in = new BufferedReader(new InputStreamReader(bp1.getInputStream()));
	 	            StringBuffer buffer = new StringBuffer();
	 	            String line = "";
	 	            while ((line = in.readLine()) != null){
	 	              buffer.append(line);
	 	            }
	 	            inputstr = buffer.toString();
	            }else if("quoted-printable".equals(strEncoding)){
	            	//获取mht文件内容代码的编码  
	            	strEncoding = getEncoding(bp1);  
		              
		            //获取mht文件的内容 
	            	if(strEncoding==null){
	            		inputstr = getHtmlText(bp1, "utf-8");
	            	}else{
	            		inputstr = getHtmlText(bp1, strEncoding);
	            	}
		            
	            }
	            if (strText == null)    
	                return 0;
	            inputstr = inputstr.replaceAll("&nbsp;", " ");
	 			
	            //创建以mht文件名称的文件夹，主要用来保存资源文件。  
	            File parent = null;  
	            if (mp.getCount() > 1) {  
	                parent = new File(new File(s_DescHtml).getAbsolutePath() + ".files");  
	                parent.mkdirs();  
	                if (!parent.exists()){   //创建文件夹失败的话则退出  
	                    return 0;  
	                }  
	            }
	              
	            //FOR中代码 主要是保存资源文件及替换路径  
	            for (int i = 1; i < mp.getCount(); ++i) {    
	                MimeBodyPart bp = (MimeBodyPart)mp.getBodyPart(i);  
	                //获取资源文件的路径   
	                //例（获取： http://xxx.com/abc.jpg）  
	                String strUrl = getResourcesUrl(bp);  
	                if (strUrl==null || strUrl.length()==0)    
	                    continue;  
	                  
	                DataHandler dataHandler = bp.getDataHandler();    
	                MimePartDataSource source = (MimePartDataSource)dataHandler.getDataSource();  
	                  
	                //获取资源文件的绝对路径  
	                String FilePath = parent.getAbsolutePath() + File.separator + getName(strUrl, i);  
	                File resources = new File(FilePath);  
	                  
	                //保存资源文件  
	                if (SaveResourcesFile(resources, bp.getInputStream())){  
	                    //将远程地址替换为本地地址  如图片、JS、CSS样式等等  
	                    strText = strText.replace(strUrl, resources.getAbsolutePath());   
	                }  
	            }  
	              
	            //最后保存HTML文件  
	            strText = html2text(inputstr);
	            strText = strText.replaceAll("#\\d+;", "").replaceAll("\t", "\r").replaceAll("(\r\n)+", "\r\n");
	            SaveHtml(strText, s_DescHtml, "utf-8");
	        }
	        return 1;
	    } catch (Exception e) {    
	    	e.printStackTrace();   
	    	return 0;
	    }    
	}  
	  
	/** 
	 * 获取mht文件内容中资源文件的名称 
	 * @param strName 
	 * @param ID 
	 * @return 
	 */  
	public static String getName(String strName, int ID) {    
	    char separator1 = '/';  
	    char separator2 = '\\';  
	    //将换行替换  
	    strName = strName.replaceAll("\r\n", "");  
	      
	    //获取文件名称  
	    if( strName.lastIndexOf(separator1) >= 0){  
	        return strName.substring(strName.lastIndexOf(separator1) + 1);  
	    }  
	    if( strName.lastIndexOf(separator2) >= 0){  
	        return strName.substring(strName.lastIndexOf(separator2) + 1);  
	    }  
	    return "";  
	}  
	  
	  
	/** 
	 * 将提取出来的html内容写入保存的路径中。 
	 * @param strText 
	 * @param strHtml 
	 * @param strEncodng 
	 */  
	public static boolean SaveHtml(String s_HtmlTxt, String s_HtmlPath , String s_Encode) {    
	    try{  
	        Writer out = null;  
	        out = new OutputStreamWriter(new FileOutputStream(s_HtmlPath, false), s_Encode);  
	        out.write(s_HtmlTxt);  
	        out.close();  
	    }catch(Exception e){  
	        return false;  
	    }  
	    return true;  
	}    
	  
	  
	/** 
	 * 保存网页中的JS、图片、CSS样式等资源文件 
	 * @param SrcFile     源文件 
	 * @param inputStream 输入流 
	 * @return 
	 */  
	private static boolean SaveResourcesFile(File SrcFile, InputStream inputStream) {    
	    if (SrcFile == null || inputStream == null) {    
	        return false;     
	    }    
	    
	    BufferedInputStream in = null;    
	    FileOutputStream fio = null;    
	    BufferedOutputStream osw = null;    
	    try {    
	        in = new BufferedInputStream(inputStream);    
	        fio = new FileOutputStream(SrcFile);    
	        osw = new BufferedOutputStream(new DataOutputStream(fio));    
	        int index = 0;    
	        byte[] a = new byte[1024];    
	        while ((index = in.read(a)) != -1) {    
	            osw.write(a, 0, index);  
	        }  
	        osw.flush();    
	        return true;    
	    } catch (Exception e) {       
	        e.printStackTrace();    
	        return false;    
	    } finally{    
	        try {    
	        if (osw != null)    
	            osw.close();    
	        if (fio != null)    
	            fio.close();    
	        if (in != null)    
	            in.close();    
	        if (inputStream != null)    
	            inputStream.close();    
	        } catch (Exception e) {    
	            e.printStackTrace();  
	            return false;  
	        }   
	    }    
	}    
	  
	//base64解码
    private static String base64Decoder(String s) throws Exception {
        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
        byte[] b = decoder.decodeBuffer(s);
        return (new String(b));
    }
	  
	/** 
	 * 获取mht文件里资源文件的URL路径 
	 * @param bp 
	 * @return 
	 */  
	private static String getResourcesUrl(MimeBodyPart bp) {   
	    if(bp==null){  
	        return null;  
	    }  
	    try {    
	        Enumeration list = bp.getAllHeaders();    
	        while (list.hasMoreElements()) {    
	            javax.mail.Header head = (javax.mail.Header)list.nextElement();    
	            if (head.getName().compareTo("Content-Location") == 0) {    
	                return head.getValue();  
	            }    
	        }  
	        return null;  
	    } catch (MessagingException e) {    
	        return null;  
	    }    
	}   
	  
	  
	/** 
	 * 获取mht文件中的内容代码 
	 * @param bp 
	 * @param strEncoding 该mht文件的编码 
	 * @return 
	 */  
	private static String getHtmlText(MimeBodyPart bp, String strEncoding) {    
	    InputStream textStream = null;    
	    BufferedInputStream buff = null;    
	    BufferedReader br = null;    
	    Reader r = null;    
	    try {    
	        textStream = bp.getInputStream();    
	        buff = new BufferedInputStream(textStream);    
	        r = new InputStreamReader(buff, strEncoding);   
	        br = new BufferedReader(r);    
	        StringBuffer strHtml = new StringBuffer("");    
	        String strLine = null;    
	        while ((strLine = br.readLine()) != null) {    
	            strHtml.append(strLine + "\r\n");    
	        }    
	        br.close();    
	        r.close();    
	        textStream.close();    
	        return strHtml.toString();
	    } catch (Exception e) {    
	    e.printStackTrace();    
	    } finally{  
	        try{    
	            if (br != null)    
	            br.close();    
	            if (buff != null)    
	            buff.close();    
	            if (textStream != null)    
	            textStream.close();    
	        }catch(Exception e){    
	        }    
	    }    
	    return null;    
	}  
	  
	/** 
	 * 获取mht网页文件中内容代码的编码 
	 * @param bp 
	 * @return 
	 */  
	private static String getEncoding(MimeBodyPart bp) {  
	    if(bp==null){  
	        return null;  
	    }  
	    try {    
	        Enumeration list = bp.getAllHeaders();    
	        while (list.hasMoreElements()) {    
	            javax.mail.Header head = (javax.mail.Header)list.nextElement();    
	            if (head.getName().compareTo("Content-Type") == 0) {    
	                String strType = head.getValue();    
	                int pos = strType.indexOf("charset=");   
	                if (pos>=0) {    
	                    String strEncoding = strType.substring(pos + 8, strType.length());    
	                    if(strEncoding.startsWith("\"") || strEncoding.startsWith("\'")){  
	                        strEncoding = strEncoding.substring(1 , strEncoding.length());  
	                    }  
	                    if(strEncoding.endsWith("\"") || strEncoding.endsWith("\'")){  
	                        strEncoding = strEncoding.substring(0 , strEncoding.length()-1);  
	                    }  
	                    if (strEncoding.toLowerCase().compareTo("gb2312") == 0) {    
	                        strEncoding = "gbk";    
	                    }
	                    return strEncoding;    
	                }    
	            }  
	        }    
	    } catch (MessagingException e) {    
	        e.printStackTrace();    
	    }  
	    return null;   
	}  
	
	
	 public static String html2text(String html) {
	        StringBuffer sb = new StringBuffer(html.length());
	        char[] data = html.toCharArray();
	        int start = 0;
	        boolean previousIsPre = false;
	        Token token = null;
	        for(;;) {
	            token = parse(data, start, previousIsPre);
	            if(token==null)
	                break;
	            previousIsPre = token.isPreTag();
	            sb = sb.append(token.getText());
	            start += token.getLength();
	        }
	        return sb.toString();
	    }

	    private static Token parse(char[] data, int start, boolean previousIsPre) {
	        if(start>=data.length)
	            return null;
	        // try to read next char:
	        char c = data[start];
	        if(c=='<') {
	            // this is a tag or comment or script:
	            int end_index = indexOf(data, start+1, '>');
	            if(end_index==(-1)) {
	                // the left is all text!
	                return new Token(Token.TOKEN_TEXT, data, start, data.length, previousIsPre);
	            }
	            String s = new String(data, start, end_index-start+1);
	            // now we got s="<...>":
	            if(s.startsWith("<!--")) { // this is a comment!
	                int end_comment_index = indexOf(data, start+1, "-->");
	                if(end_comment_index==(-1)) {
	                    // illegal end, but treat as comment:
	                    return new Token(Token.TOKEN_COMMENT, data, start, data.length, previousIsPre);
	                }
	                else
	                    return new Token(Token.TOKEN_COMMENT, data, start, end_comment_index+3, previousIsPre);
	            }
	            String s_lowerCase = s.toLowerCase();
	            if(s_lowerCase.startsWith("<script")) { // this is a script:
	                int end_script_index = indexOf(data, start+1, "</script>");
	                if(end_script_index==(-1))
	                    // illegal end, but treat as script:
	                    return new Token(Token.TOKEN_SCRIPT, data, start, data.length, previousIsPre);
	                else
	                    return new Token(Token.TOKEN_SCRIPT, data, start, end_script_index+9, previousIsPre);
	            }
	            else { // this is a tag:
	                return new Token(Token.TOKEN_TAG, data, start, start+s.length(), previousIsPre);
	            }
	        }
	        // this is a text:
	        int next_tag_index = indexOf(data, start+1, '<');
	        if(next_tag_index==(-1))
	            return new Token(Token.TOKEN_TEXT, data, start, data.length, previousIsPre);
	        return new Token(Token.TOKEN_TEXT, data, start, next_tag_index, previousIsPre);
	    }

	    private static int indexOf(char[] data, int start, String s) {
	        char[] ss = s.toCharArray();
	        // TODO: performance can improve!
	        for(int i=start; i<(data.length-ss.length); i++) {
	            // compare from data[i] with ss[0]:
	            boolean match = true;
	            for(int j=0; j<ss.length; j++) {
	                if(data[i+j]!=ss[j]) {
	                    match = false;
	                    break;
	                }
	            }
	            if(match)
	                return i;
	        }
	        return (-1);
	    }

	    private static int indexOf(char[] data, int start, char c) {
	        for(int i=start; i<data.length; i++) {
	            if(data[i]==c)
	                return i;
	        }
	        return (-1);
	    }

	}

	class Token {

	    public static final int TOKEN_TEXT    = 0; // html text.
	    public static final int TOKEN_COMMENT = 1; // comment like <!-- comments... -->
	    public static final int TOKEN_TAG     = 2; // tag like <pre>, <font>, etc.
	    public static final int TOKEN_SCRIPT  = 3;

	    private static final char[] TAG_BR  = "<br".toCharArray();
	    private static final char[] TAG_P   = "<p".toCharArray();
	    private static final char[] TAG_LI  = "<li".toCharArray();
	    private static final char[] TAG_PRE = "<pre".toCharArray();
	    private static final char[] TAG_HR  = "<hr".toCharArray();

	    private static final char[] END_TAG_TD = "</td>".toCharArray();
	    private static final char[] END_TAG_TR = "</tr>".toCharArray();
	    private static final char[] END_TAG_LI = "</li>".toCharArray();

	    private static final Map SPECIAL_CHARS = new HashMap();

	    private int type;
	    private String html;           // original html
	    private String text = null;    // text!
	    private int length = 0;        // html length
	    private boolean isPre = false; // isPre tag?

	    static {
	        SPECIAL_CHARS.put("&quot;", "\"");
	        SPECIAL_CHARS.put("&lt;",   "<");
	        SPECIAL_CHARS.put("&gt;",   ">");
	        SPECIAL_CHARS.put("&amp;",  "&");
	        SPECIAL_CHARS.put("&reg;",  "(r)");
	        SPECIAL_CHARS.put("&copy;", "(c)");
	        SPECIAL_CHARS.put("&nbsp;", " ");
	        SPECIAL_CHARS.put("&pound;", "?");
	    }

	    public Token(int type, char[] data, int start, int end, boolean previousIsPre) {
	        this.type = type;
	        this.length = end - start;
	        this.html = new String(data, start, length);
	        System.out.println("[Token] html=" + html + ".");
	        parseText(previousIsPre);
	        System.out.println("[Token] text=" + text + ".");
	    }

	    public int getLength() {
	        return length;
	    }

	    public boolean isPreTag() {
	        return isPre;
	    }

	    private void parseText(boolean previousIsPre) {
	        if(type==TOKEN_TAG) {
	            char[] cs = html.toCharArray();
	            if(compareTag(TAG_BR, cs) || compareTag(TAG_P, cs))
	                text = "\n";
	            else if(compareTag(TAG_LI, cs))
	                text = "\n* ";
	            else if(compareTag(TAG_PRE, cs))
	                isPre = true;
	            else if(compareTag(TAG_HR, cs))
	                text = "\n--------\n";
	            else if(compareString(END_TAG_TD, cs))
	                text = "\t";
	            else if(compareString(END_TAG_TR, cs) || compareString(END_TAG_LI, cs))
	                text = "\n";
	        }
	        // text token:
	        else if(type==TOKEN_TEXT) {
	            text = toText(html, previousIsPre);
	        }
	    }

	    public String getText() {
	        return text==null ? "" : text;
	    }

	    private String toText(String html, final boolean isPre) {
	        char[] cs = html.toCharArray();
	        StringBuffer buffer = new StringBuffer(cs.length);
	        int start = 0;
	        boolean continueSpace = false;
	        char current, next;
	        for(;;) {
	            if(start>=cs.length)
	                break;
	            current = cs[start]; // read current char
	            if(start+1<cs.length) // and next char
	                next = cs[start+1];
	            else
	                next = '\0';
	            if(current==' ') {
	                if(isPre || !continueSpace)
	                    buffer = buffer.append(' ');
	                continueSpace = true;
	                // continue loop:
	                start++;
	                continue;
	            }
	            // not ' ', so:
	            if(current=='\r' && next=='\n') {
	                if(isPre)
	                    buffer = buffer.append('\n');
	                // continue loop:
	                start+=2;
	                continue;
	            }
	            if(current=='\n' || current=='\r') {
	                if(isPre)
	                    buffer = buffer.append('\n');
	                // continue loop:
	                start++;
	                continue;
	            }
	            // cannot continue space:
	            continueSpace = false;
	            if(current=='&') {
	                // maybe special char:
	                int length = readUtil(cs, start, ';', 10);
	                if(length==(-1)) { // just '&':
	                    buffer = buffer.append('&');
	                    // continue loop:
	                    start++;
	                    continue;
	                }
	                else { // check if special character:
	                    String spec = new String(cs, start, length);
	                    String specChar = (String)SPECIAL_CHARS.get(spec);
	                    if(specChar!=null) { // special chars!
	                        buffer = buffer.append(specChar);
	                        // continue loop:
	                        start+=length;
	                        continue;
	                    }
	                    else { // check if like '&#1234':
	                        if(next=='#') { // maybe a char
	                            String num = new String(cs, start+2, length-3);
	                            try {
	                                int code = Integer.parseInt(num);
	                                if(code>0 && code<65536) { // this is a special char:
	                                    buffer = buffer.append((char)code);
	                                    // continue loop:
	                                    start++;
	                                    continue;
	                                }
	                            }
	                            catch(Exception e) {}
	                            // just normal char:
	                            buffer = buffer.append("&#");
	                            // continue loop:
	                            start+=2;
	                            continue;
	                        }
	                        else { // just '&':
	                            buffer = buffer.append('&');
	                            // continue loop:
	                            start++;
	                            continue;
	                        }
	                    }
	                }
	            }
	            else { // just a normal char!
	                buffer = buffer.append(current);
	                // continue loop:
	                start++;
	                continue;
	            }
	        }
	        return buffer.toString();
	    }

	    // read from cs[start] util meet the specified char 'util',
	    // or null if not found:
	    private int readUtil(final char[] cs, final int start, final char util, final int maxLength) {
	        int end = start+maxLength;
	        if(end>cs.length)
	            end = cs.length;
	        for(int i=start; i<start+maxLength; i++) {
	            if(cs[i]==util) {
	                return i-start+1;
	            }
	        }
	        return (-1);
	    }

	    // compare standard tag "<input" with tag "<INPUT value=aa>"
	    private boolean compareTag(final char[] ori_tag, char[] tag) {
	        if(ori_tag.length>=tag.length)
	            return false;
	        for(int i=0; i<ori_tag.length; i++) {
	            if(Character.toLowerCase(tag[i])!=ori_tag[i])
	                return false;
	        }
	        // the following char should not be a-z:
	        if(tag.length>ori_tag.length) {
	            char c = Character.toLowerCase(tag[ori_tag.length]);
	            if(c<'a' || c>'z')
	                return true;
	            return false;
	        }
	        return true;
	    }

	    private boolean compareString(final char[] ori, char[] comp) {
	        if(ori.length>comp.length)
	            return false;
	        for(int i=0; i<ori.length; i++) {
	            if(Character.toLowerCase(comp[i])!=ori[i])
	                return false;
	        }
	        return true;
	    }

	    public String toString() {
	        return html;
	    }
	
	
	
}  
