package com.xxxx.note.dao;
import com.xxxx.note.po.User;
import com.xxxx.note.utils.DbUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础的JDBC操作类
 *      更新操作 （添加、修改、删除）
 *      查询操作
 *          1. 查询一个字段 （只会返回一条记录且只有一个字段；常用场景：查询总数量）
 *          2. 查询集合
 *          3. 查询某个对象
 */

public class BaseDao {
    /**
     * 更新操作
     *   添加、修改、删除
     *   1. 得到数据库连接
     *   2. 定义sql语句 （添加语句、修改语句、删除语句）
     *   3. 预编译
     *   4. 如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
     *   5. 执行更新，返回受影响的行数
     *   6. 关闭资源
     *
     *   注：需要两个参数:sql语句、所需参数的集合
     * @param sql
     * @param list
     * @return
     */
    public static int excuteUpdate(String sql, List<Object> list){
        int row=0;
        Connection conn=null;
        PreparedStatement pre=null;
        //获取连接
       conn= DbUtils.getConnection();
       //预处理sql语句
        try {
            pre=conn.prepareStatement(sql);
            //循环为预处理块赋值
            if(list!=null &&list.size()>0){
                for(int i=1;i<=list.size();i++){
                    pre.setObject(i, list.get(i-1));
                }
            }
            row=pre.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DbUtils.close(null,conn,pre);
        }
      return row;
    }

    /**
     * * 查询一个字段 （只会返回一条记录且只有一个字段；常用场景：查询总数量）
     *   1. 得到数据库连接
     *   2. 定义sql语句
     *   3. 预编译
     *   4. 如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
     *   5. 执行查询，返回结果集
     *   6. 判断并分析结果集
     *   7. 关闭资源
     *
     *   注：需要两个参数:sql语句、所需参数的集合
     * @param sql
     * @param list
     * @return
     */
    public static Object findSingle(String sql,List<Object> list){
        Object obj=null;
        ResultSet res=null;
        PreparedStatement pre=null;
        Connection conn=null;

        try {
            //获取连接
            conn= DbUtils.getConnection();
            //预处理快处理sql
            pre=conn.prepareStatement(sql);
            //预处理块赋值
            if(list!=null&& list.size()>0 ){
                for(int i=1;i<=list.size();i++){
                    pre.setObject(i,list.get(i-1));
                }
            }
            //得到结果集
            res=pre.executeQuery();
            if(res.next()){
                //如果结果有改变,就把查到的第一个对象赋给obj
                obj=res.getObject(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DbUtils.close(res,conn,pre);
        }
        return obj ;
    }

    /**
     *查询集合 （JavaBean中的字段与数据库中表的字段对应）
     *   1. 获取数据库连接
     *   2. 定义SQL语句
     *   3. 预编译
     *   4. 如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
     *   5. 执行查询，得到结果集
     *   6. 得到结果集的元数据对象（查询到的字段数量以及查询了哪些字段）
     *   7. 判断并分析结果集
     *       8. 实例化对象
     *       9. 遍历查询的字段数量，得到数据库中查询到的每一个列名
     *       10. 通过反射，使用列名得到对应的field对象
     *       11. 拼接set方法，得到字符串
     *       12. 通过反射，将set方法的字符串反射成类中的指定set方法
     *       13. 通过invoke调用set方法
     *       14. 将对应的JavaBean设置到集合中
     *   15. 关闭资源
     * @param sql
     * @param params
     * @param clazz
     * @return
     */
    public static List queryRows(String sql,List<Object> params,Class clazz){
        List list=new ArrayList<>();
        ResultSet res=null;
        PreparedStatement pre=null;
        Connection conn=null;
        try {
            //获取连接
            conn= DbUtils.getConnection();
            //准备预处理块
            pre=conn.prepareStatement(sql);
            //赋值
            if(params!=null &&params.size()>0){
                for(int i=1;i<=params.size();i++){
                    pre.setObject(i,params.get(i-1));
                }
            }
            //得到结果集
            res=pre.executeQuery();
            //拿到结果集的元数据 用来获取查询的字段名和字段数量
            ResultSetMetaData meteData=res.getMetaData();
            //根据元数据获取字段数量 getColumnCount
            int meteNum=meteData.getColumnCount();
            while(res.next()){
                //如果结果发生变化，实例化Class对象
                Object obj=clazz.newInstance();
                //遍历字段数量根据序号获取对应的字段名
                //getColumnName字段名 getColumnLabel字段名或别名
                for(int i=1;i<=meteNum;i++){
                    String fieldName=meteData.getColumnLabel(i);
                    //根据字段名通过反射获取字段属性
                    Field field=obj.getClass().getDeclaredField(fieldName);
                    //拼接set方法字符串
                    String setSplicing="set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
                    //根据拼接的set方法字符串和对应属性的类型反射拿到真正的set方法
                    Method setMethod=obj.getClass().getMethod(setSplicing,field.getType());
                    //通过别名查询结果集中每个字段的值
                    Object fieldValue=res.getObject(fieldName);
                    //调用set方法的invoke把值存到obj对象中
                    setMethod.invoke(obj,fieldValue);
                }
                //将添加完值的obj对象添加到User类型的集合中
                list.add(obj);

            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            DbUtils.close(res,conn,pre);
        }
        return list;
    }
    /**
     * 查询对象
     * @param sql
     * @param params
     * @param clazz
     * @return
     */
    public static Object queryFirstRow(String sql,List<Object> params,Class clazz) {
        Object obj = null;
        List list = queryRows(sql, params, clazz);
        if (list != null && list.size() > 0) {
            obj = list.get(0);
        }
        return obj;
    }

  /*  public static int updateUser(User user) {
        String sql="update tb_user set nick=?,mood=?,head=? where userId=?";
        String sql2="update tb_user set nick=?,mood=?,head=? where userId=?";
        List<Object> params=new ArrayList<>();
        params.add(user.getNick());
        params.add(user.getMood());
        params.add(user.getHead());
        params.add(user.getUserId());
        int rows= BaseDao.excuteUpdate(sql,params);
        return rows;
    }*/

    /*public static long queryCountNotebyTypeId(String typeId) {
        //准备sql
        String sql="select count(*) from tb_note where typeId=? ";
        List<Object> params=new ArrayList<>();
        //把属性装进去
        params.add(typeId);
        //拿到查询的云记的数量
        long count= (long) BaseDao.findSingle(sql,params);
        return count;
    }*/

   /* public static int deleteNoteTypebyTypeId(String typeId) {
        //准备sql
        String sql="delete from tb_note_type where typeId=?";
        List<Object> params=new ArrayList<>();
        //装属性
        params.add(typeId);
        //拿到删除操作的影响行数
        int rows = BaseDao.excuteUpdate(sql, params);
        return rows;
    }*/


}
