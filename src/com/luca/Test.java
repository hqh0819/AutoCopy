/**
 * 
 */
package com.luca;

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
		String line="0125 fd\\da\\fd\\fda\\a.txt";
		System.out.println("D:\\DATA\\import\\lcd\\CELL\\4600\\T36A2\\T36A20E0\\"+line.split(" ")[1].substring(line
				.split(" ")[1]
				.lastIndexOf("\\")));
		System.out.println();
	}

}
