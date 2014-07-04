package com.bole.resumeparser.service;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.bole.resumeparser.exception.ResumeParseException;
import com.bole.resumeparser.exception.document.UnSupportedResumeTypeException;
import com.bole.resumeparser.exception.html.DocResumeParseException;
import com.bole.resumeparser.exception.html.ResumeMessageParserException;
import com.bole.resumeparser.message.SpiderResumeMessage;
import com.bole.resumeparser.models.ESIndexWriterMessage;

public class SpiderResumeParser extends AbstractQueueListenerThread<SpiderResumeMessage> {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public SpiderResumeMessage parseMessage(String message) throws ResumeMessageParserException {
		// TODO Auto-generated method stub
		/**
		 *将rabbitmq中读取的信息转换为ResumeMessage类型的消息并进行处理
		 */
		SpiderResumeMessage spiderInfo = new SpiderResumeMessage();

		JSONObject json = (JSONObject) JSONValue.parse(message);
		if(json.containsKey("id")){
			if(json.get("id")!=null){
				spiderInfo.setResumeId(json.get("id").toString());
			}			
		}
		if(json.containsKey("collection")){
			if(json.get("collection")!=null){
				spiderInfo.setCollection(json.get("collection").toString());
			}			
		}
		if(json.containsKey("source")){
			if(json.get("source")!=null){
				spiderInfo.setSource(json.get("source").toString());
			}			
		}

		return spiderInfo;
	}

	@Override
	public void process(SpiderResumeMessage obj) throws ResumeParseException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process() throws DocResumeParseException,
			UnSupportedResumeTypeException, Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getQueueName() {
		// TODO Auto-generated method stub
		return "spiderqueue";
	}

}
