package org.chernatkin.decompiler.tree;

import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JStatement;

public class JDirectStatement implements JStatement {
  private final String stmt;

  public JDirectStatement(String stmt) {
    this.stmt = stmt;
  }

  @Override
  public void state(JFormatter f) {
    f.p(stmt).nl();
  }
}
