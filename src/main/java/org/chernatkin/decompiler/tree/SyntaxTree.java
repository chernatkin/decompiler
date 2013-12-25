package org.chernatkin.decompiler.tree;

import java.util.Deque;
import java.util.LinkedList;
import org.chernatkin.decompiler.StackElement;

public class SyntaxTree {
  private TreeElement root;

  private TreeElement last;

  private Deque<StackElement> lastStackState = new LinkedList<>();

  public TreeElement getRoot() {
    return root;
  }

  public void setRoot(TreeElement root) {
    this.root = root;
  }

  public TreeElement getLast() {
    return last;
  }

  public void setLast(TreeElement last) {
    this.last = last;
  }

  public Deque<StackElement> getLastStackState() {
    if (last == null) {
      return lastStackState;
    }
    return last.getCurrentStackState();
  }

  public TreeElement pushElement(TreeElement element) {
    if (root == null) {
      root = element;
      last = element;
      last.setCurrentStackState(lastStackState);
      lastStackState = null;
      return element;
    }

    last.addNext(element);
    element.setPrev(last);
    element.setCurrentStackState(last.getCurrentStackState());
    last = element;
    return element;
  }
}
