package com.example.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/12/28 0028.
 */
public class Test2 {

    public static void main(String args[]) {
        t14("abc:efg:fcg");
    }

    public static void t14(String s)
    {
        ArrayList<String> list=new ArrayList<>();
        list.add(s);
        list.add(s);
        list.add(s);
        list.add(s);
        list.add(s);
        System.out.println(list.get(2).split(":")[0]);
        System.out.println(list.get(2).split(":")[2]);
    }

    private static void t13()
    {
        String s="【采集云科技】你的药管家注册验证码为:1520，10分钟内有效，请勿将验证码告知他人。";
        Pattern p = Pattern.compile("(\\d+)");
        Matcher matcher = p.matcher(s);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String find = matcher.group(1);
            sb.append(find);
        }

        System.out.println(sb.substring(0, 4));
    }

    private static void t12()
    {
        String s="【采集云科技】你的药管家注册验证码为:1520，10分钟内有效，请勿将验证码告知他人。";
        System.out.println(s.split(":")[1].split("，")[0]);
    }

    private static void t11() {
        String s="1,2,3";
        Pattern p = Pattern.compile("(\\d+)");
        Matcher matcher = p.matcher(s);
        while (matcher.find()) {
            String find = matcher.group(1);
      System.out.println(find);
        }
    }

    private static void t10() {
        for(int i=0;i<10;i++){
            if(i==5){
                break;
            }
            System.out.println(i);
        }
    }

    private static void t9() {
        String s2="MedicineManager1.07zip.apk";
        System.out.println(s2.split("MedicineManager")[1].split("zip")[0]);
    }

    private static void t8() {
//        String s="收到一条在 11:55 及时服药通知 2016-02-22";
        String s="收到一条已服药通知 2016-02-22 11:55";
        String s2="{\"device_uid\":\"10000001\"}";
        System.out.println(s.substring(10,20));
//        System.out.println(s.substring(20,30));
        System.out.println(s.substring(21,26));
        System.out.println(s.substring(12,13));
        System.out.println(s2.substring(1, s2.length() - 1));
        System.out.println(s2.substring(1,s2.length()-1).split(":")[1].trim());
        String ss = s2.substring(1, s2.length() - 1).split(":")[1].trim();
        System.out.println(ss.substring(1,ss.length()-1).trim());
    }

    private static void t7() {
        String s="点击上面的“注册”按钮，即表示您同意《药管家服务协议及隐私政策》";
        System.out.println(s.substring(0,18));
        System.out.println(s.substring(18,32));
    }

    private static void t6() {
        String s="您有一次07:00服药记录";
        String s2="您有一次07:00未服药记录";
        System.out.println(s);
        System.out.println(s.substring(4,9));
        System.out.println(s2.substring(9,11));
    }

    private static void t5() {
        SimpleDateFormat sdmm = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String string2="00:00";
        String string3="00:10";
        try {
            long mTime = sdmm.parse("10:59").getTime();
            long nTime = sdmm.parse("14:00").getTime();
            long aTime = sdmm.parse("20:00").getTime();
            long eTime = sdmm.parse("23:59").getTime();
            long t= sdmm.parse("16:10").getTime();
            System.out.println(mTime);
            System.out.println(nTime);
            System.out.println(aTime);
            System.out.println(eTime);
            System.out.println(t);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void t4() {
        String s="[1,2,3,4]";
        System.out.println(s.substring(1,s.length()-1));
    }

    private static void t3() {
        StringBuilder sb=new StringBuilder();
        sb.append(1).append(",").append(2).append(",");
//        sb.substring(0, sb.length() - 2);
        sb.deleteCharAt(sb.length()-1);
        System.out.println(sb.toString());
    }

    private static void t2() {
        HashMap<String, List<Integer>> map = new HashMap<>();
        List<Integer> list = new ArrayList<Integer>();
        list.add(9);
        list.add(8);
        map.put("1", list);
        map.put("2", list);
        System.out.println(map.get("1").size());
        System.out.println(map.get("2").size());
        map.remove("1");
        if (map.containsKey("1")) {
            System.out.println(map.get("1").size());
        }
        System.out.println(map.remove("1"));
    }

    private static void t1() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        try {
            System.out.println(simpleDateFormat.parse(simpleDateFormat.format(new Date())).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(simpleDateFormat.parse("2015-12-28").getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(sdf.format(simpleDateFormat.parse("2015-12-21")));
            System.out.println(sdf.format(simpleDateFormat.parse("2015-12-22")));
            System.out.println(sdf.format(simpleDateFormat.parse("2015-12-23")));
            System.out.println(sdf.format(simpleDateFormat.parse("2015-12-24")));
            System.out.println(sdf.format(simpleDateFormat.parse("2015-12-25")));
            System.out.println(sdf.format(simpleDateFormat.parse("2015-12-26")));
            System.out.println(sdf.format(simpleDateFormat.parse("2015-12-27")));
            System.out.println(sdf.format(simpleDateFormat.parse("2015-12-28")));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
