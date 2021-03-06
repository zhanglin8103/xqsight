package com.xqsight.common.model.constants;

import java.util.ListResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 错误代码对照表
 *
 * @author wangganggang
 * @date 2017/04/07
 */
public class ErrorMessageConstants extends ListResourceBundle {

    /** 自定义message */
    public static final int ERROR_10000 = 10000;

    /** 当前对象不存在 **/
    public static final int ERROR_10001 = 10001;

    /** 未授权异常 **/
    public static final int ERROR_10005 = 10005;

    /** 生成模板错误 */
    public static final int ERROR_20001 = 20001;

    /** 未知异常 **/
    public static final int ERROR_99999 = 99999;


    private static ResourceBundle rb = null;

    public static String[][] errMsg = null;

    static {
        rb = PropertyResourceBundle.getBundle("errMessage");
        Set<String> keys = rb.keySet();
        int size = rb.keySet().size();
        int i = 0;
        errMsg = new String[size][2];
        for (String key : keys) {
            errMsg[i][0] = key;
            errMsg[i++][1] = rb.getString(key);
        }
    }

    @Override
    protected Object[][] getContents() {
        return ErrorMessageConstants.errMsg;
    }

    /**
     * 是否有某个出错信息代码
     *
     * @param key
     * @return
     */
    public static boolean contains(String key) {
        return rb.containsKey(key);
    }

    /**
     * 获取出错信息
     *
     * @param key
     * @return
     */
    public static String getValue(String key) {
        return rb.containsKey(key) ? rb.getString(key) : "";
    }

    /**
     * 获取出错信息 key:value 形式
     *
     * @param key
     * @return
     */
    public static String getKeyValue(String key) {
        return key + ":" + rb.getString(key);
    }

}
