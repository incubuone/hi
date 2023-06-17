package com.xxxx.note.service;

import cn.hutool.core.util.StrUtil;
import com.xxxx.note.dao.NoteDao;
import com.xxxx.note.po.Note;
import com.xxxx.note.utils.Page;
import com.xxxx.note.vo.NoteVo;
import com.xxxx.note.vo.ResultInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteService {
    private NoteDao noteDao=new NoteDao();
    private ResultInfo resultInfo=new ResultInfo();
    /**
     * 查询云记详情
     1. 参数的非空判断
     2. 调用Dao层的查询，通过noteId查询note对象
     3. 返回note对象
     * @param noteId
     * @return
     */
    public static Note findNoteByNoteId(String noteId) {
        //参数非空判断
        if(StrUtil.isBlank(noteId)){
            return null;
        }
        //调用查询方法返回note对象
        Note note=NoteDao.findNoteByNoteId(noteId);
        return note;
    }
    /**
     * 添加或修改云记
        1. 参数的非空判断
        如果为空，code=0，msg=xxx，result=note对象，返回resultInfo对象
        2. 设置回显对象 Note对象
        3. 调用Dao层，添加云记记录，返回受影响的行数
        4. 判断受影响的行数
        如果大于0，code=1
        如果不大于0，code=0，msg=xxx，result=note对象
        5. 返回resultInfo对象
     * @param typeId
     * @param title
     * @param content
     * @return
     */
    public ResultInfo<Note> addOrUpdate(String typeId, String title, String content,
                                        String noteId,String lon,String lat) {
        //生成回显对象
        Note note=new Note();
        //参数非空判断
        if(StrUtil.isBlank(typeId)){
           resultInfo.setMsg("云记类型不能为空");
           resultInfo.setFlag(false);
           return resultInfo;
        }
        if(StrUtil.isBlank(title)){
            resultInfo.setMsg("云记标题不能为空");
            resultInfo.setFlag(false);
            return resultInfo;
        }
        if(StrUtil.isBlank(content)){
            resultInfo.setMsg("云记内容不能为空");
            resultInfo.setFlag(false);
            return resultInfo;
        }
        //判断noteId是否为空，如果为空的画，证明是修改操作
        //需要加上noteId的属性
        if(!StrUtil.isBlank(noteId)){
            note.setNoteId(Integer.parseInt(noteId));
        }
        //设置参数
        note.setTypeId(Integer.parseInt(typeId));
        note.setTitle(title);
        note.setContent(content);

        //经纬度非空判断，如果为空采用默认值
        if(lon==null || lat==null){
            lon="112.882736";
            lat="28.236875";
        }
        note.setLon(Float.parseFloat(lon));
        note.setLat(Float.parseFloat(lat));
        int rows=noteDao.addOrUpdate(note);
        if(rows>0){
            resultInfo.setFlag(true);
            resultInfo.setMsg("操作成功");
        }else{
            resultInfo.setResultInfo(note);
            resultInfo.setFlag(false);
            resultInfo.setMsg("操作失败");
        }
        return resultInfo;
    }
    /**
     * 分页查询云记列表
        1. 参数的非空校验
        如果分页参数为空，则设置默认值
        2. 查询当前登录用户的云记数量，返回总记录数 （long类型）
        3. 判断总记录数是否大于0
        4. 如果总记录数大于0，调用Page类的带参构造，得到其他分页参数的值，返回Page对象
        5. 查询当前登录用户下当前页的数据列表，返回note集合
        6. 将note集合设置到page对象中
        7. 返回Page对象
     * @param pageNumStr
     * @param pageSizeStr
     * @param userId
     * @param title  条件查询的参数：标题
     * @return
     */
    public Page<Note> findNoteListByPage(String pageNumStr, String pageSizeStr, Integer userId,String title,String date,String typeId) {
        //设置分页开始的页数和长度
        Integer pageNum=1;
        Integer pageSize=5;
        //如果不为空，替换pageNum,pageSize
        if(!StrUtil.isBlank(pageNumStr)){
            pageNum=Integer.parseInt(pageNumStr);
        }
        if(!StrUtil.isBlank(pageSizeStr)){
            pageSize=Integer.parseInt(pageSizeStr);
        }
        //返回查询的云记数量
        long noteCount=NoteDao.queryNoteCount(userId,title,date,typeId);
        Page<Note> page=new Page<>(pageNum,pageSize,noteCount);
        if(noteCount<1){
           return null;
        }
        //云记每一页开始的序号
        Integer index=(pageNum-1)*pageSize;
        //返回查询到的note集合
        List<Note> noteList=NoteDao.findNoteListByPage(index,pageSize,userId,title,date,typeId);
        //将noteList集合设置到page对象中
        page.setDataList(noteList);
        return page;
    }
    /**
     * 通过日期分组查询当前登录用户下的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByData(Integer userId) {
        return noteDao.findNoteCountByData(userId);
    }
    /**
     * 通过类型分组查询当前登录用户下的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByType(Integer userId) {
        return noteDao.findNoteCountByType(userId);
    }
    /**
     * 删除云记
     1. 判断参数
     2. 调用Dao层的更新方法，返回受影响的行数
     3. 判断受影响的行数是否大于0
     如果大于0，返回1；否则返回0
     * @param noteId
     * @return
     */
    public Boolean deleteNote(String noteId) {
        //参数的非空判断
        if(StrUtil.isBlank(noteId)){
            return false;
        }
        //返回删除方法的受影响行数
        int rows=noteDao.deleteNoteByNoteId(noteId);
        if(rows>0){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 通过月份查询对应的云记数量
     * @param userId
     * @return
     */
    public ResultInfo<Map<String, Object>> queryNoteCountByMonth(Integer userId) {
        ResultInfo<Map<String,Object>> resultInfo=new ResultInfo<>();
        //通过月份查询云记数量
        List<NoteVo> noteList=noteDao.findNoteCountByData(userId);
        //非空判断
        if(noteList!=null& noteList.size()>0){
            //新建月份和数据的数组
            List<String> monthList=new ArrayList<>();
            List<Integer> dataList=new ArrayList<>();
            //遍历noteList数组赋值
            for(NoteVo a:noteList){
                //groupName代表分组数据
                monthList.add(a.getGroupName());
                //noteCount云记数量
                dataList.add((int) a.getNoteCount());
            }
            //新建一个Map集合，将dataList和monthList放入，key要ajax的设置一样
            Map<String,Object> map=new HashMap<>();
            map.put("monthArray",monthList);
            map.put("dataArray",dataList);
            resultInfo.setFlag(true);
            resultInfo.setResultInfo(map);
            /*resultInfo.getResultInfo().get("monthArray");*/
        }
        return resultInfo;
    }
    /**
     * 查询用户发布云记时的坐标
     * @param userId
     * @return
     */
    public ResultInfo<List<Note>> getNoteLocation(Integer userId) {
        ResultInfo<List<Note>> resultInfo=new ResultInfo<>();
        //调用dao层的方法通过userId查询
        List<Note> noteList=noteDao.queryNoteLonAndLat(userId);
        if(noteList!=null & noteList.size()>0){
            resultInfo.setFlag(true);
            resultInfo.setResultInfo(noteList);
        }
        return resultInfo;
    }
}
