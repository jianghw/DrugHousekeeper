package com.example.test;

import java.lang.reflect.Method;

/**
 * Created by jianghw on 2016/4/11 0011.
 * Description
 */
public class Test3 {
    public static void main(String[] args)
    {
        t4();

    }

    private static void t4()
    {
        String s="收到一条在 21:44 未及时服药通知 2016-04-12";
        System.out.println(s.substring(12, 13));
        System.out.println(s.substring(20, 30));
        System.out.println(s.substring(6, 11));
    }

    private static void t3()
    {
        System.out.println(0%30);
        System.out.println(2%30);
    }

    private static void t2()
    {
        Class c = Test2.class;
        try {
            Method v = c.getMethod("t14", String.class);
            Object object2=c.newInstance();
            v.invoke(object2,"abc:dfe:ojg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void t1()
    {
        Test2.t14("123:456:789");
    }
}
