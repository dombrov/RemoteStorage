package com.storage.app.utils;

public class IndexFactory {
	
	private static int INDEX = 1;
	
	public static int getIndex() {
		return INDEX++;
	}
	
	public static void setINDEX(int iNDEX) {
		INDEX = iNDEX;
	}

}
