package com.storage.app.utils;

public class IndexGenerator {
	
	private static int INDEX = 0;
	
	public static int nextIndex() {
		return ++INDEX;
	}
	
	public static void resetIndex() {
		INDEX = 0;
	}
	

}
