package com.xxxx.note.dao;

import com.xxxx.note.po.User;
import com.xxxx.note.utils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    /**
     * 通过昵称与用户ID查询用户对象
     *  1. 定义SQL语句
     *     通过用户ID查询除了当前登录用户之外是否有其他用户使用了该昵称
     *          指定昵称  nick （前台传递的参数）
     *          当前用户  userId （session作用域中的user对象）
     *          String sql = "select * from tb_user where nick = ? and userId != ?";
     *  2. 设置参数集合
     *  3. 调用BaseDao的查询方法
     * @param nick
     * @param userId
     * @return
     */
    public static User querybyNickID(String nick, Integer userId) {
        //把参数放到集合中
        List<Object> params=new ArrayList<>();
        params.add(nick);
        params.add(userId);
        //准备sql
        String sql="select * from tb_user where nick=? and userId!=?";
        //调用方法
        User user= (User) BaseDao.queryFirstRow(sql,params,User.class);
        return user;
    }
    /**
     通过用户名查询用户对象， 返回用户对象
     1. 获取数据库连接
     2. 定义sql语句
     3. 预编译
     4. 设置参数
     5. 执行查询，返回结果集
     6. 判断并分析结果集
     7. 关闭资源
     * @param name
     * @return
     */
    public User queryByName2(String name){
        User user=null;
        Connection conn=null;
        PreparedStatement pre=null;
        ResultSet resultSet=null;
        String sql=null;
        try {
            //获取连接
            conn= DbUtils.getConnection();
            //准备sql
            sql="select * from tb_user where uname=?";
            //预处理块
            pre=conn.prepareStatement(sql);
            //设置预处理块的值
            pre.setString(1,name);
            //执行
            resultSet=pre.executeQuery();
            //处理结果
            if(resultSet.next()){
                user=new User();
                user.setUserId(resultSet.getInt("userId"));
                user.setUname(name);
                user.setUpwd(resultSet.getString("upwd"));
                user.setNick(resultSet.getString("nick"));
                user.setHead(resultSet.getString("head"));
                user.setMood(resultSet.getString("mood"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DbUtils.close(resultSet,conn,pre);
        }
        return user;
    }
    /**
     * 通过用户名查询用户对象
     *  1. 定义sql语句
     *  2. 设置参数集合
     *  3. 调用BaseDao的查询方法
     * @param name
     * @return
     */
    public User queryByName(String name){
        User user=null;
        List<Object> params=new ArrayList<>();
        String sql="select * from tb_user where uname = ?";
        params.add(name);
        user= (User) BaseDao.queryFirstRow(sql,params,User.class);
        return user;
    }
    /**
     * 通过用户ID修改用户信息
     1. 定义SQL语句
     String sql = "update tb_user set nick = ?, mood = ?, head = ? where userId = ? ";
     2. 设置参数集合
     3. 调用BaseDao的更新方法，返回受影响的行数
     4. 返回受影响的行数
     * @param user
     * @return
     */
    public static int updateUser(User user) {
        String sql= " update tb_user set nick=?,mood=?,head=? where userId=? ";
        List<Object> params=new ArrayList<>();
        params.add(user.getNick());
        params.add(user.getMood());
        params.add(user.getHead());
        params.add(user.getUserId());
        int rows= BaseDao.excuteUpdate(sql,params);
        return rows;
    }
}
