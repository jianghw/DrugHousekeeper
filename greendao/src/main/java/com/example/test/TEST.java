package com.example.test;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/12/21 0021.
 */
public class TEST {


    public static void main(String[] args) throws Exception {
        //        String json = "{response:[],count:0}";
        //        Gson gson = new Gson();
        //        String jsonS = gson.toJson(json);
        //        JsonElement jsonElement = new JsonParser().parse(json);
        //        JsonObject jsonObject = jsonElement.getAsJsonObject();
        //        JsonElement element = jsonObject.get("response");
        //        JsonArray jsonArray = element.getAsJsonArray();
        //        System.out.print(jsonArray.toString());

        //        if (10 != 20) {
        //            System.out.println("1");
        //        } else if (10 <= 30) {
        //            System.out.println("2");
        //        } else if (10 <= 40) {
        //            System.out.println("3");
        //        }

        //        Calendar calendar  =   new GregorianCalendar();
        //        calendar.set( Calendar.DATE,  1 );
        //        SimpleDateFormat simpleFormate  =   new  SimpleDateFormat( "yyyy-MM-dd" );
        //        System.out.println(simpleFormate.format(calendar.getTime()));
        //        String str = "1990-04-06";
        //        String day = str.split("-")[2].substring(1);
        //        System.out.println(day);

        //        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //        String[] string = sdf.format(new Date()).split("-");
        //        SimpleDateFormat sdformat = new SimpleDateFormat("E");
        //        StringBuilder stringBuilder=new StringBuilder();
        //        stringBuilder.append(string[0]).append("-").append(string[1]).append("-").append("06");
        //        Date week = sdf.parse(stringBuilder.toString());
        //        String s=sdformat.format(week);
        //        System.out.println(s);
        //        long time1 = System.currentTimeMillis();
        //        for (int i = 0; i < 2000; i++) {
        //            System.out.println(i);
        //        }
        //        long time2 = System.currentTimeMillis();
        //        System.out.println(time2 - time1);
        //        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm");
        //        StringBuilder stringBuilder = new StringBuilder("2015121211:00");
        //        System.out.println(sdf.parse(stringBuilder.toString()).getTime());
        //        System.out.println(sdf.parse("2015121214:00").getTime());
        //        System.out.println(sdf.format(sdf.parse("2015121214:00")));
        //
        //        stringBuilder.replace(8, 13, "12:19");
        //        System.out.println(sdf.parse(stringBuilder.toString()).getTime());
        ArrayList<Integer> number = new ArrayList<>();
        number.add(0);
        number.add(1);
        number.add(2);
        number.add(3);
        number.add(4);
        String string = "qqqqq" + "-" + 3;

        System.out.println(string.split("-")[1].length());
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(string.split("-")[1]);
        while (m.find()) {
            String find = m.group(1).toString();
            System.out.println(find);
        }
    }
}

