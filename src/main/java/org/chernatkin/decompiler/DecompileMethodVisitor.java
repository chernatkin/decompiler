package org.chernatkin.decompiler;

import com.sun.codemodel.JMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	private final Deque<StackElement> stack = new LinkedList<>();
	
	private final Map<String, List<String>> labels = new HashMap<>();
	
	private final Set<String> visitedLabels = new HashSet<>();
	
	public DecompileMethodVisitor(final JMethod method) {
		super(new MethodVisitor(Opcodes.ASM5) {});
		this.method = method;
	}

	private void addToLabel(Label label, String value){
		List<String> values = labels.get(label.toString());
		if(values == null){
			values = new ArrayList<>();
			labels.put(label.toString(), values);
		}
		values.add(value);
	}
	
	@Override
	public void nop() {
		method.body().directStatement("//nop");
	}

	@Override
	public void aconst(Object cst) {
		method.body().directStatement("//aconst" + cst);
		stack.push(new StackElement(cst, Object.class));
	}

	@Override
	public void iconst(int cst) {
		method.body().directStatement("//iconst" + cst);
		stack.push(new StackElement(cst, Integer.class));
	}

	@Override
	public void lconst(long cst) {
		method.body().directStatement("//lconst" + cst);
		stack.push(new StackElement(cst, Long.class));
	}

	@Override
	public void fconst(float cst) {
		method.body().directStatement("//fconst" + cst);
		stack.push(new StackElement(cst, Float.class));
	}

	@Override
	public void dconst(double cst) {
		method.body().directStatement("//dconst" + cst);
		stack.push(new StackElement(cst, Double.class));
	}

	@Override
	public void tconst(Type type) {
		method.body().directStatement("//tconst" + type);
		stack.push(new StackElement(type, Type.class));
	}

	@Override
	public void hconst(Handle handle) {
		method.body().directStatement("//hconst" + handle);
		stack.push(new StackElement(handle, Handle.class));
	}

	@Override
	public void load(int var, Type type) {
		method.body().directStatement("//" + type + " load " + var);
		stack.push(new StackElement("var" + var, Integer.class));
	}

	@Override
	public void aload(Type type) {
		method.body().directStatement("//" + type + " aload ");
		final StackElement index = stack.pop();
		stack.push(new StackElement(stack.pop().getValue() + "[" + index.getValue() + "]", Integer.class));
	}

	@Override
	public void store(int var, Type type) {
		method.body().directStatement("//store var " + var  + " type " + type + ";");
		method.body().directStatement("var" + var + "=" + stack.pop().getValue() + ";");
	}

	@Override
	public void astore(Type type) {
		method.body().directStatement("//astore type " + type + ";");
		final StackElement value = stack.pop();
		final StackElement index = stack.pop();
		method.body().directStatement(stack.pop().getValue() + "[" + index.getValue() + "] = " + value.getValue() + ";");
	}

	@Override
	public void pop() {
		method.body().directStatement("//pop");
		stack.pop();
	}

	@Override
	public void pop2() {
		method.body().directStatement("//pop2");
		final StackElement value = stack.pop();
		if(!value.getClass().equals(Long.class) && !value.getClass().equals(Double.class)){
			stack.pop();
		}
	}

	@Override
	public void dup() {
		method.body().directStatement("//dup");
		stack.push(stack.getFirst());
	}

	@Override
	public void dup2() {
		method.body().directStatement("//dup2");
		final StackElement value = stack.pop();
		if(!value.getClass().equals(Long.class) && !value.getClass().equals(Double.class)){
			stack.push(value);
			stack.push(value);
		}
		else{
			final StackElement value2 = stack.getFirst();
			stack.push(value);
			stack.push(value2);
			stack.push(value);
		}
	}

	@Override
	public void dupX1() {
		method.body().directStatement("//dupX1");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(value1);
		stack.push(value2);
		stack.push(value1);
	}

	@Override
	public void dupX2() {
		method.body().directStatement("//dupX2");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		if(value2.getType().equals(Long.class) || value2.getType().equals(Double.class)){
			stack.push(value1);
			stack.push(value2);
			stack.push(value1);
		}
		else{
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
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		if(value1.getType().equals(Long.class) || value1.getType().equals(Double.class)){
			stack.push(value1);
			stack.push(value2);
			stack.push(value1);
		}
		else{
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
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		if(value1.isCategory2() && value2.isCategory2()){
			stack.push(value1);
			stack.push(value2);
			stack.push(value1);
			return;
		}
		
		final StackElement value3 = stack.pop();
		if(!value1.isCategory2() && !value2.isCategory2() && value3.isCategory2()){
			stack.push(value2);
			stack.push(value1);
			stack.push(value3);
			stack.push(value2);
			stack.push(value1);
			return;
		}
		else if(value1.isCategory2() && !value2.isCategory2() && !value3.isCategory2()){
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
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(value1);
		stack.push(value2);
	}

	@Override
	public void add(Type type) {
		method.body().directStatement("//add");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " + " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void sub(Type type) {
		method.body().directStatement("//sub");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " - " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void mul(Type type) {
		method.body().directStatement("//mul");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " * " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void div(Type type) {
		method.body().directStatement("//div");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " / " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void rem(Type type) {
		method.body().directStatement("//rem");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " % " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void neg(Type type) {
		method.body().directStatement("//neg");
		final StackElement value1 = stack.pop();
		stack.push(new StackElement("(-" + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void shl(Type type) {
		method.body().directStatement("//shl");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " << " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void shr(Type type) {
		method.body().directStatement("//shr");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " >> " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void ushr(Type type) {
		method.body().directStatement("//ushr");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " >>> " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void and(Type type) {
		method.body().directStatement("//and");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " & " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void or(Type type) {
		method.body().directStatement("//or");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " | " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void xor(Type type) {
		method.body().directStatement("//xor");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " ^ " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void iinc(int var, int increment) {
		method.body().directStatement("//iinc var " + var + " increment " + increment);
		method.body().directStatement("var" + var + " += " + increment + ";");
	}

	@Override
	public void cast(Type from, Type to) {
		method.body().directStatement("//cast from " + from + " to " + to);
		stack.push(new StackElement("(" + to.getClass().getSimpleName() + ")" + stack.pop().getValue(), to.getClass()));
	}

	@Override
	public void lcmp() {
		method.body().directStatement("//lcmp");
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " > " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void cmpl(Type type) {
		method.body().directStatement("//cmpl type " + type);
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " > " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void cmpg(Type type) {
		method.body().directStatement("//cmpg type " + type);
		final StackElement value1 = stack.pop();
		final StackElement value2 = stack.pop();
		stack.push(new StackElement("(" + value2.getValue() + " > " + value1.getValue() + ")", Integer.class));
	}

	@Override
	public void ifeq(Label label) {
		method.body().directStatement("//ifeq label " + label.toString());
		final StackElement value = stack.pop();
		
		if(value instanceof CmpStackElement){
			final CmpStackElement cmpValue = (CmpStackElement)value;
			method.body().directStatement("if(" + cmpValue.getValue1() + " == " + cmpValue.getValue2() + ") {");
		}
		else{
			method.body().directStatement("if(" + value.getValue() + " == 0) {");
		}
		
		addToLabel(label, CLOSE_BLOCK);
	}

	@Override
	public void ifne(Label label) {
		method.body().directStatement("//ifne label " + label.toString());
		final StackElement value = stack.pop();
		
		if(value instanceof CmpStackElement){
			final CmpStackElement cmpValue = (CmpStackElement)value;
			method.body().directStatement("if(" + cmpValue.getValue1() + " != " + cmpValue.getValue2() + ") {");
		}
		else{
			method.body().directStatement("if(" + value.getValue() + " != 0) {");
		}
		
		addToLabel(label, CLOSE_BLOCK);
	}

	@Override
	public void iflt(Label label) {
		method.body().directStatement("//iflt label " + label.toString());
		final StackElement value = stack.pop();
		
		if(value instanceof CmpStackElement){
			final CmpStackElement cmpValue = (CmpStackElement)value;
			method.body().directStatement("if(" + cmpValue.getValue1() + " < " + cmpValue.getValue2() + ") {");
		}
		else{
			method.body().directStatement("if(" + value.getValue() + " < 0) {");
		}
		
		addToLabel(label, CLOSE_BLOCK);
	}

	@Override
	public void ifge(Label label) {
		method.body().directStatement("//ifge label " + label.toString());
		final StackElement value = stack.pop();
		
		if(value instanceof CmpStackElement){
			final CmpStackElement cmpValue = (CmpStackElement)value;
			method.body().directStatement("if(" + cmpValue.getValue1() + " >= " + cmpValue.getValue2() + ") {");
		}
		else{
			method.body().directStatement("if(" + value.getValue() + " >= 0) {");
		}
		
		addToLabel(label, CLOSE_BLOCK);
	}

	@Override
	public void ifgt(Label label) {
		method.body().directStatement("//ifgt label " + label.toString());
		final StackElement value = stack.pop();
		
		if(value instanceof CmpStackElement){
			final CmpStackElement cmpValue = (CmpStackElement)value;
			method.body().directStatement("if(" + cmpValue.getValue1() + " > " + cmpValue.getValue2() + ") {");
		}
		else{
			method.body().directStatement("if(" + value.getValue() + " > 0) {");
		}
		
		addToLabel(label, CLOSE_BLOCK);
	}

	@Override
	public void ifle(Label label) {
		method.body().directStatement("//ifle label " + label.toString());
		final StackElement value = stack.pop();
		
		if(value instanceof CmpStackElement){
			final CmpStackElement cmpValue = (CmpStackElement)value;
			method.body().directStatement("if(" + cmpValue.getValue1() + " <= " + cmpValue.getValue2() + ") {");
		}
		else{
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
		
		if(visitedLabels.contains(label.toString())){
			addToLabel(label, CLOSE_BLOCK);
		}
		else{
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
	public void invokedynamic(String name, String desc, Handle bsm,	Object[] bsmArgs) {
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
	public void checkcast(Type type) {
		
	}

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
		method.body().directStatement("//frame type: " + type + ", nlocal: " + nLocal + ", local: " + Arrays.toString(local) + ", nStack: " + nStack + ", stack: " + Arrays.toString(stack));
	}

	@Override
	public void visitLabel(Label label) {
		method.body().directStatement("//label " + label);
		
		visitedLabels.add(label.toString());
		if(labels.containsKey(label.toString())){
			for(String brace : labels.get(label.toString())){
				method.body().directStatement(brace);
			}
		}
	}

	@Override
	public void visitLineNumber(int line, Label start) {
	}

	/*@Override
	public void visitJumpInsn(int opcode, Label label) {
		method.body().directStatement("//jump " + label);
	}*/
	
	
}
