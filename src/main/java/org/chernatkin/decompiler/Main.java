package org.chernatkin.decompiler;

import java.io.FileInputStream;
import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

public class Main {

	private static final String PATH = "D:\\java\\workspace_http\\decompiler\\target\\classes\\org\\chernatkin\\decompiler\\DecompileClassVisitor.class";
	
	public static void main(String[] args) throws Exception{
		
		final ClassVisitor tv = new TraceClassVisitor(new PrintWriter(System.out));
		//final ClassVisitor tv = new DecompileClassVisitor();
		final ClassReader cr = new ClassReader(new FileInputStream(PATH));
		cr.accept(tv, 0);
	}

}
