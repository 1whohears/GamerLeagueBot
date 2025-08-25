package com.onewho.gamerbot.util;

public class UtilDebug {
	
	public static void printIntArray(String bug, int[] a) {
		bug += " =";
        StringBuilder bugBuilder = new StringBuilder(bug);
        for (int j : a) bugBuilder.append(" ").append(j);
        bug = bugBuilder.toString();
        System.out.println(bug);
	}
	
}
