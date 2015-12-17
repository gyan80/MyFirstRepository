package com.kiwi.spring.util;

public class MyServletContext {

	private static String   contextPath;

    private MyServletContext() {
    }

    public static String getContextPath() {
        return contextPath;
    }

    public static void setContextPath(String cp) {
        contextPath = cp;
    }
}
