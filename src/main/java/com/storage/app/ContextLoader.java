package com.storage.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ContextLoader {
	
	private final static ContextLoader context = new ContextLoader(); 
	
	private File containerPath;
	
	private ContextLoader() {
		Properties properties = new Properties();
		
		InputStream in = getClass().getResourceAsStream("/containers.properties");
		try {
			
			properties.load(in);
			
			containerPath = new File((String) properties.get("container_path"));
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				in.close();	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static ContextLoader getInstance() {
		return context;
	}
	
	public File getContainerPath() {
		return containerPath;
	}
	
	

}
