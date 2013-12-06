package org.chernatkin.decompiler;

public class StackElement {
  private final Object value;

  private final Class<?> type;

  public StackElement(Object value, Class<?> type) {
    super();
    this.value = value;
    this.type = type;
  }

  public Object getValue() {
    return value;
  }

  public Class<?> getType() {
    return type;
  }

  public boolean isCategory2() {
    return value.getClass().equals(Long.class) || value.getClass().equals(Double.class);
  }
}
