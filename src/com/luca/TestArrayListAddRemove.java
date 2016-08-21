/**
 * 
 */
package com.luca;

import java.util.ArrayList;

import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

/**
 * @author qiheng.hu
 * @date 2016年8月21日
 */
public class TestArrayListAddRemove {
	static ArrayList al = new ArrayList();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		alAdd add = new alAdd();
		alRemove remove = new alRemove();
		Thread t1 = new Thread(add);
		Thread t2 = new Thread(add);
		Thread t3 = new Thread(add);

		Thread t4 = new Thread(remove);
		Thread t5 = new Thread(remove);
		Thread t6 = new Thread(remove);
		Thread t7 = new Thread(remove);
		Thread t8 = new Thread(remove);
		Thread t9 = new Thread(remove);
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
		t7.start();
		t8.start();
		t9.start();
	}

}

class alAdd implements Runnable {
	static int count=0;
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 10000; i++)
			add();
	}

	synchronized void add() {
		TestArrayListAddRemove.al.add(""+count++);
	}
}

class alRemove implements Runnable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 1000000; i++)
			remove();

	}

	synchronized void remove() {
		if (TestArrayListAddRemove.al.size() > 0) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()+"----"+TestArrayListAddRemove.al.get(0));
			TestArrayListAddRemove.al.remove(0);
			
		}
	}
}