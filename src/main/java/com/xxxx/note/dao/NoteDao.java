package com.xxxx.note.dao;

import cn.hutool.core.util.StrUtil;
import com.xxxx.note.po.Note;
import com.xxxx.note.vo.NoteVo;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {
    /**
     * 查询当前登录用户的云记数量，返回总记录数
     * @param userId
     * @return
     */
    public static long queryNoteCount(Integer userId,String title,String date,String typeId) {
        //准备sql  连表查
        String sql="select count(*) from tb_note natural join tb_note_type where userId=? ";
        List<Object> params=new ArrayList<>();
        //设置参数
        params.add(userId);
        //如果title不为空就拼接，作为模糊查询的条件
        if(!StrUtil.isBlank(title)){
            sql += " and title like concat('%',?,'%') ";
            params.add(title);
        }else if(!StrUtil.isBlank(date)){//如果date不为空就拼接，作为模糊查询的条件
           sql+= " and date_format(pubTime,'%Y年%m月')= ?";
           params.add(date);
        }else if(!StrUtil.isBlank(typeId)){//如果typeId不为空就拼接，作为模糊查询的条件
           sql+= " and typeId= ?";
           params.add(typeId);
        }
        //调用查询方法返回查询的数量
        long count= (long) BaseDao.findSingle(sql,params);
        return count;

    }
    /**
     * 分页查询当前登录用户下当前页的数据列表，返回note集合
     * @param userId
     * @param index
     * @param pageSize
     * @return
     */
    public static List<Note> findNoteListByPage(Integer index, Integer pageSize, Integer userId,String title,String date,String typeId) {
        //准备sql  连表查
        String sql="select noteId,title,pubTime from tb_note natural join tb_note_type where userId=? ";
        List<Object> params=new ArrayList<>();
        //设置参数
        params.add(userId);
        if(!StrUtil.isBlank(title)){
            //如果标题不为空，就拼接
            sql+= " and title like concat('%',?,'%') ";
            params.add(title);
        }else if(!StrUtil.isBlank(date)){//如果date不为空就拼接，作为模糊查询的条件
            sql+= " and date_format(pubTime,'%Y年%m月')= ?";
            params.add(date);
        }else if(!StrUtil.isBlank(typeId)){//如果typeId不为空就拼接，作为模糊查询的条件
            sql+= " and typeId = ?";
            params.add(typeId);
        }

        //拼接分页条件
        sql+= " order by pubTime limit ?,?";
        params.add(index);
        params.add(pageSize);
        //调用查询方法返回noteList集合
        List<Note> noteList= BaseDao.queryRows(sql,params,Note.class);
        return noteList;
    }
    /**
     * 通过id查询云记对象
     * @param noteId
     * @return
     */
    public static Note findNoteByNoteId(String noteId) {
        //准备sql
        String sql ="select * from tb_note where noteId=?";
        List<Object> params=new ArrayList<>();
        //添加参数
        params.add(noteId);
        //调用baseDao的查询方法返回note对象
        Note note= (Note) BaseDao.queryFirstRow(sql,params,Note.class);
        return note;
    }
    /**
     * 添加或修改云记，返回受影响的行数
     * @param note
     * @return
     */
    public int addOrUpdate(Note note) {
        //准备sql
        String sql=" ";
        List<Object> params=new ArrayList<>();
        //添加参数
        params.add(note.getTypeId());
        params.add(note.getTitle());
        params.add(note.getContent());
        params.add(note.getLon());
        params.add(note.getLat());
        //如果是修改操作，需要noteId指定修改哪一条云记
        if(note.getNoteId()!=null){
           params.add(note.getNoteId());
           sql+= "update tb_note set typeId=?,title=?,content=? where noteId=?";
        }else{
           sql+= "insert into tb_note(typeId,title,content,pubTime,lon,lat) values(?,?,?,now(),?,?)";
        }
        //调用方法返回首影响行数
        int rows= BaseDao.excuteUpdate(sql,params);
        return rows;
    }
    /**
     * 通过日期分组查询当前登录用户下的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByData(Integer userId) {
        String sql="SELECT" +
                " count( 1 ) noteCount," +
                " DATE_FORMAT(pubTime,'%Y年%m月') groupName " +
                " FROM tb_note natural join tb_note_type where userId=? " +
                " group by " +
                " DATE_FORMAT(pubTime,'%Y年%m月' ) " +
                " ORDER BY" +
                " DATE_FORMAT(pubTime,'%Y年%m月') DESC";
        List<Object> params=new ArrayList<>();
        //添加参数
        params.add(userId);
        //返回list集合
        List<NoteVo> list = BaseDao.queryRows(sql,params, NoteVo.class);
        return list;
    }
    /**
     * 通过类型分组查询当前登录用户下的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByType(Integer userId) {
        String sql="SELECT count(noteId) noteCount, t.typeId,typeName groupName FROM tb_note n " +
                " RIGHT JOIN tb_note_type t ON n.typeId = t.typeId WHERE userId = ? " +
                " GROUP BY t.typeId ORDER BY COUNT(noteId) DESC ";
        List<Object> params=new ArrayList<>();
        //添加参数
        params.add(userId);
        //返回list集合
        List<NoteVo> list = BaseDao.queryRows(sql,params, NoteVo.class);
        return list;
    }
    /**
     * 通过noteId删除云记记录，返回受影响的行数
     * @param noteId
     * @return
     */
    public int deleteNoteByNoteId(String noteId) {
        //准备sql
        String sql="delete from tb_note where noteId=?";
        List<Object> params=new ArrayList<>();
        //添加参数
        params.add(noteId);
        int rows= BaseDao.excuteUpdate(sql,params);
        return rows;
    }
    /**
     * 通过用户ID查询云记列表的经纬度
     * @param userId
     * @return
     */
    public List<Note> queryNoteLonAndLat(Integer userId) {
        //准备sql
        String sql="select lon,lat from tb_note natural join tb_note_type where userId=?";
        //创建一个object类型的集合
        List<Object> params=new ArrayList<>();
        //添加参数
        params.add(userId);
        //调用baseDao的查询集合的方法
        List<Note> locList=BaseDao.queryRows(sql,params,Note.class);
        return locList;
    }
}
