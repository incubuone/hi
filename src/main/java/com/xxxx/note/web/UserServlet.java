package com.xxxx.note.web;

import com.xxxx.note.po.User;
import com.xxxx.note.service.UserService;
import com.xxxx.note.vo.ResultInfo;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@WebServlet("/user")
@MultipartConfig
public class UserServlet extends HttpServlet {
    /**
     * 根据不同的用户行为(actionName)进行不同的操作
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //是个照顾导航栏高亮
        req.setAttribute("menuPage","user");
        //接受用户执行的行为
        String actionName=req.getParameter("actionName");
        UserService uservice=new UserService();
        //不同行为执行不同方法
        if("login".equals(actionName)){
            //登录
           userLogin(req,resp);
        }else if("logout".equals(actionName)){
            //退出
            userLogout(req,resp);
        }else if("userCenter".equals(actionName)){
            //进入个人中心
            userCenter(req,resp);
        }else if("userHead".equals(actionName)){
            //加载图片
            userHead(req,resp);
        }else if("checkNick".equals(actionName)){
            checkNick(req,resp);
        }else if("updateUser".equals(actionName)){
            updateUser(req,resp);
        }
    }

    /**
     * 修改用户信息
     注：文件上传必须在Servlet类上提那家注解！！！ @MultipartConfig
        1. 调用Service层的方法，传递request对象作为参数，返回resultInfo对象
        2. 将resultInfo对象存到request作用域中
        3. 请求转发跳转到个人中心页面 （user?actionName=userCenter）
     * @param req
     * @param resp
     */
    private void updateUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
        //得到resultInfo对象,传递request为参数对象
        ResultInfo resultInfo= UserService.updateUser(req);
        //存到request作用域中
        req.setAttribute("resultInfo",resultInfo);
        //请求转发到个人中心
        req.getRequestDispatcher("user?actionName=userCenter").forward(req,resp);
    }
    /**
     * 验证昵称的唯一性
     *  1. 获取参数（昵称）
     *  2. 从session作用域获取用户对象，得到用户ID
     *  3. 调用Service层的方法，得到返回的结果
     *  4. 通过字符输出流将结果响应给前台的ajax的回调函数
     *  5. 关闭资源
     * @param req
     * @param resp
     */
    private void checkNick(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
        System.out.println("修改昵称");
        //获取昵称
        String nick=req.getParameter("nickName");
        //从Session作用域中拿user对象
        User user1=new User();
         user1= (User) req.getSession().getAttribute("user");
        //在service层写一个方法进行校验得到返回的结果
        Boolean flag= UserService.checkNick(nick,user1.getUserId());
        //通过字符输出流相应给前台的ajax回调函数
        resp.getWriter().print(flag);
        //关闭资源
        resp.getWriter().close();
    }
    /**
     * 加载头像
     *  1. 获取参数 （图片名称）
     *  2. 得到图片的存放路径 （request.getServletContext().getealPathR("/")）
     *  3. 通过图片的完整路径，得到file对象
     *  4. 通过截取，得到图片的后缀
     *  5. 通过不同的图片后缀，设置不同的响应的类型
     *  6. 利用FileUtils的copyFile()方法，将图片拷贝给浏览器
     * @param req
     * @param resp
     */
    private void userHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        /*System.out.println("拿头像图片");*/
        //得到图片的名称
        String imgName=req.getParameter("imageName");
        //得到要拿取图片的路径
        String path=req.getServletContext().getRealPath("/statics/images");
        //路径+图片名拼接成完整路径
        File file=new File(path+"/"+imgName);
        //得到图片的类型
        String suffix=imgName.substring(imgName.lastIndexOf(".")+1);
        if("JPG".equalsIgnoreCase(suffix) || "JPEG".equalsIgnoreCase(suffix)){
            resp.setContentType("image/JPG");
        }else if("PNG".equalsIgnoreCase(suffix)){
            resp.setContentType("image/PNG");
        }else if("GIF".equals(suffix)){
            resp.setContentType("image/GIF");
        }
        //输出
        FileUtils.copyFile(file, resp.getOutputStream());
    }
    /**
     * 进入个人中心
     *  1. 设置首页动态包含的页面值
     *  2. 请求转发跳转到index.jsp
     * @param req
     * @param resp
     */
    private void userCenter(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置首页动态包含的页面值，跳转到index
        req.setAttribute("whichPage","user/list.jsp");
        req.getRequestDispatcher("index.jsp").forward(req,resp);

    }
    /**
     * 用户退出
     *  1. 销毁Session对象
     *  2. 删除Cookie对象
     *  3. 重定向跳转到登录页面
     * @param req
     * @param resp
     */
    private void userLogout(HttpServletRequest req,HttpServletResponse resp) throws  ServletException,IOException{
        //销毁session对象
        req.getSession().invalidate();
        //销毁cookie
        Cookie cookie=new Cookie("user",null);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        //重定向到登录界面
        resp.sendRedirect("login.jsp");
    }
    /**
     * 用户登录
        1. 获取参数 （姓名、密码）
        2. 调用Service层的方法，返回ResultInfo对象
        3. 判断是否登录成功
            如果失败
                将resultInfo对象设置到request作用域中
                请求转发跳转到登录页面
            如果成功
                将用户信息设置到session作用域中
                判断用户是否选择记住密码（rem的值是1）
                    如果是，将用户姓名与密码存到cookie中，设置失效时间，并响应给客户端
                    如果否，清空原有的cookie对象
                重定向跳转到index页面
     * @param req
     * @param resp
     */
    private void userLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("这里是登陆方法");
        //接收参数
        String name=req.getParameter("userName");
        String pwd=req.getParameter("userPwd");
        UserService uservice=new UserService();
        //拿到service里面登录方法验证的用户名和密码
        ResultInfo<User> resultInfo = uservice.login(name, pwd);
//        ResultInfo<User> resultInfo=new ResultInfo<User>();
        //如果flag=false进入if执行体
        if(!resultInfo.getFlag()){
            req.setAttribute("resultInfo",resultInfo);
            req.getRequestDispatcher("login.jsp").forward(req,resp);
            return;
        }
        //成功时，将用户信息设置到session作用域
        //gerResultInfo里面存的是用户名和密码信息
        req.getSession().setAttribute("user",resultInfo.getResultInfo());
        //记住我 --看用户是否勾选这个复选框
        //设置的value值为1，如果传回的值为1代表勾选，如果没传值，代表没勾选
        //将用户名和密码存到网页cookie里面
        String duo=req.getParameter("duo");
        if("1".equals(duo)){
            //得到Cookie对象
            Cookie cookie=new Cookie("user",name+"-->"+pwd);
            //设置存在时间
            cookie.setMaxAge(5*24*60*60);
            resp.addCookie(cookie);
            //重定向跳转
            resp.sendRedirect("index");
        }else{
            //如果没选中，就清除原有cookie
            Cookie cookie=new Cookie("user",null);
            //时间设置为0
            cookie.setMaxAge(0);
            resp.addCookie(cookie);
            //重定向跳转
            resp.sendRedirect("index");
        }


    }
}


