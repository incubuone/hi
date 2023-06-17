package com.xxxx.note.web;

import com.xxxx.note.po.Note;
import com.xxxx.note.po.User;
import com.xxxx.note.service.NoteService;
import com.xxxx.note.utils.JsonUtils;
import com.xxxx.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    /**
     *根据不同的用户行为(actionName)进行不同的操作
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置导航栏高亮
        req.setAttribute("menuPage","report");
        //得到用户行为
        String actionName=req.getParameter("actionName");
        if("dataReport".equals(actionName)){
            dataReport(req,resp);
        }else if("monthChart".equals(actionName)){
           monthChart(req,resp);
        }else if("getLocation".equals(actionName)){
            getNoteLocation(req,resp);
        }
    }
    /**
     * 查询用户发布云记时的坐标
     * @param req
     * @param resp
     */
    private void getNoteLocation(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
        //从session作用域得到user对象
        User user=(User)req.getSession().getAttribute("user");
        //调用service层的查询方法得到经纬度集合
        ResultInfo<List<Note>> locList=new NoteService().getNoteLocation(user.getUserId());
        //将locList集合转成Json类型的字符串，并传给ajax的回调函数
        JsonUtils.JsonToString(resp,locList);
    }
    /**
     * 通过月份查询对应的云记数量
     * @param req
     * @param resp
     */
    private void monthChart(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
        //得到user对象
        User user= (User) req.getSession().getAttribute("user");
        //调用service层的查询方法，得到resultInfo对象
        ResultInfo<Map<String,Object>> resultInfo=new NoteService().queryNoteCountByMonth(user.getUserId());
        //将resultInfo对象转为json字符串，返回ajax的回调函数
        JsonUtils.JsonToString(resp,resultInfo);
    }
    /**
     * 进入报表页面
     * @param req
     * @param resp
     */
    private void dataReport(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
        //设置动态包含的页面值
        req.setAttribute("whichPage","report/data.jsp");
        //请求转发到首页
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }
}
