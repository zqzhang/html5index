package org.html5index.idl;

import java.util.List;

import org.html5index.model.Artifact;
import org.html5index.model.DocumentationProvider;
import org.html5index.model.Library;
import org.html5index.model.Model;
import org.html5index.model.Property;
import org.html5index.model.Type;
import org.html5index.model.Type.Kind;
import org.html5index.util.Tokenizer;


public class ProParser {
  Tokenizer tokenizer;
  Model model;
  Library lib;
  DocumentationProvider documentationProvider;
  List<String> proList;
  
  public ProParser(Library lib, List<String> properties) {
    this.model = lib.getModel();
    this.lib = lib;
    this.documentationProvider = lib.getDocumentationProvider();
    this.proList = properties;
  }
  
  public void parse(){
	  // add type
	  String[] property = this.proList.get(0).split("===");
	  // replace '/' to ', '
	  String typeName = property[1];
	  if (null != typeName && typeName.indexOf("/") != -1)
		  typeName = typeName.replace("/", ", ");
	  Type type = new Type(typeName, Kind.PROPERTY);
	  lib.addType(type);
	  
	  //type.setDocumentationUrl("www.baidu.com");
	  documentationProvider.addDocumentation(type);
	  
	  // add properties
	  for (int i = 1; i < proList.size(); i++){
		  String[] arr = proList.get(i).split("===");
		  Property pro = new Property(Artifact.CSS, type, arr[0], arr[1]);
		  pro.setNo(String.valueOf(i));
		  type.addPropertyInOrder(pro);
	  }
  }

}