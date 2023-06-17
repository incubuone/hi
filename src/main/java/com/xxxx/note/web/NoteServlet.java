package com.xxxx.note.web;

import com.xxxx.note.po.Note;
import com.xxxx.note.po.User;
import com.xxxx.note.po.NoteType;
import com.xxxx.note.service.NoteService;
import com.xxxx.note.service.TypeService;
import com.xxxx.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/note")
public class NoteServlet extends HttpServlet {
    /**
     * 行为判断区
     * @param req   the {@link HttpServletRequest} object that
     *                  contains the request the client made of
     *                  the servlet
     * @param resp  the {@link HttpServletResponse} object that
     *                  contains the response the servlet returns
     *                  to the client
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       String actionName=req.getParameter("actionName");
       if("view".equals(actionName)){
           noteView(req,resp);
       }else if("addOrUpdate".equals(actionName)){
          NoteAddOrUpdate(req,resp);
       }else if("detail".equals(actionName)){
           //查询每个云记的细节
           noteDetail(req,resp);
       }else if("deleteNote".equals(actionName)){
           //删除云记的方法
           deleteNote(req,resp);
       }
    }
    /**
     * 删除云记
        1. 接收参数 （noteId）
        2. 调用Service层删除方法，返回状态码 （1=成功，0=失败）
        3. 通过流将结果响应给ajax的回调函数 （输出字符串）
     * @param req
     * @param resp
     */
    private void deleteNote(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
        //接收参数
        String noteId=req.getParameter("noteId");
        //调用service层的删除方法
        Boolean flag=new NoteService().deleteNote(noteId);
        //返回给ajax的回调函数
        resp.getWriter().print(flag);
        //关闭资源
        resp.getWriter().close();
    }
    /**
     * 查询云记详情
        1. 接收参数 （noteId）
        2. 调用Service层的查询方法，返回Note对象
        3. 将Note对象设置到request请求域中
        4. 设置首页动态包含的页面值
        5. 请求转发跳转到index.jsp
     * @param req
     * @param resp
     */
    private void noteDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //得到noteId
        String noteId=req.getParameter("noteId");
        //得到查询的note对象
        Note note=NoteService.findNoteByNoteId(noteId);
        //把note对象设置到request请求域中
        req.setAttribute("note",note);
        //设置动态包含的页面
        req.setAttribute("whichPage","note/detail.jsp");
        //请求转发到首页
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }
    /**
     * 添加或修改操作
        1. 接收参数 （类型ID、标题、内容）
        2. 调用Service层方法，返回resultInfo对象
        3. 判断resultInfo的code值
            如果flag=true，表示成功
            重定向跳转到首页 index
            如果flag=false，表示失败
            将resultInfo对象设置到request作用域
     请求转发跳转到note?actionName=view
     * @param req
     * @param resp
     */
    private void NoteAddOrUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
        //拿到云记类型、标题、内容
        String typeId= req.getParameter("typeId");
        String title=req.getParameter("title");
        String content=req.getParameter("content");
        //如果要进行的是修改操作，还需要一个主键noteId
        String noteId=req.getParameter("noteId");
        //获取经纬度
        String lon=req.getParameter("lon");
        String lat=req.getParameter("lat");
        //调用service层的方法返回resultInfo对象
        ResultInfo<Note> resultInfo= new NoteService().addOrUpdate(typeId,title,content,noteId,lon,lat);
        if(resultInfo.getFlag()){
                resp.sendRedirect("index");
        }else{
            //将resultInfo对象存到request作用域中
            req.setAttribute("resultInfo",resultInfo);
            String uri="note?actionName=view";
            //如果noteId不为空，证明执行的是修改操作，所以需要拼接uri
            if(noteId!=null){
                uri+="&noteId="+noteId;
            }

            req.getRequestDispatcher(uri).forward(req,resp);

        }
    }
    /**
     * 进入发布云记页面
        1. 从Session对象中获取用户对象
        2. 通过用户ID查询对应的类型列表
        3. 将类型列表设置到request请求域中
        4. 设置首页动态包含的页面值
        5. 请求转发跳转到index.jsp
     * @param req
     * @param resp
     */
    private void noteView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*修改云记区域*/
        //拿到noteId
        String noteId=req.getParameter("noteId");
        //通过service层的方法通过noteId查询note对象
        Note note=NoteService.findNoteByNoteId(noteId);
        //把得到的note对象存到request作用域中
        req.setAttribute("noteInfo",note);


        //设置导航栏高亮
        req.setAttribute("menuPage","note");
        //设置动态包含的页面
        req.setAttribute("whichPage","note/view.jsp");
        //从Session作用域中拿到User对象
        User user= (User) req.getSession().getAttribute("user");
        //根据userid去查询云记列表
        List<NoteType> typeList= new TypeService().findNoteTypeList(user.getUserId());
        //设置到request作用域中
        req.setAttribute("typeList",typeList);
        //请求转发到首页
        req.getRequestDispatcher("index.jsp").forward(req,resp);

    }
}
