package org.chernatkin.decompiler;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

public class DecompileAnnotationVisitor extends AnnotationVisitor {

	public DecompileAnnotationVisitor() {
		super(Opcodes.ASM5);
	}

	@Override
	public void visit(String name, Object value) {
		// TODO Auto-generated method stub
		super.visit(name, value);
	}

	@Override
	public void visitEnum(String name, String desc, String value) {
		// TODO Auto-generated method stub
		super.visitEnum(name, desc, value);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		// TODO Auto-generated method stub
		return super.visitAnnotation(name, desc);
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		// TODO Auto-generated method stub
		return super.visitArray(name);
	}

	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub
		super.visitEnd();
	}

	
	
}
