package com.xxxx.note.vo;
/**
 * 封装返回结果的类
 *      状态码
 *          flag=true成功
 *          flag=false失败
 *      提示信息
 *      返回的对象（字符串、JavaBean、集合、Map等）
 */
public class ResultInfo<T> {
    private boolean flag=true;
    private T  resultInfo;
    private String msg;

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public T getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(T resultInfo) {
        this.resultInfo = resultInfo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
