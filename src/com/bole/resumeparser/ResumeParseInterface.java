package com.bole.resumeparser;

import com.bole.resumeparser.exception.ResumeParseException;
import com.bole.resumeparser.models.ResumeData;

public interface ResumeParseInterface {
	public ResumeData parse() throws ResumeParseException;
}
