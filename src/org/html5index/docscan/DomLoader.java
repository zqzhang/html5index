package org.html5index.docscan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class DomLoader {
  
  
  public static BufferedReader openReader(String url) throws IOException {
    if (url.startsWith("/")) {
      InputStream inputStream = DomLoader.class.getResourceAsStream(url);
      return new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
    } 
    String cacheName = url.replace(":", "").replace("/", "");
    File cacheFile = new File("cache", cacheName);
    if (cacheFile.exists()) {
      return new BufferedReader(new InputStreamReader(new FileInputStream(cacheFile), "utf-8"));
    }
      
    URLConnection con = new URL(url).openConnection();
    String contentType = con.getContentType();
    String charSet = "ISO-8859-1";
    if (contentType != null) {
      for (String part: contentType.split(";")) {
        part = part.trim();
        if (part.startsWith("charset=")) {
          charSet = part.substring(8);
          charSet = charSet.replaceAll("\"", "");
          break;
        }
      }
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), charSet));
    String text = removeScriptTag(loadText(reader));
    reader.close();
    
    Writer writer = new OutputStreamWriter(new FileOutputStream(cacheFile), "utf-8");
    writer.write(text);
    writer.close();
    
    return new BufferedReader(new StringReader(text));
  }

  static String removeScriptTag(String data){
	  StringBuilder regex = new StringBuilder("<script[^>]*>(.*?)</script>");
	  int flags = Pattern.MULTILINE | Pattern.DOTALL| Pattern.CASE_INSENSITIVE;
	  Pattern pattern = Pattern.compile(regex.toString(), flags);
	  Matcher matcher = pattern.matcher(data);
	  return matcher.replaceAll("");
  }
  
  static void createDirDefault(){
	  File dir = new File("cache");
	  if (!dir.exists())
		  dir.mkdirs();
	  dir = new File("gen");
	  if (!dir.exists())
		  dir.mkdirs();
  }
  
  static String loadText(String url) throws IOException {
    BufferedReader reader = openReader(url);
    String result = loadText(reader);
    reader.close();
    return result;
  }
  
  static String loadText(BufferedReader reader) throws IOException {
    StringBuilder sb = new StringBuilder();
    String line = reader.readLine();
    if (line != null) {
      sb.append(line);
      while (true) {
        line = reader.readLine();
        if (line == null) {
          break;
        }
        sb.append('\n');
        sb.append(line);
      }
    }
    return sb.toString();
  }
  
  public static Document loadDom(String url) {
	createDirDefault();
    Parser parser = new Parser();

    try {
      parser.setFeature(Parser.namespacesFeature, false);
      parser.setFeature(Parser.namespacePrefixesFeature, false);
      Reader reader = openReader(url);
      DOMResult result = new DOMResult();
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.transform(new SAXSource(parser, new InputSource(reader)), result);
      reader.close();
      return (Document) result.getNode();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
   }
}
