package com.bole.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ResourcesConfig {
/**
 * 加载所有资源，用于建立解析
 */

	public static HashSet<String> segmentTitleSet = new HashSet<String>();
	public static HashSet<String> lastNameSet = new HashSet<String>();
	public static ArrayList<String> lastNameArray = new ArrayList<String>();
	public static HashSet<String> universitySet = new HashSet<String>();
	public static HashSet<String> universityEndsSet = new HashSet<String>();
	public static HashSet<String> roleSet = new HashSet<String>();
	public static HashSet<String> degreeSet = new HashSet<String>();
	public static HashSet<String> rolesKeywordsSet = new HashSet<String>();
	
	public static String nameStartRegex = "";
	public static String degreeRegex = "";
	public static String universityRegex = "";
	public static String rolesKeywordsRegex = "";
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void loadConfig(String folder) throws IOException{
		File segmentTitlefile = new File(folder+"/SegmentName.txt");
		File lastNamefile = new File(folder+"/LastNames.txt");
		File universityFile = new File(folder+"/Universities.txt");
		File universityEndsFile = new File(folder+"/UniversityEnds.txt");
		File rolesFile = new File(folder+"/Roles.txt");
		File degreeFile = new File(folder+"/Degrees.txt");
		File rolesKeywordsFile = new File(folder+"/RolesKeywords.txt");
		
		BufferedReader segmentTitleReader = null;
		BufferedReader lastNameReader = null;
		BufferedReader universityReader = null;
		BufferedReader universityEndsReader = null;
		BufferedReader rolesReader = null;
		BufferedReader degreeReader = null;
		BufferedReader rolesKeywordsReader = null;
		
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            
            segmentTitleReader = new BufferedReader(new FileReader(segmentTitlefile));
            lastNameReader = new BufferedReader(new FileReader(lastNamefile));
            universityReader = new BufferedReader(new FileReader(universityFile));
            universityEndsReader = new BufferedReader(new FileReader(universityEndsFile));
            rolesReader = new BufferedReader(new FileReader(rolesFile));
            degreeReader = new BufferedReader(new FileReader(degreeFile));
            rolesKeywordsReader = new BufferedReader(new FileReader(rolesKeywordsFile));
            
            String line = "";

            // 一次读入一行，直到读入null为文件结束
            while ((line = segmentTitleReader.readLine()) != null) {
            	line = line.trim();
            	segmentTitleSet.add(line);
            }
   
            while ((line = lastNameReader.readLine()) != null) {
            	line = line.trim();
            	lastNameSet.add(line);
            	lastNameArray.add(line);
            }
            
            while ((line = universityReader.readLine()) != null) {
            	line = line.trim();
            	universitySet.add(line);
            }
            
            while ((line = universityEndsReader.readLine()) != null) {
            	line = line.trim();
            	universityEndsSet.add(line);
            }
            
            while ((line = rolesReader.readLine()) != null) {
            	line = line.trim();
            	roleSet.add(line);
            }
            
            while ((line = degreeReader.readLine()) != null) {
            	line = line.trim();
            	degreeSet.add(line);
            }
            
            while ((line = rolesKeywordsReader.readLine()) != null) {
            	line = line.trim();
            	rolesKeywordsSet.add(line);
            }
            
            Iterator<String> iter = lastNameArray.iterator();
    		while(iter.hasNext()){
    			if(nameStartRegex.equals("")){
    				nameStartRegex = iter.next();
    			}else{
    				nameStartRegex = nameStartRegex+"|"+iter.next();
    			}
    		}
    		
    		iter = degreeSet.iterator();
    		while(iter.hasNext()){
    			if(degreeRegex.equals("")){
    				degreeRegex = iter.next();
    			}else{
    				degreeRegex = degreeRegex+"|"+iter.next();
    			}
    		}
    		
    		iter = universitySet.iterator();
    		while(iter.hasNext()){
    			if(universityRegex.equals("")){
    				universityRegex = iter.next();
    			}else{
    				universityRegex = universityRegex+"|"+iter.next();
    			}
    		}
    		
    		iter = rolesKeywordsSet.iterator();
    		while(iter.hasNext()){
    			if(rolesKeywordsRegex.equals("")){
    				rolesKeywordsRegex = iter.next();
    			}else{
    				rolesKeywordsRegex = rolesKeywordsRegex+"|"+iter.next();
    			}
    		}
    		
    		
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	segmentTitleReader.close();
        	lastNameReader.close();
        	universityReader.close();
        	universityEndsReader.close();
        	rolesReader.close();
        	degreeReader.close();
        }
        
        
	}

}
