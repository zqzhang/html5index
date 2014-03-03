package org.html5index.docscan;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.html5index.model.DocumentationProvider;

public class Sources {
	public static final String CSS_SPEC_LINKS = "/spec/css-links.properties";
	public static final String JS_SPEC_LINKS = "/spec/js-links.properties";
	
	public static DocumentationProvider[] SOURCES = {};
	
	public static void init(){
		InputStream iscss = Sources.class.getClass().getResourceAsStream(CSS_SPEC_LINKS);
		InputStream isjs = Sources.class.getClass().getResourceAsStream(JS_SPEC_LINKS);
		List<DocumentationProvider> list = new ArrayList<DocumentationProvider>();
		Properties prop = new Properties();
		try {
			prop.load(iscss);
			iscss.close();
			// read css properties from spec
			Set<Entry<Object, Object>> set = prop.entrySet();
			for (Entry<Object, Object> entry : set){
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				DocumentationProvider scan = new CrosswalkCSSSpecScan(key, value);
				list.add(scan);
			}
			prop = new Properties();
			prop.load(isjs);
			isjs.close();
			// read js interfaces from spec
			set = prop.entrySet();
			for (Entry<Object, Object> entry : set){
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				DocumentationProvider scan = new Html5SpecScan(key, value);
				list.add(scan);
			}
			
			SOURCES = list.toArray(SOURCES);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
