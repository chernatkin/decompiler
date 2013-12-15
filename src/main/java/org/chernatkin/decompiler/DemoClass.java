package org.chernatkin.decompiler;

public class DemoClass {

	
	
	public void main(String[] args) {
		final String tr = "true";
		final String fa = "false";
		boolean b = true;
		if(b){
			System.out.println(tr);
		}
		else{
			System.out.println(fa);
		}
		System.out.println("hello");
		
		int a = 1;
		/*if(a > 1){
			System.out.println(fa);
		}
		System.out.println("hello");*/
		//sign(1);
	}


    public static int sign(long value) {
        return  value > 0l ? 1 : value < 0l ? -1 : 0;
    }
	
}
