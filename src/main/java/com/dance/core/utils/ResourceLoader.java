package com.dance.core.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * The tools used resource loader!
 * @author zzm
 *
 */
public class ResourceLoader {
	protected static Log log = LogFactory.getLog(ResourceLoader.class);
	private ClassLoader classLoader;
	
	public ResourceLoader() {
		classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader ==null) {
			classLoader = getClass().getClassLoader();
		}
	}
	
	public URL getResource(String resourceName) {
		return classLoader.getResource(resourceName);
	}
	
	public Enumeration<URL> getUrls(String resourceName) {
		Enumeration<URL> urls = null;
		try {
			 urls = this.classLoader.getResources(resourceName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return urls;
	}
	
	public InputStream getResourceAsStream(String resourceName) {
		return this.classLoader.getResourceAsStream(resourceName);
	}
	
	public File getFile(String resourceName) {
		String file = getFileName(resourceName);
		return file==null?null:new File(file);
	}
	
	public String getFileName(String resourceName) {
		URL url = getResource(resourceName);
		if(url == null) {
			log.warn(resourceName + " is not exist!");
		}
		return url.getFile();
	}
	
	public boolean fileExists(String resourceName) {
		return getResource(resourceName) != null;
	}
}
