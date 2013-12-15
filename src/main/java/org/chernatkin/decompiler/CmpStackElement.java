package org.chernatkin.decompiler;

public class CmpStackElement extends StackElement {

	private final Object value2;
	
	public CmpStackElement(Object value1, Object value2) {
		super(value1, Integer.class);
		this.value2 = value1;
	}

	public Object getValue2() {
		return value2;
	}

	public Object getValue1() {
		return getValue();
	}
}
