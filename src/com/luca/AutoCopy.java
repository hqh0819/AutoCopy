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

class ReadIndex implements Runnable {

	synchronized void addPanel(String path) {

		AutoCopy.panelList.add(path);

	}

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
		// index還沒讀完或者indexlist中還有內容則繼續
		File file;
		String line;
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
	static long time;

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
		int min = 0;
		if (args.length != 0 && args[0].matches("\\d+"))
			min = Integer.parseInt(args[0]);
		time = System.currentTimeMillis();
		startTime = Calendar.getInstance();
		startTime .add(Calendar.SECOND, -1);
		endTime = Calendar.getInstance();
		startTime.add(Calendar.MINUTE, -1 * (min > 0 ? min : 20));
		startTime .add(Calendar.SECOND, 1);
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
		Thread addindexfileThread = new Thread(new AddIndexFile());
		addindexfileThread.start();
		// 建立3個線程讀取INDEX
		ReadIndex readIndex = new ReadIndex();
		Thread readindexThread1 = new Thread(readIndex);
		Thread readindexThread2 = new Thread(readIndex);
		Thread readindexThread3 = new Thread(readIndex);
		readindexThread1.start();
		readindexThread2.start();
		readindexThread3.start();
		// 建立5個線程種複製文件
		AutoCopy aCopy = new AutoCopy();
		Thread autocopyThread1 = new Thread(aCopy);
		Thread autocopyThread2 = new Thread(aCopy);
		Thread autocopyThread3 = new Thread(aCopy);
		Thread autocopyThread4 = new Thread(aCopy);
		Thread autocopyThread5 = new Thread(aCopy);
		autocopyThread1.start();
		autocopyThread2.start();
		autocopyThread3.start();
		autocopyThread4.start();
		autocopyThread5.start();
	}

}
