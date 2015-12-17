package com.kiwi.spring.logs;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kiwi.spring.util.MyServletContext;

@Component
public class Log {
	public static Logger logger = Logger.getLogger("com.kiwi.spring");
	private static FileHandler fh;
	
	static{
		 try {
			 String contextPath = MyServletContext.getContextPath();
			 if(contextPath != null){
				 fh = new FileHandler(contextPath + "loging.log");
				 logger.addHandler(fh);
			 }
		} catch (Exception e) {
			 logger.log(Level.INFO, "Log file couldn't be created.");
			e.printStackTrace();
		}
	 }

}

