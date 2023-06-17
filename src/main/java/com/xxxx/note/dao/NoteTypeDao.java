package com.xxxx.note.dao;

import com.xxxx.note.po.NoteType;
import com.xxxx.note.utils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoteTypeDao {
    /**
     * 通过用户ID查询类型集合
     1. 定义SQL语句
     String sql = "select typeId,typeName,userId from tb_note_type where userId = ? ";
     2. 设置参数列表
     3. 调用BaseDao的查询方法，返回集合
     4. 返回集合
     * @param userId
     * @return
     */
    public List<NoteType> queryListByUserId(Integer userId){
        /*noteType noteType=new noteType();*/
        //准备sql
        String sql="select * from tb_note_type where userId =?";
        //准备放userId参数的list集合
        List<Object> params=new ArrayList<>();
        params.add(userId);
        //通过queryRows方法返回查询的list集合
        List<NoteType> list= BaseDao.queryRows(sql,params, NoteType.class);
        return list;
    }
    /**
     * 查询当前登录用户下，类型名称是否唯一
     *     返回true，表示成功
     *     返回false，表示失败
     * @param typeName
     * @param userId
     * @param typeId
     * @return
     */
    public boolean queryTypeNameById(String typeName, String typeId, Integer userId) {
        //准备sql
        String sql="select * from tb_note_type where typeName=? and userId=?";
        List<Object> params=new ArrayList<>();
        //把参数放入
        params.add(typeName);
        params.add(userId);
        //调用查询方法
        NoteType noteType = (NoteType) BaseDao.queryFirstRow(sql, params, NoteType.class);
        //如果查不到用户，证明不存在该类型名称
        if(noteType==null){
            return true;
        }else{
            //如果用户id等于自己的，可以去执行修改操作
            if(userId==noteType.getUserId()){
                return true;
            }
        }
        return false;
    }
    /**
     * 添加方法，返回主键
     * @param typeName
     * @param userId
     * @return
     */
    public Integer addType(String typeName, Integer userId) {
        Integer key=null;
        Connection conn=null;
        PreparedStatement pre=null;
        ResultSet res=null;
        try {
            //获取连接
            conn= DbUtils.getConnection();
            //准备sql
            String sql="insert into tb_note_type(userId,typeName) values (?,?) ";
            //返回主键
            pre=conn.prepareStatement(sql,pre.RETURN_GENERATED_KEYS);
            //赋值
            pre.setInt(1,userId);
            pre.setString(2,typeName);
            //受影响行数
            int rows=pre.executeUpdate();
            System.out.println(rows);
            //判断受影响行数
            if(rows>0){
                res=pre.getGeneratedKeys();
                if(res.next()){
                    key=res.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DbUtils.close(res,conn,pre);
        }
        return key;
    }
    /**
     * 修改方法，返回受影响的行数
     * @param typeName
     * @param typeId
     * @return
     */
    public Integer updateType(String typeId, String typeName) {
        //准备sql
        String sql="update tb_note_type set typeName=? where typeId=?";
        List<Object> params =new ArrayList<>();
        params.add(typeName);
        params.add(typeId);
        //调用baseDao更新方法返回受影响行数
        int rows= BaseDao.excuteUpdate(sql,params);
        return rows;

    }
    /**
     * 通过类型ID删除指定的类型记录，返回受影响的行数
     * @param typeId
     * @return
     */
    public static int deleteNoteTypebyTypeId(String typeId) {
        //准备sql
        String sql="delete from tb_note_type where typeId=?";
        List<Object> params=new ArrayList<>();
        //装属性
        params.add(typeId);
        //拿到删除操作的影响行数
        int rows = BaseDao.excuteUpdate(sql, params);
        return rows;
    }
    /**
     * 通过类型ID查询云记记录的数量，返回云记数量
     *
     * @param typeId
     * @return
     */
    public static long queryCountNotebyTypeId(String typeId) {
        //准备sql
        String sql="select count(*) from tb_note where typeId=? ";
        List<Object> params=new ArrayList<>();
        //把属性装进去
        params.add(typeId);
        //拿到查询的云记的数量
        long count= (long) BaseDao.findSingle(sql,params);
        return count;
    }
}
