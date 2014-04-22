package org.html5index.docscan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.html5index.idl.ProParser;
import org.html5index.model.Artifact;
import org.html5index.model.Library;
import org.html5index.model.Type;
import org.html5index.util.HtmlWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Supports multiple sources.
 */
public class CrosswalkCSSSpecScan extends AbstractSpecScan {

	final String title;
	final List<String[]> urls = new ArrayList<String[]>();
	final List<Document> docs = new ArrayList<Document>();
	final Map<String, String[]> definitions = new HashMap<String, String[]>();
	private NodeList currentProLinks;
	private int currentProLinkIndex;
	final HashMap<String, String> typeIdMap = new HashMap<String, String>();
	boolean fetched = false;

	CrosswalkCSSSpecScan(String title, String... urls) {
		this.title = title;

		for (String url : urls) {
			this.urls.add(new String[] { url, url });
		}
	}

	public CrosswalkCSSSpecScan addTypeIdMap(String... s) {
		for (int i = 0; i < s.length; i += 2) {
			typeIdMap.put(s[i], s[i + 1]);
		}
		return this;
	}

	private String typeToId(String name) {
		String id = typeIdMap.get(name);
		if (id != null) {
			return id;
		}
		int cut = name.indexOf("Element");
		if (name.startsWith("HTML") && cut != -1 && cut != 4) {
			id = name.substring(4, cut).toLowerCase();
			if (id.equals("tablerow")) {
				id = "tr";
			} else if (id.equals("tablecell") || id.equals("tabledatacell")) {
				id = "td";
			} else if (id.equals("tableheadercell")) {
				id = "th";
			}
		}
		return id;
	}

	private boolean isValidKey(String key) {
		return definitions.containsKey(key);
	}

	private final String[] TYPE_ID_PREFIX = { "the-", "widl-", "idl-def-",
			"interface-", "" };
	private final String[] TYPE_ID_SUFFIX = { "-interface", "-interfaces",
			"-section", "-element", "" };

	private String getTypeKey(Type type) {
		String name = type.getName();
		if (name.toLowerCase().indexOf("canvas") != -1) {
			// doofl;
		}
		String id = typeToId(name);
		for (String prefix : TYPE_ID_PREFIX) {
			for (String suffix : TYPE_ID_SUFFIX) {
				String key = prefix + name + suffix;
				if (isValidKey(key)) {
					return key;
				}
				key = key.toLowerCase();
				if (isValidKey(key)) {
					return key;
				}
				if (id != null) {
					key = prefix + id + suffix;
					if (isValidKey(key)) {
						return key;
					}
					key = key.toLowerCase();
					if (isValidKey(key)) {
						return key;
					}
				}
			}
		}
		return null;
	}

	private String getKey(Artifact artifact) {
		if (artifact instanceof Type) {
			String key = getTypeKey((Type) artifact);
			if (key != null) {
				return key;
			}
		}

		String name = artifact.getName();
		int count = currentProLinks.getLength();
		for (int i = 0; i < count; i++) {
			Element a = (Element) currentProLinks.item(currentProLinkIndex);
			if (a.getTextContent().equals(name)) {
				currentProLinkIndex = i;
				String key = a.getAttribute("href");
				int cut = key.indexOf('#');
				if (cut != -1) {
					key = key.substring(cut + 1);
					if (isValidKey(key)) {
						return key;
					}
				}
			}
			currentProLinkIndex = (currentProLinkIndex + 1) % count;
		}
		return null;
	}

	@Override
	public String getSummary(Artifact artifact) {
		String key = getKey(artifact);
		if (key == null) {
			return null;
		}
		String[] urlAndSummary = definitions.get(key);
		return urlAndSummary == null || urlAndSummary[1].length() == 0 ? null
				: urlAndSummary[1];
	}

	@Override
	public String getLink(Artifact artifact) {
		String key = getKey(artifact);
		if (key == null) {
			return null;
		}
		return key == null ? null : definitions.get(key)[0] + "#" + key;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public Iterable<String[]> getUrls() {
		return urls;
	}

	void fetchAll() {
		if (!fetched) {
			fetched = true;
			for (String[] urlAndTitle : urls) {
				urlAndTitle[1] = fetch(urlAndTitle[0]);
			}
		}
	}

	String fetch(String url) {
		System.out.println(title + ": " + url);
		Document doc = DomLoader.loadDom(url);
		String title = url;
		NodeList list = doc.getElementsByTagName("title");
		if (list.getLength() > 0) {
			title = list.item(0).getTextContent();
		}
		docs.add(doc);

		// Read summaries
		list = doc.getElementsByTagName("*");
		for (int i = 0; i < list.getLength(); i++) {
			Element element = (Element) list.item(i);
			String id = element.getAttribute("id");
			if (id != null && !"".equals(id)) {
				/*
				while (element.getNodeName().equals("dfn")
						|| element.getNodeName().equals("code")) {
					element = (Element) element.getParentNode();
				}*/
				//String name = element.getNodeName();
				String text = element.getTextContent();
				definitions.put(id,	new String[] { url, HtmlWriter.summary(text) });
			}
		}
		return title;
	}

	void addCSSProperty(Library lib, List<String> properties, NodeList links) {
		try {
			// Hack... :-/
			currentProLinks = links;
			currentProLinkIndex = 0;
			new ProParser(lib, properties).parse();
		} catch (Exception e) {
			e.printStackTrace();
			//throw new RuntimeException(e);
		}
	}
	
	private boolean checkClassForProperty(String klass){
		String[] arr = new String[]{"propdef", "descdef"};
		List<String> list = Arrays.asList(arr);
		String[] klasss = klass.split(" ");
		for(String k : klasss){
			if (list.contains(k)){
				return true;
			}
		}
		return false;
	}
	
	private Element getPreviousSiblingElement(Node node) {
	      Node prevSibling = node.getPreviousSibling();
	      while (prevSibling != null) {
	          if (prevSibling.getNodeType() == Node.ELEMENT_NODE) {
	              return (Element) prevSibling;
	          }
	          Node tmpNode = prevSibling.getPreviousSibling();
	          prevSibling = tmpNode == null ? prevSibling.getParentNode() : tmpNode;
	      }

	      return null;  
	  } 

	@Override
	public void readDocumentation(Library lib) {
		fetchAll();
		lib.setDocumentationProvider(this);

		for (Document doc : docs) {
			// Read property
			NodeList list = doc.getElementsByTagName("table");
			NodeList titles = doc.getElementsByTagName("title");
		    Element _title = (Element) titles.item(0);
			for (int i = 0; i < list.getLength(); i++) {
				Element table = (Element) list.item(i);
				String klass = table.getAttribute("class");
				if (checkClassForProperty(klass)) {
					// from table element to h* element
					Element preEl = getPreviousSiblingElement(table);
					// find <h> element, not <p>.
					while (preEl.getNodeName().startsWith("p"))
						preEl = getPreviousSiblingElement(preEl);
					//Node preNode = table.getPreviousSibling();
					
					NodeList nodeList = preEl == null ? null : preEl.getElementsByTagName("a");
					// prevent null value.
					if (null == nodeList)
						throw new NullPointerException("currentProLinks is null.");
							
					List<String> properties = new ArrayList<String>();
					Element tbody = (Element) table.getFirstChild();
					// get data from table head
					if (tbody.getNodeName().equals("thead")){
						Element tr = (Element) tbody.getFirstChild();
						String key = tr.getFirstChild().getTextContent().trim();
						// remove ':'
						if (key != null && key.length() > 0)
							key = key.substring(0, key.length()-1);
						String value = tr.getLastChild().getTextContent().trim();
						if (value.contains("*"))
							value = value.replace("*", "x");
						if ("CSS Fonts Module Level 3".equals(_title.getTextContent()) && "descdef".equals(klass))
							value = value + "(@font-face)";
						properties.add(key + "===" + value);
						
						tbody = (Element) tbody.getNextSibling();
					}
					// get data from table body
					NodeList trs = null;
					if (tbody.getNodeName().equals("tbody")){
						trs = tbody.getChildNodes();
					}else{
						trs = table.getChildNodes();
					}
					for (int j = 0; j < trs.getLength(); j++){
						Element tr = (Element) trs.item(j);
						
						String key = tr.getFirstChild().getTextContent().trim();
						// remove ':'
						if (key != null && key.length() > 0)
							key = key.substring(0, key.length()-1);
						String value = tr.getLastChild().getTextContent().trim();
						if (value.contains("*"))
							value = value.replace("*", "x");
						if ("CSS Fonts Module Level 3".equals(_title.getTextContent()) && "descdef".equals(klass))
							value = value + "(@font-face)";
						properties.add(key + "===" + value);
						//System.out.print(tr.getFirstChild().getTextContent().trim());
						
					}
					
					addCSSProperty(lib, properties, nodeList);
				}
			}

		}
	}
}
