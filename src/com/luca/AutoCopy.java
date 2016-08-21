/**
 * 
 */
package com.luca;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author QiHeng.Hu
 * @date 2016年8月11日
 */
public class AutoCopy {
	private static int sum = 0;

	public static void copy(Calendar startTime, Calendar endTime) {
		int count = 0;
		if (startTime.after(endTime))
			return;

		if (startTime.get(Calendar.YEAR) * 10000
				+ (1+startTime.get(Calendar.MONTH)) * 100
				+ startTime.get(Calendar.DAY_OF_MONTH) != endTime
				.get(Calendar.YEAR)
				* 10000
				+ (1+endTime.get(Calendar.MONTH))
				* 100
				+ endTime.get(Calendar.DAY_OF_MONTH)) {
			copy(startTime,
					new GregorianCalendar(endTime.get(Calendar.YEAR), endTime
							.get(Calendar.MONTH), endTime
							.get(Calendar.DAY_OF_MONTH) - 1, 23, 59, 59));

			copy(new GregorianCalendar(endTime.get(Calendar.YEAR),
					endTime.get(Calendar.MONTH),
					endTime.get(Calendar.DAY_OF_MONTH), 0, 0, 0), endTime);
			return;
		}

		int start = startTime.get(Calendar.HOUR_OF_DAY) * 10000
				+ startTime.get(Calendar.MINUTE) * 100
				+ startTime.get(Calendar.SECOND), end = endTime
				.get(Calendar.HOUR_OF_DAY)
				* 10000
				+ endTime.get(Calendar.MINUTE)
				* 100
				+ endTime.get(Calendar.SECOND);

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
		System.out.println(dat + "/  " + start + "~" + end);
		String line;
		for (int i = 0; i < files.length; i++) {
			try {
				BufferedReader bu = new BufferedReader(new InputStreamReader(
						new FileInputStream(files[i])));
				while ((line = bu.readLine()) != null) {
					if (line.indexOf(" ") != -1
							&& line.split(" ")[0].matches("\\d+")
							&& Integer.parseInt(line.split(" ")[0]) >= start
							&& Integer.parseInt(line.split(" ")[0]) <= end) {
						try {
							// 複製文件
							// System.out.println(line);
							FileTool.copyFile(
									"Y:\\" + line.split(" ")[1],
									"D:\\DATA\\import\\lcd\\CELL\\4600\\T36A2\\T36A20E0"
											+ line.split(" ")[1].substring(line
													.split(" ")[1]
													.lastIndexOf("\\")), false);
							// System.out.println("成功複製："+line);
							count++;
						} catch (Exception e) {
							System.out.println("Copy文件錯誤：" + line);
						}
					}
				}
				bu.close();
			} catch (IOException e) {
				System.out.println("讀取文件錯誤：" + files[i].getPath()
						+ File.separator + files[i].getName());
				continue;
			}
		}
		sum += count;
		if (count > 0)
			System.out.println("成功複製" + count + "個文件");
		else
			System.out.println("一個文件都沒有複製成功");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long time=System.currentTimeMillis();
		int min = 0;
		if (args.length != 0 && args[0].matches("\\d+"))
			min = Integer.parseInt(args[0]);

		Calendar startTime = Calendar.getInstance();
		Calendar endTime = Calendar.getInstance();
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

		copy(startTime, endTime);

		System.out.println("--------------------------------");
		if (sum > 0)
			System.out.println("一共複製" + sum + "個文件");
		else
			System.out.println("一個文件都沒有複製成功");
		System.out.println("耗時："+(System.currentTimeMillis()-time)/1000);
	}
}
