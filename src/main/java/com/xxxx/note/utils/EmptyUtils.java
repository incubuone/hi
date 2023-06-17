package com.xxxx.note.utils;

public class EmptyUtils {
    /**
     * 字符串非空判断
     * @param str
     * @return
     */
    public boolean isEmpty(String str){
        if(str==null || str.trim()==""){
            return true;  //为空返回true
        }
            return false;

    }
}
