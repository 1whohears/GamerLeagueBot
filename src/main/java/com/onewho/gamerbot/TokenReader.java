package com.onewho.gamerbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TokenReader {
	
	public static String getJDAToken() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File("token.txt")));
		String st = "";
		while (br.readLine() != null) st += br.readLine();
		br.close();
		return st;
	}
	
}
