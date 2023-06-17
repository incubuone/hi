package com.xxxx.note.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.xxxx.note.dao.BaseDao;
import com.xxxx.note.dao.UserDao;
import com.xxxx.note.po.User;
import com.xxxx.note.utils.EmptyUtils;
import com.xxxx.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;

import static cn.hutool.core.util.StrUtil.*;

public class UserService {
    /**
     * 验证昵称的唯一性
     * 1. 判断昵称是否为空
     *    如果为空，返回"0"
     * 2. 调用Dao层，通过用户ID和昵称查询用户对象
     * 3. 判断用户对象存在
     *    存在，返回"0"
     *    不存在，返回"1"
     * @param nick
     * @param userId
     * @return
     */
    public static Boolean checkNick(String nick, Integer userId) {
        boolean flag=true;
        if(isBlank(nick)){
            flag=false;
            return false;
        }
        User user= UserDao.querybyNickID(nick,userId);
        if(user==null){
            return true;
        }else {
            return false;
        }
    }
    /**
     * 用户登录
        1. 判断参数是否为空
            如果为空
            设置ResultInfo对象的状态码和提示信息
            返回resultInfo对象
        2. 如果不为空，通过用户名查询用户对象
        3. 判断用户对象是否为空
            如果为空
            设置ResultInfo对象的状态码和提示信息
            返回resultInfo对象
        4. 如果用户对象不为空，将数据库中查询到的用户对象的密码与前台传递的密码作比较 （将密码加密后再比较）
            如果密码不正确
            设置ResultInfo对象的状态码和提示信息
            返回resultInfo对象
        5. 如果密码正确
            设置ResultInfo对象的状态码和提示信息
        6. 返回resultInfo对象
     * @param name
     * @param pwd
     * @return
     */
    public static ResultInfo<User> login(String name, String pwd){
        ResultInfo<User> resultInfo=new ResultInfo<User>();
        User u=new User();
        u.setUname(name);
        u.setUpwd(pwd);
        resultInfo.setResultInfo(u);
        //登录方法的校验
        //先判断用户名是否为空
        EmptyUtils emp=new EmptyUtils();
        if(emp.isEmpty(name)){
            resultInfo.setFlag(false);
            resultInfo.setMsg("用户名不能为空");
            return resultInfo;
        }
        //判断密码是否为空
        if(emp.isEmpty(pwd)){
            resultInfo.setFlag(false);
            resultInfo.setMsg("用户密码不能为空");
            return resultInfo;
        }
        UserDao dao=new UserDao();
        //去数据库种查姓名,看是否存在该用户
        User user=dao.queryByName(name);
        if(user==null){
            resultInfo.setFlag(false);
            resultInfo.setMsg("用户名不存在");
            return resultInfo;
        }
        //加密密码
        pwd= DigestUtil.md5Hex(pwd);
        //如果用户存在，继续验证密码
        if(!(user.getUpwd().equals(pwd))){
            resultInfo.setFlag(false);
            resultInfo.setMsg("用户密码不对");
            return resultInfo;
        }
        //上述条件都不满足，则通过验证
        resultInfo.setFlag(true);
        resultInfo.setResultInfo(user);
        return resultInfo;
    }
    /**
     * 修改用户信息
        1. 获取参数（昵称、心情）
        2. 参数的非空校验（判断必填参数非空）
            如果昵称为空，将状态码和错误信息设置resultInfo对象中，返回resultInfo对象
        3. 从session作用域中获取用户对象（获取用户对象中默认的头像）
        4. 实现上上传文件
            1. 获取Part对象 request.getPart("name"); name代表的是file文件域的那么属性值
            2. 通过Part对象获取上传文件的文件名
            3. 判断文件名是否为空
            4. 获取文件存放的路径  WEB-INF/upload/目录中
            5. 上传文件到指定目录
        5. 更新用户头像 （将原本用户对象中的默认头像设置为上传的文件名）
        6. 调用Dao层的更新方法，返回受影响的行数
        7. 判断受影响的行数
            如果大于0，则修改成功；否则修改失败
        8. 返回resultInfo对象

     * @param req
     * @return
     */
    public static ResultInfo updateUser(HttpServletRequest req) {
       /* System.out.println("更新对象");*/
        ResultInfo<Object> resultInfo=new ResultInfo<>();
        //拿到表单的name属性拿值
        String nick = req.getParameter("nick");
        String mood =req.getParameter("mood");
        //校验必填项（昵称）
        if(isBlank(nick)){
            resultInfo.setFlag(false);
            resultInfo.setMsg("必填项-->昵称不能为空");
            return resultInfo;
        }
        //从session作用域中拿user对象
        User user= (User) req.getSession().getAttribute("user");
        //给user赋给修改的值
        user.setNick(nick);
        user.setMood(mood);

        try { //name是file文件域的name属性值
            Part part=req.getPart("img");
            //通过part对象获取上传的请求头
            String header= part.getHeader("Content-Disposition");
            //header：form-data; name="img"; filename="to_love.jpg"  要去掉前面一个双引号所以+2
            String str=header.substring(header.lastIndexOf("=")+2);
            //str："to_love.jpg"" 所以最后一位截取掉
            String fileName=str.substring(0,str.length()-1);
            //判断文件名是否为空
            if(isBlank(fileName)){
                resultInfo.setMsg("上传文件名不能为空");
                resultInfo.setFlag(false);
                return resultInfo;
            }
            //得到文件上传的路径
            String path=req.getServletContext().getRealPath("/statics/images");
            //输出拼接路径
            part.write(path+"/"+fileName);
            //设置用户头像为修改后的
            user.setHead(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        //调用dao层更新用户的方法，返回影响行数
        int rows= new UserDao().updateUser(user);
        //判断
        if(rows<0){
            resultInfo.setMsg("修改失败");
            resultInfo.setFlag(false);
            return resultInfo;
        }

        resultInfo.setFlag(true);
        resultInfo.setMsg("信息修改成功");
        return resultInfo;
    }
}
