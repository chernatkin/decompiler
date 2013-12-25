package org.chernatkin.decompiler.tree;

import com.sun.codemodel.JStatement;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import org.chernatkin.decompiler.StackElement;

public class TreeElement {
  private List<TreeElement> next;

  private List<TreeElement> prev;

  private JStatement statement;

  private Deque<StackElement> currentStackState = new LinkedList<>();

  public TreeElement(JStatement statement) {
    this.statement = statement;
  }

  public List<TreeElement> getNext() {
    return next;
  }

  public void addNext(TreeElement next) {
    if (this.next == null) {
      this.next = new ArrayList<>();
    }
    this.next.add(next);
  }

  public void setNext(TreeElement next) {
    if (this.next == null) {
      this.next = new ArrayList<>();
    }
    this.next.clear();
    addNext(next);
  }

  public List<TreeElement> getPrev() {
    return prev;
  }

  public void addPrev(TreeElement prev) {
    if (this.prev == null) {
      this.prev = new ArrayList<>();
    }
    this.prev.add(prev);
  }

  public void setPrev(TreeElement prev) {
    if (this.prev == null) {
      this.prev = new ArrayList<>();
    }
    this.prev.clear();
    addPrev(prev);
  }

  public JStatement getStatement() {
    return statement;
  }

  public void setStatement(JStatement statement) {
    this.statement = statement;
  }

  public Deque<StackElement> getCurrentStackState() {
    return currentStackState;
  }

  public void setCurrentStackState(Deque<StackElement> currentStackState) {
    this.currentStackState = currentStackState;
  }
}
