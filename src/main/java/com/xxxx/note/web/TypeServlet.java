package com.xxxx.note.web;

import com.xxxx.note.po.User;
import com.xxxx.note.po.NoteType;
import com.xxxx.note.service.TypeService;
import com.xxxx.note.utils.JsonUtils;
import com.xxxx.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/type")
public class TypeServlet extends HttpServlet {
    /**
     * 根据不同的用户行为进行不同的操作
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置云记列表界面高亮
        req.setAttribute("menuPage","type");
        //得到用户行为
        String actionName=req.getParameter("actionName");
        if("list".equals(actionName)){
            typeList(req,resp);
        }else if("delete".equals(actionName)){
            deleteType(req,resp);
        }else if("addOrUpdate".equals(actionName)){
            addOrUpdate(req,resp);
        }
    }
    /**
     * 添加或修改类型
        1. 接收参数 （类型名称、类型ID）
        2. 获取Session作用域中的user对象，得到用户ID
        3. 调用Service层的更新方法，返回ResultInfo对象
        4. 将ResultInfo转换成JSON格式的字符串，响应给ajax的回调函数
     * @param req
     * @param resp
     */
    private void addOrUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //拿参数
        String typeName=req.getParameter("typeName");
        String typeId=req.getParameter("typeId");
        //拿session作用域中的user对象
        User user= (User) req.getSession().getAttribute("user");
        //调用service层的方法，返回resultInfo对象
        ResultInfo<Integer> resultInfo= TypeService.addOrUpdate(typeName,typeId,user.getUserId());
        //将resultInfo转换为json格式字符串，返回给ajax的回调函数
        JsonUtils.JsonToString(resp,resultInfo);


    }
    /**
     * 删除类型
        1. 接收参数（类型ID）
        2. 调用Service的更新操作，返回ResultInfo对象
        3. 将ResultInfo对象转换成JSON格式的字符串，响应给ajax的回调函数
     * @param req
     * @param resp
     */
    private void deleteType(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
        //拿到typeId
        String typeId=req.getParameter("typeId");
        //利用service层删除操作返回resultInfo对象
        ResultInfo<NoteType> resultInfo= TypeService.deleteTypeList(typeId);
        //将对象转为json字符串
        JsonUtils.JsonToString(resp,resultInfo);
    }
    /**
     * 查询类型列表
        1. 获取Session作用域设置的user对象
        2. 调用Service层的查询方法，查询当前登录用户的类型集合，返回集合
        3. 将类型列表设置到request请求域中
        4. 设置首页动态包含的页面值
        5. 请求转发跳转到index.jsp页面
     * @param req
     * @param resp
     */
    private void typeList(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
        //设置选中的页面标题高亮
        req.setAttribute("menuPage","type");
        //拿到session域中的user对象，来获取userId
        User user= (User) req.getSession().getAttribute("user");
        //根据userId返回list集合
        List<NoteType> typeList= TypeService.findNoteTypeList(user.getUserId());
        //将集合设置到request请求域中
        req.setAttribute("typeList",typeList);
        //设置动态包含的页面值
        req.setAttribute("whichPage","type/list.jsp");
        //请求转发
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }
}
