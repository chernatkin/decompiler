package org.chernatkin.decompiler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JMods;
import com.sun.codemodel.JType;

public class DecompileClassVisitor extends ClassVisitor{

	private static final String PATH = "D:\\java\\workspace_http\\decompiler\\target";
	
	private static final String OBJECT_NAME = "java.lang.Object";
	
	private static final String CONSTRUCTOR_METHOD_NAME = "<init>"; 
	
	private final JCodeModel sourceCode = new JCodeModel();
	
	private JDefinedClass clazz;
	
	private final boolean debugMode = true; 
	
	public DecompileClassVisitor() {
		super(Opcodes.ASM5);
		
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		try {
			JDefinedClass parent = createClassIfneed(convertClassName(superName));
			clazz = createClassIfneed(convertClassName(name));
			
			if(parent != null) { clazz._extends(parent); }
			if(debugMode){
				clazz.javadoc().append("Class. bytecode version=" + version + ", signature=" + signature + ", superClassName=" + superName + ", interfaces=" + Arrays.toString(interfaces));
			}
		} catch (JClassAlreadyExistsException e) {
			e.printStackTrace();
		}
	}
	
	private JDefinedClass createClassIfneed(final String className) throws JClassAlreadyExistsException{
		if(className == null || className.equals(OBJECT_NAME)){
			return null;
		}
		JDefinedClass clazz = sourceCode._getClass(className);
		if(clazz == null){
			clazz = sourceCode._class(className);
		}
		return clazz;
	}
	
	private String convertClassName(final String name){
		if(name == null){
			return null;
		}
		return name.replace('/', '.');
	}

	@Override
	public void visitSource(String source, String debug) {
		clazz.javadoc().append("Source. source=" + source + ", debug=" + debug);
	}

	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		clazz.javadoc().append("Outer class. owner=" + owner + ", name=" + name + ", desc=" + desc);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		clazz.javadoc().append("Annotation. desc=" + desc + ", visible=" + visible);
		return new DecompileAnnotationVisitor();
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		clazz.javadoc().append("Type annotation. typeRef=" + typeRef + ", typePath=" + typePath + ", desc=" + desc + ", visible=" + visible);
		return new DecompileAnnotationVisitor();
	}

	@Override
	public void visitAttribute(Attribute attr) {
		
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		super.visitInnerClass(name, outerName, innerName, access);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		return super.visitField(access, name, desc, signature, value);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		//access=1, name=<init>, desc=()V, signature=null,exceptions=null
		//access=1, name=main, desc=([Ljava/lang/String;)V, signature=null,exceptions=null
		System.out.println("access=" + access + ", name=" + name + ", desc=" + desc + ", signature=" + signature + ", exceptions=" + Arrays.toString(exceptions));
		final JMethod method;
		if(name.equals(CONSTRUCTOR_METHOD_NAME)){
			method = clazz.constructor(JMod.PUBLIC);
		}
		else{
			method = clazz.method(JMod.PUBLIC, sourceCode.VOID, name);
		}
		return new DecompileMethodVisitor(method);
	}

	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub
		super.visitEnd();
		try {
			sourceCode.build(new File(PATH));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
}
