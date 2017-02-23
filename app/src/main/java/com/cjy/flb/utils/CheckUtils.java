package com.cjy.flb.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lvzhongyi
 *         <p>
 *         description 各种正则验证
 *         date 16/2/29
 *         email lvzhongyiforchrome@gmail.com
 *         </p>
 */
public class CheckUtils {

    /**
     * 是否是手机号码
     *
     * @return
     */
    public static boolean isMobilesPhone(String mobiles) {
        if (mobiles == null || mobiles.equals(""))
            return false;
        Pattern p = Pattern.compile("^((1[3-7][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
