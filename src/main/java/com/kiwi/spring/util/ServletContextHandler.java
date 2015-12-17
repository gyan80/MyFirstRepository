package com.kiwi.spring.util;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletContextHandler implements ServletContextListener {
	
	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		
		MyServletContext.setContextPath(contextEvent.getServletContext().getRealPath("resources")+File.separator+"logs"+File.separator);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}
}
