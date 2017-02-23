package com.example.test;

import java.io.Serializable;
import java.util.PriorityQueue;

/**
 * Created by Administrator on 2016/3/12 0012.
 */
public class ThreadTest {

    public static void main(String[] args) throws Exception
    {
        test3();
    }

    private static void test3()
    {
        while (true) {
            if (1 + 2 == 3) {
                if (2 + 1 == 3) {
                    break;
                }
            }
        }
        System.out.println("lalalala");
    }

    private static void test2()
    {
        PriorityQueue<Thread> queue = new PriorityQueue<>(11, new MyThreadComparator());

        addThreadToQueue(queue);

        int size = queue.size();
        boolean flag = false;
        int i = 0;
        Thread mThread = null;

        while (!queue.isEmpty()) {
            if (!flag) {//首次进来~拿出最高级线程start
                mThread = queue.poll();
                mThread.start();
                flag = true;
                i++;
            }

            if (mThread.getState() == Thread.State.TERMINATED && !queue.isEmpty()) {
                if (size == i) {
                    //跳出循环体
                    break;
                }
                if (size == (i + 1)) {
                    //第6阻塞，7start 循环一直在询问是否为空！！
                    //如果取出，则马上跳出while
                    mThread = queue.peek();
                    mThread.start();
                    i++;
                } else {
                    mThread = queue.poll();
                    mThread.start();
                    i++;
                }
            }
        }
        System.out.println("结束啦~~");
    }

    private static void addThreadToQueue(PriorityQueue<Thread> queue)
    {
        for (int i = 1; i < 8; i++) {
            final int finalI = i;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run()
                {
                    System.out.println(Thread.currentThread().getName() + "==" + finalI);
                    new Thread(String.valueOf(finalI)) {
                        @Override
                        public void run()
                        {
                            System.out.println(Thread.currentThread().getName());
                        }
                    }.start();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setPriority(i);
            queue.add(t);
        }
    }

    private static class MyThreadComparator implements Serializable, java.util.Comparator<Thread> {
        @Override
        public int compare(Thread o1, Thread o2)
        {
            int value = o1.getPriority() < o2.getPriority() ? 1 : o1.getPriority() > o2
                    .getPriority() ? -1 : 0;
            return value;
        }
    }

    private static void test1()
    {
        new Thread("x") {
            @Override
            public void run()
            {
                System.out.println(Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread("y") {
                    @Override
                    public void run()
                    {
                        System.out.println(Thread.currentThread().getName());
                    }
                }.start();
            }
        }.start();
    }
}
