package com.xxxx.note.utils;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonUtils {
    /**
     * 将对象转化为JSON类型的字符串，响应给ajax的回调函数
     * @param resp
     * @param obj
     */
    public static void JsonToString(HttpServletResponse resp,Object obj){
        //设置响应类型和编码
        resp.setContentType("application/json;charset=UTF-8");
        //得到字符输出流
        PrintWriter out= null;
        try {
            out = resp.getWriter();
            //调用json.toJSONString方法
            String json= JSON.toJSONString(obj);
            //输出
            out.write(json);
            //关闭流
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
