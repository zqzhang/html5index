package org.html5index.model;

public class Property extends Member {
	String initialValue;
	String No;// for Order

	public Property(int modifiers, Type type, String name, String initialValue) {
		super(modifiers, type, name);
		this.initialValue = initialValue;
	}

	public String getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(String newInitialValue) {
		this.initialValue = newInitialValue;
	}

	public String getNo() {
		return No;
	}

	public void setNo(String no) {
		No = no;
	}
}
