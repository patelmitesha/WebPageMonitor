package com.misd;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class LoadConfig {
	
	Properties prop =null;
	public LoadConfig(String propertyFileName) throws Exception{
		prop = new Properties();
		InputStream input = null;
		input = new FileInputStream(propertyFileName);

		// load a properties file
		prop.load(input);
		
		
		input.close();
		
	    
	}
	
	public String getElement(String propertyName) {
		Enumeration<?> e = prop.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			if(key.equalsIgnoreCase(propertyName))
				return prop.getProperty(key);
		}
		return null;
	}

	
}