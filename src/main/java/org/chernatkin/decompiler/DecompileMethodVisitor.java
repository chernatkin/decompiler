package org.chernatkin.decompiler;

import com.sun.codemodel.JMethod;
import com.sun.codemodel.JStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.chernatkin.decompiler.tree.JDirectStatement;
import org.chernatkin.decompiler.tree.SyntaxTree;
import org.chernatkin.decompiler.tree.TreeElement;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;

public class DecompileMethodVisitor extends InstructionAdapter {
  private static final String OPEN_BLOCK = "{";

  private static final String CLOSE_BLOCK = "}";

  private final JMethod method;

  // private final Deque<StackElement> stack = new LinkedList<>();

  private final SyntaxTree tree = new SyntaxTree();

  private final Map<String, List<String>> labels = new HashMap<>();

  private final Set<String> visitedLabels = new HashSet<>();

  public DecompileMethodVisitor(final JMethod method) {
    super(new MethodVisitor(Opcodes.ASM5) { });
    this.method = method;
  }

  private void addToLabel(Label label, String value) {
    List<String> values = labels.get(label.toString());
    if (values == null) {
      values = new ArrayList<>();
      labels.put(label.toString(), values);
    }
    values.add(value);
  }

  @Override
  public void nop() {
    method.body().directStatement("//nop");
  }

  private void pushConst(Object cst, Class<?> type, String cmd) {
    method.body().directStatement("//aconst " + cst);
    tree.getLastStackState().push(new StackElement(cst, type));
  }

  @Override
  public void aconst(Object cst) {
    pushConst(cst, Object.class, "aconst");
  }

  @Override
  public void iconst(int cst) {
    pushConst(cst, Integer.class, "iconst");
  }

  @Override
  public void lconst(long cst) {
    pushConst(cst, Long.class, "lconst");
  }

  @Override
  public void fconst(float cst) {
    pushConst(cst, Float.class, "fconst");
  }

  @Override
  public void dconst(double cst) {
    pushConst(cst, Double.class, "dconst");
  }

  @Override
  public void tconst(Type type) {
    pushConst(type, Type.class, "tconst");
  }

  @Override
  public void hconst(Handle handle) {
    pushConst(handle, Handle.class, "hconst");
  }

  @Override
  public void load(int var, Type type) {
    method.body().directStatement("//" + type + " load " + var);
    tree.getLastStackState().push(new StackElement(type.getClassName() + "var" + var, Integer.class));
  }

  @Override
  public void aload(Type type) {
    method.body().directStatement("//" + type + " aload ");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement index = stack.pop();
    tree.getLastStackState().push(new StackElement(stack.pop().getValue() + "[" + index.getValue() + "]", Integer.class));
  }

  @Override
  public void store(int var, Type type) {
    method.body().directStatement("//store var " + var + " type " + type);
    tree.pushElement(
      new TreeElement(new JDirectStatement(type.getClassName() + " var" + var + " = " + tree.getLastStackState().pop().getValue() + ";")));
  }

  @Override
  public void astore(Type type) {
    method.body().directStatement("//astore type " + type + ";");
    final StackElement value = tree.getLastStackState().pop();
    final StackElement index = tree.getLastStackState().pop();
    method.body().directStatement(tree.getLastStackState().pop().getValue() + "[" + index.getValue() + "] = " + value.getValue() + ";");
  }

  @Override
  public void pop() {
    method.body().directStatement("//pop");
    tree.getLastStackState().pop();
  }

  @Override
  public void pop2() {
    method.body().directStatement("//pop2");
    final StackElement value = tree.getLastStackState().pop();
    if (!value.isCategory2()) {
      tree.getLastStackState().pop();
    }
  }

  @Override
  public void dup() {
    method.body().directStatement("//dup");
    tree.getLastStackState().push(tree.getLastStackState().getFirst());
  }

  @Override
  public void dup2() {
    method.body().directStatement("//dup2");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value = stack.pop();
    if (!value.isCategory2()) {
      stack.push(value);
      stack.push(value);
    } else {
      final StackElement value2 = stack.getFirst();
      stack.push(value);
      stack.push(value2);
      stack.push(value);
    }
  }

  @Override
  public void dupX1() {
    method.body().directStatement("//dupX1");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(value1);
    stack.push(value2);
    stack.push(value1);
  }

  @Override
  public void dupX2() {
    method.body().directStatement("//dupX2");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    if (value2.isCategory2()) {
      stack.push(value1);
      stack.push(value2);
      stack.push(value1);
    } else {
      final StackElement value3 = stack.pop();
      stack.push(value1);
      stack.push(value3);
      stack.push(value2);
      stack.push(value1);
    }
  }

  @Override
  public void dup2X1() {
    method.body().directStatement("//dup2X1");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    if (value1.isCategory2()) {
      stack.push(value1);
      stack.push(value2);
      stack.push(value1);
    } else {
      final StackElement value3 = stack.pop();
      stack.push(value2);
      stack.push(value1);
      stack.push(value3);
      stack.push(value2);
      stack.push(value1);
    }
  }

  @Override
  public void dup2X2() {
    method.body().directStatement("//dup2X2");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    if (value1.isCategory2() && value2.isCategory2()) {
      stack.push(value1);
      stack.push(value2);
      stack.push(value1);
      return;
    }

    final StackElement value3 = stack.pop();
    if (!value1.isCategory2() && !value2.isCategory2() && value3.isCategory2()) {
      stack.push(value2);
      stack.push(value1);
      stack.push(value3);
      stack.push(value2);
      stack.push(value1);
      return;
    } else if (value1.isCategory2() && !value2.isCategory2() && !value3.isCategory2()) {
      stack.push(value1);
      stack.push(value3);
      stack.push(value2);
      stack.push(value1);
      return;
    }

    final StackElement value4 = stack.pop();
    stack.push(value2);
    stack.push(value1);
    stack.push(value4);
    stack.push(value3);
    stack.push(value2);
    stack.push(value1);
  }

  @Override
  public void swap() {
    method.body().directStatement("//swap");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(value1);
    stack.push(value2);
  }

  @Override
  public void add(Type type) {
    method.body().directStatement("//add");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new StackElement("(" + value2.getValue() + " + " + value1.getValue() + ")", Integer.class));
  }

  @Override
  public void sub(Type type) {
    method.body().directStatement("//sub");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new StackElement("(" + value2.getValue() + " - " + value1.getValue() + ")", Integer.class));
  }

  @Override
  public void mul(Type type) {
    method.body().directStatement("//mul");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new StackElement("(" + value2.getValue() + " * " + value1.getValue() + ")", Integer.class));
  }

  @Override
  public void div(Type type) {
    method.body().directStatement("//div");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new StackElement("(" + value2.getValue() + " / " + value1.getValue() + ")", Integer.class));
  }

  @Override
  public void rem(Type type) {
    method.body().directStatement("//rem");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new StackElement("(" + value2.getValue() + " % " + value1.getValue() + ")", Integer.class));
  }

  @Override
  public void neg(Type type) {
    method.body().directStatement("//neg");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    stack.push(new StackElement("(-" + value1.getValue() + ")", Integer.class));
  }

  @Override
  public void shl(Type type) {
    method.body().directStatement("//shl");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new StackElement("(" + value2.getValue() + " << " + value1.getValue() + ")", Integer.class));
  }

  @Override
  public void shr(Type type) {
    method.body().directStatement("//shr");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new StackElement("(" + value2.getValue() + " >> " + value1.getValue() + ")", Integer.class));
  }

  @Override
  public void ushr(Type type) {
    method.body().directStatement("//ushr");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new StackElement("(" + value2.getValue() + " >>> " + value1.getValue() + ")", Integer.class));
  }

  @Override
  public void and(Type type) {
    method.body().directStatement("//and");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new StackElement("(" + value2.getValue() + " & " + value1.getValue() + ")", Integer.class));
  }

  @Override
  public void or(Type type) {
    method.body().directStatement("//or");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new StackElement("(" + value2.getValue() + " | " + value1.getValue() + ")", Integer.class));
  }

  @Override
  public void xor(Type type) {
    method.body().directStatement("//xor");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new StackElement("(" + value2.getValue() + " ^ " + value1.getValue() + ")", Integer.class));
  }

  @Override
  public void iinc(int var, int increment) {
    method.body().directStatement("//iinc var " + var + " increment " + increment);
    tree.pushElement(new TreeElement(new JDirectStatement("int var" + var + " += " + increment + ";")));
  }

  @Override
  public void cast(Type from, Type to) {
    method.body().directStatement("//cast from " + from + " to " + to);
    final Deque<StackElement> stack = tree.getLastStackState();
    stack.push(new StackElement("(" + to.getClassName() + ")" + stack.pop().getValue(), Object.class));
  }

  @Override
  public void lcmp() {
    method.body().directStatement("//lcmp");
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new CmpStackElement(value1, value2));
  }

  @Override
  public void cmpl(Type type) {
    method.body().directStatement("//cmpl type " + type);
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new CmpStackElement(value1, value2));
  }

  @Override
  public void cmpg(Type type) {
    method.body().directStatement("//cmpg type " + type);
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    stack.push(new CmpStackElement(value1, value2));
  }

  @Override
  public void ifeq(Label label) {
    method.body().directStatement("//ifeq label " + label.toString());
    final Deque<StackElement> stack = tree.getLastStackState();
    final StackElement value = stack.pop();

    final JStatement stmt;
    if (value instanceof CmpStackElement) {
      final CmpStackElement cmpValue = (CmpStackElement) value;
      stmt = new JDirectStatement("if(" + cmpValue.getValue1() + " == " + cmpValue.getValue2() + ") {");
    } else {
      stmt = new JDirectStatement("if(" + value.getValue() + " == 0) {");
    }
  }

  @Override
  public void ifne(Label label) {
    method.body().directStatement("//ifne label " + label.toString());
    final StackElement value = stack.pop();

    if (value instanceof CmpStackElement) {
      final CmpStackElement cmpValue = (CmpStackElement) value;
      method.body().directStatement("if(" + cmpValue.getValue1() + " != " + cmpValue.getValue2() + ") {");
    } else {
      method.body().directStatement("if(" + value.getValue() + " != 0) {");
    }

    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void iflt(Label label) {
    method.body().directStatement("//iflt label " + label.toString());
    final StackElement value = stack.pop();

    if (value instanceof CmpStackElement) {
      final CmpStackElement cmpValue = (CmpStackElement) value;
      method.body().directStatement("if(" + cmpValue.getValue1() + " < " + cmpValue.getValue2() + ") {");
    } else {
      method.body().directStatement("if(" + value.getValue() + " < 0) {");
    }

    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void ifge(Label label) {
    method.body().directStatement("//ifge label " + label.toString());
    final StackElement value = stack.pop();

    if (value instanceof CmpStackElement) {
      final CmpStackElement cmpValue = (CmpStackElement) value;
      method.body().directStatement("if(" + cmpValue.getValue1() + " >= " + cmpValue.getValue2() + ") {");
    } else {
      method.body().directStatement("if(" + value.getValue() + " >= 0) {");
    }

    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void ifgt(Label label) {
    method.body().directStatement("//ifgt label " + label.toString());
    final StackElement value = stack.pop();

    if (value instanceof CmpStackElement) {
      final CmpStackElement cmpValue = (CmpStackElement) value;
      method.body().directStatement("if(" + cmpValue.getValue1() + " > " + cmpValue.getValue2() + ") {");
    } else {
      method.body().directStatement("if(" + value.getValue() + " > 0) {");
    }

    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void ifle(Label label) {
    method.body().directStatement("//ifle label " + label.toString());
    final StackElement value = stack.pop();

    if (value instanceof CmpStackElement) {
      final CmpStackElement cmpValue = (CmpStackElement) value;
      method.body().directStatement("if(" + cmpValue.getValue1() + " <= " + cmpValue.getValue2() + ") {");
    } else {
      method.body().directStatement("if(" + value.getValue() + " <= 0) {");
    }

    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void ificmpeq(Label label) {
    method.body().directStatement("//ificmpeq label " + label.toString());
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();

    method.body().directStatement("if(" + value2.getValue() + " == " + value1.getValue() + ") {");

    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void ificmpne(Label label) {
    method.body().directStatement("//ificmpeq label " + label.toString());
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();

    method.body().directStatement("if(" + value2.getValue() + " == " + value1.getValue() + ") {");

    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void ificmplt(Label label) {
    method.body().directStatement("//ificmplt label " + label.toString());
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();

    method.body().directStatement("if(" + value2.getValue() + " < " + value1.getValue() + ") {");

    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void ificmpge(Label label) {
    method.body().directStatement("//ificmpge label " + label.toString());
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();

    method.body().directStatement("if(" + value2.getValue() + " >= " + value1.getValue() + ") {");

    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void ificmpgt(Label label) {
    method.body().directStatement("//ificmpgt label " + label.toString());
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();

    method.body().directStatement("if(" + value2.getValue() + " > " + value1.getValue() + ") {");

    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void ificmple(Label label) {
    method.body().directStatement("//ificmple label " + label.toString());
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();

    method.body().directStatement("if(" + value2.getValue() + " <= " + value1.getValue() + ") {");

    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void ifacmpeq(Label label) {
    method.body().directStatement("//ifacmpeq label " + label.toString());
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();

    method.body().directStatement("if(" + value2.getValue() + " == " + value1.getValue() + ") {");

    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void ifacmpne(Label label) {
    method.body().directStatement("//ifacmpne label " + label.toString());
    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();

    method.body().directStatement("if(" + value2.getValue() + " != " + value1.getValue() + ") {");

    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void goTo(Label label) {
    method.body().directStatement("//goTo label " + label.toString());

    if (visitedLabels.contains(label.toString())) {
      addToLabel(label, CLOSE_BLOCK);
    } else {
      addToLabel(label, OPEN_BLOCK);
    }
  }

  @Override
  public void jsr(Label label) {
    method.body().directStatement("//jsr label " + label.toString());

    stack.push(new StackElement(label.toString(), Label.class));
  }

  @Override
  public void ret(int var) {
    method.body().directStatement("//ret var " + var);

    method.body().directStatement("return var" + var + ';');
  }

  @Override
  public void tableswitch(int min, int max, Label dflt, Label... labels) {
    method.body().directStatement("//tableswitch min " + min + ", max " + max + ", dflt " + dflt + ", labels " + Arrays.toString(labels));
  }

  @Override
  public void lookupswitch(Label dflt, int[] keys, Label[] labels) {
    method.body().directStatement("//tableswitch keys " + Arrays.toString(keys) + ", dflt " + dflt + ", labels " + Arrays.toString(labels));
  }

  @Override
  public void areturn(Type t) {
    method.body().directStatement("//areturn type " + t);

    method.body().directStatement("return " + stack.pop().getValue() + ';');
  }

  @Override
  public void getstatic(String owner, String name, String desc) {
    method.body().directStatement("//getstatic owner " + owner + ", name " + name + ", desc " + desc);

    stack.push(new StackElement(owner + '.' + name, Object.class));
  }

  @Override
  public void putstatic(String owner, String name, String desc) {
    method.body().directStatement("//putstatic owner " + owner + ", name " + name + ", desc " + desc);

    method.body().directStatement(owner + '.' + name + " = " + stack.pop().getValue() + ';');
  }

  @Override
  public void getfield(String owner, String name, String desc) {
    method.body().directStatement("//getfield owner " + owner + ", name " + name + ", desc " + desc);

    stack.push(new StackElement(stack.pop().getValue() + "." + name, Object.class));
  }

  @Override
  public void putfield(String owner, String name, String desc) {
    method.body().directStatement("//putfield owner " + owner + ", name " + name + ", desc " + desc);

    final StackElement value1 = stack.pop();
    final StackElement value2 = stack.pop();
    method.body().directStatement(value2.getValue() + "." + name + " = " + value1.getValue() + ';');
  }

  @Override
  public void invokevirtual(String owner, String name, String desc) {
    method.body().directStatement("//invokevirtual owner " + owner + ", name " + name + ", desc " + desc);

    method.body().directStatement(owner + "." + name + "(...)" + ';');
  }

  @Override
  public void invokespecial(String owner, String name, String desc) {
    method.body().directStatement("//invokespecial owner " + owner + ", name " + name + ", desc " + desc);

    method.body().directStatement(owner + "." + name + "(...)" + ';');
  }

  @Override
  public void invokestatic(String owner, String name, String desc) {
    method.body().directStatement("//invokestatic owner " + owner + ", name " + name + ", desc " + desc);

    method.body().directStatement(owner + "." + name + "(...)" + ';');
  }

  @Override
  public void invokeinterface(String owner, String name, String desc) {
    method.body().directStatement("//invokeinterface owner " + owner + ", name " + name + ", desc " + desc);

    method.body().directStatement(owner + "." + name + "(...)" + ';');
  }

  @Override
  public void invokedynamic(String name, String desc, Handle bsm, Object[] bsmArgs) {
    method.body().directStatement("//invokedynamic name " + name + ", desc " + desc + ", bsm " + bsm + ", bsmArgs " + Arrays.toString(bsmArgs));

    method.body().directStatement(bsm.toString() + ';');
  }

  @Override
  public void anew(Type type) {
    method.body().directStatement("//anew type " + type);

    stack.push(new StackElement("new " + type.getClassName() + "()", Object.class));
  }

  @Override
  public void newarray(Type type) {
    method.body().directStatement("//newarray type " + type);

    stack.push(new StackElement("new " + type.getClassName() + "[" + stack.pop().getValue() + "]", Object.class));
  }

  @Override
  public void arraylength() {
    method.body().directStatement("//arraylength");

    stack.push(new StackElement(stack.pop().getValue() + ".length", Integer.class));
  }

  @Override
  public void athrow() {
    method.body().directStatement("//arraylength");

    method.body().directStatement("throw " + stack.pop().getValue() + ';');
  }

  @Override
  public void checkcast(Type type) { }

  @Override
  public void instanceOf(Type type) {
    method.body().directStatement("//instanceOf " + type);

    stack.push(new StackElement(stack.pop().getValue() + " instanceof " + type.getClassName(), Integer.class));
  }

  @Override
  public void monitorenter() {
    method.body().directStatement("//monitorenter");

    method.body().directStatement("synchronized(" + stack.pop().getValue() + "){");
  }

  @Override
  public void monitorexit() {
    method.body().directStatement("//monitorexit");

    method.body().directStatement("}");
  }

  @Override
  public void multianewarray(String desc, int dims) {
    method.body().directStatement("//multianewarray desc " + desc + ", dims " + dims);

    stack.push(new StackElement("new " + desc + "[]", Object.class));
  }

  @Override
  public void ifnull(Label label) {
    method.body().directStatement("//ifnull label " + label);

    method.body().directStatement("if(" + stack.pop().getValue() + " != null){");
    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void ifnonnull(Label label) {
    method.body().directStatement("//ifnonnull label " + label);

    method.body().directStatement("if(" + stack.pop().getValue() + " == null){");
    addToLabel(label, CLOSE_BLOCK);
  }

  @Override
  public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
    method.body().directStatement("//tryCatchBlock start " + start + ", end " + end + ", handler " + handler + ", type");

    addToLabel(start, "try{");
    addToLabel(end, "}");
    addToLabel(handler, "catch(" + type + "){");
  }

  @Override
  public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
    method.body()
    .directStatement(
      "//frame type: " + type + ", nlocal: " + nLocal + ", local: " + Arrays.toString(local) + ", nStack: " + nStack + ", stack: "
      + Arrays.toString(stack));
  }

  @Override
  public void visitLabel(Label label) {
    method.body().directStatement("//label " + label);

    visitedLabels.add(label.toString());
    if (labels.containsKey(label.toString())) {
      for (String brace : labels.get(label.toString())) {
        method.body().directStatement(brace);
      }
    }
  }

  @Override
  public void visitLineNumber(int line, Label start) { }

  /*@Override
  public void visitJumpInsn(int opcode, Label label) {
          method.body().directStatement("//jump " + label);
  }*/
}
