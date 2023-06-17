package com.xxxx.note.service;

import cn.hutool.core.util.StrUtil;
import com.xxxx.note.dao.BaseDao;
import com.xxxx.note.dao.NoteTypeDao;
import com.xxxx.note.po.NoteType;
import com.xxxx.note.vo.ResultInfo;

import java.util.List;

public class TypeService {
    private static NoteTypeDao typeDao=new NoteTypeDao();
    /**
     * 查询类型列表
     1. 调用Dao层的查询方法，通过用户ID查询类型集合
     2. 返回类型集合
     * @param userId
     * @return
     */
    public static List<NoteType> findNoteTypeList(Integer userId){
        //调用noteTypeDao，接收返回的list集合
        List<NoteType> list=typeDao.queryListByUserId(userId);
        return list;
    }
    /**
     * 删除类型
     1. 判断参数是否为空
     2. 调用Dao层，通过类型ID查询云记记录的数量
     3. 如果云记数量大于0，说明存在子记录，不可删除
     code=0，msg="该类型存在子记录，不可删除"，返回resultInfo对象
     4. 如果不存在子记录，调用Dao层的更新方法，通过类型ID删除指定的类型记录，返回受影响的行数
     5. 判断受影响的行数是否大于0
     大于0，code=1；否则，code=0，msg="删除失败"
     6. 返回ResultInfo对象
     * @param typeId
     * @return
     */
    public static ResultInfo<NoteType> deleteTypeList(String typeId) {
        ResultInfo<NoteType> resultInfo=new ResultInfo<>();
        //验证传过来的typeId是不是空
        if(StrUtil.isBlank(typeId)){
            resultInfo.setFlag(false);
            resultInfo.setMsg("typeId传输错误");
            return resultInfo;
        }
        BaseDao BaseDao =new BaseDao();
        //查询子表的云记数量
        long countNote=typeDao.queryCountNotebyTypeId(typeId);
        //数量大于0，就不能删除
        if(countNote>0){
            resultInfo.setFlag(false);
            resultInfo.setMsg("云记数量大于0，不能删除");
            return resultInfo;
        }
        //查询删除sql的影响行数
        int rows= typeDao.deleteNoteTypebyTypeId(typeId);
        //大于0成功，否则失败
        if(rows>0){
            resultInfo.setMsg("删除成功");
            resultInfo.setFlag(true);
        }else{
            resultInfo.setMsg("删除失败");
            resultInfo.setFlag(false);
        }
        return resultInfo;
    }
    /**
     * 添加或修改类型
        1. 判断参数是否为空 （类型名称）
        如果为空，code=0，msg=xxx，返回ResultInfo对象
        2. 调用Dao层，查询当前登录用户下，类型名称是否唯一，返回0或1
        如果不可用，code=0，msg=xxx，返回ResultInfo对象
        3. 判断类型ID是否为空
        如果为空，调用Dao层的添加方法，返回主键 （前台页面需要显示添加成功之后的类型ID）
        如果不为空，调用Dao层的修改方法，返回受影响的行数
        4. 判断 主键/受影响的行数 是否大于0
        如果大于0，则更新成功
        code=1，result=主键
        如果不大于0，则更新失败
        code=0，msg=xxx
     * @param typeName
     * @param userId
     * @param typeId
     * @return
     */
    public static ResultInfo<Integer> addOrUpdate(String typeName, String typeId, Integer userId) {
        ResultInfo<Integer> resultInfo=new ResultInfo<>();
        //验证用类性名称是否为空
        if(StrUtil.isBlank(typeName)) {
            resultInfo.setMsg("类型名称不能为空");
            resultInfo.setFlag(false);
            return resultInfo;
        }
        //查询是否存在相同的类型
        boolean flag=typeDao.queryTypeNameById(typeName,typeId,userId);
        //返回结果如果为假，直接return
        Integer key=0;
        if(!flag){
           resultInfo.setMsg("类型名称已存在");
           resultInfo.setFlag(false);
           return resultInfo;
        }
        //根据typeId是否存在来决定执行添加还是修改的方法
        if(StrUtil.isBlank(typeId)){
            //接收
            key=typeDao.addType(typeName,userId);
            resultInfo.setResultInfo(key);
        }else{
            key=typeDao.updateType(typeId,typeName);
        }
        //如果返回的主键/影响行数>0,证明操作成功，
        //执行添加方法事，key是主键，执行修改操作时，key时影响行数
        if(key>0){
            resultInfo.setMsg("添加或修改成功");
            resultInfo.setFlag(true);
        }else{
            resultInfo.setMsg("添加或修改失败");
            resultInfo.setFlag(false);
        }

        return resultInfo;
    }
}
