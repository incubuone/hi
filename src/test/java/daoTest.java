import com.xxxx.note.dao.BaseDao;
import com.xxxx.note.dao.UserDao;
import com.xxxx.note.po.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class daoTest {
    @Test
    public void daoTest(){
        UserDao userdao=new UserDao();
        User user=null;
        user=userdao.queryByName("admin");
        System.out.println(user);

    }
//    @Test
    public  void insertTest(){
        String sql="insert into tb_user(uname,upwd,nick,head,mood) values(?,?,?,?,?)";
        List<Object> list=new ArrayList<>();
        list.add("lisi");
        list.add("e10adc3949ba59abbe56e057f20f883e");
        list.add("ls");
        list.add("404.jpg");
        list.add("I'm GOD!!");
        int num= BaseDao.excuteUpdate(sql,list);
        System.out.println(num);
    }
}
