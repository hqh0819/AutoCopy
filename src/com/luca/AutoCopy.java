/**
 * 
 */
package com.luca;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

class addIndexFile implements Runnable {

	static void add(Calendar startTime, Calendar endTime) {

		if (startTime.after(endTime))
			return;

		if (startTime.get(Calendar.YEAR) * 10000
				+ (1 + startTime.get(Calendar.MONTH)) * 100
				+ startTime.get(Calendar.DAY_OF_MONTH) != endTime
				.get(Calendar.YEAR)
				* 10000
				+ (1 + endTime.get(Calendar.MONTH))
				* 100 + endTime.get(Calendar.DAY_OF_MONTH)) {
			add(startTime,
					new GregorianCalendar(endTime.get(Calendar.YEAR), endTime
							.get(Calendar.MONTH), endTime
							.get(Calendar.DAY_OF_MONTH) - 1, 23, 59, 59));

			add(new GregorianCalendar(endTime.get(Calendar.YEAR),
					endTime.get(Calendar.MONTH),
					endTime.get(Calendar.DAY_OF_MONTH), 0, 0, 0), endTime);
			return;
		}

		// yyyymmdd
		String dat = ""
				+ startTime.get(Calendar.YEAR)
				+ new String("0" + (1 + startTime.get(Calendar.MONTH)))
						.substring(new String("0"
								+ (1 + startTime.get(Calendar.MONTH))).length() - 2)
				+ new String("0" + startTime.get(Calendar.DAY_OF_MONTH))
						.substring(new String("0"
								+ startTime.get(Calendar.DAY_OF_MONTH))
								.length() - 2);

		File files[];
		// index文件路徑
		if (!new File("Y:\\LCD\\INDEX\\" + dat + "\\4600").exists()) {
			System.out.println("找不到INDEX路徑：" + "Y:\\LCD\\INDEX\\" + dat
					+ "\\4600");
			return;
		}

		if ((files = new File("Y:\\LCD\\INDEX\\" + dat + "\\4600").listFiles()) == null
				|| files.length == 0) {
			System.out.println("INDEX路徑下無文件：" + "Y:\\LCD\\INDEX\\" + dat
					+ "\\4600");
			return;
		}

		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().toUpperCase().endsWith(".TXT"))
				addIF(files[i]);
		}

	}

	synchronized static void addIF(File f) {
		AutoCopy.indexList.add(f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub
		add(AutoCopy.startTime, AutoCopy.endTime);
		AutoCopy.addindexfileover = true;
	}
}

class ReadIndex implements Runnable {

	synchronized void addPanel(String path) {
		
		AutoCopy.panelList.add(path);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub
		// index還沒讀完或者indexlist中還有內容則繼續
		while (!AutoCopy.addindexfileover || AutoCopy.indexList.size() > 0) {

		}
		// index全部讀完並且indexlist為空時設置標識符
		AutoCopy.addpanelover = true;
	}

}

/**
 * @author QiHeng.Hu
 * @date 2016年8月11日
 */
public class AutoCopy implements Runnable {

	static List indexList = new ArrayList();
	static List panelList = new ArrayList();
	static boolean addindexfileover = false;
	static boolean addpanelover = false;
	static Calendar startTime;
	static Calendar endTime;
	static int sum = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int min = 0;
		if (args.length != 0 && args[0].matches("\\d+"))
			min = Integer.parseInt(args[0]);

		startTime = Calendar.getInstance();
		endTime = Calendar.getInstance();
		startTime.add(Calendar.MINUTE, -1 * (min > 0 ? min : 20));
		System.out.println("複製時間段：" + startTime.get(Calendar.YEAR) + "/"
				+ (1 + startTime.get(Calendar.MONTH)) + "/"
				+ startTime.get(Calendar.DAY_OF_MONTH) + " "
				+ startTime.get(Calendar.HOUR_OF_DAY) + ":"
				+ startTime.get(Calendar.MINUTE) + ":"
				+ startTime.get(Calendar.SECOND) + "~"
				+ endTime.get(Calendar.YEAR) + "/"
				+ (1 + endTime.get(Calendar.MONTH)) + "/"
				+ endTime.get(Calendar.DAY_OF_MONTH) + " "
				+ endTime.get(Calendar.HOUR_OF_DAY) + ":"
				+ endTime.get(Calendar.MINUTE) + ":"
				+ endTime.get(Calendar.SECOND));
		// 開始添加INDEX文件到indexList中,這里只能開一個線程，否則內容會重複
		Thread addindexfileThread = new Thread(new addIndexFile());
		addindexfileThread.start();

	}

}
