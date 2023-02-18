package com.onewho.gamerbot.util;

public class UtilDebug {
	
	public static void printIntArray(String bug, int[] a) {
		bug += " =";
		for (int i = 0; i < a.length; ++i) bug += " " + a[i]; 
		System.out.println(bug);
	}
	
}
