package com.zhonghuasheng.basic.java.lang.thread.demo2;

public class RunnableTest {
    public static void main(String[] args) {
        MyRunnable mt = new MyRunnable();

        Thread t1 = new Thread(mt);
        Thread t2 = new Thread(mt);
        Thread t3 = new Thread(mt);
        t1.start();
        t2.start();
        t3.start();
    }
}