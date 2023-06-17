import com.xxxx.note.utils.DbUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class dbTest {
    private Logger logger= LoggerFactory.getLogger(dbTest.class);
    @Test
    public void dbTest(){
        System.out.println(DbUtils.getConnection());
        logger.info("连接"+ DbUtils.getConnection());
        logger.info("{}连接", DbUtils.getConnection());
    }
}
