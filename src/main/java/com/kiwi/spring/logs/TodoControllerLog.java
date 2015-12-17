package com.kiwi.spring.logs;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.kiwi.spring.util.MyServletContext;

public class TodoControllerLog {

	public static Logger logger = Logger.getLogger("com.kiwi.spring");
	private static FileHandler fh;
	
	static{
		 try {
			 String contextPath = MyServletContext.getContextPath();
			 if(contextPath != null){
				 logger.setLevel(Level.ALL);
				 fh = new FileHandler(contextPath + "TodoController_log.txt");
				 SimpleFormatter sformatter = new SimpleFormatter();
				 fh.setFormatter(sformatter);
				 logger.addHandler(fh);
			 }
		} catch (Exception e) {
			 logger.log(Level.INFO, "Log file couldn't be created.");
			e.printStackTrace();
		}
	 }
}
