package net.spring.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bole.resumeparser.document.impl.DocumentResumeParserInterface;
import com.bole.resumeparser.exception.ResumeParseException;
import com.bole.resumeparser.html.impl.HtmlResumeParserInterface;
import com.bole.resumeparser.html.impl.LiePinResumeParser;
import com.bole.resumeparser.html.impl.ZhiLianResumeParser;
import com.bole.resumeparser.html.impl._51jobResumeParser;
import com.bole.resumeparser.models.ResumeData;
import com.bole.resumeparser.models.ResumeInformation;
import com.bole.resumeparser.models.TextResumeData;


@RestController
@RequestMapping("/test/")
public class ResumeParserController {


@RequestMapping(value = "/{name}", method = RequestMethod.GET)
	 public String getGreeting(@PathVariable String name) {
	  String result="Hello "+name;  
	  return result;
	 }
}
