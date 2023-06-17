package com.xxxx.note.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static cn.hutool.core.util.StrUtil.isBlank;

/**
 *
 * 字符乱码处理过滤器
 *
 * 请求乱码解决
 *  sdf
    乱码原因：
    服务器默认的解析编码为ISO-8859-1，不支持中文。
    乱码情况：
        POST请求
            Tomcat7及以下版本    乱码
            Tomcat8及以上版本    乱码

        GET请求
            Tomcat7及以下版本    乱码
            Tomcat8及以上版本    不乱码

        解决方案：
        POST请求：
            无论是什么版本的服务器，都会出现乱码，需要通过request.setCharacterEncoding("UTF-8")设置编码格式。（只针对POST请求有效）
        GET请求
            Tomcat8及以上版本，不会乱码，不需要处理。
            Tomcat7及以下版本，需要单独处理。
            new String(request.getParamater("xxx").getBytes("ISO-8859-1"),"UTF-8");

 */
@WebFilter("/*")
public class EncdingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req= (HttpServletRequest) request;
        HttpServletResponse resp= (HttpServletResponse) response;
        //处理Post请求的乱码
        req.setCharacterEncoding("UTF-8");
        /*System.out.println("处理post请求乱码问题");*/
        //得到是get还是post请求
        String method=req.getMethod();
        if("GET".equalsIgnoreCase(method)){
            /*System.out.println("处理get请求乱码问题");*/
            //得到服务器信息
            String serverInfo=req.getServletContext().getServerInfo();
            //剪切
            String version=serverInfo.substring(serverInfo.lastIndexOf("/")+1,serverInfo.indexOf("."));
            if(version!=null && Integer.parseInt(version)<8){
                //针对7或以下放行
                MyWapper myWapper=new MyWapper(req);
                chain.doFilter(myWapper,response);
                return;
            }

        }
        chain.doFilter(req,resp);
    }
    class MyWapper extends HttpServletRequestWrapper{
        private HttpServletRequest req;

        /**
         * Constructs a request object wrapping the given request.
         *
         * @param request the {@link HttpServletRequest} to be wrapped.
         * @throws IllegalArgumentException if the request is null
         */
        public MyWapper(HttpServletRequest request) {
            super(request);
            this.req=request;
        }

        @Override
        public String getParameter(String name) {
            String value=req.getParameter(name);
            if(isBlank(value)){
                return value;
            }
            try {
                value=new String(value.getBytes("ISO-8859-1"),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            return value;
        }
    }

    @Override
    public void destroy() {

    }
}
