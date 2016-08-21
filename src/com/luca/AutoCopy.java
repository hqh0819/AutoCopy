/**
 * 
 */
package com.luca;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 從路徑中獲取全部索引文件 這個類只能用一個線程來進行，否則list中會重複INDEX文件
 * 
 * @author qiheng.hu
 * @date 2016年8月21日
 */
class AddIndexFile implements Runnable {

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

/**
 * 多線程讀取index文件，并判斷裏面的每一行PANEL記錄是否需要複製
 * 
 * @author qiheng.hu
 * @date 2016年8月21日
 */
class ReadIndex implements Runnable {
	/**
	 * 將需要複製的PANEL添加到panelList中 需要設置線程同步
	 * 
	 * @param path
	 */
	synchronized void addPanel(String path) {

		AutoCopy.panelList.add(path);

	}

	/**
	 * 從indexList讀取一個INDEX文件 需要設置線程同步
	 * 
	 * @return
	 */
	synchronized File getIndexFile() {
		File file = null;
		if (AutoCopy.indexList.size() > 0) {
			file = (File) AutoCopy.indexList.get(0);
			AutoCopy.indexList.remove(0);
		}
		return file;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub

		File file;
		String line;
		// index還沒讀完或者indexlist中還有內容則繼續
		while (!AutoCopy.addindexfileover || AutoCopy.indexList.size() > 0) {
			file = getIndexFile();
			if (file == null)
				continue;
			Calendar time = Calendar.getInstance();
			int dateint = Integer.parseInt(file.getAbsolutePath().split(
					"\\\\{1}")[3]);
			int hourminsec;
			time.set(dateint / 10000, dateint % 10000 / 100 - 1, dateint % 100);
			if (file != null) {

				try {
					BufferedReader bu = new BufferedReader(
							new InputStreamReader(new FileInputStream(file)));
					while ((line = bu.readLine()) != null) {

						if (line.indexOf(" ") != -1
								&& line.split(" ")[0].matches("\\d+")) {
							hourminsec = Integer.parseInt(line.split(" ")[0]);
							time.set(Calendar.HOUR_OF_DAY, hourminsec / 10000);
							time.set(Calendar.MINUTE, hourminsec % 10000 / 100);
							time.set(Calendar.SECOND, hourminsec % 100);
							if (time.after(AutoCopy.startTime)
									&& time.before(AutoCopy.endTime))
								addPanel("Y:\\" + line.split(" ")[1]);

						}
					}
					bu.close();
				} catch (IOException e) {
					System.out.println("讀取文件錯誤：" + file.getPath()
							+ File.separator + file.getName());
				}

			}
		}

		// index全部讀完並且indexlist為空時設置標識符
		AutoCopy.addpanelover = true;

	}
}

/**
 * 多線程將panellist中需要複製的文件進行複製操作
 * 
 * @author QiHeng.Hu
 * @date 2016年8月11日
 */
public class AutoCopy implements Runnable {

	static List indexList = new ArrayList();// 索引文件list
	static List panelList = new ArrayList();// 需要複製方文件路徑list
	static boolean addindexfileover = false;// 索引文件是否全部導入到list中的標識符
	static boolean addpanelover = false;// 要複製Panel路徑是否全部導入到list中的標識符
	static Calendar startTime;// 開始時間
	static Calendar endTime;// 結束時間
	static int sum = 0;// 計算總共複製了多少個
	static long time;// 計算耗時

	/**
	 * 從Panellist中獲得一個需要複製的文件路徑 需要設置線程同步
	 * 
	 * @return
	 */
	synchronized String getPanel() {
		String string = null;
		if (panelList.size() > 0) {
			string = (String) panelList.get(0);
			panelList.remove(0);
		}
		return string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub
		while (!addpanelover || panelList.size() > 0) {
			String sourcepathString;
			sourcepathString = getPanel();
			if (sourcepathString != null) {
				try {
					// 複製文件

					FileTool.copyFile(
							sourcepathString,
							"D:\\DATA\\import\\lcd\\CELL\\4600\\T36A2\\T36A20E0\\"
									+ sourcepathString
											.substring(sourcepathString
													.lastIndexOf("\\") + 1),
							false);
					sum++;
				} catch (Exception e) {
					System.out.println("Copy文件錯誤：" + sourcepathString);
				}
			}
		}

		System.out.println("複製" + sum + "個文件");
		System.out.println("耗時：" + (System.currentTimeMillis() - time) / 1000);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 從當前時間后退一定時間到當時時間進行複製操作

		// args第一個參數為全數字時為自定義后退時間，單位為分鐘。否則默認倒退30分鐘開始
		int min = 0;
		if (args.length != 0 && args[0].matches("\\d+"))
			min = Integer.parseInt(args[0]);

		time = System.currentTimeMillis();
		startTime = Calendar.getInstance();
		startTime.add(Calendar.SECOND, -1);
		endTime = Calendar.getInstance();
		startTime.add(Calendar.MINUTE, -1 * (min > 0 ? min : 30));
		startTime.add(Calendar.SECOND, 1);
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
		new Thread(new AddIndexFile(),"AddIndexFileThread").start();;
		
		// 建立3個線程讀取INDEX
		ReadIndex readIndex = new ReadIndex();
		new Thread(readIndex,"ReadIndexFileThread1").start();
		new Thread(readIndex,"ReadIndexFileThread2").start();
		new Thread(readIndex,"ReadIndexFileThread3").start();

		// 建立5個線程種複製文件
		AutoCopy aCopy = new AutoCopy();
		new Thread(aCopy,"AutoCopyThread1").start();
		new Thread(aCopy,"AutoCopyThread2").start();
		new Thread(aCopy,"AutoCopyThread3").start();
		new Thread(aCopy,"AutoCopyThread4").start();
		new Thread(aCopy,"AutoCopyThread5").start();

	}

}
