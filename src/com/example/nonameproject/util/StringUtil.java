package com.example.nonameproject.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.util.Base64;

public class StringUtil{
	public static final String parseJsonString(String str){
	
		int openingBraceIndex = str.indexOf('{');
		int closingBraceIndex = str.lastIndexOf('}');

		String jsonResponse = str.substring(
				openingBraceIndex, (closingBraceIndex + 1));
		
		return jsonResponse;
	}
	
	/**
	 * BELOW METHOD RETURNS Base64 STRING OF BITMAP
	 * @param bitmap
	 * @return
	 */
     public static String bitmapToBase64(Bitmap bitmap){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG,10, bos);
		byte[] byteArray = bos.toByteArray();
		
		return Base64.encodeToString(byteArray,Base64.DEFAULT);
	}
}
