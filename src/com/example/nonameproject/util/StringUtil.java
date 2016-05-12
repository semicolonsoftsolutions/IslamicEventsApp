package com.example.nonameproject.util;

public class StringUtil{
	public static final String parseJsonString(String str){
	
		int openingBraceIndex = str.indexOf('{');
		int closingBraceIndex = str.lastIndexOf('}');

		String jsonResponse = str.substring(
				openingBraceIndex, (closingBraceIndex + 1));
		
		return jsonResponse;
	}
}
