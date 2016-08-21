/**
 * 
 */
package com.luca;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author qiheng.hu
 * @date 2016年8月10日
 */
public class FileTool {
	public static boolean FileExists(String filepath) {

		return (new File(filepath)).isFile();

	}

	public static boolean FolderExists(String Folderpath) {

		return (new File(Folderpath)).isDirectory();
	}

	// 复制文件
	public static void copyFile(File sourceFile, File targetFile,boolean overWriteFile)
			throws IOException {
		//目標文件存在并選擇了不覆蓋則跳過
		if (targetFile.isFile() && !overWriteFile)return;
		targetFile.getParentFile().mkdir();
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 *5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}
	// 复制文件
		public static void copyFile(File sourceFile, File targetFile) throws IOException{
			copyFile(sourceFile,targetFile,true);
		}
	public static void copyFile(String sourcesPath, String Targetpath,boolean overWriteFile)
				throws IOException {
			copyFile(new File(sourcesPath), new File(Targetpath),overWriteFile);
		}

	public static void copyFile(String sourcesPath, String Targetpath)
			throws IOException {
		copyFile(sourcesPath, Targetpath,true);
	}

	// 复制文件夹
	public static void copyDirectiory(File sourceDir, File targetDir,boolean overWriteFile)
			throws IOException {
		
		if(!sourceDir.isDirectory())return;
		
		// 新建目标目录
		targetDir.mkdirs();
		//迴圈所有文件
		File[] files=sourceDir.listFiles();
		for (int i=0;i<files.length;i++) {
			if (files[i].isFile()) {
				// 複製文件
				copyFile(files[i],
						new File(targetDir.getAbsolutePath()
								+ File.separator + files[i].getName()),overWriteFile);
			}
			else if (files[i].isDirectory()) {
				// 再次複製文件夾
				copyDirectiory(files[i],
						new File(targetDir.getAbsolutePath() + File.separator + files[i].getName()),overWriteFile);
			}
		}
	}
	
	public static void copyDirectiory(File sourceDir, File targetDir)
			throws IOException {
		copyDirectiory(sourceDir, targetDir,true);
	}
	public static void copyDirectiory(String sourceDir, String targetDir,boolean overWriteFile) throws IOException{
		
		copyDirectiory(new File(sourceDir),new File(targetDir),overWriteFile);
	}
	public static void copyDirectiory(String sourceDir, String targetDir) throws IOException{
		copyDirectiory(sourceDir, targetDir,true);
	}
}
