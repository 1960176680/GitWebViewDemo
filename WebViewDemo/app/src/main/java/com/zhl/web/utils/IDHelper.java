package com.zhl.web.utils;

import java.util.UUID;

public class IDHelper {

	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String getKey(){
		//return "sdklgjsdiofg";
		return "sdklgjsdiofg";
	}
}
