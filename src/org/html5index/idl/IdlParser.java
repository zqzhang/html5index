package org.html5index.idl;

import java.util.ArrayList;
import java.util.List;

import org.html5index.model.Artifact;
import org.html5index.model.DocumentationProvider;
import org.html5index.model.Library;
import org.html5index.model.Model;
import org.html5index.model.Operation;
import org.html5index.model.Parameter;
import org.html5index.model.Property;
import org.html5index.model.Type;
import org.html5index.model.Type.Kind;
import org.html5index.util.Tokenizer;


public class IdlParser {
  Tokenizer tokenizer;
  Model model;
  Library lib;
  DocumentationProvider documentationProvider;
  
  public IdlParser(Library lib, String idl) {
    this.model = lib.getModel();
    this.lib = lib;
    this.documentationProvider = lib.getDocumentationProvider();
    tokenizer = new Tokenizer(idl);
  }
 
  Type parseNewTypeName(Type.Kind kind) {
    String name = consumeIdentifier();
    Type type = lib.getType(name);
    if (type == null) {
      type = model.getType(name);
    }
    if (kind == Type.Kind.PARTIAL) {
      if (type != null) {
        if (type.getLibrary() == lib) {
          return type;
        }
        Type newType = new Type(name, kind);
        lib.addType(newType);
        documentationProvider.addDocumentation(newType);
        newType.setSuperType(type);
        return newType;
      } else {
        kind = Type.Kind.INTERFACE;
      }
    }
    if (type == null) {
      type = new Type(name, kind);
      lib.addType(type);
    } 
    type.setKind(kind);
    lib.addType(type);
    documentationProvider.addDocumentation(type);
    return type;
  }
  
  private Type parseInterface(Kind kind) {
    tokenizer.nextToken();
    Type type;
    type = parseNewTypeName(kind);
    if (tokenizer.ttype == ':' || tokenizer.sval.equals("extends")) { // DOM spec error?
      tokenizer.nextToken();
      Type superType = parseType();
      type.setSuperType(superType);
    } else if (tokenizer.sval.equals("implements")) {
      tokenizer.nextToken();
      Type base = parseType();
      type.addType(base);
      base.addImplemenetedBy(type);
    }
    
    if (tokenizer.ttype == ';') {
      tokenizer.nextToken();
      return type;
    }
    
    consume('{');
    while (tokenizer.ttype != '}') {
      parseOptions();

      int modifiers = 0;
      if ("static".equals(tokenizer.sval)) {
        consumeIdentifier();
        modifiers |= Artifact.STATIC;
      }
      if ("enum".equals(tokenizer.sval)) {
    	  parseEnum();
      }
      if ("stringifier".equals(tokenizer.sval)) {
        consumeIdentifier();
        if (tokenizer.ttype == ';') {
          consume(';');
          continue;
        }
      } 
      String sval = tokenizer.sval;
      if ("readonly".equals(sval) || "attribute".equals(sval)) {
        Property property = parseProperty(modifiers);
        type.addProperty(property);
        documentationProvider.addDocumentation(property);
      } else if ("const".equals(sval)) {
        Property c = parseConst();
        type.addProperty(c);
        documentationProvider.addDocumentation(c);
      } else {
        Operation operation = parseOperation(modifiers);
        Operation old = type.getOperation(operation.getName());
        if (old != null) {
          old.merge(model, operation);
        } else {
          type.addOperation(operation);
          documentationProvider.addDocumentation(operation);
        }
      }
    }
    consume('}');
    if (tokenizer.ttype == ';') {
      consume(';');
    }
    return type;
  }
  
  private Property parseConst() {
    consume(Tokenizer.TT_WORD, "const");
    Type type = parseType();
    String name = consumeIdentifier();
    consume('=');
    StringBuilder sb = new StringBuilder();
    while(tokenizer.ttype != ';' && tokenizer.ttype != Tokenizer.TT_EOF) {
      sb.append(tokenizer.sval);
      sb.append(' ');
      tokenizer.nextToken();
    }
    consume(';');
    return new Property(Property.CONSTANT, type, name, sb.toString().trim());
  }

  private Property parseProperty(int modifiers) {
    if (tokenizer.sval.equals("readonly")) {
      modifiers |= Artifact.READ_ONLY;
      tokenizer.nextToken();
    }
    consume(Tokenizer.TT_WORD, "attribute");
    Type propertyType = parseType();
    
    String name = tokenizer.sval;
    consume(Tokenizer.TT_WORD);
    if (tokenizer.ttype == ';') {
    	consume(';');
    }
    return new Property(modifiers, propertyType, name, null);
  }
 
  private Operation parseOperation(int modifiers) {
    StringBuilder special = new StringBuilder();
    while ("getter".equals(tokenizer.sval) || "setter".equals(tokenizer.sval) ||
        "deleter".equals(tokenizer.sval) || "creator".equals(tokenizer.sval) ||
        "legacycaller".equals(tokenizer.sval)) {
      if (special.length() > 0) {
        special.append(' ');
      }
      special.append(consumeIdentifier());
    }
    Type type;
    if ("void".equals(tokenizer.sval)) {
      type = null;
      consumeIdentifier();
    } else {
      type = parseType();
    }
    String name;
    if (tokenizer.ttype == '(' && special.length() != 0) {
      name = special.toString();
    } else {
      name = consumeIdentifier();
    }
    Operation op = new Operation(modifiers, type, name);
    parseParameterList(op);
    consume(';');
    return op;
  }
  
  private void fail(String msg) {
    tokenizer.fail("Error parsing " + lib.getName() + ": " + msg);
  }
  
  private Type parseUnionType() {
    List<Type> types = new ArrayList<Type>();
    do {
      tokenizer.nextToken();
      types.add(parseType());
    } while(tokenizer.sval.equals("or"));
    consume(')');
    if (tokenizer.ttype == '?') {
      tokenizer.nextToken();
    }
    StringBuilder sb = new StringBuilder("(");
    for (Type t: types) {
      if (sb.length() > 1) {
        sb.append(" or ");
      }
      sb.append(t.getName());
    }
    sb.append(')');
    Type union = new Type(sb.toString());
    union.setKind(Type.Kind.UNION);
    for (Type t: types) {
      union.addType(t);
    }
    return union;
  }
  
  private Type parseType() {
    if (tokenizer.ttype == '(') {
      return parseUnionType();
    }
    
    String name;
    while (true) {
      if (tokenizer.sval.equals("unrestricted")) {
        consumeIdentifier();
        name = "unrestricted " + consumeIdentifier();
      } else if (tokenizer.sval.equals("unsigned") || 
    		  tokenizer.sval.equals("unsigend")) {
    	  consumeIdentifier();
    	if (tokenizer.ttype == '?'){
	        consume('?');
	        name = "unsigned ?";
        }else{
	        name = "unsigned " + consumeIdentifier();
        }
      } else if (tokenizer.sval.equals("optional")) {
    	  consumeIdentifier();
          name = "optional " + consumeIdentifier();
      } else {
        name = consumeIdentifier();
      }
      if (tokenizer.ttype != Tokenizer.TT_SCOPE) {
        break;
      }
      tokenizer.nextToken();
    }
    
    if ("long".equals(tokenizer.sval) && name.endsWith("long")) {
      name += " long";
      consumeIdentifier();
    }
    
    Type type;
    if (name.equals("sequence") && tokenizer.ttype == '<') {
    	if (tokenizer.ttype == '<')
    		consume('<');
      Type baseType = parseType();
      if (tokenizer.ttype == '>')
      	consume('>');
      type = new Type("sequence<" + baseType.getName() + ">", Type.Kind.SEQUENCE, baseType);
    } else if (name.equals("Promise") && tokenizer.ttype == '<') {
    	if (tokenizer.ttype == '<')
    		consume('<');
      Type baseType = parseType();
      if (tokenizer.ttype == '>')
      	consume('>');
      type = new Type("Promise<" + baseType.getName() + ">", Type.Kind.PROMISE, baseType);
    } else {
      type = lib.getType(name);
      if (type == null) {
        type = model.getType(name);
        if (type == null) {
          type = new Type(name);
          model.addHiddenType(type);
        }
      }
      if (tokenizer.ttype == '?') {
        type = new Type(type.getName() + "?", Type.Kind.NULLABLE, type);
        tokenizer.nextToken();
      }
      if (tokenizer.ttype == '[') {
        tokenizer.nextToken();
        consume(']');
        type = new Type(type.getName() + "[]", Type.Kind.ARRAY, type);
      }
    }
    if (tokenizer.ttype == '?') {
      type = new Type(type.getName() + "?", Type.Kind.NULLABLE, type);
      tokenizer.nextToken();
    }
    return type;
  }
  
  private void consume(int type) {
    if (type != tokenizer.ttype) {
      fail("Expected type: " + tokenizer.ttypeToString(type));
    }
    tokenizer.nextToken();
  }
    
  private void consume(int type, String sval) {
    if (!sval.equals(tokenizer.sval)) {
      fail("Expected: " + sval);
    }
    consume(type);
  }

  private String consumeIdentifier() {
    String s = tokenizer.sval;
    consume(Tokenizer.TT_WORD);
    return s;
  }

  public void parse() {
    tokenizer.nextToken();
    parseModuleBody();
    consume(Tokenizer.TT_EOF);
  }
  
  public void parseModuleBody() {
    while(tokenizer.ttype != Tokenizer.TT_EOF && tokenizer.ttype != '}') {
      String sval = tokenizer.sval;
      if (tokenizer.ttype == '[' || "dictionary".equals(sval) || "exception".equals(sval) ||
          "partial".equals(sval) || "callback".equals(sval) || "interface".equals(sval) || 
          "class".equals(sval)) {
        parseClassifier();
      } else if ("typedef".equals(sval)) {
        parseTypedef();
      } else if ("valuetype".equals(sval)) {
        parseValueType();
      } else if ("module".equals(sval)) {
        parseModule();
      } else if ("const".equals(sval)) {
        lib.getGlobals().addProperty(parseConst());
      } else if ("enum".equals(sval)) {
        parseEnum();
      } else if (tokenizer.ttype == Tokenizer.TT_WORD) {
        Type target = parseType();
        consume(Tokenizer.TT_WORD, "implements");
        Type interfaceType = parseType();
        target.addType(interfaceType);
        interfaceType.addImplemenetedBy(target);
        consume(';');
      } else {
        fail("dictionary, callback, exception, interface, typedef, valuetype, module or const expected");
      }
    }
  }
  
  private void parseDictionary() {
    consume(Tokenizer.TT_WORD, "dictionary");
    Type type = parseNewTypeName(Type.Kind.DICTIONARY);
    if (tokenizer.ttype == ':') {
      tokenizer.nextToken();
      type.setSuperType(parseType());
    } 
    consume('{');
    do {
		if (tokenizer.sval.equals("readonly"))
			tokenizer.nextToken();
		if (tokenizer.sval.equals("attribute"))
			consume(Tokenizer.TT_WORD, "attribute");
      Type fieldType = parseType();
      String fieldName = consumeIdentifier();
      String value = null;
      if (tokenizer.ttype == '=') {
        consume('=');
        StringBuilder sb = new StringBuilder();
        while(tokenizer.ttype != ';' && tokenizer.ttype != Tokenizer.TT_EOF) {
          sb.append(tokenizer.sval);
          sb.append(' ');
          tokenizer.nextToken();
        }
        value = sb.toString();
      }
      Property property = new Property(0, fieldType, fieldName, value);
      type.addProperty(property);
      documentationProvider.addDocumentation(property);
      consume(';');
    } while (tokenizer.ttype != '}');
    consume('}');
    if (tokenizer.ttype != Tokenizer.TT_EOF)
    consume(';');
  }

  private void parseEnum() {
    consume(Tokenizer.TT_WORD, "enum");
    Type type = parseNewTypeName(Type.Kind.ENUM);
    
    do {
      tokenizer.nextToken();
      if (tokenizer.ttype == '}') {
        break;
      }
      type.addEnumLiteral(tokenizer.sval);
      // save old value for following code
      String _sval = tokenizer.sval;
      if (tokenizer.ttype == '"')
    	  consume('"');
      else
    	  tokenizer.nextToken();
      // some enum attributes like xx-x
      if (tokenizer.ttype == '-'){
    	  consume('-');
    	  type.delEnumLiteral(_sval);
    	  type.addEnumLiteral(_sval + '-' + tokenizer.sval);
    	  tokenizer.nextToken();
      }
    } while(tokenizer.ttype == ',');
    consume('}');
    if (tokenizer.ttype == ';')
    	consume(';');
  }
  
  private Type parseException() {
    consume(Tokenizer.TT_WORD, "exception");
    Type type = parseNewTypeName(Type.Kind.EXCEPTION);
    
    if (tokenizer.ttype == ':') {
      tokenizer.nextToken();
      type.setSuperType(parseType());
    }
    
    consume('{');
    
    while(tokenizer.ttype != '}' && tokenizer.ttype != Tokenizer.TT_EOF) {
      if (tokenizer.sval.equals("const")) {
        Property constant = parseConst();
        type.addProperty(constant);
        documentationProvider.addDocumentation(constant);
      } else {
        Type pType = parseType();
        String pName = consumeIdentifier();
        Property property = new Property(0, pType, pName, null);
        type.addProperty(property);
        documentationProvider.addDocumentation(property);
        consume(';');
      }
    }
    consume('}');
    consume(';');
    return type;
  }

  private void parseClassifier() {
    List<Operation> constructors = new ArrayList<Operation>();
    Type.Kind kind = Type.Kind.INTERFACE;
    if (tokenizer.ttype == '[') {
      do {
        tokenizer.nextToken();
        String option = consumeIdentifier();
        if ("NoInterfaceObject".equals(option) || "NoObject".equals(option)) {
          if (kind != Type.Kind.GLOBAL) {
            kind = Type.Kind.NO_OBJECT;
          }
        } else if ("PrimaryGlobal".equals(option) || "Global".equals(option)) {
          kind = Type.Kind.GLOBAL;
        } else if ("Callback".equals(option) || "Exposed".equals(option)) {
        	if (tokenizer.ttype == Tokenizer.TT_COMMA)
        		consume(',');
        	else
        		consume('=');
        	consumeIdentifier();
        } else if ("ArrayClass".equals(option) || 
            "TreatNonCallableAsNull".equals(option) ||
            "OverrideBuiltins".equals(option) ||
            "Unforgeable".equals(option) ||
            "Worker".equals(option) ||
            "Supplemental".equals(option)) {
        } else if ("Constructor".equals(option) || 
        		"MapClass".equals(option) || 
        		"NamedConstructor".equals(option)) {
          String name = "";
          if (option.equals("NamedConstructor")) {
            consume('=');
            name = consumeIdentifier();
          }
          Operation c = new Operation(Artifact.CONSTRUCTOR, null, name);
          if (tokenizer.ttype == '(') {
            parseParameterList(c);
          }
          constructors.add(c);
        } else {
          throw new RuntimeException("Unrecognized option: " + option);
        }
      } while(tokenizer.ttype == ',');
      consume(']');
    }
    if ("partial".equals(tokenizer.sval)) {
      kind = Type.Kind.PARTIAL;
      tokenizer.nextToken();
    } else if ("callback".equals(tokenizer.sval)) {
      tokenizer.nextToken();
      if (!tokenizer.sval.equals("interface")) {
        do {
          tokenizer.nextToken();
        } while (tokenizer.ttype != ';');
        tokenizer.nextToken();
        return;
      }
      kind = Type.Kind.CALLBACK_INTERFACE;
    }
    Type type = null;
    if ("interface".equals(tokenizer.sval) || "class".equals(tokenizer.sval)) {
      type = parseInterface(kind);
    } else if ("exception".equals(tokenizer.sval)) {
      type = parseException();
    } else if ("dictionary".equals(tokenizer.sval)) {
      parseDictionary();
    } else {
      throw new RuntimeException(
          "dictionary, interface, callback or exception expected, got: " + tokenizer.sval);
    }
    if (type != null) {
      for (Operation constructor: constructors) {
        if (constructor.getName().isEmpty()) {
          constructor.setName(type.getName());
        }
        type.addConstructor(constructor);
        documentationProvider.addDocumentation(constructor);
      }
    }
  }

  private void parseModule() {
    consume(Tokenizer.TT_WORD, "module");
    // name
    consumeIdentifier();
    consume('{');
    parseModuleBody();
    consume('}');
    consume(';');
  }

  private void parseParameterList(Operation op) {
	if (tokenizer.ttype != '(')
		return;
    consume('(');
    while(tokenizer.ttype != ')') {
      parseOptions();
      int modifiers = 0;
      if ("optional".equals(tokenizer.sval)) {
        modifiers = Parameter.OPTIONAL;
        consumeIdentifier();
      }
      if ("in".equals(tokenizer.sval) || 
          "out".equals(tokenizer.sval) ||
          "inout".equals(tokenizer.sval)) {
        consumeIdentifier();
      }
      parseOptions();
      
      Type pType = parseType();
      if (tokenizer.ttype == Tokenizer.TT_ELLIPSIS) {
        modifiers |= Parameter.VARIADIC;
        tokenizer.nextToken();
      }
      String pName = consumeIdentifier();
      Parameter parameter = new Parameter(modifiers, pType, pName);
      op.addParameter(parameter);
      
      if (tokenizer.ttype == '=') {
        tokenizer.nextToken();  // =
        if (tokenizer.ttype == '-') {
          tokenizer.nextToken();
        }
        tokenizer.nextToken();  // value
      }
      
      if(tokenizer.ttype == ',') {
        tokenizer.nextToken();
      } else if (tokenizer.ttype != ')') {
        fail("',' or ')' expected");
      }
    }
    consume(')');
    if ("raises".equals(tokenizer.sval)) {
      consumeIdentifier();
      consume('(');
      parseType();
      consume(')');
    }
  }
  
  private void parseOptions() {
    while (tokenizer.ttype == '[') {
      do {
        tokenizer.nextToken();
      } while (tokenizer.ttype != ']');
      tokenizer.nextToken();
    }
  }

  private void parseTypedef() {
    consume(Tokenizer.TT_WORD, "typedef");
  
    Type oldType = parseType();
    Type newType = parseNewTypeName(Kind.ALIAS);
    if (oldType != newType) {
      newType.setSuperType(oldType);
    }
    if (tokenizer.ttype == '[')
    	consume('[');
    if (tokenizer.ttype == ']')
    	consume(']');
    if (tokenizer.ttype == ';')
    	consume(';');
  }

  private void parseValueType() {
    consume(Tokenizer.TT_WORD, "valuetype");
    Type newType = parseNewTypeName(Type.Kind.PRIMITIVE);
    Type oldType = parseType();
    if (oldType != newType) {
      newType.setSuperType(oldType);
    }
    consume(';');
  }

}