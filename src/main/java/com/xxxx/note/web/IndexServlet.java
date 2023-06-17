package com.xxxx.note.web;

import com.xxxx.note.dao.NoteDao;
import com.xxxx.note.po.Note;
import com.xxxx.note.po.User;
import com.xxxx.note.service.NoteService;
import com.xxxx.note.utils.Page;
import com.xxxx.note.vo.NoteVo;
import org.omg.IOP.IOR;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    /**
     * cationName行为判断区
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
        //导航栏高亮
        req.setAttribute("menuPage","index");
        //设置whichPage的值 中心动态包含的区域
        //查询actionName
        String actionName=req.getParameter("actionName");
        //将行为设置到request作用域中
        req.setAttribute("action",actionName);
        if("searchTitle".equals(actionName)){
            //拿输入框输入的title的值
            String title=req.getParameter("title");
            //存到request作用域中
            req.setAttribute("title",title);
            //设置云记列表分页查询
            noteList(req,resp,title,null,null);
        }else if("searchDate".equals(actionName)){
            //查询日期
            String date=req.getParameter("date");
            //存到request作用域中
            req.setAttribute("date",date);
            noteList(req,resp,null,date,null);
        }else if("searchId".equals(actionName)){
            //查询云记类别
            String typeId=req.getParameter("typeId");
            //存到request作用域中
            req.setAttribute("typeId",typeId);
            noteList(req,resp,null,null,typeId);
        }  else{
            noteList(req,resp,null,null,null);
        }

        Object obj=req.getAttribute("whichPage");
        if(obj==null){
            req.setAttribute("whichPage","note/list.jsp");
        }

        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }

    /**
     * 分页查询云记列表
        1. 接收参数 （当前页、每页显示的数量）
        2. 获取Session作用域中的user对象
        3. 调用Service层查询方法，返回Page对象
        4. 将page对象设置到request作用域中
     * @param req
     * @param resp
     * @param title 标题
     */

    private void noteList(HttpServletRequest req, HttpServletResponse resp,String title,String date,String typeId) throws ServletException,IOException{
        //接收参数
        String pageNum=req.getParameter("pageNum");
        String pageSize=req.getParameter("pageSize");
        //获取user对象
        User user= (User) req.getSession().getAttribute("user");
        //查询
        Page<Note> page= new NoteService().findNoteListByPage(pageNum,pageSize,user.getUserId(),title,date,typeId);
        req.getSession().setAttribute("page",page);

        //查询云记日期
        List<NoteVo> dateInfo=new NoteService().findNoteCountByData(user.getUserId());
        req.getSession().setAttribute("dateInfo",dateInfo);
        //查询云记类别
        List<NoteVo> typeInfo=new NoteService().findNoteCountByType(user.getUserId());
        req.getSession().setAttribute("typeInfo",typeInfo);
//        resp.sendRedirect("index.jsp");
    }
}
