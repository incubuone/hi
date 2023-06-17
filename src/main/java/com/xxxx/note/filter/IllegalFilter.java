package com.xxxx.note.filter;

import com.xxxx.note.po.User;
import com.xxxx.note.service.UserService;
import com.xxxx.note.vo.ResultInfo;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * 非法访问拦截
 *  拦截的资源：
 *      所有的资源   /*
 *
 *      需要被放行的资源
 *          1. 指定页面，放行 （用户无需登录的即可访问的页面；例如：登录页面login.jsp、注册页面register.jsp等）
 *          2. 静态资源，放行 （存放在statics目录下的资源；例如：js、css、images等）
 *          3. 指定行为，放行 （用户无需登录即可执行的操作；例如：登录操作actionName=login等）
 *          4. 登录状态，放行 （判断session作用域中是否存在user对象；存在则放行，不存在，则拦截跳转到登录页面）
 *
 *  免登录（自动登录）
 *      通过Cookie和Session对象实现
 *
 *      什么时候使用免登录：
 *          当用户处于未登录状态，且去请求需要登录才能访问的资源时，调用自动登录功能
 *
 *      目的：
 *          让用户处于登录状态（自动调用登录方法）
 *
 *      实现：
 *          从Cookie对象中获取用户的姓名与密码，自动执行登录操作
 *              1. 获取Cookie数组  request.getCookies()
 *              2. 判断Cookie数组
 *              3. 遍历Cookie数组，获取指定的Cookie对象 （name为user的cookie对象）
 *              4. 得到对应的cookie对象的value （姓名与密码：userName-userPwd）
 *              5. 通过split()方法将value字符串分割成数组
 *              6. 从数组中分别得到对应的姓名与密码值
 *              7. 请求转发到登录操作  user?actionName=login&userName=姓名&userPwd=密码
 *              8. return
 *
 *     如果以上判断都不满足，则拦截跳转到登录页面
 *
 */
@WebFilter("/*")
public class IllegalFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req= (HttpServletRequest) request;
        HttpServletResponse resp= (HttpServletResponse) response;

        //设置需要放行的资源，阻止非法访问
        String path=req.getRequestURI();
        //登录界面放行
        if(path.contains("/login.jsp")){
            chain.doFilter(request,response);
            return;
        }
        //静态资源放行
        if(path.contains("/statics")){
            chain.doFilter(request,response);
            return;
        }


        //cookie免登录
        //让处于未登录状态的用户可以自动登录，获取cookie中的账号密码req.getCookies()
        //遍历cookie数组，拿到name=user的值(用户名-->密码)
        //通过split()方法分割，拿到用户名和密码
        //手动转发到登录操作user?xxxxxx
        Cookie[] cookie=req.getCookies();
        String str="";
        if(cookie!=null && cookie.length>0){
            for (int i = 0; i <cookie.length ; i++) {
                if("user".equals(cookie[i].getName())){
                    str=cookie[i].getValue();
                    String[] strs=str.split("-->");
                    String name=strs[0];
                    String pwd=strs[1];
                    //用从cookie中拆分的用户名和密码再去执行一次登录的方法，拿到resultInfo对象
                    /*ResultInfo<User> resultInfo= UserService.login(name,pwd);*/
                    //如果登陆成功，user被塞进resultInfo对象中
                   /* if(resultInfo.getFlag()){
                        //再把装着user对象的resultInfo对象放到Session作用域中，名字为user
                        //再放行之后再去执行加载图片昵称等信息时，就可以从user对象中拿到对应的信息
                        req.getSession().setAttribute("user",resultInfo.getResultInfo());
                        System.out.println("走完过滤器");
                        chain.doFilter(req,resp);
                        return;
                    }*/
                    String uri="user?actionName=login&userName="+name+"&userPwd="+pwd;
                    req.getRequestDispatcher(uri).forward(request,response);
                  /*  resp.sendRedirect(uri);*/
                    chain.doFilter(request,response);
//                    resp.sendRedirect("login.jsp");
                    return;
                }
            }

        }
        //登录活动放行
        if(path.contains("/user")){
            //拿到用户行为
            String action=req.getParameter("actionName");
            if("login".equals(action)){
                chain.doFilter(req,resp);
                //return一定要写在判断里面
                return;
            }

        }


        //登录之后放行
        User user=(User)req.getSession().getAttribute("user");
        if(user!=null){
            chain.doFilter(request,response);
            return;
        }


        resp.sendRedirect("login.jsp");
    }
}
