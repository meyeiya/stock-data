import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration({ 
    "file:src/main/resources/applicationContext.xml" 
})  
  
// 添加注释@Transactional 回滚对数据库操作    
@Transactional  
public class BaseCase {

}
