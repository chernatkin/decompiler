package org.chernatkin.decompiler;

import com.sun.codemodel.JMethod;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;

public class DecompileMethodVisitor extends InstructionAdapter {

	private final JMethod method;
	
	private final Deque<StackElement> stack = new LinkedList<>();
	
	public DecompileMethodVisitor(final JMethod method) {
		super(new MethodVisitor(Opcodes.ASM5) {});
		this.method = method;
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
		
	}

	@Override
	public void swap() {
		// TODO Auto-generated method stub
		super.swap();
	}

	@Override
	public void add(Type type) {
		// TODO Auto-generated method stub
		super.add(type);
	}

	@Override
	public void sub(Type type) {
		// TODO Auto-generated method stub
		super.sub(type);
	}

	@Override
	public void mul(Type type) {
		// TODO Auto-generated method stub
		super.mul(type);
	}

	@Override
	public void div(Type type) {
		// TODO Auto-generated method stub
		super.div(type);
	}

	@Override
	public void rem(Type type) {
		// TODO Auto-generated method stub
		super.rem(type);
	}

	@Override
	public void neg(Type type) {
		// TODO Auto-generated method stub
		super.neg(type);
	}

	@Override
	public void shl(Type type) {
		// TODO Auto-generated method stub
		super.shl(type);
	}

	@Override
	public void shr(Type type) {
		// TODO Auto-generated method stub
		super.shr(type);
	}

	@Override
	public void ushr(Type type) {
		// TODO Auto-generated method stub
		super.ushr(type);
	}

	@Override
	public void and(Type type) {
		// TODO Auto-generated method stub
		super.and(type);
	}

	@Override
	public void or(Type type) {
		// TODO Auto-generated method stub
		super.or(type);
	}

	@Override
	public void xor(Type type) {
		// TODO Auto-generated method stub
		super.xor(type);
	}

	@Override
	public void iinc(int var, int increment) {
		// TODO Auto-generated method stub
		super.iinc(var, increment);
	}

	@Override
	public void cast(Type from, Type to) {
		// TODO Auto-generated method stub
		super.cast(from, to);
	}

	@Override
	public void lcmp() {
		// TODO Auto-generated method stub
		super.lcmp();
	}

	@Override
	public void cmpl(Type type) {
		// TODO Auto-generated method stub
		super.cmpl(type);
	}

	@Override
	public void cmpg(Type type) {
		// TODO Auto-generated method stub
		super.cmpg(type);
	}

	@Override
	public void ifeq(Label label) {
		// TODO Auto-generated method stub
		super.ifeq(label);
	}

	@Override
	public void ifne(Label label) {
		// TODO Auto-generated method stub
		super.ifne(label);
	}

	@Override
	public void iflt(Label label) {
		// TODO Auto-generated method stub
		super.iflt(label);
	}

	@Override
	public void ifge(Label label) {
		// TODO Auto-generated method stub
		super.ifge(label);
	}

	@Override
	public void ifgt(Label label) {
		// TODO Auto-generated method stub
		super.ifgt(label);
	}

	@Override
	public void ifle(Label label) {
		// TODO Auto-generated method stub
		super.ifle(label);
	}

	@Override
	public void ificmpeq(Label label) {
		// TODO Auto-generated method stub
		super.ificmpeq(label);
	}

	@Override
	public void ificmpne(Label label) {
		// TODO Auto-generated method stub
		super.ificmpne(label);
	}

	@Override
	public void ificmplt(Label label) {
		// TODO Auto-generated method stub
		super.ificmplt(label);
	}

	@Override
	public void ificmpge(Label label) {
		// TODO Auto-generated method stub
		super.ificmpge(label);
	}

	@Override
	public void ificmpgt(Label label) {
		// TODO Auto-generated method stub
		super.ificmpgt(label);
	}

	@Override
	public void ificmple(Label label) {
		// TODO Auto-generated method stub
		super.ificmple(label);
	}

	@Override
	public void ifacmpeq(Label label) {
		// TODO Auto-generated method stub
		super.ifacmpeq(label);
	}

	@Override
	public void ifacmpne(Label label) {
		// TODO Auto-generated method stub
		super.ifacmpne(label);
	}

	@Override
	public void goTo(Label label) {
		// TODO Auto-generated method stub
		super.goTo(label);
	}

	@Override
	public void jsr(Label label) {
		// TODO Auto-generated method stub
		super.jsr(label);
	}

	@Override
	public void ret(int var) {
		// TODO Auto-generated method stub
		super.ret(var);
	}

	@Override
	public void tableswitch(int min, int max, Label dflt, Label... labels) {
		// TODO Auto-generated method stub
		super.tableswitch(min, max, dflt, labels);
	}

	@Override
	public void lookupswitch(Label dflt, int[] keys, Label[] labels) {
		// TODO Auto-generated method stub
		super.lookupswitch(dflt, keys, labels);
	}

	@Override
	public void areturn(Type t) {
		// TODO Auto-generated method stub
		super.areturn(t);
	}

	@Override
	public void getstatic(String owner, String name, String desc) {
		// TODO Auto-generated method stub
		super.getstatic(owner, name, desc);
	}

	@Override
	public void putstatic(String owner, String name, String desc) {
		// TODO Auto-generated method stub
		super.putstatic(owner, name, desc);
	}

	@Override
	public void getfield(String owner, String name, String desc) {
		// TODO Auto-generated method stub
		super.getfield(owner, name, desc);
	}

	@Override
	public void putfield(String owner, String name, String desc) {
		// TODO Auto-generated method stub
		super.putfield(owner, name, desc);
	}

	@Override
	public void invokevirtual(String owner, String name, String desc) {
		// TODO Auto-generated method stub
		super.invokevirtual(owner, name, desc);
	}

	@Override
	public void invokespecial(String owner, String name, String desc) {
		// TODO Auto-generated method stub
		super.invokespecial(owner, name, desc);
	}

	@Override
	public void invokestatic(String owner, String name, String desc) {
		// TODO Auto-generated method stub
		super.invokestatic(owner, name, desc);
	}

	@Override
	public void invokeinterface(String owner, String name, String desc) {
		// TODO Auto-generated method stub
		super.invokeinterface(owner, name, desc);
	}

	@Override
	public void invokedynamic(String name, String desc, Handle bsm,	Object[] bsmArgs) {
		// TODO Auto-generated method stub
		super.invokedynamic(name, desc, bsm, bsmArgs);
	}

	@Override
	public void anew(Type type) {
		// TODO Auto-generated method stub
		super.anew(type);
	}

	@Override
	public void newarray(Type type) {
		// TODO Auto-generated method stub
		super.newarray(type);
	}

	@Override
	public void arraylength() {
		// TODO Auto-generated method stub
		super.arraylength();
	}

	@Override
	public void athrow() {
		// TODO Auto-generated method stub
		super.athrow();
	}

	@Override
	public void checkcast(Type type) {
		// TODO Auto-generated method stub
		super.checkcast(type);
	}

	@Override
	public void instanceOf(Type type) {
		// TODO Auto-generated method stub
		super.instanceOf(type);
	}

	@Override
	public void monitorenter() {
		// TODO Auto-generated method stub
		super.monitorenter();
	}

	@Override
	public void monitorexit() {
		// TODO Auto-generated method stub
		super.monitorexit();
	}

	@Override
	public void multianewarray(String desc, int dims) {
		// TODO Auto-generated method stub
		super.multianewarray(desc, dims);
	}

	@Override
	public void ifnull(Label label) {
		// TODO Auto-generated method stub
		super.ifnull(label);
	}

	@Override
	public void ifnonnull(Label label) {
		// TODO Auto-generated method stub
		super.ifnonnull(label);
	}

	@Override
	public void mark(Label label) {
		// TODO Auto-generated method stub
		super.mark(label);
	}

	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		method.body().directStatement("//frame type: " + type + ", nlocal: " + nLocal + ", local: " + Arrays.toString(local) + ", nStack: " + nStack + ", stack: " + Arrays.toString(stack));
	}
	
}
