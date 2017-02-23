package com.cjy.flb.enums;

import java.util.Random;

/**
 * Created by Administrator on 2015/12/3 0003.
 * 用于随机生成对应枚举中的值
 */
public class Enums {
    private static Random rand = new Random(47);

    public static <T extends Enum<T>> T random(Class<T> ec) {
        return random(ec.getEnumConstants());
    }

    public static <T> T random(T[] values) {
        return values[rand.nextInt(values.length)];
    }
}