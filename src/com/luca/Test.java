/**
 * 
 */
package com.luca;

import java.io.File;
import java.util.Calendar;

/**
 * @author qiheng.hu
 * @date 2016年8月19日
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sourcepathString = "Y:\\LCD\\INDEX\\20160819\\4600\\abc.txt";
	
		System.out.println(sourcepathString
		.substring(sourcepathString
				.lastIndexOf("\\")+1));
	}
}
